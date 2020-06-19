package com.balance.service;

import java.util.List;

import com.balance.model.Invoice;

public interface InvoiceService {

	List<Invoice> findAllInvoicesByYear(int year);

	double getTotalRevenueByYear(int year);

}
