package com.balance.repository;

import java.util.List;

public interface Repository<T> {
	public List<T> findAll();
	public T findById(String id);
	public T save(T newElement);
	public T delete(String id);
}
