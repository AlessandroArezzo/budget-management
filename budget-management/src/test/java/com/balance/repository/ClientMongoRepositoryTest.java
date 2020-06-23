package com.balance.repository;

import static org.assertj.core.api.Assertions.assertThat;

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
import com.balance.repository.mongodb.ClientMongoRepository;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class ClientMongoRepositoryTest {
	
	private static final String BUDGET_DB_NAME = "budget";
	private static final String CLIENT_COLLECTION_NAME = "client";
	private static final String FIELD_PK="_id";
	private static final String FIELD_IDENTIFIER="identifier";
	
	@SuppressWarnings("rawtypes")
	@ClassRule
	public static final GenericContainer mongo = new GenericContainer("mongo:4.2.3") .withExposedPorts(27017) .withCommand("--replSet rs0");
	
	private MongoClient mongoClient;
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
		mongoClient = new MongoClient(new ServerAddress(
				mongo.getContainerIpAddress(), mongo.getMappedPort(27017)));		
		clientRepository = new ClientMongoRepository(mongoClient, mongoClient.startSession(), 
				BUDGET_DB_NAME, CLIENT_COLLECTION_NAME);
		MongoDatabase database = mongoClient.getDatabase(BUDGET_DB_NAME);
		database.drop();
		clientCollection = database.getCollection(CLIENT_COLLECTION_NAME);
	}
	
	@After
	public void tearDown() { 
		mongoClient.close();
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
		addTestClientToDatabase("test identifier 1"); 
		addTestClientToDatabase("test identifier 2"); 
		assertThat(clientRepository.findAll())
				.containsExactly(new Client("test identifier 1"),
				new Client("test identifier 2"));
	}
	
	@Test
	public void testFindByIdNotFound() {
		assertThat(clientRepository.findById(""+new ObjectId())).isNull();
	}
	
	@Test
	public void testFindByIdFound() {
		String idClientTest1=addTestClientToDatabase("test identifier 1"); 
		addTestClientToDatabase("test identifier 2");  
		assertThat(clientRepository.findById(idClientTest1)).isEqualTo(new Client("test identifier 1"));
	}
	
	@Test
	public void testSave() {
		Client client = new Client("Client to add");
		clientRepository.save(client);
		assertThat(readAllClientsFromDatabase()).containsExactly(new Client("Client to add"));
	}
	
	@Test
	public void testDelete() {
		String idClientTest=addTestClientToDatabase("test identifier 1"); 
		clientRepository.delete(idClientTest); 
		assertThat(readAllClientsFromDatabase()).isEmpty();
	}
	
	private List<Client> readAllClientsFromDatabase() {
		return StreamSupport.stream(clientCollection.find().spliterator(), false).
				map(d -> new Client(d.get(FIELD_PK).toString(), d.getString(FIELD_IDENTIFIER))).
				collect(Collectors.toList());		
	}

	
	private String addTestClientToDatabase(String identifier) {
		Document clientToAdd=new Document().append(FIELD_IDENTIFIER, identifier);
		clientCollection.insertOne(clientToAdd);
		return clientToAdd.get( FIELD_PK ).toString();
	}
	
}
