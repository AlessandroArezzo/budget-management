package com.balance.service;

import java.util.List;

import com.balance.exception.ClientNotFoundException;
import com.balance.model.Client;
import com.balance.repository.ClientRepository;
import com.balance.repository.InvoiceRepository;
import com.balance.transaction.TransactionManager;

public class ClientServiceTransactional implements ClientService {
	
	private TransactionManager transactionManager;
	private static final String ERROR_MESSAGE_CLIENT_NOT_FOUND="Il cliente con id %s non è presente nel database";

	public ClientServiceTransactional(TransactionManager transactionManager) {
		this.transactionManager=transactionManager;
	}
	
	@Override
	public List<Client> findAllClients() {
		return transactionManager.doInTransaction(
			factory -> { 
				ClientRepository clientRepository=factory.createClientRepository();
			    return clientRepository.findAll();
			});
	}

	public Client addClient(Client client) {
		return transactionManager.doInTransaction(
			factory -> { 
				 return factory.createClientRepository().save(client);
			});
	}

	@Override
	public void removeClient(String clientId) {
		transactionManager.doInTransaction(
			factory -> { 
				ClientRepository clientRepository=factory.createClientRepository();
				if(clientRepository.findById(clientId)==null) {
					throw new ClientNotFoundException(String.format(ERROR_MESSAGE_CLIENT_NOT_FOUND,clientId));

				}
				InvoiceRepository invoiceRepository=factory.createInvoiceRepository();
				invoiceRepository.deleteAllInvoicesByClient(clientId);
				return clientRepository.delete(clientId);
			});
	}

}
