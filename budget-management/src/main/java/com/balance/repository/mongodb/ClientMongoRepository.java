package com.balance.repository.mongodb;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.balance.model.Client;
import com.balance.repository.ClientRepository;
import com.mongodb.MongoClient;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

public class ClientMongoRepository implements ClientRepository{
	private MongoCollection<Document> clientCollection;
	private ClientSession clientSession;
	private static final String FIELD_PK="_id";
	private static final String FIELD_IDENTIFIER="identifier";

	public ClientMongoRepository(MongoClient client, ClientSession clientSession, String balanceDbName, 
			String clientCollectionName) {
		MongoDatabase database=client.getDatabase(balanceDbName);
		if(!database.listCollectionNames().into(new ArrayList<String>()).contains(clientCollectionName))
			database.createCollection(clientCollectionName);
		clientCollection = database.getCollection(clientCollectionName);
		this.clientSession=clientSession;
	}
	
	public MongoCollection<Document> getClientCollection() {
		return clientCollection;
	}
	
	private Client fromDocumentToClient(Document d) { 
		return new Client(d.get(FIELD_PK).toString(), 
				d.getString(FIELD_IDENTIFIER));
	}

	@Override
	public List<Client> findAll() {
		return StreamSupport.
				stream(clientCollection.find(clientSession).spliterator(), false) 
				.map(d -> new Client(d.get(FIELD_PK).toString(), 
						d.getString(FIELD_IDENTIFIER)))
				.collect(Collectors.toList());
	}

	@Override
	public Client findById(String id) {
		Document d = clientCollection.find(clientSession,Filters.eq(FIELD_PK, new ObjectId(id))).first();
		if (d != null) {
			return fromDocumentToClient(d); 
		}
		return null;
	}

	@Override
	public Client save(Client newClient) {
	    Document clientToAdd=new Document().append(FIELD_IDENTIFIER, newClient.getIdentifier());
		clientCollection.insertOne(clientSession,clientToAdd);
		newClient.setId(clientToAdd.get( FIELD_PK ).toString());
		return newClient;
	}

	@Override
	public Client delete(String id) {
		Client clientToRemove=findById(id);
		clientCollection.deleteOne(clientSession, Filters.eq(FIELD_PK, new ObjectId(id)));
		return clientToRemove;
	}
	
	
}
