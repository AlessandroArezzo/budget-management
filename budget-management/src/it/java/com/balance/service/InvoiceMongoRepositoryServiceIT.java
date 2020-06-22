package com.balance.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Calendar;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.balance.model.Client;
import com.balance.model.Invoice;
import com.balance.repository.mongodb.ClientMongoRepository;
import com.balance.repository.mongodb.InvoiceMongoRepository;
import com.balance.transaction.TransactionManager;
import com.balance.transaction.mongodb.TransactionMongoManager;
import com.mongodb.MongoClient;
import com.mongodb.client.ClientSession;



public class InvoiceMongoRepositoryServiceIT {
	InvoiceService invoiceService;
	InvoiceMongoRepository invoiceRepository;
		
	private MongoClient mongoClient;
	private static ClientMongoRepository clientRepository;
	
	private static final String DB_NAME="balance";
	private static final String COLLECTION_CLIENTS_NAME="client";
	private static final String COLLECTION_INVOICES_NAME="invoice";
	
	private static Client CLIENT_FIXTURE_1;
	private static Client CLIENT_FIXTURE_2;

	private static final int YEAR_FIXTURE=2019;
	
	private static Invoice INVOICE_OF_YEAR_FIXTURE_1;
	private static Invoice INVOICE_OF_YEAR_FIXTURE_2;
	private static Invoice INVOICE_OF_NOT_YEAR_FIXTURE;
	
	@BeforeClass
	public static void init() {
		MongoClient mongoClient=new MongoClient("localhost");
		clientRepository=new ClientMongoRepository(mongoClient, 
				mongoClient.startSession(),DB_NAME, COLLECTION_CLIENTS_NAME);
		for (Client client : clientRepository.findAll()) {
			clientRepository.delete(client.getId()); 
		}
		clientRepository.save(new Client("test identifier 1"));
		clientRepository.save(new Client("test identifier 2"));
		CLIENT_FIXTURE_1=clientRepository.findAll().get(0);
		CLIENT_FIXTURE_2=clientRepository.findAll().get(1);
		INVOICE_OF_YEAR_FIXTURE_1= new Invoice(CLIENT_FIXTURE_1,
				getDateFromYear(YEAR_FIXTURE), 10);
		INVOICE_OF_YEAR_FIXTURE_2= new Invoice(CLIENT_FIXTURE_2,
				getDateFromYear(YEAR_FIXTURE), 20);
		INVOICE_OF_NOT_YEAR_FIXTURE= new Invoice(CLIENT_FIXTURE_2,
				getDateFromYear(YEAR_FIXTURE-1),30);
		mongoClient.close();
	}
	
	@Before
	public void setup() {
		mongoClient=new MongoClient("localhost");
		ClientSession clientSession=mongoClient.startSession();
		TransactionManager transactionManager=new TransactionMongoManager(mongoClient, 
				DB_NAME, COLLECTION_CLIENTS_NAME, COLLECTION_INVOICES_NAME);
		invoiceService= new InvoiceServiceTransactional(transactionManager);
		invoiceRepository=new InvoiceMongoRepository(mongoClient, clientSession ,
				DB_NAME, COLLECTION_INVOICES_NAME, new ClientMongoRepository(mongoClient, 
						clientSession,DB_NAME, COLLECTION_CLIENTS_NAME));
		for (Invoice invoice : invoiceRepository.findAll()) {
			invoiceRepository.delete(invoice.getId()); 
		}
	}
	
	@After
	public void onTearDown() {
		mongoClient.close();
	}
	

	@Test
	public void testFindAllInvoicesByYear() {
		invoiceRepository.save(INVOICE_OF_YEAR_FIXTURE_1);
		invoiceRepository.save(INVOICE_OF_YEAR_FIXTURE_2);
		invoiceRepository.save(INVOICE_OF_NOT_YEAR_FIXTURE);
		assertThat(invoiceService.findAllInvoicesByYear(YEAR_FIXTURE)).containsExactly(
				INVOICE_OF_YEAR_FIXTURE_1,
				INVOICE_OF_YEAR_FIXTURE_2);
	}
	
	@Test
	public void testgetTotalRevenueOfAnYear() {
		invoiceRepository.save(INVOICE_OF_YEAR_FIXTURE_1);
		invoiceRepository.save(INVOICE_OF_YEAR_FIXTURE_2);
		invoiceRepository.save(INVOICE_OF_NOT_YEAR_FIXTURE);
		assertThat(invoiceService.getTotalRevenueOfAnYear(YEAR_FIXTURE)).isEqualTo(
				INVOICE_OF_YEAR_FIXTURE_1.getRevenue()+INVOICE_OF_YEAR_FIXTURE_2.getRevenue());
		
	}
	
	
	private static Date getDateFromYear(int year) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		return cal.getTime();
	}
	
}
