package com.balance.transaction.mongodb;

import com.balance.repository.mongodb.RepositoryMongoFactory;
import com.balance.transaction.TransactionCode;
import com.balance.transaction.TransactionManager;
import com.mongodb.MongoClient;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.TransactionOptions;
import com.mongodb.WriteConcern;
import com.mongodb.client.ClientSession;
import com.mongodb.client.TransactionBody;

public class TransactionMongoManager implements TransactionManager {

	private String databaseName;
	private String clientsCollectionName;
	private String invoicesCollectionName;

	private MongoClient mongoClient;
	
	public TransactionMongoManager(MongoClient mongoClient, String databaseName, String clientsCollectionName, String invoicesCollectionName) {
		this.mongoClient=mongoClient;
		this.databaseName=databaseName;
		this.clientsCollectionName=clientsCollectionName;
		this.invoicesCollectionName=invoicesCollectionName;
	}
	
	@Override
	public <R> R doInTransaction(TransactionCode<R> code) {
		TransactionOptions transactionOptions = TransactionOptions.builder()
	            .readPreference(ReadPreference.primary())
	            .readConcern(ReadConcern.SNAPSHOT)
	            .writeConcern(WriteConcern.MAJORITY)
	            .build();
		ClientSession clientSession=mongoClient.startSession();
		try {
			RepositoryMongoFactory repositoryManager= new RepositoryMongoFactory(mongoClient, 
					clientSession, databaseName,clientsCollectionName, invoicesCollectionName);
			TransactionBody<R> transactionBody = new TransactionBody<R>() {
				 public R execute() {
					 return code.apply(repositoryManager);
				 }
			};
			return clientSession.withTransaction(transactionBody , transactionOptions);
		} 
		catch(Exception ex){
			return null;
		}
	}

}
