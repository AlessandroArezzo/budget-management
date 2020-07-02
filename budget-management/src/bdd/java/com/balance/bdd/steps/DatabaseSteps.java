package com.balance.bdd.steps;

import java.util.Date;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.balance.model.Client;
import com.balance.utils.DateTestsUtil;
import com.mongodb.DBRef;
import com.mongodb.MongoClient;
import com.mongodb.client.model.Filters;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;

public class DatabaseSteps {
	static final String DB_NAME = "test-balance-db";
	static final String COLLECTION_CLIENTS_NAME = "test-clients-collection";
	static final String COLLECTION_INVOICES_NAME = "test-invoices-collection";
	
	static final String CLIENT_FIXTURE_1_IDENTIFIER="first client";
	static String CLIENT_FIXTURE_1_ID;
	static final String CLIENT_FIXTURE_2_IDENTIFIER="second client";
	static String CLIENT_FIXTURE_2_ID;
	static final String CLIENT_FIXTURE_3_IDENTIFIER="third client";
	static String CLIENT_FIXTURE_3_ID;
	
	static final int CURRENT_YEAR=2020;
	static final int YEAR_FIXTURE=2019;
	
	static final double INVOICE_OF_THE_YEAR_FIXTURE_1_REVENUE=10.0;
	static final Date INVOICE_OF_THE_YEAR_FIXTURE_1_DATE=DateTestsUtil.getDate(1, 5, YEAR_FIXTURE);
	static Client INVOICE_OF_THE_YEAR_FIXTURE_1_CLIENT;
	static String INVOICE_OF_THE_YEAR_FIXTURE_1_ID;
	
	static final double INVOICE_OF_THE_YEAR_FIXTURE_2_REVENUE=20.0;
	static final Date INVOICE_OF_THE_YEAR_FIXTURE_2_DATE=DateTestsUtil.getDate(2, 5, YEAR_FIXTURE);
	static Client INVOICE_OF_THE_YEAR_FIXTURE_2_CLIENT;
	static String INVOICE_OF_THE_YEAR_FIXTURE_2_ID;
	
	static final double INVOICE_OF_THE_YEAR_FIXTURE_3_REVENUE=30.0;
	static final Date INVOICE_OF_THE_YEAR_FIXTURE_3_DATE=DateTestsUtil.getDate(3, 5, YEAR_FIXTURE);
	static Client INVOICE_OF_THE_YEAR_FIXTURE_3_CLIENT;
	static String INVOICE_OF_THE_YEAR_FIXTURE_3_ID;
	
	static final double INVOICE_OF_THE_CURRENT_YEAR_1_REVENUE=10.0;
	static final Date INVOICE_OF_THE_CURRENT_YEAR_1_DATE=DateTestsUtil.getDate(15, 10, CURRENT_YEAR);
	static Client INVOICE_OF_THE_CURRENT_YEAR_1_CLIENT;
	static String INVOICE_OF_THE_CURRENT_YEAR_1_ID;
	
	static final double INVOICE_OF_THE_CURRENT_YEAR_2_REVENUE=20.0;
	static final Date INVOICE_OF_THE_CURRENT_YEAR_2_DATE=DateTestsUtil.getDate(20, 10, CURRENT_YEAR);
	static Client INVOICE_OF_THE_CURRENT_YEAR_2_CLIENT;
	static String INVOICE_OF_THE_CURRENT_YEAR_2_ID;
	
	private MongoClient mongoClient;
	
	@Before 
	public void setUp() {
		mongoClient=new MongoClient(); 
		mongoClient.getDatabase(DB_NAME).drop();
	}
	
	@After
	public void tearDown() {
		mongoClient.close();
	}
	
	@Given("The database contains a few clients")
	public void the_database_contains_a_few_clients() {
		CLIENT_FIXTURE_1_ID=addClientToDatabase(CLIENT_FIXTURE_1_IDENTIFIER);
		CLIENT_FIXTURE_2_ID=addClientToDatabase(CLIENT_FIXTURE_2_IDENTIFIER);
		CLIENT_FIXTURE_3_ID=addClientToDatabase(CLIENT_FIXTURE_3_IDENTIFIER);
		Client client1=new Client(CLIENT_FIXTURE_1_ID,CLIENT_FIXTURE_1_IDENTIFIER);
		Client client2=new Client(CLIENT_FIXTURE_2_ID,CLIENT_FIXTURE_2_IDENTIFIER);
		INVOICE_OF_THE_YEAR_FIXTURE_1_CLIENT=client1;
		INVOICE_OF_THE_YEAR_FIXTURE_2_CLIENT=client2;
		INVOICE_OF_THE_YEAR_FIXTURE_3_CLIENT=client2;
		INVOICE_OF_THE_CURRENT_YEAR_1_CLIENT=client1;
		INVOICE_OF_THE_CURRENT_YEAR_2_CLIENT=client2;
	}
	
	@Given("The database contains a few invoices of different years")
	public void the_database_contains_a_few_invoices_of_different_years() {
		INVOICE_OF_THE_YEAR_FIXTURE_1_ID=addInvoiceToDatabase(
				INVOICE_OF_THE_YEAR_FIXTURE_1_CLIENT.getId(), INVOICE_OF_THE_YEAR_FIXTURE_1_DATE,
				INVOICE_OF_THE_YEAR_FIXTURE_1_REVENUE);
		INVOICE_OF_THE_YEAR_FIXTURE_2_ID=addInvoiceToDatabase(
				INVOICE_OF_THE_YEAR_FIXTURE_2_CLIENT.getId(),INVOICE_OF_THE_YEAR_FIXTURE_2_DATE,
				INVOICE_OF_THE_YEAR_FIXTURE_2_REVENUE);
		INVOICE_OF_THE_YEAR_FIXTURE_3_ID=addInvoiceToDatabase(
				INVOICE_OF_THE_YEAR_FIXTURE_3_CLIENT.getId(),INVOICE_OF_THE_YEAR_FIXTURE_3_DATE,
				INVOICE_OF_THE_YEAR_FIXTURE_3_REVENUE);
		INVOICE_OF_THE_CURRENT_YEAR_1_ID=addInvoiceToDatabase(
				INVOICE_OF_THE_CURRENT_YEAR_1_CLIENT.getId(),INVOICE_OF_THE_CURRENT_YEAR_1_DATE,
				INVOICE_OF_THE_CURRENT_YEAR_1_REVENUE);
		INVOICE_OF_THE_CURRENT_YEAR_2_ID=addInvoiceToDatabase(
				INVOICE_OF_THE_CURRENT_YEAR_2_CLIENT.getId(),INVOICE_OF_THE_CURRENT_YEAR_2_DATE,
				INVOICE_OF_THE_CURRENT_YEAR_2_REVENUE);
	}
	
	@Given("A client is removed from the database")
	public void a_client_is_removed_from_the_database() {
		removeClientFromDatabase(CLIENT_FIXTURE_2_ID);
	}
	
	@Given("The client is in meantime removed from the database")
	public void the_client_is_in_meantime_removed_from_the_database() {
		removeClientFromDatabase(CLIENT_FIXTURE_2_ID);
	}
	
	@Given("The invoice is in meantime removed from the database")
	public void the_invoice_is_in_meantime_removed_from_the_database() {
		removeInvoiceFromDatabase(INVOICE_OF_THE_YEAR_FIXTURE_2_ID);
	}
	
	@Given("The client of invoice selected is in meantime removed from the database")
	public void the_client_of_invoice_selected_is_in_meantime_removed_from_the_database() {
		removeClientFromDatabase(CLIENT_FIXTURE_2_ID);
	}
	
	private void removeInvoiceFromDatabase(String invoiceId) {
		mongoClient.getDatabase(DB_NAME).getCollection(COLLECTION_INVOICES_NAME)
			.deleteOne( Filters.eq("_id", new ObjectId(invoiceId)));
	}
	
	private void removeClientFromDatabase(String clientId) {
		mongoClient.getDatabase(DB_NAME).getCollection(COLLECTION_INVOICES_NAME)
			.deleteMany(Filters.eq("client"+".$id", 
					new ObjectId(clientId)));
		mongoClient.getDatabase(DB_NAME).getCollection(COLLECTION_CLIENTS_NAME)
			.deleteOne( Filters.eq("_id", new ObjectId(clientId)));
	}
	
	private String addInvoiceToDatabase(String clientId, Date data, double revenue) {
		Document invoiceToAdd=new Document().append("client",
				 new DBRef(COLLECTION_CLIENTS_NAME,
					 		new ObjectId(clientId)))
			 .append("date", data)
			 .append("revenue", revenue);
		mongoClient.getDatabase(DB_NAME).getCollection(COLLECTION_INVOICES_NAME).insertOne(invoiceToAdd);
		return invoiceToAdd.get( "_id" ).toString();
	}
	
	private String addClientToDatabase(String identifier) {
		Document clientToAdd=new Document().append("identifier", identifier);
		mongoClient.getDatabase(DB_NAME).getCollection(COLLECTION_CLIENTS_NAME).insertOne(clientToAdd);
		return clientToAdd.get( "_id" ).toString();
	}

	
}
