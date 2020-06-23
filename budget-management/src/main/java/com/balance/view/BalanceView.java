package com.balance.view;

import java.util.List;

import com.balance.model.Client;
import com.balance.model.Invoice;

public interface BalanceView {

	public void showClients(List<Client> clients);

	public void showInvoices(List<Invoice> invoices);

	public void setAnnualTotalRevenue(int year, double  totalRevenue);

}
