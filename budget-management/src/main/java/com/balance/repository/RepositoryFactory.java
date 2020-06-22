package com.balance.repository;

public interface RepositoryFactory {
	@SuppressWarnings("rawtypes")
	public Repository createRepository(TypeRepository type);
}
