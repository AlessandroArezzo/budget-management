package com.balance.controller;

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

	public void allInvoiceByYear(int year) {
		balanceView.showInvoices(invoiceService.findAllInvoicesByYear(year));
	}

	public void annualRevenue(int year) {
		balanceView.setAnnualTotalRevenue(year, 
				invoiceService.getTotalRevenueByYear(year));
	}
}