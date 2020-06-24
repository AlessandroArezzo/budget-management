package com.balance.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalAnswers.answer;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.balance.model.Client;
import com.balance.model.Invoice;
import com.balance.repository.InvoiceRepository;
import com.balance.repository.RepositoryFactory;
import com.balance.repository.TypeRepository;
import com.balance.transaction.TransactionCode;
import com.balance.transaction.TransactionManager;

public class InvoiceServiceTransactionalTest {
	
	@InjectMocks
	private InvoiceServiceTransactional invoiceService;
	
	@Mock
	private TransactionManager transactionManager;
	
	@Mock
	private RepositoryFactory repositoryFactory;
	
	@Mock 
	private InvoiceRepository invoiceRepository;
	
	private static final int YEAR_FIXTURE=2019;

	
	
	@Before
	public void init(){
		MockitoAnnotations.initMocks(this); 
		when(transactionManager.doInTransaction(
				any())).thenAnswer(answer((TransactionCode<?> code) -> code.apply(repositoryFactory)));
		doReturn(invoiceRepository).when(repositoryFactory).createRepository(TypeRepository.INVOICE);

	}
	
	@Test
	public void testFindAllInvoicesByYear() {
		List<Invoice> invoices=Arrays.asList(new Invoice());
		when(invoiceRepository.findInvoicesByYear(YEAR_FIXTURE)).thenReturn(invoices);
		assertThat(invoiceService.findAllInvoicesByYear(YEAR_FIXTURE)).isEqualTo(
				invoices);
	}
	
	@Test
	public void testGetTotalRevenueOfAnYear() {
		double revenueOfYear=100.0;
		when(invoiceRepository.getTotalRevenueOfAnYear(YEAR_FIXTURE)).thenReturn(
				revenueOfYear);
		assertThat(invoiceService.getTotalRevenueOfAnYear(YEAR_FIXTURE)).isEqualTo(
				revenueOfYear);
	}
	
	@Test
	public void testGetYearsOfTheInvoicesInDatabase() {
		List<Integer> years=Arrays.asList(YEAR_FIXTURE);
		when(invoiceRepository.getYearsOfInvoicesInDatabase()).thenReturn(years);
		assertThat(invoiceService.findYearsOfTheInvoices()).isEqualTo(
				years);
		
	}
	
	
}
