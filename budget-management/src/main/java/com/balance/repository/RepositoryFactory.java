package com.balance.repository;

public interface RepositoryFactory {
	public <T extends Repository<?>> T createRepository(TypeRepository type);
}
