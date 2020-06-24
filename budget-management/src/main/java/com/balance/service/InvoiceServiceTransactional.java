package com.balance.service;

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
	public List<Invoice> findAllInvoicesByYear(int year) {
		return transactionManager.doInTransaction(
				factory -> { 
					InvoiceRepository invoiceRepository=(InvoiceRepository) factory.createRepository(TypeRepository.INVOICE);
				    return invoiceRepository.findInvoicesByYear(year);
		});
	}

	@Override
	public double getTotalRevenueOfAnYear(int year) {
		return transactionManager.doInTransaction(
				factory -> { 
					InvoiceRepository invoiceRepository=(InvoiceRepository) factory.createRepository(TypeRepository.INVOICE);
				    return invoiceRepository.getTotalRevenueOfAnYear(year);
		});
	}

	@Override
	public List<Integer> findYearsOfTheInvoices() {
		return transactionManager.doInTransaction(
				factory -> { 
					InvoiceRepository invoiceRepository=(InvoiceRepository) factory.createRepository(TypeRepository.INVOICE);
				    return invoiceRepository.getYearsOfInvoicesInDatabase();
		});
	}

	
	
	
}
