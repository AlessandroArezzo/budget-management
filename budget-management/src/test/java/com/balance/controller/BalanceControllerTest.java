package com.balance.controller;

import static org.mockito.AdditionalAnswers.answer;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.balance.controller.BalanceController;
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
	
	private static final int YEAR_FIXTURE=2019;
	private static final double TOTAL_REVENUE_FIXTURE=50.6;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testAllClients() {
		List<Client> clients = Arrays.asList(new Client());
		when(clientService.findAllClients()).thenReturn(clients);
		balanceController.allClients();
		verify(balanceView).showClients(clients); // Controllo che metodo allClients() del controller invochi il metodo showClients con la lista attesa come parametro(quella ritornata dal metodo findAllClients() del repository)
	}
	
	@Test
	public void testAllInvoicesByYear() {
		List<Invoice> invoices = Arrays.asList(new Invoice());
		when(invoiceService.findAllInvoicesByYear(YEAR_FIXTURE)).thenReturn(invoices);
		balanceController.allInvoiceByYear(YEAR_FIXTURE);
		verify(balanceView).showInvoices(invoices);
	}
	
	@Test
	public void testAnnualRevenue() {
		when(invoiceService.getTotalRevenueByYear(YEAR_FIXTURE)).thenReturn(TOTAL_REVENUE_FIXTURE);
		balanceController.annualRevenue(YEAR_FIXTURE);
		verify(balanceView).setAnnualTotalRevenue(YEAR_FIXTURE,TOTAL_REVENUE_FIXTURE);
	}
}
