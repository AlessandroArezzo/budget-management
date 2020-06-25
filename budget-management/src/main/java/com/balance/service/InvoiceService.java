package com.balance.service;

import java.util.List;

import com.balance.exception.ClientNotFoundException;
import com.balance.model.Client;
import com.balance.model.Invoice;

public interface InvoiceService {

	public List<Invoice> findAllInvoicesByYear(int year);

	public double getTotalRevenueOfAnYear(int year);
	
	public List<Integer> findYearsOfTheInvoices();

	public List<Invoice> findInvoicesByClientAndYear(Client client, int year);

	public double getAnnualClientRevenue(Client client, int year);
	
}
