package com.balance.transaction;

public interface TransactionManager {
	public <R> R doInTransaction(TransactionCode<R> code);

}
