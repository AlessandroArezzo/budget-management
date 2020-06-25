package com.balance.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.balance.exception.ClientNotFoundException;
import com.balance.model.Client;
import com.balance.model.Invoice;
import com.balance.repository.TypeRepository;
import com.balance.repository.mongodb.ClientMongoRepository;
import com.balance.repository.mongodb.InvoiceMongoRepository;
import com.balance.repository.mongodb.RepositoryMongoFactory;
import com.balance.transaction.TransactionManager;
import com.balance.transaction.mongodb.TransactionMongoManager;
import com.balance.utils.DateTestsUtil;
import com.mongodb.MongoClient;



public class InvoiceMongoRepositoryServiceIT {
	private InvoiceService invoiceService;
	private InvoiceMongoRepository invoiceRepository;
		
	private MongoClient mongoClient;
	private ClientMongoRepository clientRepository;
	
	private static final String DB_NAME="balance";
	private static final String COLLECTION_CLIENTS_NAME="client";
	private static final String COLLECTION_INVOICES_NAME="invoice";
	
	private Client CLIENT_FIXTURE_1;
	private Client CLIENT_FIXTURE_2;

	private static final int YEAR_FIXTURE=2019;
	
	private Invoice INVOICE_OF_YEAR_FIXTURE_1;
	private Invoice INVOICE_OF_YEAR_FIXTURE_2;
	private Invoice INVOICE_OF_PREVIOUS_YEAR_FIXTURE;
	
	@Before
	public void setup() {
		mongoClient=new MongoClient("localhost");
		RepositoryMongoFactory mongoFactory=new RepositoryMongoFactory(
				mongoClient,mongoClient.startSession(),
				DB_NAME, COLLECTION_CLIENTS_NAME,COLLECTION_INVOICES_NAME);
		clientRepository=(ClientMongoRepository) mongoFactory.createRepository(
				TypeRepository.CLIENT);
		invoiceRepository=(InvoiceMongoRepository) mongoFactory.createRepository(
				TypeRepository.INVOICE);
		for (Client client : clientRepository.findAll()) {
			clientRepository.delete(client.getId()); 
		}
		for (Invoice invoice : invoiceRepository.findAll()) {
			invoiceRepository.delete(invoice.getId()); 
		}
		clientRepository.save(new Client("test identifier 1"));
		clientRepository.save(new Client("test identifier 2"));
		CLIENT_FIXTURE_1=clientRepository.findAll().get(0);
		CLIENT_FIXTURE_2=clientRepository.findAll().get(1);
		INVOICE_OF_YEAR_FIXTURE_1= new Invoice(CLIENT_FIXTURE_1,
				DateTestsUtil.getDateFromYear(YEAR_FIXTURE), 10);
		INVOICE_OF_YEAR_FIXTURE_2= new Invoice(CLIENT_FIXTURE_2,
				DateTestsUtil.getDateFromYear(YEAR_FIXTURE), 20);
		INVOICE_OF_PREVIOUS_YEAR_FIXTURE= new Invoice(CLIENT_FIXTURE_2,
				DateTestsUtil.getDateFromYear(YEAR_FIXTURE-1),30);
		TransactionManager transactionManager=new TransactionMongoManager(mongoClient, 
				DB_NAME, COLLECTION_CLIENTS_NAME, COLLECTION_INVOICES_NAME);
		invoiceService= new InvoiceServiceTransactional(transactionManager);
	}
	
	@After
	public void onTearDown() {
		mongoClient.close();
	}
	
	@Test
	public void testFindAllInvoicesByYear() {
		invoiceRepository.save(INVOICE_OF_YEAR_FIXTURE_1);
		invoiceRepository.save(INVOICE_OF_YEAR_FIXTURE_2);
		invoiceRepository.save(INVOICE_OF_PREVIOUS_YEAR_FIXTURE);
		assertThat(invoiceService.findAllInvoicesByYear(YEAR_FIXTURE)).containsExactly(
				INVOICE_OF_YEAR_FIXTURE_1,
				INVOICE_OF_YEAR_FIXTURE_2);
	}
	
	@Test
	public void testGetTotalRevenueOfAnYear() {
		invoiceRepository.save(INVOICE_OF_YEAR_FIXTURE_1);
		invoiceRepository.save(INVOICE_OF_YEAR_FIXTURE_2);
		invoiceRepository.save(INVOICE_OF_PREVIOUS_YEAR_FIXTURE);
		assertThat(invoiceService.getTotalRevenueOfAnYear(YEAR_FIXTURE)).isEqualTo(
				INVOICE_OF_YEAR_FIXTURE_1.getRevenue()+INVOICE_OF_YEAR_FIXTURE_2.getRevenue());
	}
	
	@Test
	public void testGetYearsOfInvoicesInDatabase() {
		invoiceRepository.save(INVOICE_OF_YEAR_FIXTURE_1);
		invoiceRepository.save(INVOICE_OF_YEAR_FIXTURE_2);
		invoiceRepository.save(INVOICE_OF_PREVIOUS_YEAR_FIXTURE);
		assertThat(invoiceService.getTotalRevenueOfAnYear(YEAR_FIXTURE)).isEqualTo(
				INVOICE_OF_YEAR_FIXTURE_1.getRevenue()+INVOICE_OF_YEAR_FIXTURE_2.getRevenue());
	}
	
	@Test
	public void testFindYearsOfTheInvoices() {
		invoiceRepository.save(INVOICE_OF_YEAR_FIXTURE_1);
		invoiceRepository.save(INVOICE_OF_YEAR_FIXTURE_2);
		invoiceRepository.save(INVOICE_OF_PREVIOUS_YEAR_FIXTURE);
		assertThat(invoiceService.findYearsOfTheInvoices())
			.containsExactly(YEAR_FIXTURE-1,YEAR_FIXTURE);
	}
	
	@Test
	public void testFindInvoicesOfAClientAndYear() {
		invoiceRepository.save(INVOICE_OF_YEAR_FIXTURE_1);
		invoiceRepository.save(INVOICE_OF_YEAR_FIXTURE_2);
		invoiceRepository.save(INVOICE_OF_PREVIOUS_YEAR_FIXTURE);
		assertThat(invoiceService.findInvoicesByClientAndYear(CLIENT_FIXTURE_1, YEAR_FIXTURE))
			.containsExactly(INVOICE_OF_YEAR_FIXTURE_1);
	}
	
	@Test
	public void testGetAnnualClientRevenue() {
		invoiceRepository.save(INVOICE_OF_YEAR_FIXTURE_1);
		invoiceRepository.save(INVOICE_OF_YEAR_FIXTURE_2);
		invoiceRepository.save(INVOICE_OF_PREVIOUS_YEAR_FIXTURE);
		assertThat(invoiceService.getAnnualClientRevenue(CLIENT_FIXTURE_1, YEAR_FIXTURE))
			.isEqualTo(INVOICE_OF_YEAR_FIXTURE_1.getRevenue());
	}
	
	@Test
	public void testFindInvoicesOfAClientAndYearWhenClientIsNotInDatabase() {
		invoiceRepository.save(INVOICE_OF_YEAR_FIXTURE_1);
		invoiceRepository.save(INVOICE_OF_YEAR_FIXTURE_2);
		invoiceRepository.save(INVOICE_OF_PREVIOUS_YEAR_FIXTURE);
		clientRepository.delete(CLIENT_FIXTURE_1.getId());
		try {
			invoiceService.findInvoicesByClientAndYear(CLIENT_FIXTURE_1, YEAR_FIXTURE);
			fail("Excpected a ClientNotFoundException to be thrown");
		}
		catch(ClientNotFoundException e) {
			assertThat("Cliente "+CLIENT_FIXTURE_1.getIdentifier()+" non presente nel database")
					.isEqualTo(e.getMessage());
		}	
	}
	
	@Test
	public void testGetAnnualClientRevenueWhenClientIsNotInDatabase() {
		invoiceRepository.save(INVOICE_OF_YEAR_FIXTURE_1);
		invoiceRepository.save(INVOICE_OF_YEAR_FIXTURE_2);
		invoiceRepository.save(INVOICE_OF_PREVIOUS_YEAR_FIXTURE);
		clientRepository.delete(CLIENT_FIXTURE_1.getId());
		try {
			invoiceService.getAnnualClientRevenue(CLIENT_FIXTURE_1, YEAR_FIXTURE);
			fail("Excepted a ClientNotFoundException to be thrown");
		}
		catch(ClientNotFoundException e) {
			assertThat("Cliente "+CLIENT_FIXTURE_1.getIdentifier()+" non presente nel database")
					.isEqualTo(e.getMessage());
		}	
	}
}
