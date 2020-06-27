package com.balance.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testcontainers.containers.GenericContainer;

import com.balance.model.Client;
import com.balance.model.Invoice;
import com.balance.repository.mongodb.ClientMongoRepository;
import com.balance.repository.mongodb.InvoiceMongoRepository;
import com.balance.utils.DateTestsUtil;
import com.mongodb.DBRef;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class InvoiceMongoRepositoryTest {
	private static final String BUDGET_DB_NAME = "budget";
	private static final String CLIENT_COLLECTION_NAME = "client";
	private static final String INVOICE_COLLECTION_NAME = "invoice";

	private static final String FIELD_PK="_id";
	private static final String FIELD_CLIENT="client";
	private static final String FIELD_DATE="date";
	private static final String FIELD_REVENUE="revenue";
	
	private static final Client CLIENT_FIXTURE_1=
			new Client(new ObjectId().toString(), "test identifier 1"); 
	private static final Client CLIENT_FIXTURE_2=
			new Client(new ObjectId().toString(), "test identifier 2");
	
	private static final int YEAR_FIXTURE=2019;
	
	private static final Date DATE_OF_THE_YEAR_FIXTURE=DateTestsUtil.getDateFromYear(YEAR_FIXTURE);
	private static final Date FIRST_DAY_OF_THE_YEAR_FIXTURE=DateTestsUtil.getFirstDayOfYear(YEAR_FIXTURE);
	private static final Date LAST_DAY_OF_THE_YEAR_FIXTURE=DateTestsUtil.getLastDayOfYear(YEAR_FIXTURE);
	private static final Date LAST_DAY_OF_THE_PREVIOUS_YEAR_FIXTURE=DateTestsUtil.getLastDayOfYear(YEAR_FIXTURE-1);
	private static final Date FIRST_DAY_OF_THE_NEXT_YEAR_FIXTURE=DateTestsUtil.getFirstDayOfYear(YEAR_FIXTURE+1);
	private static final Date DATE_NOT_OF_THE_YEAR_FIXTURE=DateTestsUtil.getDateFromYear(YEAR_FIXTURE-1);
	
	@SuppressWarnings("rawtypes")
	@ClassRule
	public static final GenericContainer mongo = new GenericContainer("mongo:4.2.3") .withExposedPorts(27017) .withCommand("--replSet rs0");
	
	private MongoClient mongoClient;
	private MongoCollection<Document> invoiceCollection;

	@Mock
	private ClientMongoRepository clientRepository;
	
	@InjectMocks
	private InvoiceMongoRepository invoiceRepository;
	
	@BeforeClass
	public static void init() throws UnsupportedOperationException, IOException, InterruptedException {
		mongo.start();
		mongo.execInContainer("/bin/bash", "-c", "mongo --eval 'rs.initiate()' --quiet");
        mongo.execInContainer("/bin/bash", "-c",
	            "until mongo --eval 'rs.isMaster()' | grep ismaster | grep true > /dev/null 2>&1;do sleep 1;done"); 
	}
	
	@Before
	public void setup() { 
		mongoClient = new MongoClient(new ServerAddress(mongo.getContainerIpAddress(), mongo.getMappedPort(27017)));	
		invoiceRepository = new InvoiceMongoRepository(mongoClient, mongoClient.startSession(),
				BUDGET_DB_NAME, INVOICE_COLLECTION_NAME, clientRepository);
		MockitoAnnotations.initMocks(this);
		MongoDatabase database = mongoClient.getDatabase(BUDGET_DB_NAME);
		database.drop();
		invoiceCollection = database.getCollection(INVOICE_COLLECTION_NAME);
	}
	
	@After
	public void tearDown() { 
		mongoClient.close();
	}
	
	@Test
	public void testFindAllInvoicesWhenDatabaseIsEmpty() {
		assertThat(invoiceRepository.findAll()).isEmpty();
	}
	
	@Test
	public void testFindAllWhenDatabaseIsNotEmpty() {
		addTestInvoiceToDatabase(CLIENT_FIXTURE_1.getId(), DATE_OF_THE_YEAR_FIXTURE , 10);
		addTestInvoiceToDatabase(CLIENT_FIXTURE_2.getId(), DATE_OF_THE_YEAR_FIXTURE, 20);
		when(clientRepository.findById(CLIENT_FIXTURE_1.getId())).thenReturn(CLIENT_FIXTURE_1);
		when(clientRepository.findById(CLIENT_FIXTURE_2.getId())).thenReturn(CLIENT_FIXTURE_2);
		assertThat(invoiceRepository.findAll())
			.containsExactly(new Invoice(CLIENT_FIXTURE_1, DATE_OF_THE_YEAR_FIXTURE, 10 ),
			new Invoice(CLIENT_FIXTURE_2, DATE_OF_THE_YEAR_FIXTURE, 20));
	}
	
	@Test
	public void testFindByIdNotFound() {
		assertThat(invoiceRepository.findById(new ObjectId().toString())).isNull();
	}
	
	@Test
	public void testFindByIdFound() {
		String idInvoiceTest1=addTestInvoiceToDatabase(CLIENT_FIXTURE_1.getId(), DATE_OF_THE_YEAR_FIXTURE, 10);
		addTestInvoiceToDatabase(CLIENT_FIXTURE_2.getId(), DATE_OF_THE_YEAR_FIXTURE, 20);
		when(clientRepository.findById(CLIENT_FIXTURE_1.getId())).thenReturn(CLIENT_FIXTURE_1);
		assertThat(invoiceRepository.findById(idInvoiceTest1)).isEqualTo(
				new Invoice(CLIENT_FIXTURE_1, DATE_OF_THE_YEAR_FIXTURE, 10 ));
	}
	
	@Test
	public void testSave() {
		Invoice invoice = new Invoice(CLIENT_FIXTURE_1, DATE_OF_THE_YEAR_FIXTURE, 10 );
		when(clientRepository.findById(CLIENT_FIXTURE_1.getId())).thenReturn(CLIENT_FIXTURE_1);
		when(clientRepository.getClientCollection())
			.thenReturn(mongoClient.getDatabase(BUDGET_DB_NAME).getCollection(CLIENT_COLLECTION_NAME));
		Invoice invoiceResult=invoiceRepository.save(invoice);
		assertThat(invoiceResult).isEqualTo(
				new Invoice(CLIENT_FIXTURE_1, DATE_OF_THE_YEAR_FIXTURE, 10 ));
		assertThat(invoiceResult.getId()).isNotNull();
		assertThat(readAllInvoicesFromDatabase()).containsExactly(new Invoice(
				CLIENT_FIXTURE_1, DATE_OF_THE_YEAR_FIXTURE, 10 ));
	}
	
	@Test
	public void testDeleteWhenInvoiceIsPresentInDatabase() {
		String idInvoiceTest=addTestInvoiceToDatabase(CLIENT_FIXTURE_1.getId(), DATE_OF_THE_YEAR_FIXTURE , 10);
		when(clientRepository.findById(CLIENT_FIXTURE_1.getId())).thenReturn(CLIENT_FIXTURE_1);
		Invoice invoiceRemoved=invoiceRepository.delete(idInvoiceTest); 
		assertThat(invoiceRemoved).isEqualTo(new Invoice(CLIENT_FIXTURE_1, DATE_OF_THE_YEAR_FIXTURE , 10));
		assertThat(readAllInvoicesFromDatabase()).isEmpty();
	}
	
	@Test
	public void testDeleteWhenInvoiceIsNotPresentInDatabase() {
		assertThat(invoiceRepository.delete(new ObjectId().toString())).isNull();
	}
	
	@Test
	public void testFindInvoicesByYearWhenDatabaseIsEmpty() {
		assertThat(invoiceRepository.findInvoicesByYear(YEAR_FIXTURE)).isEmpty();
	}
	
	@Test
	public void testFindInvoicesByYearWhenThereAreInvoicesAllSameYearInDatabase() {
		addTestInvoiceToDatabase(CLIENT_FIXTURE_1.getId(), DATE_OF_THE_YEAR_FIXTURE, 10);
		addTestInvoiceToDatabase(CLIENT_FIXTURE_2.getId(), DATE_OF_THE_YEAR_FIXTURE, 20);
		when(clientRepository.findById(CLIENT_FIXTURE_1.getId())).thenReturn(CLIENT_FIXTURE_1);
		when(clientRepository.findById(CLIENT_FIXTURE_2.getId())).thenReturn(CLIENT_FIXTURE_2);
		assertThat(invoiceRepository.findInvoicesByYear(YEAR_FIXTURE))
			.containsExactly(new Invoice(CLIENT_FIXTURE_1, DATE_OF_THE_YEAR_FIXTURE, 10 ),
			new Invoice(CLIENT_FIXTURE_2, DATE_OF_THE_YEAR_FIXTURE, 20));
	}
	
	@Test
	public void testFindAllInvoicesByYearWhenThereAreInvoicesAllDifferentYearInDatabase() {
		addTestInvoiceToDatabase(CLIENT_FIXTURE_1.getId(), DATE_OF_THE_YEAR_FIXTURE, 10);
		addTestInvoiceToDatabase(CLIENT_FIXTURE_2.getId(), DATE_NOT_OF_THE_YEAR_FIXTURE, 20);
		when(clientRepository.findById(CLIENT_FIXTURE_1.getId())).thenReturn(CLIENT_FIXTURE_1);
		when(clientRepository.findById(CLIENT_FIXTURE_2.getId())).thenReturn(CLIENT_FIXTURE_2);
		assertThat(invoiceRepository.findInvoicesByYear(YEAR_FIXTURE))
			.containsOnly(new Invoice(CLIENT_FIXTURE_1, DATE_OF_THE_YEAR_FIXTURE, 10));
	}
	
	@Test
	public void testFindInvoicesByYearWhenThereAreInvoicesOfLimitDayYearsInDatabase() {
		addTestInvoiceToDatabase(CLIENT_FIXTURE_1.getId(), FIRST_DAY_OF_THE_YEAR_FIXTURE, 10);
		addTestInvoiceToDatabase(CLIENT_FIXTURE_1.getId(), LAST_DAY_OF_THE_YEAR_FIXTURE, 20);
		addTestInvoiceToDatabase(CLIENT_FIXTURE_1.getId(), LAST_DAY_OF_THE_PREVIOUS_YEAR_FIXTURE, 30);
		addTestInvoiceToDatabase(CLIENT_FIXTURE_1.getId(), FIRST_DAY_OF_THE_NEXT_YEAR_FIXTURE, 40);
		when(clientRepository.findById(CLIENT_FIXTURE_1.getId())).thenReturn(CLIENT_FIXTURE_1);
		assertThat(invoiceRepository.findInvoicesByYear(YEAR_FIXTURE))
		.containsExactly(new Invoice(CLIENT_FIXTURE_1, FIRST_DAY_OF_THE_YEAR_FIXTURE, 10),
				new Invoice(CLIENT_FIXTURE_1, LAST_DAY_OF_THE_YEAR_FIXTURE, 20));
	}
	
	@Test
	public void testGetYearsOfInvoicesInDatabaseWhenDatabaseIsEmpty() {
		assertThat(invoiceRepository.getYearsOfInvoicesInDatabase()).isEmpty();
	}
	
	@Test
	public void testGetYearsOfInvoicesInDatabaseWhenDatabaseIsNotEmpty() {
		addTestInvoiceToDatabase(CLIENT_FIXTURE_1.getId(), DateTestsUtil.getDateFromYear(YEAR_FIXTURE), 10);
		addTestInvoiceToDatabase(CLIENT_FIXTURE_1.getId(), DateTestsUtil.getDateFromYear(YEAR_FIXTURE-1), 20);
		addTestInvoiceToDatabase(CLIENT_FIXTURE_2.getId(), DateTestsUtil.getDateFromYear(YEAR_FIXTURE), 30);
		when(clientRepository.findById(CLIENT_FIXTURE_1.getId())).thenReturn(CLIENT_FIXTURE_1);
		when(clientRepository.findById(CLIENT_FIXTURE_2.getId())).thenReturn(CLIENT_FIXTURE_2);
		assertThat(invoiceRepository.getYearsOfInvoicesInDatabase()).containsExactly(
				YEAR_FIXTURE-1,YEAR_FIXTURE);
	}
	
	@Test
	public void testGetInvoicesOfClientAndYearWhenNotPresentInvoicesOfTheClientInDatabase() {
		addTestInvoiceToDatabase(CLIENT_FIXTURE_2.getId(), 
				DATE_OF_THE_YEAR_FIXTURE, 10);
		addTestInvoiceToDatabase(CLIENT_FIXTURE_2.getId(), 
				DATE_NOT_OF_THE_YEAR_FIXTURE, 20);
		assertThat(invoiceRepository.findInvoicesByClientAndYear(CLIENT_FIXTURE_1, YEAR_FIXTURE))
			.isEmpty();
	}
	
	@Test
	public void testGetInvoicesOfClientAndYearWhenThereAreOnlyInvoicesOfTheClientAndYearInDatabase() {
		addTestInvoiceToDatabase(CLIENT_FIXTURE_1.getId(), 
				DATE_OF_THE_YEAR_FIXTURE, 10);
		addTestInvoiceToDatabase(CLIENT_FIXTURE_1.getId(), 
				DATE_OF_THE_YEAR_FIXTURE, 20);
		when(clientRepository.findById(CLIENT_FIXTURE_1.getId())).thenReturn(CLIENT_FIXTURE_1);
		assertThat(invoiceRepository.findInvoicesByClientAndYear(CLIENT_FIXTURE_1, YEAR_FIXTURE))
			.containsExactly(new Invoice(CLIENT_FIXTURE_1, DATE_OF_THE_YEAR_FIXTURE, 10),
					new Invoice(CLIENT_FIXTURE_1, DATE_OF_THE_YEAR_FIXTURE, 20));
	}
	
	@Test
	public void testGetInvoicesOfClientAndYearWhenThereAreOnlyInvoicesOfTheClientInDatabase() {
		addTestInvoiceToDatabase(CLIENT_FIXTURE_1.getId(), 
				DATE_OF_THE_YEAR_FIXTURE, 10);
		addTestInvoiceToDatabase(CLIENT_FIXTURE_1.getId(), 
				DATE_NOT_OF_THE_YEAR_FIXTURE, 20);
		when(clientRepository.findById(CLIENT_FIXTURE_1.getId())).thenReturn(CLIENT_FIXTURE_1);
		assertThat(invoiceRepository.findInvoicesByClientAndYear(CLIENT_FIXTURE_1, YEAR_FIXTURE))
			.containsExactly(new Invoice(CLIENT_FIXTURE_1, DATE_OF_THE_YEAR_FIXTURE, 10));
	}
	
	@Test
	public void testGetInvoicesOfClientAndYearWhenThereAreInvoicesOfTheDifferentClientsInDatabase() {
		addTestInvoiceToDatabase(CLIENT_FIXTURE_1.getId(), 
				DATE_OF_THE_YEAR_FIXTURE, 10);
		addTestInvoiceToDatabase(CLIENT_FIXTURE_1.getId(), 
				DATE_NOT_OF_THE_YEAR_FIXTURE, 20);
		addTestInvoiceToDatabase(CLIENT_FIXTURE_2.getId(), 
				DATE_OF_THE_YEAR_FIXTURE, 30);
		when(clientRepository.findById(CLIENT_FIXTURE_1.getId())).thenReturn(CLIENT_FIXTURE_1);
		when(clientRepository.findById(CLIENT_FIXTURE_2.getId())).thenReturn(CLIENT_FIXTURE_2);
		assertThat(invoiceRepository.findInvoicesByClientAndYear(CLIENT_FIXTURE_1, YEAR_FIXTURE))
			.containsExactly(new Invoice(CLIENT_FIXTURE_1, DATE_OF_THE_YEAR_FIXTURE, 10));
	}
	
	@Test
	public void testDeleteAllInvoicesOfAClient() {
		addTestInvoiceToDatabase(CLIENT_FIXTURE_1.getId(), 
				DATE_OF_THE_YEAR_FIXTURE, 10);
		addTestInvoiceToDatabase(CLIENT_FIXTURE_1.getId(), 
				DATE_NOT_OF_THE_YEAR_FIXTURE, 20);
		addTestInvoiceToDatabase(CLIENT_FIXTURE_2.getId(), 
				DATE_OF_THE_YEAR_FIXTURE, 30);
		when(clientRepository.findById(CLIENT_FIXTURE_2.getId())).thenReturn(CLIENT_FIXTURE_2);
		invoiceRepository.deleteAllInvoicesByClient(CLIENT_FIXTURE_1.getId());
		assertThat(readAllInvoicesFromDatabase()).containsOnly(
				new Invoice(CLIENT_FIXTURE_2, DATE_OF_THE_YEAR_FIXTURE,30));
	}
	
	private List<Invoice> readAllInvoicesFromDatabase() {
		return StreamSupport.stream(invoiceCollection.find().spliterator(), false).
				map(d -> new Invoice(d.get(FIELD_PK).toString(),
						 clientRepository.findById(((DBRef) d.get(FIELD_CLIENT)).getId().toString()),
						 d.getDate(FIELD_DATE),
						 d.getDouble(FIELD_REVENUE))).
				collect(Collectors.toList());		
	}
	 
	
	private String addTestInvoiceToDatabase(String clientId, Date date, double revenue) {
		Document invoiceToAdd=new Document().append(FIELD_CLIENT,
				 new DBRef(CLIENT_COLLECTION_NAME,
					 		new ObjectId(clientId)))
			 .append(FIELD_DATE, date)
			 .append(FIELD_REVENUE, revenue);
		invoiceCollection.insertOne(invoiceToAdd);
		return invoiceToAdd.get( FIELD_PK ).toString();
	}
	
}
