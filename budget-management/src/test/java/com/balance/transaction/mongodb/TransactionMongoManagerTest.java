package com.balance.transaction.mongodb;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.testcontainers.containers.GenericContainer;

import com.balance.model.Client;
import com.balance.model.Invoice;
import com.mongodb.DBRef;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;



public class TransactionMongoManagerTest {
	
	private TransactionMongoManager transactionManager;
		
	private static final String DB_NAME = "dbTest";
	private static final String COLLECTION_CLIENTS_NAME = "collectionClientsTest";
	private static final String COLLECTION_INVOICES_NAME = "collectionInvoicesTest";

	@SuppressWarnings("rawtypes")
	@ClassRule
	public static final GenericContainer mongo = new GenericContainer("mongo:4.2.3")
														.withExposedPorts(27017) 
														.withCommand("--replSet rs0");
	
	private MongoClient mongoClient;
	
	private MongoCollection<Document> clientsCollection;
	
	private MongoCollection<Document> invoicesCollection;
	
	private ClientSession clientSession;
	
	private final static String CLIENT_IDENTIFIER_FIXTURE="test identifier 1";
	private final static Date INVOICE_DATE_FIXTURE=new Date();
	private final static double INVOICE_REVENUE_FIXTURE=10.5;
	
	private static final String FIELD_PK_COLLECTIONS="_id";
	private static final String FIELD_CLIENT_IDENTIFIER="identifier";
	private static final String FIELD_INVOICE_CLIENT="client";
	private static final String FIELD_INVOICE_DATE="date";
	private static final String FIELD_INVOICE_REVENUE="revenue";
	
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
		mongoClient = spy(new MongoClient(new ServerAddress(
											  mongo.getContainerIpAddress(),
											  mongo.getMappedPort(27017))));		
		MongoDatabase database = mongoClient.getDatabase(DB_NAME);
		database.drop();
		database.createCollection(COLLECTION_CLIENTS_NAME);
		database.createCollection(COLLECTION_INVOICES_NAME);
		clientsCollection = database.getCollection(COLLECTION_CLIENTS_NAME);
		invoicesCollection=database.getCollection(COLLECTION_INVOICES_NAME);
		transactionManager=new TransactionMongoManager(mongoClient,DB_NAME,COLLECTION_CLIENTS_NAME,
				COLLECTION_INVOICES_NAME);
		clientSession=mongoClient.startSession();
		when(mongoClient.startSession()).thenReturn(clientSession);
	}
	
	@After
	public void tearDown() { 
		mongoClient.close();
	}
	
	@Test
	public void testOnCommitDoInTransaction() {
		List<String> idElementsAdded=transactionManager.doInTransaction( 
				factory -> {
					String idClientAdded=addTestClientToDatabaseInTransaction(
							CLIENT_IDENTIFIER_FIXTURE);
					String idInvoiceAdded=addTestInvoiceToDatabaseInTransaction(idClientAdded,
							INVOICE_DATE_FIXTURE,INVOICE_REVENUE_FIXTURE);
					return new ArrayList<String>(Arrays.asList(idClientAdded,idInvoiceAdded));
				});
		List<Client> clientsInDatabase=readAllClientsFromDatabase();
		assertThat(clientsInDatabase).containsOnly(
				new Client(idElementsAdded.get(0), CLIENT_IDENTIFIER_FIXTURE));
		List<Invoice> invoicesInDatabase=readAllInvoicesFromDatabase();
		assertThat(invoicesInDatabase).containsOnly(
				new Invoice (idElementsAdded.get(1), 
							new Client(idElementsAdded.get(0), CLIENT_IDENTIFIER_FIXTURE),
							INVOICE_DATE_FIXTURE, INVOICE_REVENUE_FIXTURE));
	}
	
	@Test
	public void testOnRollBackDoInTransaction() {
		transactionManager.doInTransaction( 
				factory -> {
					String idClientAdded=addTestClientToDatabaseInTransaction(
							CLIENT_IDENTIFIER_FIXTURE);
					addTestInvoiceToDatabaseInTransaction(idClientAdded,
								INVOICE_DATE_FIXTURE,INVOICE_REVENUE_FIXTURE);
					throw new MongoException("Error: abort transaction.");
				});
		List<Client> clientsInDatabase=readAllClientsFromDatabase();
		assertThat(clientsInDatabase).isEmpty();
		List<Invoice> invoicesInDatabase=readAllInvoicesFromDatabase();
		assertThat(invoicesInDatabase).isEmpty();
	}
	

	private List<Invoice> readAllInvoicesFromDatabase() {
		return StreamSupport.stream(invoicesCollection.find().spliterator(), false).
				map(d -> new Invoice((""+d.get(FIELD_PK_COLLECTIONS)),
						findClientById(((DBRef) d.get(FIELD_INVOICE_CLIENT)).getId().toString()),
						 (Date) d.get(FIELD_INVOICE_DATE),
						 Double.parseDouble(""+d.get(FIELD_INVOICE_REVENUE)))).
				collect(Collectors.toList());		
	}
	
	private List<Client> readAllClientsFromDatabase() {
		return StreamSupport.stream(clientsCollection.find().spliterator(), false).
				map(d -> new Client(""+d.get(FIELD_PK_COLLECTIONS), ""+d.get(FIELD_CLIENT_IDENTIFIER))).
				collect(Collectors.toList());		
	}

	private Client findClientById(String id) {
		Document d = clientsCollection.find(clientSession,Filters.eq(FIELD_PK_COLLECTIONS, new ObjectId(id))).first(); 
		if (d != null)
			return new Client(""+d.get(FIELD_PK_COLLECTIONS), 
					""+d.get(FIELD_CLIENT_IDENTIFIER));
		return null;
	}
	
	private String addTestInvoiceToDatabaseInTransaction(String clientId, Date data, double revenue) {
		Document invoiceToAdd=new Document().append(FIELD_INVOICE_CLIENT,
				 new DBRef(COLLECTION_CLIENTS_NAME,
					 		new ObjectId(clientId)))
			 .append(FIELD_INVOICE_DATE, data)
			 .append(FIELD_INVOICE_REVENUE, revenue);
		invoicesCollection.insertOne(clientSession,invoiceToAdd);
		return invoiceToAdd.get( FIELD_PK_COLLECTIONS ).toString();
	}
	
	private String addTestClientToDatabaseInTransaction(String identifier) {
		Document clientToAdd=new Document().append(FIELD_CLIENT_IDENTIFIER, identifier);
		clientsCollection.insertOne(clientSession, clientToAdd);
		return clientToAdd.get( FIELD_PK_COLLECTIONS ).toString();
	}
}
