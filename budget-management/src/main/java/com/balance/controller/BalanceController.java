package com.balance.controller;

import java.util.Calendar;

import com.balance.exception.ClientNotFoundException;
import com.balance.model.Client;
import com.balance.model.Invoice;
import com.balance.service.ClientService;
import com.balance.service.InvoiceService;
import com.balance.view.BalanceView;

public class BalanceController {
	
	private static final String CLIENT_NOT_FOUND_ERROR_LABEL="Cliente non più presente nel database";
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

	public void yearsOfTheInvoices() {		
		balanceView.setChoiceYearInvoices( 
				invoiceService.findYearsOfTheInvoices());
	}
	
	public void initializeView() {
		allClients();
		yearsOfTheInvoices();
	}	
	
	public void allInvoicesByClientAndYear(Client client, int year) {
		try {
			balanceView.showInvoices(
					invoiceService.findInvoicesByClientAndYear(client, year));
		} 
		catch(ClientNotFoundException e) {
			balanceView.showClientError(CLIENT_NOT_FOUND_ERROR_LABEL, client);
			balanceView.clientRemoved(client);
		}		
	}

	public void newClient(Client client) {
		balanceView.clientAdded(clientService.addClient(client));
	}

	public void deleteClient(Client clientToRemove) {
		try {
			clientService.removeClient(clientToRemove.getId());
		}
		catch(ClientNotFoundException e){
			balanceView.showClientError(CLIENT_NOT_FOUND_ERROR_LABEL, clientToRemove);
		}
		balanceView.clientRemoved(clientToRemove);
	}

	public void newInvoice(Invoice invoice) {
		balanceView.invoiceAdded(invoiceService.addInvoice(invoice));
	}
	
}
