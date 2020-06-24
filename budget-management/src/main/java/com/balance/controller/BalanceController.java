package com.balance.controller;

import java.util.Calendar;

import com.balance.service.ClientService;
import com.balance.service.InvoiceService;
import com.balance.view.BalanceView;

public class BalanceController {
	private BalanceView balanceView;
	private ClientService clientService;
	private InvoiceService invoiceService;
	
	public BalanceController(BalanceView balanceView, ClientService clientService, InvoiceService invoiceService) {
		this.balanceView=balanceView;
		this.clientService=clientService;
		this.invoiceService=invoiceService;
	}

	public void allClients() {
		balanceView.showClients(clientService.findAllClients());
	}

	public void allInvoicesByYear(int year) {
		balanceView.showInvoices(invoiceService.findAllInvoicesByYear(year));
	}

	public void annualRevenue(int year) {
		balanceView.setAnnualTotalRevenue(year, 
				invoiceService.getTotalRevenueOfAnYear(year));
	}

	public void yearsOfTheInvoices() {		
		balanceView.setChoiceYearInvoices( 
				invoiceService.findYearsOfTheInvoices());
	}
	
	public void initializeView() {
		allClients();
		yearsOfTheInvoices();
		balanceView.setYearSelected(Calendar.getInstance().get(Calendar.YEAR));
	}
}
