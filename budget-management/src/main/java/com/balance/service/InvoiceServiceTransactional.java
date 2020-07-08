package com.balance.service;

import java.util.List;

import com.balance.exception.ClientNotFoundException;
import com.balance.exception.InvoiceNotFoundException;
import com.balance.model.Client;
import com.balance.model.Invoice;
import com.balance.repository.ClientRepository;
import com.balance.repository.InvoiceRepository;
import com.balance.transaction.TransactionManager;

public class InvoiceServiceTransactional implements InvoiceService{

	private TransactionManager transactionManager;
	
	private static final String ERROR_MESSAGE_CLIENT_NOT_FOUND=
			"Il cliente con id %s non è presente nel database";
	private static final String ERROR_MESSAGE_INVOICE_NOT_FOUND=
			"La fattura con id %s non è presente nel database";
	
	public InvoiceServiceTransactional(TransactionManager transactionManager) {
		this.transactionManager=transactionManager;
	}
	
	@Override
	public List<Invoice> findAllInvoicesByYear(int year) {
		return transactionManager.doInTransaction(
			factory -> factory.createInvoiceRepository().findInvoicesByYear(year) );
	}

	@Override
	public List<Integer> findYearsOfTheInvoices() {
		return transactionManager.doInTransaction(
			factory -> factory.createInvoiceRepository().getYearsOfInvoicesInDatabase() );
	}

	@Override
	public List<Invoice> findInvoicesByClientAndYear(Client client, int year){
		return transactionManager.doInTransaction(
			factory -> {
				ClientRepository clientRepository=factory.createClientRepository();
				if(clientRepository.findById(client.getId())==null) {
					throw new ClientNotFoundException(String.format(ERROR_MESSAGE_CLIENT_NOT_FOUND,
							client.getId()));
				}
				InvoiceRepository invoiceRepository=factory.createInvoiceRepository();
			    return invoiceRepository.findInvoicesByClientAndYear(client, year);
			});
		
	}

	public Invoice addInvoice(Invoice invoice) {
		return transactionManager.doInTransaction(
			factory -> { 
				ClientRepository clientRepository=factory.createClientRepository();
				if(clientRepository.findById(invoice.getClient().getId())==null) {
					throw new ClientNotFoundException(String.format(ERROR_MESSAGE_CLIENT_NOT_FOUND,
							invoice.getClient().getId()));
				}
				return factory.createInvoiceRepository().save(invoice);
			});
	}

	public void removeInvoice(Invoice invoice) {
		transactionManager.doInTransaction(
			factory -> {
				String clientId=invoice.getClient().getId();
				if (factory.createClientRepository().findById(clientId)==null) {
					throw new ClientNotFoundException(String.format(ERROR_MESSAGE_CLIENT_NOT_FOUND,
													clientId));
				}
				String invoiceId=invoice.getId();
				InvoiceRepository invoiceRepository=factory.createInvoiceRepository();
				if(invoiceRepository.findById(invoiceId)==null) {
					throw new InvoiceNotFoundException(String.format(ERROR_MESSAGE_INVOICE_NOT_FOUND,
							invoiceId));
				}
				return invoiceRepository.delete(invoiceId);
			});
	}
	
}
