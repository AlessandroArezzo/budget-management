package com.balance.controller;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.assertj.swing.annotation.GUITest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.balance.controller.BalanceController;
import com.balance.exception.ClientNotFoundException;
import com.balance.exception.InvoiceNotFoundException;
import com.balance.model.Client;
import com.balance.model.Invoice;
import com.balance.service.ClientService;
import com.balance.service.InvoiceService;
import com.balance.view.BalanceView;


public class BalanceControllerTest {
	@Mock
	private ClientService clientService;
	
	@Mock
	private InvoiceService invoiceService;
	
	@Mock
	private BalanceView balanceView;
	
	@InjectMocks
	private BalanceController balanceController;
	
	private static final int CURRENT_YEAR=Calendar.getInstance().get(Calendar.YEAR);
	private static final int YEAR_FIXTURE=2019;
	private static final Client CLIENT_FIXTURE=new Client("1", "test identifier");
	private static final Invoice INVOICE_FIXTURE=new Invoice("1", CLIENT_FIXTURE, new Date(), 10);
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test @GUITest
	public void testInitializeView() {
		List<Client> clients = Arrays.asList(new Client());
		when(clientService.findAllClients()).thenReturn(clients);
		List<Integer> yearsOfTheinvoices=Arrays.asList(CURRENT_YEAR);
		when(invoiceService.findYearsOfTheInvoices()).thenReturn(yearsOfTheinvoices);
		balanceController.initializeView();
		verify(balanceView).showClients(clients);
		verify(balanceView).setChoiceYearInvoices(yearsOfTheinvoices);
	}
	
	@Test
	public void testAllClients() {
		List<Client> clients = Arrays.asList(new Client());
		when(clientService.findAllClients()).thenReturn(clients);
		balanceController.allClients();
		verify(balanceView).showClients(clients);
	}
	
	@Test
	public void testAllInvoicesByYear() {
		List<Invoice> invoices = Arrays.asList(new Invoice());
		when(invoiceService.findAllInvoicesByYear(YEAR_FIXTURE)).thenReturn(invoices);
		balanceController.allInvoicesByYear(YEAR_FIXTURE);
		verify(balanceView).showInvoices(invoices);
	}
	
	@Test
	public void testYearsOfTheInvoices() {
		List<Integer> yearsOfTheinvoices=Arrays.asList(YEAR_FIXTURE);
		when(invoiceService.findYearsOfTheInvoices()).thenReturn(yearsOfTheinvoices);
		balanceController.yearsOfTheInvoices();
		verify(balanceView).setChoiceYearInvoices(yearsOfTheinvoices);
	}
	
	@Test
	public void testInvoicesByClientAndYear(){
		List<Invoice> invoiceofClientAndYear = Arrays.asList(new Invoice());
		when(invoiceService.findInvoicesByClientAndYear(CLIENT_FIXTURE,YEAR_FIXTURE))
			.thenReturn(invoiceofClientAndYear);
		balanceController.allInvoicesByClientAndYear(CLIENT_FIXTURE, YEAR_FIXTURE);
		verify(balanceView).showInvoices(invoiceofClientAndYear);
	}
	
	@Test
	public void testInvoicesByClientAndYearWhenClientIsNotPresentInDatabase() {
		List<Invoice> invoices = Arrays.asList(new Invoice());
		when(invoiceService.findAllInvoicesByYear(YEAR_FIXTURE)).thenReturn(invoices);
		when(invoiceService.findInvoicesByClientAndYear(CLIENT_FIXTURE,YEAR_FIXTURE))
			.thenThrow(new ClientNotFoundException("Client not found"));
		balanceController.allInvoicesByClientAndYear(CLIENT_FIXTURE, YEAR_FIXTURE);
		verify(balanceView).showClientError("Cliente non più presente nel database", 
				CLIENT_FIXTURE);
		verify(balanceView).clientRemoved(CLIENT_FIXTURE);
	}
	
	@Test
	public void testAddNewClient() {
		when(clientService.addClient(CLIENT_FIXTURE)).thenReturn(CLIENT_FIXTURE);
		balanceController.newClient(CLIENT_FIXTURE);
		InOrder inOrder = Mockito.inOrder(clientService, balanceView);
		inOrder.verify(clientService).addClient(CLIENT_FIXTURE);
		inOrder.verify(balanceView).clientAdded(CLIENT_FIXTURE);
	}
	
	@Test 
	public void testDeleteClientWhenClientIsPresentInDatabase() {
		balanceController.deleteClient(CLIENT_FIXTURE);
		InOrder inOrder = Mockito.inOrder(clientService, balanceView);
		inOrder.verify(clientService).removeClient(CLIENT_FIXTURE.getId());
		inOrder.verify(balanceView).clientRemoved(CLIENT_FIXTURE);
	}
	
	@Test 
	public void testDeleteClientWhenClientIsNotPresentInDatabase() {
		doThrow(new ClientNotFoundException("Client not found")).when(clientService)
			.removeClient(CLIENT_FIXTURE.getId());
		balanceController.deleteClient(CLIENT_FIXTURE);
		verify(balanceView).showClientError("Cliente non più presente nel database", 
				CLIENT_FIXTURE);
		InOrder inOrder = Mockito.inOrder(clientService, balanceView);
		inOrder.verify(clientService).removeClient(CLIENT_FIXTURE.getId());
		inOrder.verify(balanceView).showClientError("Cliente non più presente nel database", 
				CLIENT_FIXTURE);
		inOrder.verify(balanceView).clientRemoved(CLIENT_FIXTURE);
	}
	
	@Test
	public void testAddNewInvoiceWhenClientIsPresentInDatabase() {
		when(invoiceService.addInvoice(INVOICE_FIXTURE)).thenReturn(INVOICE_FIXTURE);
		balanceController.newInvoice(INVOICE_FIXTURE);
		InOrder inOrder = Mockito.inOrder(invoiceService, balanceView);
		inOrder.verify(invoiceService).addInvoice(INVOICE_FIXTURE);
		inOrder.verify(balanceView).invoiceAdded(INVOICE_FIXTURE);
	}
	
	@Test
	public void testAddNewInvoiceWhenClientIsNotPresentInDatabase() {
		doThrow(new ClientNotFoundException("Client not found"))
			.when(invoiceService).addInvoice(INVOICE_FIXTURE);
		balanceController.newInvoice(INVOICE_FIXTURE);
		InOrder inOrder = Mockito.inOrder(invoiceService, balanceView);
		inOrder.verify(invoiceService).addInvoice(INVOICE_FIXTURE);
		inOrder.verify(balanceView).showClientError("Cliente non più presente nel database", 
				CLIENT_FIXTURE);
		inOrder.verify(balanceView).clientRemoved(CLIENT_FIXTURE);
		inOrder.verify(balanceView).removeInvoicesOfClient(CLIENT_FIXTURE);
	}
	
	@Test
	public void testDeleteInvoiceWhenInvoiceAndClientExistingInDatabase() {
		balanceController.deleteInvoice(INVOICE_FIXTURE);
		InOrder inOrder = Mockito.inOrder(invoiceService, balanceView);
		inOrder.verify(invoiceService).removeInvoice(INVOICE_FIXTURE.getId());
		inOrder.verify(balanceView).invoiceRemoved(INVOICE_FIXTURE);
	}
	
	@Test
	public void testDeleteInvoiceWhenInvoiceNoExistingInDatabase() {
		doThrow(new InvoiceNotFoundException("Invoice not found"))
			.when(invoiceService).removeInvoice(INVOICE_FIXTURE.getId());
		balanceController.deleteInvoice(INVOICE_FIXTURE);
		InOrder inOrder = Mockito.inOrder(invoiceService, balanceView);
		inOrder.verify(invoiceService).removeInvoice(INVOICE_FIXTURE.getId());
		inOrder.verify(balanceView).showInvoiceError("Fattura non più presente nel database", 
				INVOICE_FIXTURE);
		inOrder.verify(balanceView).invoiceRemoved(INVOICE_FIXTURE);
	}
	
}
