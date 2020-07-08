package com.balance.repository.mongodb;

import com.balance.repository.ClientRepository;
import com.balance.repository.InvoiceRepository;
import com.balance.repository.RepositoryFactory;
import com.mongodb.MongoClient;
import com.mongodb.client.ClientSession;


public class RepositoryMongoFactory implements RepositoryFactory {
	
	 private MongoClient client;
	 private ClientSession clientSession;
	 private String databaseName;
	 private String collectionClientsName;
	 private String collectionInvoicesName;
	
	public RepositoryMongoFactory(MongoClient client, ClientSession clientSession, String databaseName, 
			String collectionClientsName, String collectionInvoicesName) {
		this.client=client;
		this.clientSession=clientSession;
		this.databaseName=databaseName;
		this.collectionClientsName=collectionClientsName;
		this.collectionInvoicesName=collectionInvoicesName;
	}

	@Override
	public ClientRepository createClientRepository() {
		return new ClientMongoRepository(client,
				clientSession,databaseName,collectionClientsName);
	}

	@Override
	public InvoiceRepository createInvoiceRepository() {
		return new InvoiceMongoRepository(client,
				clientSession,databaseName,collectionInvoicesName, 
					(ClientMongoRepository) createClientRepository());
	}

}
