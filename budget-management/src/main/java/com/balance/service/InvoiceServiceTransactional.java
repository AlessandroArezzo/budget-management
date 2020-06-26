package com.balance.service;

import java.util.List;

import com.balance.exception.ClientNotFoundException;
import com.balance.model.Client;
import com.balance.model.Invoice;
import com.balance.repository.ClientRepository;
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

	@Override
	public List<Invoice> findInvoicesByClientAndYear(Client client, int year){
		return transactionManager.doInTransaction(
			factory -> {
				ClientRepository clientRepository=(ClientRepository) factory.createRepository(TypeRepository.CLIENT);
				if(clientRepository.findById(client.getId())==null) {
					throw new ClientNotFoundException("Cliente "+client.getIdentifier()+" non è presente nel database");
				}
				InvoiceRepository invoiceRepository=(InvoiceRepository) factory.createRepository(TypeRepository.INVOICE);
			    return invoiceRepository.findInvoicesByClientAndYear(client, year);
			});
		
	}

	@Override
	public double getAnnualClientRevenue(Client client, int year){
		return transactionManager.doInTransaction(
				factory -> {
					ClientRepository clientRepository=(ClientRepository) factory.createRepository(TypeRepository.CLIENT);
					if(clientRepository.findById(client.getId())==null) {
						throw new ClientNotFoundException("Il cliente con id "
								+client.getId()+" non presente nel database");
					}
					InvoiceRepository invoiceRepository=(InvoiceRepository) factory.createRepository(TypeRepository.INVOICE);
				    return invoiceRepository.getClientRevenueOfAnYear(client, year);
			});
	}

	
	
	
}
