package com.balance.repository;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.balance.model.Invoice;
import com.mongodb.DBRef;
import com.mongodb.MongoClient;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

public class InvoiceMongoRepository implements InvoiceRepository{

	private MongoCollection<Document> invoiceCollection;
	private ClientMongoRepository clientRepository;
	private ClientSession clientSession;
	
	public InvoiceMongoRepository(MongoClient client,ClientSession clientSession, String balanceDbName, String invoiceCollectionName, ClientMongoRepository clientRepository) {
		invoiceCollection = client.getDatabase(balanceDbName).getCollection(invoiceCollectionName);
		this.clientRepository=clientRepository;
		this.clientSession=clientSession;
	}
	
	private Invoice fromDocumentToInvoice(Document d) { 
		return new Invoice((""+d.get("_id")),
				clientRepository.findById(((DBRef) d.get("client")).getId().toString()),
				 (Date) d.get("date"),
				 Double.parseDouble(""+d.get("revenue")));
	}
	
	@Override
	public List<Invoice> findAll() {
		return StreamSupport.
				stream(invoiceCollection.find(clientSession).spliterator(), false) 
				.map(d -> fromDocumentToInvoice(d))
				.collect(Collectors.toList());
	}

	@Override
	public Invoice findById(String id) {
		Document d = invoiceCollection.find(clientSession,Filters.eq("_id", new ObjectId(id))).first(); 
		if (d != null)
			return fromDocumentToInvoice(d); 
		return null;
	}

	@Override
	public void save(Invoice newInvoice) {
		invoiceCollection.insertOne(clientSession,new Document().append("date", newInvoice.getDate())
				.append("revenue", newInvoice.getRevenue())
				.append("client",
						new DBRef(clientRepository.getClientCollection().getNamespace().getCollectionName(),
						new ObjectId(newInvoice.getClient().getId()))));
	}

	@Override
	public void delete(String id) {
		invoiceCollection.deleteOne(Filters.eq("_id", new ObjectId(id)));
	}

}
