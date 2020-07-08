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
	public static final GenericContainer mongo = new GenericContainer("mongo:4.2.3")
														.withExposedPorts(27017) 
														.withCommand("--replSet rs0");
	private MongoClient mongoClient;
	private MongoCollection<Document> clientCollection;
	private ClientMongoRepository clientRepository; 
	
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
		mongoClient = new MongoClient(new ServerAddress(
				mongo.getContainerIpAddress(), mongo.getMappedPort(27017)));
		MongoDatabase database = mongoClient.getDatabase(BUDGET_DB_NAME);
		database.drop();
		mongoClient.getDatabase(BUDGET_DB_NAME).createCollection(CLIENT_COLLECTION_NAME);
		clientRepository = new ClientMongoRepository(mongoClient, mongoClient.startSession(), 
				BUDGET_DB_NAME, CLIENT_COLLECTION_NAME);
		clientCollection = database.getCollection(CLIENT_COLLECTION_NAME);
	}
	
	@After
	public void tearDown() { 
		mongoClient.close();
	}
	
	@Test
	public void testCreateClientCollectionInConstructorWhenCollectionNotExistingInDatabase() {
		String clientCollectionNotExisting="client_collection_not_existing_in_db";
		clientRepository = new ClientMongoRepository(mongoClient, mongoClient.startSession(), 
				BUDGET_DB_NAME, clientCollectionNotExisting);
		assertThat(mongoClient.getDatabase(BUDGET_DB_NAME).listCollectionNames())
			.contains(clientCollectionNotExisting);
	}
	
	@Test
	public void testGetClientCollection() {
		assertThat(clientRepository.getClientCollection().getNamespace())
			.isEqualTo(clientCollection.getNamespace());
	}
	
	@Test
	public void testFindAllClientsWhenDatabaseIsEmpty() {
		List<Client> clientsInDatabase=clientRepository.findAll();
		assertThat(clientsInDatabase).isEmpty();
	}
	
	@Test
	public void testFindAllWhenDatabaseIsNotEmpty() {
		addTestClientToDatabase("test identifier 1"); 
		addTestClientToDatabase("test identifier 2");
		List<Client> clientsInDatabase=clientRepository.findAll();
		assertThat(clientsInDatabase).containsExactly(
					new Client("test identifier 1"),
				    new Client("test identifier 2"));
	}
	
	@Test
	public void testFindByIdNotFound() {
		Client clientFound=clientRepository.findById(""+new ObjectId());
		assertThat(clientFound).isNull();
	}
	
	@Test
	public void testFindByIdFound() {
		String idClientToFind=addTestClientToDatabase("test identifier 1"); 
		addTestClientToDatabase("test identifier 2");
		Client clientFound=clientRepository.findById(idClientToFind);
		assertThat(clientFound).isEqualTo(new Client("test identifier 1"));
	}
	
	@Test
	public void testSave() {
		Client clientToAdd = new Client("Client to add");
		Client clientSaved=clientRepository.save(clientToAdd);
		assertThat(clientSaved).isEqualTo(
				new Client("Client to add"));
		assertThat(clientSaved.getId()).isNotNull();
		List<Client> clientsInDatabase=readAllClientsFromDatabase();
		assertThat(clientsInDatabase).containsExactly(new Client("Client to add"));
	}
	
	@Test
	public void testDeleteWhenClientExistingInDatabase() {
		String idClientToRemove=addTestClientToDatabase("Client to remove"); 
		Client clientRemoved=clientRepository.delete(idClientToRemove); 
		assertThat(clientRemoved).isEqualTo(new Client("Client to remove"));
		List<Client> clientsInDatabase=readAllClientsFromDatabase();
		assertThat(clientsInDatabase).isEmpty();
	}
	
	@Test
	public void testDeleteWhenClientNotExistingInDatabase() {
		Client clientRemoved=clientRepository.delete(new ObjectId().toString());
		assertThat(clientRemoved).isNull();
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
