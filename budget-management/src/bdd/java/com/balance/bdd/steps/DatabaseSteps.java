package com.balance.bdd.steps;

import java.util.Calendar;
import java.util.Date;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.balance.model.Client;
import com.mongodb.DBRef;
import com.mongodb.MongoClient;

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
	static final Date INVOICE_OF_THE_YEAR_FIXTURE_1_DATE=getDateFromYear(YEAR_FIXTURE);
	static Client INVOICE_OF_THE_YEAR_FIXTURE_1_CLIENT;
	
	static final double INVOICE_OF_THE_YEAR_FIXTURE_2_REVENUE=20.0;
	static final Date INVOICE_OF_THE_YEAR_FIXTURE_2_DATE=getDateFromYear(YEAR_FIXTURE);
	static Client INVOICE_OF_THE_YEAR_FIXTURE_2_CLIENT;
	
	static final double INVOICE_OF_THE_YEAR_FIXTURE_3_REVENUE=30.0;
	static final Date INVOICE_OF_THE_YEAR_FIXTURE_3_DATE=getDateFromYear(YEAR_FIXTURE);
	static Client INVOICE_OF_THE_YEAR_FIXTURE_3_CLIENT;
	
	static final double INVOICE_OF_THE_CURRENT_YEAR_1_REVENUE=10.0;
	static final Date INVOICE_OF_THE_CURRENT_YEAR_1_DATE=getDateFromYear(CURRENT_YEAR);
	static Client INVOICE_OF_THE_CURRENT_YEAR_1_CLIENT;
	
	static final double INVOICE_OF_THE_CURRENT_YEAR_2_REVENUE=20.0;
	static final Date INVOICE_OF_THE_CURRENT_YEAR_2_DATE=getDateFromYear(CURRENT_YEAR);
	static Client INVOICE_OF_THE_CURRENT_YEAR_2_CLIENT;
	
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
		INVOICE_OF_THE_YEAR_FIXTURE_1_CLIENT=new Client(CLIENT_FIXTURE_1_ID,
				CLIENT_FIXTURE_1_IDENTIFIER);
		INVOICE_OF_THE_YEAR_FIXTURE_2_CLIENT=new Client(CLIENT_FIXTURE_2_ID,
				CLIENT_FIXTURE_2_IDENTIFIER);
		INVOICE_OF_THE_YEAR_FIXTURE_3_CLIENT=INVOICE_OF_THE_YEAR_FIXTURE_2_CLIENT;
		INVOICE_OF_THE_CURRENT_YEAR_1_CLIENT=INVOICE_OF_THE_YEAR_FIXTURE_1_CLIENT;
		INVOICE_OF_THE_CURRENT_YEAR_2_CLIENT=INVOICE_OF_THE_YEAR_FIXTURE_3_CLIENT;
	}
	
	@Given("The database contains a few invoices of different years")
	public void the_database_contains_a_few_invoices_of_different_years() {
		
		addInvoiceToDatabase(INVOICE_OF_THE_YEAR_FIXTURE_1_CLIENT.getId(),INVOICE_OF_THE_YEAR_FIXTURE_1_DATE,
				INVOICE_OF_THE_YEAR_FIXTURE_1_REVENUE);
		addInvoiceToDatabase(INVOICE_OF_THE_YEAR_FIXTURE_2_CLIENT.getId(),INVOICE_OF_THE_YEAR_FIXTURE_2_DATE,
				INVOICE_OF_THE_YEAR_FIXTURE_2_REVENUE);
		addInvoiceToDatabase(INVOICE_OF_THE_YEAR_FIXTURE_3_CLIENT.getId(),INVOICE_OF_THE_YEAR_FIXTURE_3_DATE,
				INVOICE_OF_THE_YEAR_FIXTURE_3_REVENUE);
		addInvoiceToDatabase(INVOICE_OF_THE_CURRENT_YEAR_1_CLIENT.getId(),INVOICE_OF_THE_CURRENT_YEAR_1_DATE,
				INVOICE_OF_THE_CURRENT_YEAR_1_REVENUE);
		addInvoiceToDatabase(INVOICE_OF_THE_CURRENT_YEAR_2_CLIENT.getId(),INVOICE_OF_THE_CURRENT_YEAR_2_DATE,
				INVOICE_OF_THE_CURRENT_YEAR_2_REVENUE);
	}
	
	@Given("A client is removed from the database")
	public void a_client_is_removed_from_the_database() {
	    // Write code here that turns the phrase above into concrete actions
	    throw new cucumber.api.PendingException();
	}
	
	@Given("The client is in meantime removed from the database")
	public void the_client_is_in_meantime_removed_from_the_database() {
	    // Write code here that turns the phrase above into concrete actions
	    throw new cucumber.api.PendingException();
	}
	
	@Given("The invoice is in meantime removed from the database")
	public void the_invoice_is_in_meantime_removed_from_the_database() {
	    // Write code here that turns the phrase above into concrete actions
	    throw new cucumber.api.PendingException();
	}
	
	@Given("The client of invoice selected is in meantime removed from the database")
	public void the_client_of_invoice_selected_is_in_meantime_removed_from_the_database() {
	    // Write code here that turns the phrase above into concrete actions
	    throw new cucumber.api.PendingException();
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
	
	private static Date getDateFromYear(int year) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		return cal.getTime();
	}
}
