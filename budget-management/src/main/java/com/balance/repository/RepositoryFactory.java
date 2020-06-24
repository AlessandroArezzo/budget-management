package com.balance.repository;

public interface RepositoryFactory {
	public Repository<?> createRepository(TypeRepository type);
}
