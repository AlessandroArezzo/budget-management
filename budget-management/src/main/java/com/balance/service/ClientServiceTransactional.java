package com.balance.service;

import java.util.List;

import com.balance.model.Client;
import com.balance.repository.ClientRepository;
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

}
