package com.balance.service;

import java.util.List;

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
				InvoiceRepository invoiceRepository=(InvoiceRepository) factory.createRepository(TypeRepository.INVOICE);
				invoiceRepository.deleteAllInvoicesByClient(clientId);
				return clientRepository.delete(clientId);
			});
	}

}
