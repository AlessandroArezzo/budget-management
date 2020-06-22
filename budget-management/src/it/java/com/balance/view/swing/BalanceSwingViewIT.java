package com.balance.view.swing;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Calendar;
import java.util.Date;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.BeforeClass;
import org.junit.Test;

import com.balance.controller.BalanceController;
import com.balance.model.Client;
import com.balance.model.Invoice;
import com.balance.repository.mongodb.ClientMongoRepository;
import com.balance.repository.mongodb.InvoiceMongoRepository;
import com.balance.service.ClientService;
import com.balance.service.ClientServiceTransactional;
import com.balance.service.InvoiceService;
import com.balance.service.InvoiceServiceTransactional;
import com.balance.transaction.TransactionManager;
import com.balance.transaction.mongodb.TransactionMongoManager;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.ClientSession;

import de.bwaldvogel.mongo.MongoServer;

public class BalanceSwingViewIT extends AssertJSwingJUnitTestCase{
	
	private static final String DB_NAME="balance";
	private static final String COLLECTION_CLIENTS_NAME="client";
	private static final String COLLECTION_INVOICES_NAME="invoice";
	
	private static final String CLIENT_IDENTIFIER_1="test identifier 1";
	private static final String CLIENT_IDENTIFIER_2="test identifier 2";
	
	private static final int YEAR_FIXTURE=2019;
	
	private static final Date DATE_OF_THE_YEAR_FIXTURE=getDateFromYear(YEAR_FIXTURE);
	private static final Date DATE_NOT_OF_THE_YEAR_FIXTURE=getDateFromYear(YEAR_FIXTURE-1);
	
	private static final double INVOICE_REVENUE_1=10.0;
	private static final double INVOICE_REVENUE_2=20.0;
	private static final double INVOICE_REVENUE_3=30.0;
	
	private MongoClient mongoClient;
	
	private ClientMongoRepository clientRepository;
	private InvoiceMongoRepository invoiceRepository;
	private BalanceController balanceController;
	private BalanceSwingView balanceSwingView;
	
	private FrameFixture window;
	@Override
	protected void onSetUp() {
		mongoClient=new MongoClient("localhost");
		ClientSession clientSession=mongoClient.startSession();
		clientRepository=new ClientMongoRepository(mongoClient,clientSession,DB_NAME, COLLECTION_CLIENTS_NAME);
		invoiceRepository=new InvoiceMongoRepository(mongoClient,clientSession,DB_NAME, 
				COLLECTION_INVOICES_NAME, clientRepository);
		for (Client client : clientRepository.findAll()) {
			clientRepository.delete(client.getId()); 
		}
		for (Invoice invoice : invoiceRepository.findAll()) {
			invoiceRepository.delete(invoice.getId()); 
		}
		GuiActionRunner.execute(() -> {
			balanceSwingView=new BalanceSwingView();
			TransactionManager transactionManager=new TransactionMongoManager(mongoClient, 
					DB_NAME, COLLECTION_CLIENTS_NAME, COLLECTION_INVOICES_NAME);
			ClientService clientService=new ClientServiceTransactional(transactionManager);
			InvoiceService invoiceService=new InvoiceServiceTransactional(transactionManager);
			balanceController=new BalanceController(balanceSwingView, clientService, invoiceService);
			balanceSwingView.setBalanceController(balanceController);
			return balanceSwingView;
		});
		window = new FrameFixture(robot(), balanceSwingView);
		window.show();
	}
	
	@Override
	protected void onTearDown() {
		mongoClient.close();
	}
	
	@Test @GUITest
	public void testAllClients() {
		Client client1 = new Client(CLIENT_IDENTIFIER_1);
		Client client2 = new Client(CLIENT_IDENTIFIER_2);
		clientRepository.save(client1);
		clientRepository.save(client2);
		GuiActionRunner.execute( 
				() -> balanceController.allClients() );
		assertThat(window.list("clientsList").contents())
			.containsExactly(client1.toString(),client2.toString());
	}
	
	@Test @GUITest
	public void testAllInvoicesByYear() {
		Client client1 = new Client(CLIENT_IDENTIFIER_1);
		Client client2 = new Client(CLIENT_IDENTIFIER_2);
		clientRepository.save(client1);
		clientRepository.save(client2);
		client1.setId(clientRepository.findAll().get(0).getId());
		client2.setId(clientRepository.findAll().get(1).getId());
		Invoice invoice1=new Invoice(client1, DATE_OF_THE_YEAR_FIXTURE, INVOICE_REVENUE_1);
		Invoice invoice2=new Invoice(client2, DATE_OF_THE_YEAR_FIXTURE, INVOICE_REVENUE_2);
		Invoice invoice3=new Invoice(client2, DATE_NOT_OF_THE_YEAR_FIXTURE, INVOICE_REVENUE_3);
		invoiceRepository.save(invoice1);
		invoiceRepository.save(invoice2);
		invoiceRepository.save(invoice3);
		GuiActionRunner.execute( 
				() -> balanceController.allInvoiceByYear(YEAR_FIXTURE) 
		);
		assertThat(window.list("invoicesList").contents())
			.containsExactly(invoice1.toString(),invoice2.toString());
	}
	
	@Test @GUITest
	public void testGetTotalRevenueOfAnYear() {
		Client client1 = new Client(CLIENT_IDENTIFIER_1);
		Client client2 = new Client(CLIENT_IDENTIFIER_2);
		clientRepository.save(client1);
		clientRepository.save(client2);
		client1.setId(clientRepository.findAll().get(0).getId());
		client2.setId(clientRepository.findAll().get(1).getId());
		Invoice invoice1=new Invoice(client1, DATE_OF_THE_YEAR_FIXTURE, INVOICE_REVENUE_1);
		Invoice invoice2=new Invoice(client2, DATE_OF_THE_YEAR_FIXTURE, INVOICE_REVENUE_2);
		Invoice invoice3=new Invoice(client2, DATE_NOT_OF_THE_YEAR_FIXTURE, INVOICE_REVENUE_3);
		invoiceRepository.save(invoice1);
		invoiceRepository.save(invoice2);
		invoiceRepository.save(invoice3);
		GuiActionRunner.execute( 
				() -> balanceController.annualRevenue(YEAR_FIXTURE)
		);
		window.label("revenueLabel").requireText(
				"Il ricavo totale del "+YEAR_FIXTURE+" è di "+String.format("%.2f", 
						INVOICE_REVENUE_1+INVOICE_REVENUE_2)+"€");
	}
	
	private static Date getDateFromYear(int year) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		return cal.getTime();
	}
	
}
