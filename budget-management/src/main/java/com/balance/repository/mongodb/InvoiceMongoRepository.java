package com.balance.repository.mongodb;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.balance.model.Client;
import com.balance.model.Invoice;
import com.balance.repository.InvoiceRepository;
import com.mongodb.DBRef;
import com.mongodb.MongoClient;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;

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
		return new Invoice((d.get(FIELD_PK).toString()),
				 clientRepository.findById(((DBRef) d.get(FIELD_CLIENT)).getId().toString()),
				 d.getDate(FIELD_DATE),
				 d.getDouble(FIELD_REVENUE));
	}
	
	@Override
	public List<Invoice> findAll() {
		return StreamSupport.
				stream(invoiceCollection.find(clientSession).spliterator(), false) 
				.map(d -> new Invoice(d.get(FIELD_PK).toString(),
						clientRepository.findById(((DBRef) d.get(FIELD_CLIENT)).getId().toString()),
						d.getDate(FIELD_DATE),
						d.getDouble(FIELD_REVENUE)))
				.collect(Collectors.toList());
	}

	@Override
	public Invoice findById(String id) {
		Document d = invoiceCollection.find(clientSession,Filters.eq(FIELD_PK, new ObjectId(id))).first(); 
		if (d != null) {
			return fromDocumentToInvoice(d); 
		}
		return null;
	}

	@Override
	public Invoice save(Invoice newInvoice) {
		Document invoiceToAdd=new Document().append(FIELD_CLIENT,
				 new DBRef(clientRepository.getClientCollection().getNamespace().getCollectionName(),
					 new ObjectId(newInvoice.getClient().getId())))
			 .append(FIELD_DATE, newInvoice.getDate())
			 .append(FIELD_REVENUE, newInvoice.getRevenue());
		invoiceCollection.insertOne(clientSession, invoiceToAdd);
		newInvoice.setId(invoiceToAdd.get( FIELD_PK ).toString());
		return newInvoice;
		
	}

	@Override
	public Invoice delete(String id) {
		Invoice invoiceToRemove=findById(id);
		invoiceCollection.deleteOne(clientSession, Filters.eq(FIELD_PK, new ObjectId(id)));
		return invoiceToRemove;
	}
	
	@Override
	public List<Invoice> findInvoicesByYear(int year) {
		return StreamSupport.
					stream(invoiceCollection.find(clientSession,
							Filters.and(Filters.gte(FIELD_DATE, getFirstDayOfYear(year)),
									Filters.lte(FIELD_DATE, getLastDayOfYear(year)))
							).spliterator(), false) 
					.map(d -> new Invoice(d.get(FIELD_PK).toString(),
							clientRepository.findById(((DBRef) d.get(FIELD_CLIENT)).getId().toString()),
							d.getDate(FIELD_DATE),
							d.getDouble(FIELD_REVENUE)))
					.collect(Collectors.toList());
	}
	
	private Date getFirstDayOfYear(int year) {
		LocalDate localDate = LocalDate.of( year, 1, 1);
		return Date.from(localDate.atStartOfDay(ZoneId.of("Z")).toInstant());
	}
	
	private Date getLastDayOfYear(int year) {
		LocalDate localDate = LocalDate.of( year, 12, 31);
		return Date.from(localDate.atStartOfDay(ZoneId.of("Z")).toInstant());
	}

	@Override
	public List<Integer> getYearsOfInvoicesInDatabase() {
		return new ArrayList<> (
				StreamSupport.
					stream(invoiceCollection.aggregate(clientSession,
							  Arrays.asList(
							          Aggregates.project(
							        		  Projections.fields(
							        		  Projections.computed("year", 
						        				  	Document.parse("{ $year: '$date' }"))
				        				  )))
								  ).spliterator(), false)
					.map(d -> d.getInteger("year"))
					.collect(Collectors.toSet())
			);
	}

	@Override
	public List<Invoice> findInvoicesByClientAndYear(Client client, int year) {
		return StreamSupport.
				stream(invoiceCollection.find(clientSession,
						Filters.and(
								Filters.gte(FIELD_DATE, getFirstDayOfYear(year)),
								Filters.lte(FIELD_DATE, getLastDayOfYear(year)),
								Filters.eq(FIELD_CLIENT+".$id", new ObjectId(client.getId())))
						).spliterator(), false) 
				.map(d -> new Invoice(d.get(FIELD_PK).toString(),
						clientRepository.findById(((DBRef) d.get(FIELD_CLIENT)).getId().toString()),
						d.getDate(FIELD_DATE),
						d.getDouble(FIELD_REVENUE)))
				.collect(Collectors.toList());
	}

	@Override
	public void deleteAllInvoicesByClient(String clientId) {
		invoiceCollection.deleteMany(clientSession, Filters.eq(FIELD_CLIENT+".$id", 
				new ObjectId(clientId)));
	}
	
	
}
