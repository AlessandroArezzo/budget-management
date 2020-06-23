package com.balance.repository;

import java.util.List;

public interface Repository<T> {
	public List<T> findAll();
	public T findById(String id);
	public void save(T newElement);
	public void delete(String id);
}
