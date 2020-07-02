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
	private static final Date DATE_OF_THE_PREVIOUS_YEAR_FIXTURE=DateTestsUtil.getDateFromYear(YEAR_FIXTURE-1);
	
	@SuppressWarnings("rawtypes")
	@ClassRule
	public static final GenericContainer mongo = new GenericContainer("mongo:4.2.3")
														.withExposedPorts(27017) 
														.withCommand("--replSet rs0");
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
        mongo.execInContainer("/bin/bash", "-c", "until mongo --eval 'rs.isMaster()' "
											  + "| grep ismaster | grep true > /dev/null 2>&1;"
											  + "do sleep 1;done"); 
	}
	
	@Before
	public void setup() { 
		mongoClient = new MongoClient(new ServerAddress(mongo.getContainerIpAddress(), mongo.getMappedPort(27017)));	
		MongoDatabase database = mongoClient.getDatabase(BUDGET_DB_NAME);
		database.drop();
		database.createCollection(INVOICE_COLLECTION_NAME);
		invoiceRepository = new InvoiceMongoRepository(mongoClient, mongoClient.startSession(),
				BUDGET_DB_NAME, INVOICE_COLLECTION_NAME, clientRepository);
		MockitoAnnotations.initMocks(this);
		invoiceCollection = database.getCollection(INVOICE_COLLECTION_NAME);
		when(clientRepository.findById(CLIENT_FIXTURE_1.getId())).thenReturn(CLIENT_FIXTURE_1);
		when(clientRepository.findById(CLIENT_FIXTURE_2.getId())).thenReturn(CLIENT_FIXTURE_2);
	}
	
	@After
	public void tearDown() { 
		mongoClient.close();
	}
	
	@Test
	public void testCreateInvoiceCollectionInConstructorWhenCollectionNotExistingInDatabase() {
		String invoiceCollectionNotExisting="invoice_collection_not_existing_in_db";
		invoiceRepository = new InvoiceMongoRepository(mongoClient, mongoClient.startSession(),
				BUDGET_DB_NAME, invoiceCollectionNotExisting, clientRepository);
		assertThat(mongoClient.getDatabase(BUDGET_DB_NAME).listCollectionNames())
			.contains(invoiceCollectionNotExisting);
	}
	
	@Test
	public void testFindAllInvoicesWhenDatabaseIsEmpty() {
		List<Invoice> invoicesInDatabase=invoiceRepository.findAll();
		assertThat(invoicesInDatabase).isEmpty();
	}
	
	@Test
	public void testFindAllWhenDatabaseIsNotEmpty() {
		addTestInvoiceToDatabase(CLIENT_FIXTURE_1.getId(), DATE_OF_THE_YEAR_FIXTURE , 10);
		addTestInvoiceToDatabase(CLIENT_FIXTURE_2.getId(), DATE_OF_THE_YEAR_FIXTURE, 20);
		List<Invoice> invoicesInDatabase=invoiceRepository.findAll();
		assertThat(invoicesInDatabase) .containsExactly(
				new Invoice(CLIENT_FIXTURE_1, DATE_OF_THE_YEAR_FIXTURE, 10 ),
				new Invoice(CLIENT_FIXTURE_2, DATE_OF_THE_YEAR_FIXTURE, 20));
	}
	
	@Test
	public void testFindByIdNotFound() {
		Invoice invoiceFound=invoiceRepository.findById(new ObjectId().toString());
		assertThat(invoiceFound).isNull();
	}
	
	@Test
	public void testFindByIdFound() {
		String idInvoiceToFind=addTestInvoiceToDatabase(
				CLIENT_FIXTURE_1.getId(), DATE_OF_THE_YEAR_FIXTURE, 10);
		addTestInvoiceToDatabase(CLIENT_FIXTURE_1.getId(), DATE_OF_THE_YEAR_FIXTURE, 20);
		Invoice invoiceFound=invoiceRepository.findById(idInvoiceToFind);
		assertThat(invoiceFound).isEqualTo(
				new Invoice(CLIENT_FIXTURE_1, DATE_OF_THE_YEAR_FIXTURE, 10 ));
	}
	
	@Test
	public void testSave() {
		Invoice invoiceToAdd = new Invoice(CLIENT_FIXTURE_1, DATE_OF_THE_YEAR_FIXTURE, 10 );
		when(clientRepository.getClientCollection())
			.thenReturn(mongoClient.getDatabase(BUDGET_DB_NAME).getCollection(CLIENT_COLLECTION_NAME));
		Invoice invoiceAdded=invoiceRepository.save(invoiceToAdd);
		assertThat(invoiceAdded).isEqualTo(
				new Invoice(CLIENT_FIXTURE_1, DATE_OF_THE_YEAR_FIXTURE, 10 ));
		assertThat(invoiceAdded.getId()).isNotNull();
		List<Invoice> invoicesInDatabase=readAllInvoicesFromDatabase();
		assertThat(invoicesInDatabase).containsOnly(
				new Invoice(CLIENT_FIXTURE_1, DATE_OF_THE_YEAR_FIXTURE, 10 ));
	}
	
	@Test
	public void testDeleteWhenInvoiceExistingInDatabase() {
		String idInvoiceToRemove=addTestInvoiceToDatabase(
				CLIENT_FIXTURE_1.getId(), DATE_OF_THE_YEAR_FIXTURE , 10);
		Invoice invoiceRemoved=invoiceRepository.delete(idInvoiceToRemove); 
		assertThat(invoiceRemoved).isEqualTo(
				new Invoice(CLIENT_FIXTURE_1, DATE_OF_THE_YEAR_FIXTURE , 10));
		List<Invoice> invoicesInDatabase=readAllInvoicesFromDatabase();
		assertThat(invoicesInDatabase).isEmpty();
	}
	
	@Test
	public void testDeleteWhenInvoiceNotExistingInDatabase() {
		Invoice invoiceRemoved=invoiceRepository.delete(new ObjectId().toString());
		assertThat(invoiceRemoved).isNull();
	}
	
	@Test
	public void testFindInvoicesByYearWhenDatabaseIsEmpty() {
		List<Invoice> invoicesOfYearFixture=invoiceRepository.findInvoicesByYear(YEAR_FIXTURE);
		assertThat(invoicesOfYearFixture).isEmpty();
	}
	
	@Test
	public void testFindInvoicesByYearWhenThereAreInvoicesAllSameYearInDatabase() {
		addTestInvoiceToDatabase(CLIENT_FIXTURE_1.getId(), DATE_OF_THE_YEAR_FIXTURE, 10);
		addTestInvoiceToDatabase(CLIENT_FIXTURE_1.getId(), DATE_OF_THE_YEAR_FIXTURE, 20);
		List<Invoice> invoicesOfYear=invoiceRepository.findInvoicesByYear(YEAR_FIXTURE);
		assertThat(invoicesOfYear) .containsExactly(
					new Invoice(CLIENT_FIXTURE_1, DATE_OF_THE_YEAR_FIXTURE, 10 ),
					new Invoice(CLIENT_FIXTURE_1, DATE_OF_THE_YEAR_FIXTURE, 20));
	}
	
	@Test
	public void testFindAllInvoicesByYearWhenThereAreInvoicesAllDifferentYearsInDatabase() {
		addTestInvoiceToDatabase(CLIENT_FIXTURE_1.getId(), DATE_OF_THE_YEAR_FIXTURE, 10);
		addTestInvoiceToDatabase(CLIENT_FIXTURE_1.getId(), DATE_OF_THE_PREVIOUS_YEAR_FIXTURE, 20);
		when(clientRepository.findById(CLIENT_FIXTURE_1.getId())).thenReturn(CLIENT_FIXTURE_1);
		List<Invoice> invoicesOfYear=invoiceRepository.findInvoicesByYear(YEAR_FIXTURE);
		assertThat(invoicesOfYear).containsOnly(
					new Invoice(CLIENT_FIXTURE_1, DATE_OF_THE_YEAR_FIXTURE, 10));
	}
	
	@Test
	public void testFindInvoicesByYearWhenThereAreInvoicesOfLimitDayYearsInDatabase() {
		addTestInvoiceToDatabase(CLIENT_FIXTURE_1.getId(), FIRST_DAY_OF_THE_YEAR_FIXTURE, 10);
		addTestInvoiceToDatabase(CLIENT_FIXTURE_1.getId(), LAST_DAY_OF_THE_YEAR_FIXTURE, 20);
		addTestInvoiceToDatabase(CLIENT_FIXTURE_1.getId(), LAST_DAY_OF_THE_PREVIOUS_YEAR_FIXTURE, 30);
		addTestInvoiceToDatabase(CLIENT_FIXTURE_1.getId(), FIRST_DAY_OF_THE_NEXT_YEAR_FIXTURE, 40);
		List<Invoice> invoicesOfYear=invoiceRepository.findInvoicesByYear(YEAR_FIXTURE);
		assertThat(invoicesOfYear) .containsExactly(
					new Invoice(CLIENT_FIXTURE_1, FIRST_DAY_OF_THE_YEAR_FIXTURE, 10),
					new Invoice(CLIENT_FIXTURE_1, LAST_DAY_OF_THE_YEAR_FIXTURE, 20));
	}
	
	@Test
	public void testGetYearsOfInvoicesInDatabaseWhenDatabaseIsEmpty() {
		List<Integer> yearsOfTheInvoices=invoiceRepository.getYearsOfInvoicesInDatabase();
		assertThat(yearsOfTheInvoices).isEmpty();
	}
	
	@Test
	public void testGetYearsOfInvoicesInDatabaseWhenDatabaseIsNotEmpty() {
		addTestInvoiceToDatabase(CLIENT_FIXTURE_1.getId(), DATE_OF_THE_YEAR_FIXTURE, 10);
		addTestInvoiceToDatabase(CLIENT_FIXTURE_1.getId(), DATE_OF_THE_YEAR_FIXTURE, 30);
		addTestInvoiceToDatabase(CLIENT_FIXTURE_1.getId(), DATE_OF_THE_PREVIOUS_YEAR_FIXTURE, 20);
		List<Integer> yearsOfTheInvoices=invoiceRepository.getYearsOfInvoicesInDatabase();
		assertThat(yearsOfTheInvoices).contains(YEAR_FIXTURE-1,YEAR_FIXTURE);
	}
	
	@Test
	public void testGetInvoicesOfClientAndYearWhenNotExistingInvoicesOfTheClientInDatabase() {
		addTestInvoiceToDatabase(CLIENT_FIXTURE_2.getId(), DATE_OF_THE_YEAR_FIXTURE, 10);
		addTestInvoiceToDatabase(CLIENT_FIXTURE_2.getId(), DATE_OF_THE_PREVIOUS_YEAR_FIXTURE, 20);
		List<Invoice> invoicesOfYearAndClient=invoiceRepository
					.findInvoicesByClientAndYear(CLIENT_FIXTURE_1, YEAR_FIXTURE);
		assertThat(invoicesOfYearAndClient).isEmpty();
	}
	
	@Test
	public void testGetInvoicesOfClientAndYearWhenThereAreOnlyInvoicesOfTheClientAndYearInDatabase() {
		addTestInvoiceToDatabase(CLIENT_FIXTURE_1.getId(), DATE_OF_THE_YEAR_FIXTURE, 10);
		addTestInvoiceToDatabase(CLIENT_FIXTURE_1.getId(), DATE_OF_THE_YEAR_FIXTURE, 20);
		List<Invoice> invoicesOfYearAndClient=invoiceRepository
				.findInvoicesByClientAndYear(CLIENT_FIXTURE_1, YEAR_FIXTURE);
		assertThat(invoicesOfYearAndClient).containsExactly(
					new Invoice(CLIENT_FIXTURE_1, DATE_OF_THE_YEAR_FIXTURE, 10),
					new Invoice(CLIENT_FIXTURE_1, DATE_OF_THE_YEAR_FIXTURE, 20));
	}
	
	@Test
	public void testGetInvoicesOfClientAndYearWhenThereAreOnlyInvoicesOfTheClientInDatabase() {
		addTestInvoiceToDatabase(CLIENT_FIXTURE_1.getId(), DATE_OF_THE_YEAR_FIXTURE, 10);
		addTestInvoiceToDatabase(CLIENT_FIXTURE_1.getId(), DATE_OF_THE_PREVIOUS_YEAR_FIXTURE, 20);
		List<Invoice> invoicesOfYearAndClient=invoiceRepository
				.findInvoicesByClientAndYear(CLIENT_FIXTURE_1, YEAR_FIXTURE);
		assertThat(invoicesOfYearAndClient).containsOnly(
				new Invoice(CLIENT_FIXTURE_1, DATE_OF_THE_YEAR_FIXTURE, 10));
	}
	
	@Test
	public void testGetInvoicesOfClientAndYearWhenThereAreInvoicesOfTheDifferentClientsInDatabase() {
		addTestInvoiceToDatabase(CLIENT_FIXTURE_1.getId(), DATE_OF_THE_YEAR_FIXTURE, 10);
		addTestInvoiceToDatabase(CLIENT_FIXTURE_1.getId(), DATE_OF_THE_PREVIOUS_YEAR_FIXTURE, 20);
		addTestInvoiceToDatabase(CLIENT_FIXTURE_2.getId(), DATE_OF_THE_YEAR_FIXTURE, 30);
		List<Invoice> invoicesOfYearAndClient=invoiceRepository
				.findInvoicesByClientAndYear(CLIENT_FIXTURE_1, YEAR_FIXTURE);
		assertThat(invoicesOfYearAndClient).containsOnly(
				new Invoice(CLIENT_FIXTURE_1, DATE_OF_THE_YEAR_FIXTURE, 10));
	}
	
	@Test
	public void testDeleteAllInvoicesOfAClient() {
		addTestInvoiceToDatabase(CLIENT_FIXTURE_1.getId(), DATE_OF_THE_YEAR_FIXTURE, 10);
		addTestInvoiceToDatabase(CLIENT_FIXTURE_1.getId(), DATE_OF_THE_PREVIOUS_YEAR_FIXTURE, 20);
		addTestInvoiceToDatabase(CLIENT_FIXTURE_2.getId(), DATE_OF_THE_YEAR_FIXTURE, 30);
		invoiceRepository.deleteAllInvoicesByClient(CLIENT_FIXTURE_1.getId());
		List<Invoice> invoicesRemainingInDatabase=readAllInvoicesFromDatabase();
		assertThat(invoicesRemainingInDatabase).containsOnly(
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
