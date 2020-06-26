package com.balance.repository.mongodb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;

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

	@Override
	public double getTotalRevenueOfAnYear(int year) {
		String totalRevenueField="totalRevenue";
		Document sumDocument=invoiceCollection.aggregate(clientSession,
				  Arrays.asList(
				          Aggregates.match(Filters.and(Filters.gte(FIELD_DATE, getFirstDayOfYear(year)),
									Filters.lte(FIELD_DATE, getLastDayOfYear(year)))),
				          Aggregates.group("",Accumulators.sum(totalRevenueField, "$"+FIELD_REVENUE))
				  )).first();
		if (sumDocument==null) 
			return 0;
		return sumDocument.getDouble(totalRevenueField);
	}
	
	private Date getFirstDayOfYear(int year) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.DAY_OF_YEAR, 1); 
		cal.set(Calendar.HOUR_OF_DAY, 0); 
		cal.set(Calendar.MINUTE, 0); 
		cal.set(Calendar.SECOND, 0); 
		cal.set(Calendar.MILLISECOND, 0); 
		return cal.getTime();
	}
	
	private Date getLastDayOfYear(int year) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, 11); 
		cal.set(Calendar.DAY_OF_MONTH, 31);
		return cal.getTime();
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
				        				  )),
							  			Aggregates.sort(Sorts.ascending("year")))
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
	public double getClientRevenueOfAnYear(Client client, int year) {
		String totalRevenueField="totalClientRevenue";
		Document sumClientDocument= invoiceCollection.aggregate(clientSession,
				  Arrays.asList(
				          Aggregates.match(
				        		  Filters.and(
				        		    Filters.gte(FIELD_DATE, getFirstDayOfYear(year)),
									Filters.lte(FIELD_DATE, getLastDayOfYear(year)),
									Filters.eq(FIELD_CLIENT+".$id", 
											new ObjectId(client.getId()))
									)),
				          Aggregates.group("",
				        		  Accumulators.sum(totalRevenueField, "$"+FIELD_REVENUE))
						  )).first();
		if(sumClientDocument==null) {
			return 0;
		}
		return sumClientDocument.getDouble(totalRevenueField);
	}

	@Override
	public void deleteAllInvoicesByClient(String clientId) {
		invoiceCollection.deleteMany(clientSession, Filters.eq(FIELD_CLIENT+".$id", 
				new ObjectId(clientId)));
	}
	
	
}
