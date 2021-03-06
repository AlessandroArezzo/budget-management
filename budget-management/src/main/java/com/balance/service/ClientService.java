package com.balance.service;

import java.util.List;

import com.balance.model.Client;

public interface ClientService {

	public List<Client> findAllClients();
	
	public Client addClient(Client client);
	
	public void removeClient(String clientId);
}
