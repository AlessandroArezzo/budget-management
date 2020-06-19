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
	private static final String FIELD_PK="_id";
	private static final String FIELD_DATE="date";
	private static final String FIELD_REVENUE="revenue";
	private static final String FIELD_CLIENT="client";

	
	public InvoiceMongoRepository(MongoClient client,ClientSession clientSession, String balanceDbName, 
			String invoiceCollectionName, ClientMongoRepository clientRepository) {
		invoiceCollection = client.getDatabase(balanceDbName).getCollection(invoiceCollectionName);
		this.clientRepository=clientRepository;
		this.clientSession=clientSession;
	}
	
	private Invoice fromDocumentToInvoice(Document d) { 
		return new Invoice((""+d.get(FIELD_PK)),
				clientRepository.findById(((DBRef) d.get(FIELD_CLIENT)).getId().toString()),
				 (Date) d.get(FIELD_DATE),
				 Double.parseDouble(""+d.get(FIELD_REVENUE)));
	}
	
	@Override
	public List<Invoice> findAll() {
		return StreamSupport.
				stream(invoiceCollection.find(clientSession).spliterator(), false) 
				.map(d -> new Invoice((""+d.get(FIELD_PK)),
						clientRepository.findById(((DBRef) d.get(FIELD_CLIENT)).getId().toString()),
						 (Date) d.get(FIELD_DATE),
						 Double.parseDouble(""+d.get(FIELD_REVENUE))))
				.collect(Collectors.toList());
	}

	@Override
	public Invoice findById(String id) {
		Document d = invoiceCollection.find(clientSession,Filters.eq(FIELD_PK, new ObjectId(id))).first(); 
		if (d != null)
			return fromDocumentToInvoice(d); 
		return null;
	}

	@Override
	public void save(Invoice newInvoice) {
		invoiceCollection.insertOne(clientSession,new Document().append(FIELD_DATE, newInvoice.getDate())
				.append(FIELD_REVENUE, newInvoice.getRevenue())
				.append(FIELD_CLIENT,
						new DBRef(clientRepository.getClientCollection().getNamespace().getCollectionName(),
						new ObjectId(newInvoice.getClient().getId()))));
	}

	@Override
	public void delete(String id) {
		invoiceCollection.deleteOne(Filters.eq(FIELD_PK, new ObjectId(id)));
	}

}
