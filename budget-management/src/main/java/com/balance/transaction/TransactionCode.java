package com.balance.transaction;

import java.util.function.Function;

import com.balance.repository.RepositoryFactory;


@FunctionalInterface
public interface TransactionCode<R> extends Function<RepositoryFactory,R>{ }