package com.balance.transaction;

import com.balance.exception.DatabaseException;

public interface TransactionManager {
	public <R> R doInTransaction(TransactionCode<R> code);

}
