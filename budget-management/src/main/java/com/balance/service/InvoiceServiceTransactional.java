package com.balance.service;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import com.balance.model.Invoice;
import com.balance.repository.InvoiceRepository;
import com.balance.repository.TypeRepository;
import com.balance.transaction.TransactionManager;

public class InvoiceServiceTransactional implements InvoiceService{

	private TransactionManager transactionManager;
	
	public InvoiceServiceTransactional(TransactionManager transactionManager) {
		this.transactionManager=transactionManager;
	}

	@Override
	public List<Invoice> findAllInvoices() {
		return transactionManager.doInTransaction(
				factory -> { 
					InvoiceRepository invoiceRepository=(InvoiceRepository) factory.createRepository(TypeRepository.INVOICE);
				    return invoiceRepository.findAll();
		});
	}
	
	@Override
	public List<Invoice> findAllInvoicesByYear(int year) {
		List<Invoice> invoices=this.findAllInvoices();
		Calendar cal = Calendar.getInstance();
		Iterator<Invoice> iterator = invoices.iterator();
		while (iterator.hasNext()) {
			Invoice i = iterator.next();
			cal.setTime(i.getDate());
			if(cal.get(Calendar.YEAR) != year) {
				iterator.remove();
			}
		}
		return invoices;
	}

	@Override
	public double getTotalRevenueByYear(int year) {
		List<Invoice> invoicesOfTheYear=this.findAllInvoicesByYear(year);
		double totalRevenue=0;
		for (Invoice invoice : invoicesOfTheYear) {
			totalRevenue += invoice.getRevenue();
		}
		return totalRevenue;
	}
	
	
}
