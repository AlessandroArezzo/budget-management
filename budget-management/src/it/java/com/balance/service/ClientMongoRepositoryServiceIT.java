package com.balance.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.balance.model.Client;
import com.balance.repository.mongodb.ClientMongoRepository;
import com.balance.transaction.TransactionManager;
import com.balance.transaction.mongodb.TransactionMongoManager;
import com.mongodb.MongoClient;

public class ClientMongoRepositoryServiceIT {
	
	ClientService clientService;
	ClientMongoRepository clientRepository;
		
	private MongoClient mongoClient;
	
	private static final String DB_NAME="balance";
	private static final String COLLECTION_CLIENTS_NAME="client";
	private static final String COLLECTION_INVOICES_NAME="invoice";
	
	private static final Client CLIENT_FIXTURE_1=new Client("test identifier 1");
	private static final Client CLIENT_FIXTURE_2=new Client("test identifier 2");

	
	@Before
	public void setup() {
		mongoClient=new MongoClient("localhost");
		TransactionManager transactionManager=new TransactionMongoManager(mongoClient, 
				DB_NAME, COLLECTION_CLIENTS_NAME, COLLECTION_INVOICES_NAME);
		clientService= new ClientServiceTransactional(transactionManager);
		 
		clientRepository=new ClientMongoRepository(mongoClient, mongoClient.startSession(),
				DB_NAME, COLLECTION_CLIENTS_NAME);
		for (Client client : clientRepository.findAll()) {
			clientRepository.delete(client.getId()); 
		}
	}
	
	@After
	public void onTearDown() {
		mongoClient.close();
	}
	
	@Test
	public void testFindAllClients() {
		clientRepository.save(CLIENT_FIXTURE_1);
		clientRepository.save(CLIENT_FIXTURE_2);
		assertThat(clientService.findAllClients()).containsExactly(CLIENT_FIXTURE_1,CLIENT_FIXTURE_2);
	}
	
	
}
