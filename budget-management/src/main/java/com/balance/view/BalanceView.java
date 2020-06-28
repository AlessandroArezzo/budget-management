package com.balance.view;

import java.util.List;

import com.balance.model.Client;
import com.balance.model.Invoice;

public interface BalanceView {

	public void showClients(List<Client> clients);

	public void showInvoices(List<Invoice> invoices);

	public void setChoiceYearInvoices(List<Integer> yearsOfTheInvoices);
	
	public void clientRemoved(Client clientToRemove);
	
	public void showClientError(String message, Client client);

	public void clientAdded(Client clientToAdd);

	public void invoiceAdded(Invoice invoiceToAdd);

	public void removeInvoicesOfClient(Client client);

	public void invoiceRemoved(Invoice invoiceToRemove);

}
