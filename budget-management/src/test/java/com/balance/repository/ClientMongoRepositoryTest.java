package com.balance.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
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
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class ClientMongoRepositoryTest {
	private static final String BUDGET_DB_NAME = "budget";
	private static final String CLIENT_COLLECTION_NAME = "client";
	
	@SuppressWarnings("rawtypes")
	@ClassRule
	public static final GenericContainer mongo = new GenericContainer("mongo:4.2.3") .withExposedPorts(27017) .withCommand("--replSet rs0");
	
	private MongoClient client;
	private MongoCollection<Document> clientCollection;

	private ClientMongoRepository clientRepository; 
	
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
		clientRepository = new ClientMongoRepository(client, client.startSession(), 
				BUDGET_DB_NAME, CLIENT_COLLECTION_NAME);
		MongoDatabase database = client.getDatabase(BUDGET_DB_NAME);
		database.drop();
		clientCollection = database.getCollection(CLIENT_COLLECTION_NAME);
	}
	
	@After
	public void tearDown() { 
		client.close();
	}
	
	@Test
	public void testGetClientCollection() {
		assertThat(clientRepository.getClientCollection().getNamespace())
			.isEqualTo(clientCollection.getNamespace());
	}
	
	@Test
	public void testFindAllClientsWhenDatabaseIsEmpty() {
		assertThat(clientRepository.findAll()).isEmpty();
	}
	
	@Test
	public void testFindAllWhenDatabaseIsNotEmpty() {
		String idClientTest1=addTestClientToDatabase("test identifier 1"); 
		String idClientTest2=addTestClientToDatabase("test identifier 2"); 
		assertThat(clientRepository.findAll())
				.containsExactly(new Client(idClientTest1, "test identifier 1"),
				new Client(idClientTest2, "test identifier 2"));
	}
	
	@Test
	public void testFindByIdNotFound() {
		assertThat(clientRepository.findById(""+new ObjectId())).isNull();
	}
	
	@Test
	public void testFindByIdFound() {
		String idClientTest1=addTestClientToDatabase("test identifier 1"); 
		addTestClientToDatabase("test identifier 2");  
		assertThat(clientRepository.findById(idClientTest1)).isEqualTo(new Client(idClientTest1, "test identifier 1"));
	}
	
	@Test
	public void testSave() {
		Client client = new Client("Client to add");
		clientRepository.save(client);
		List<Client> clientsInDatabase=readAllClientsFromDatabase();
		assertThat(clientsInDatabase).containsExactly(new Client(clientsInDatabase.get(0).getId(),"Client to add"));
	}
	
	@Test
	public void testDelete() {
		String idClientTest1=addTestClientToDatabase("test identifier 1"); 
		clientRepository.delete(idClientTest1); 
		assertThat(readAllClientsFromDatabase()).isEmpty();
	}
	
	private List<Client> readAllClientsFromDatabase() {
		return StreamSupport.stream(clientCollection.find().spliterator(), false).
				map(d -> new Client(""+d.get("_id"), ""+d.get("identifier"))).
				collect(Collectors.toList());		
	}

	
	private String addTestClientToDatabase(String identifier) {
		Document clientToAdd=new Document().append("identifier", identifier);
		clientCollection.insertOne(clientToAdd);
		return clientToAdd.get( "_id" ).toString();
	}
	
}
