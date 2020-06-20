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
import com.mongodb.DBRef;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class InvoiceMongoRepositoryTest {
	private static final String BUDGET_DB_NAME = "budget";
	private static final String CLIENT_COLLECTION_NAME = "client";
	private static final String INVOICE_COLLECTION_NAME = "invoice";

	private static final Client CLIENT_FIXTURE_1=
			new Client(new ObjectId().toString(), "test identifier 1"); 
	private static final Client CLIENT_FIXTURE_2=
			new Client(new ObjectId().toString(), "test identifier 2");
	
	private static final Date DATE_FIXTURE_1=new Date();
	private static final Date DATE_FIXTURE_2=new Date();
	
	
	@SuppressWarnings("rawtypes")
	@ClassRule
	public static final GenericContainer mongo = new GenericContainer("mongo:4.2.3") .withExposedPorts(27017) .withCommand("--replSet rs0");
	
	private MongoClient client;
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
		client = new MongoClient(new ServerAddress(mongo.getContainerIpAddress(), mongo.getMappedPort(27017)));	
		invoiceRepository = new InvoiceMongoRepository(client, client.startSession(),
				BUDGET_DB_NAME, INVOICE_COLLECTION_NAME, clientRepository);
		MockitoAnnotations.initMocks(this);
		MongoDatabase database = client.getDatabase(BUDGET_DB_NAME);
		database.drop();
		invoiceCollection = database.getCollection(INVOICE_COLLECTION_NAME);
	}
	
	@After
	public void tearDown() { 
		client.close();
	}
	
	@Test
	public void testFindAllInvoicesWhenDatabaseIsEmpty() {
		assertThat(invoiceRepository.findAll()).isEmpty();
	}
	
	@Test
	public void testFindAllWhenDatabaseIsNotEmpty() {
		String idInvoiceTest1=addTestInvoiceToDatabase(CLIENT_FIXTURE_1.getId(), DATE_FIXTURE_1 , 10);
		String idInvoiceTest2=addTestInvoiceToDatabase(CLIENT_FIXTURE_2.getId(), DATE_FIXTURE_2, 20);
		when(clientRepository.findById(CLIENT_FIXTURE_1.getId())).thenReturn(CLIENT_FIXTURE_1);
		when(clientRepository.findById(CLIENT_FIXTURE_2.getId())).thenReturn(CLIENT_FIXTURE_2);
		assertThat(invoiceRepository.findAll())
			.containsExactly(new Invoice(idInvoiceTest1,CLIENT_FIXTURE_1, DATE_FIXTURE_1, 10 ),
			new Invoice(idInvoiceTest2,CLIENT_FIXTURE_2, DATE_FIXTURE_2, 20));
	}
	
	@Test
	public void testFindByIdNotFound() {
		assertThat(invoiceRepository.findById(""+new ObjectId())).isNull();
	}
	
	@Test
	public void testFindByIdFound() {
		String idInvoiceTest1=addTestInvoiceToDatabase(CLIENT_FIXTURE_1.getId(), DATE_FIXTURE_1 , 10);
		addTestInvoiceToDatabase(CLIENT_FIXTURE_2.getId(), DATE_FIXTURE_2, 20);
		when(clientRepository.findById(CLIENT_FIXTURE_1.getId())).thenReturn(CLIENT_FIXTURE_1);
		assertThat(invoiceRepository.findById(idInvoiceTest1)).isEqualTo(
				new Invoice(idInvoiceTest1,CLIENT_FIXTURE_1, DATE_FIXTURE_1, 10 ));
	}
	
	@Test
	public void testSave() {
		Invoice invoice = new Invoice(CLIENT_FIXTURE_1, DATE_FIXTURE_1, 10 );
		when(clientRepository.findById(CLIENT_FIXTURE_1.getId())).thenReturn(CLIENT_FIXTURE_1);
		when(clientRepository.getClientCollection())
			.thenReturn(client.getDatabase(BUDGET_DB_NAME).getCollection(CLIENT_COLLECTION_NAME));
		invoiceRepository.save(invoice);
		List<Invoice> invoicesInDatabase=readAllInvoicesFromDatabase();
		assertThat(invoicesInDatabase).containsExactly(new Invoice(
				invoicesInDatabase.get(0).getId(),CLIENT_FIXTURE_1, DATE_FIXTURE_1, 10 ));
	}
	
	@Test
	public void testDelete() {
		String idInvoiceTest1=addTestInvoiceToDatabase(CLIENT_FIXTURE_1.getId(), DATE_FIXTURE_1 , 10);
		invoiceRepository.delete(idInvoiceTest1); 
		assertThat(readAllInvoicesFromDatabase()).isEmpty();
	}
	
	private List<Invoice> readAllInvoicesFromDatabase() {
		
		return StreamSupport.stream(invoiceCollection.find().spliterator(), false).
				map(d -> new Invoice((""+d.get("_id")),
						clientRepository.findById(((DBRef) d.get("client")).getId().toString()),
						 (Date) d.get("date"),
						 Double.parseDouble(""+d.get("revenue")))).
				collect(Collectors.toList());		
	}
	
	private String addTestInvoiceToDatabase(String clientId, Date data, double revenue) {
		Document invoiceToAdd=new Document().append("client",
				 new DBRef(CLIENT_COLLECTION_NAME,
					 		new ObjectId(clientId)))
			 .append("date", data)
			 .append("revenue", revenue);
		invoiceCollection.insertOne(invoiceToAdd);
		return invoiceToAdd.get( "_id" ).toString();
	}
	
}
