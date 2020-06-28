package com.balance.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.AdditionalAnswers.answer;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.balance.exception.ClientNotFoundException;
import com.balance.model.Client;
import com.balance.model.Invoice;
import com.balance.repository.ClientRepository;
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
	
	@Mock 
	private ClientRepository clientRepository;
	
	private static final int YEAR_FIXTURE=2019;
	private static final Client CLIENT_FIXTURE=new Client("1","test identifier");
	
	@Before
	public void init(){
		MockitoAnnotations.initMocks(this); 
		when(transactionManager.doInTransaction(
				any())).thenAnswer(answer((TransactionCode<?> code) -> code.apply(repositoryFactory)));
		doReturn(invoiceRepository).when(repositoryFactory).createRepository(TypeRepository.INVOICE);
		doReturn(clientRepository).when(repositoryFactory).createRepository(TypeRepository.CLIENT);

	}
	
	@Test
	public void testFindAllInvoicesByYear() {
		List<Invoice> invoices=Arrays.asList(new Invoice());
		when(invoiceRepository.findInvoicesByYear(YEAR_FIXTURE)).thenReturn(invoices);
		assertThat(invoiceService.findAllInvoicesByYear(YEAR_FIXTURE)).isEqualTo(
				invoices);
	}
	
	@Test
	public void testGetYearsOfTheInvoicesInDatabase() {
		List<Integer> years=Arrays.asList(YEAR_FIXTURE);
		when(invoiceRepository.getYearsOfInvoicesInDatabase()).thenReturn(years);
		assertThat(invoiceService.findYearsOfTheInvoices()).isEqualTo(
				years);
	}
	
	@Test
	public void testFindInvoicesByClientAndYear() {
		when(clientRepository.findById(CLIENT_FIXTURE.getId()))
			.thenReturn(CLIENT_FIXTURE);
		List<Invoice> invoices=Arrays.asList(new Invoice());
		when(invoiceRepository.findInvoicesByClientAndYear(CLIENT_FIXTURE, YEAR_FIXTURE))
			.thenReturn(invoices);
		assertThat(invoiceService.findInvoicesByClientAndYear(CLIENT_FIXTURE, YEAR_FIXTURE))
			.isEqualTo(invoices);
	}
	
	@Test
	public void testFindInvoicesByClientAndYearWhenClientIsNotPresentInDatabase() {
		when(clientRepository.findById(CLIENT_FIXTURE.getId()))
			.thenReturn(null);
		try {
			invoiceService.findInvoicesByClientAndYear(CLIENT_FIXTURE, YEAR_FIXTURE);
			fail("Expected a ClientNotFoundException to be thrown");
		}
		catch(ClientNotFoundException e) {
			assertThat("Il cliente con id "+CLIENT_FIXTURE.getId()+" non è presente nel database")
					.isEqualTo(e.getMessage());
		}
	}
	
	@Test
	public void testAddInvoiceWhenClientIsPresentInDatabase() {
		Invoice invoiceToAdd=new Invoice(CLIENT_FIXTURE, new Date(), 10);
		when(clientRepository.findById(CLIENT_FIXTURE.getId())).thenReturn(CLIENT_FIXTURE);
		assertThat(invoiceService.addInvoice(invoiceToAdd))	
			.isEqualTo(invoiceToAdd);
		verify(invoiceRepository).save(invoiceToAdd);
	}
	
	@Test
	public void testAddInvoiceWhenClientIsNotPresentInDatabase() {
		Invoice invoiceToAdd=new Invoice(CLIENT_FIXTURE, new Date(), 10);
		when(clientRepository.findById(CLIENT_FIXTURE.getId())).thenReturn(null);
		try {
			invoiceService.addInvoice(invoiceToAdd);
			fail("Expected a ClientNotFoundException to be thrown");
		}
		catch(ClientNotFoundException e) {
			assertThat("Il cliente con id "+CLIENT_FIXTURE.getId()+" non è presente nel database")
					.isEqualTo(e.getMessage());
		}
	}
	
	@Test
	public void testRemoveInvoiceWhenInvoiceAndClientExistingInDatabase() {
		String idInvoiceToDelete="1";
		invoiceService.removeInvoice(idInvoiceToDelete);
		verify(invoiceRepository).delete(idInvoiceToDelete);
	}
	
}
