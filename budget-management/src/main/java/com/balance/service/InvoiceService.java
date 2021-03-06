package com.balance.service;

import java.util.List;

import com.balance.model.Client;
import com.balance.model.Invoice;

public interface InvoiceService {

	public List<Invoice> findAllInvoicesByYear(int year);
	
	public List<Integer> findYearsOfTheInvoices();

	public List<Invoice> findInvoicesByClientAndYear(Client client, int year);
	
	public Invoice addInvoice(Invoice invoice);
	
	public void removeInvoice(Invoice invoice);
}
