package com.balance.repository;

public interface RepositoryFactory {
	public ClientRepository createClientRepository();
	public InvoiceRepository createInvoiceRepository();
}
