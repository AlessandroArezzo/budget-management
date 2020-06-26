package com.balance.service;

import java.util.List;

import com.balance.exception.ClientNotFoundException;
import com.balance.model.Client;
import com.balance.repository.ClientRepository;
import com.balance.repository.InvoiceRepository;
import com.balance.repository.TypeRepository;
import com.balance.transaction.TransactionManager;

public class ClientServiceTransactional implements ClientService {
	
	private TransactionManager transactionManager;
	
	public ClientServiceTransactional(TransactionManager transactionManager) {
		this.transactionManager=transactionManager;
	}
	
	@Override
	public List<Client> findAllClients() {
		return transactionManager.doInTransaction(
			factory -> { 
				ClientRepository clientRepository=(ClientRepository) factory.createRepository(TypeRepository.CLIENT);
			    return clientRepository.findAll();
			});
	}

	public Client addClient(Client client) {
		return transactionManager.doInTransaction(
			factory -> { 
				 ((ClientRepository) factory.createRepository(TypeRepository.CLIENT))
					.save(client);
				 return client;
			});
	}

	@Override
	public void removeClient(String clientId) {
		transactionManager.doInTransaction(
			factory -> { 
				ClientRepository clientRepository=(ClientRepository) factory.createRepository(TypeRepository.CLIENT);
				if(clientRepository.findById(clientId)!=null) {
					InvoiceRepository invoiceRepository=(InvoiceRepository) factory.createRepository(TypeRepository.INVOICE);
					invoiceRepository.deleteAllInvoicesByClient(clientId);
					return clientRepository.delete(clientId);
				}
				throw new ClientNotFoundException("Il cliente con id "+clientId+" non è presente nel database");
			});
	}

}
