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
	
	private static final Date DATE_OF_THE_YEAR_FIXTURE=getDateFromYear(YEAR_FIXTURE);
	private static final Date DATE_NOT_OF_THE_YEAR_FIXTURE=getDateFromYear(YEAR_FIXTURE-1);
	
	
	private static final Client CLIENT_FIXTURE=new Client("1","test identifier 1");

	
	private static final Invoice INVOICE_OF_THE_YEAR_FIXTURE_1=new Invoice("1", 
			CLIENT_FIXTURE, DATE_OF_THE_YEAR_FIXTURE, 10);
	private static final Invoice INVOICE_OF_THE_YEAR_FIXTURE_2=new Invoice("2", 
			CLIENT_FIXTURE, DATE_OF_THE_YEAR_FIXTURE, 20);
	private static final Invoice INVOICE_NOT_OF_THE_YEAR_FIXTURE=new Invoice("3",
			CLIENT_FIXTURE, DATE_NOT_OF_THE_YEAR_FIXTURE, 30);
	
	
	@Before
	public void init(){
		MockitoAnnotations.initMocks(this); 
		when(transactionManager.doInTransaction(
				any())).thenAnswer(answer((TransactionCode<?> code) -> code.apply(repositoryFactory)));
		doReturn(invoiceRepository).when(repositoryFactory).createRepository(TypeRepository.INVOICE);

	}
	
	@Test
	public void testFindAllInvoices() {
		List<Invoice> invoices = Arrays.asList(new Invoice());
		when(invoiceRepository.findAll()).thenReturn(invoices);
		assertThat(invoiceService.findAllInvoices())
			.isEqualTo(invoices);
	}
	@Test
	public void testFindAllInvoicesByYearWhenDatabaseIsEmpty() {
		when(invoiceRepository.findAll()).thenReturn(new ArrayList<Invoice>());
		assertThat(invoiceService.findAllInvoicesByYear(YEAR_FIXTURE)).isEmpty();
	}
	
	@Test
	public void testFindAllInvoicesByYearWhenThereAreInvoicesAllSameYearInDatabase() {
		List<Invoice> invoices=new ArrayList<Invoice>();
		invoices.add(INVOICE_OF_THE_YEAR_FIXTURE_1);
		invoices.add(INVOICE_OF_THE_YEAR_FIXTURE_2);
		when(invoiceRepository.findAll()).thenReturn(invoices);
		assertThat(invoiceService.findAllInvoicesByYear(YEAR_FIXTURE)).isEqualTo(invoices);
	}
	
	@Test
	public void testFindAllInvoicesByYearWhenThereAreInvoicesAllDifferentYearInDatabase() {
		List<Invoice> invoices=new ArrayList<Invoice>();
		invoices.add(INVOICE_OF_THE_YEAR_FIXTURE_1);
		invoices.add(INVOICE_OF_THE_YEAR_FIXTURE_2);
		invoices.add(INVOICE_NOT_OF_THE_YEAR_FIXTURE);
		when(invoiceRepository.findAll()).thenReturn(invoices);
		assertThat(invoiceService.findAllInvoicesByYear(YEAR_FIXTURE)).containsExactly(
				INVOICE_OF_THE_YEAR_FIXTURE_1,INVOICE_OF_THE_YEAR_FIXTURE_2);
	}
	
	@Test
	public void testTotalRevenueByYearWhenDatabaseIsEmpty() {
		when(invoiceRepository.findAll()).thenReturn(new ArrayList<Invoice>());
		assertThat(invoiceService.getTotalRevenueByYear(YEAR_FIXTURE)).isZero();
	}
	
	@Test
	public void testGetTotalRevenueByYearWhenThereAreInvoicesAllSameYearInDatabase() {
		List<Invoice> invoices=new ArrayList<Invoice>();
		invoices.add(INVOICE_OF_THE_YEAR_FIXTURE_1);
		invoices.add(INVOICE_OF_THE_YEAR_FIXTURE_2);
		when(invoiceRepository.findAll()).thenReturn(invoices);
		assertThat(invoiceService.getTotalRevenueByYear(YEAR_FIXTURE)).isEqualTo(
				INVOICE_OF_THE_YEAR_FIXTURE_1.getRevenue()+INVOICE_OF_THE_YEAR_FIXTURE_2.getRevenue());
	}
	
	@Test
	public void testGetTotalRevenueByYearWhenThereAreInvoicesAllDifferentYearInDatabase() {
		List<Invoice> invoices=new ArrayList<Invoice>();
		invoices.add(INVOICE_OF_THE_YEAR_FIXTURE_1);
		invoices.add(INVOICE_OF_THE_YEAR_FIXTURE_2);
		invoices.add(INVOICE_NOT_OF_THE_YEAR_FIXTURE);
		when(invoiceRepository.findAll()).thenReturn(invoices);
		assertThat(invoiceService.getTotalRevenueByYear(YEAR_FIXTURE)).isEqualTo(
				INVOICE_OF_THE_YEAR_FIXTURE_1.getRevenue()+INVOICE_OF_THE_YEAR_FIXTURE_2.getRevenue());
	}
	
	
	private static Date getDateFromYear(int year) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		return cal.getTime();
	}
}
