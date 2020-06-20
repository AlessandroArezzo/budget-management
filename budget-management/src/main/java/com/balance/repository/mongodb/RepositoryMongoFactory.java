package com.balance.repository.mongodb;

import com.balance.repository.Repository;
import com.balance.repository.RepositoryFactory;
import com.balance.repository.TypeRepository;
import com.mongodb.MongoClient;
import com.mongodb.client.ClientSession;


public class RepositoryMongoFactory implements RepositoryFactory {
	
	 private MongoClient client;
	 private ClientSession clientSession;
	 String databaseName;
	 String collectionClientsName;
	 String collectionInvoicesName;
	
	public RepositoryMongoFactory(MongoClient client, ClientSession clientSession, String databaseName, 
			String collectionClientsName, String collectionInvoicesName) {
		this.client=client;
		this.clientSession=clientSession;
		this.databaseName=databaseName;
		this.collectionClientsName=collectionClientsName;
		this.collectionInvoicesName=collectionInvoicesName;
	}
	
	@Override
	public Repository<?> createRepository(TypeRepository type) {
		switch(type) {
			case CLIENT:
				return createClientRepository();

			case INVOICE:
				return createInvoiceRepository();
				
			default:
				return null;
		}
	}
	
	private ClientMongoRepository createClientRepository() {
		return new ClientMongoRepository(client,
				clientSession,databaseName,collectionClientsName);
	}


	private  InvoiceMongoRepository createInvoiceRepository() {
		return new InvoiceMongoRepository(client,
			clientSession,databaseName,collectionInvoicesName, createClientRepository());
}

}
