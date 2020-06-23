package com.balance.repository;

import java.util.List;

import com.balance.model.Invoice;

public interface InvoiceRepository extends Repository<Invoice> {
	public List<Invoice> findInvoicesByYear(int year);
	
	public double getTotalRevenueOfAnYear(int year) ;
}
