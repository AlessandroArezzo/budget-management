package com.balance.repository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.balance.model.Client;
import com.mongodb.MongoClient;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

public class ClientMongoRepository implements ClientRepository{
	private MongoCollection<Document> clientCollection;
	private ClientSession clientSession;
	
	public ClientMongoRepository(MongoClient client, ClientSession clientSession, String balanceDbName, String clientCollectionName) {
		clientCollection = client.getDatabase(balanceDbName).getCollection(clientCollectionName);
		this.clientSession=clientSession;
	}
	
	public MongoCollection<Document> getClientCollection() {
		return clientCollection;
	}
	
	private Client fromDocumentToClient(Document d) { 
		return new Client(""+d.get("_id"), 
				""+d.get("identifier"));
	}

	@Override
	public List<Client> findAll() {
		return StreamSupport.
				stream(clientCollection.find(clientSession).spliterator(), false) 
				.map(d -> fromDocumentToClient(d))
				.collect(Collectors.toList());
	}

	@Override
	public Client findById(String id) {
		Document d = clientCollection.find(clientSession,Filters.eq("_id", new ObjectId(id))).first(); 
		if (d != null)
			return fromDocumentToClient(d); 
		return null;
	}

	@Override
	public void save(Client newClient) {
	   clientCollection.insertOne(clientSession,new Document().append("identifier", newClient.getIdentifier()));
	}

	@Override
	public void delete(String id) {
		clientCollection.deleteOne(Filters.eq("_id", new ObjectId(id)));
	}
	
	
}
