package com.balance.service;

import java.util.List;

import com.balance.model.Invoice;

public interface InvoiceService {

	public List<Invoice> findAllInvoicesByYear(int year);

	public double getTotalRevenueOfAnYear(int year);
	
	public List<Invoice> findAllInvoices();

}
