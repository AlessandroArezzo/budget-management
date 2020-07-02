package com.balance.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.AdditionalAnswers.answer;
import static org.mockito.ArgumentMatchers.any;
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
import com.balance.exception.InvoiceNotFoundException;
import com.balance.model.Client;
import com.balance.model.Invoice;
import com.balance.repository.ClientRepository;
import com.balance.repository.InvoiceRepository;
import com.balance.repository.RepositoryFactory;
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
		when(transactionManager.doInTransaction(any()))
			.thenAnswer(answer((TransactionCode<?> code) -> code.apply(repositoryFactory)));
		when(repositoryFactory.createClientRepository()).thenReturn(clientRepository);
		when(repositoryFactory.createInvoiceRepository()).thenReturn(invoiceRepository);
	}
	
	@Test
	public void testFindAllInvoicesByYear() {
		List<Invoice> invoices=Arrays.asList(new Invoice());
		when(invoiceRepository.findInvoicesByYear(YEAR_FIXTURE)).thenReturn(invoices);
		List<Invoice> invoicesFound=invoiceService.findAllInvoicesByYear(YEAR_FIXTURE);
		assertThat(invoicesFound).isEqualTo(invoices);
	}
	
	@Test
	public void testGetYearsOfTheInvoicesInDatabase() {
		List<Integer> years=Arrays.asList(YEAR_FIXTURE);
		when(invoiceRepository.getYearsOfInvoicesInDatabase()).thenReturn(years);
		List<Integer> yearsOfInvoicesFound=invoiceService.findYearsOfTheInvoices();
		assertThat(yearsOfInvoicesFound).isEqualTo(years);
	}
	
	@Test
	public void testFindInvoicesByClientAndYear() {
		when(clientRepository.findById(CLIENT_FIXTURE.getId())).thenReturn(CLIENT_FIXTURE);
		List<Invoice> invoices=Arrays.asList(new Invoice());
		when(invoiceRepository.findInvoicesByClientAndYear(CLIENT_FIXTURE, YEAR_FIXTURE))
			.thenReturn(invoices);
		List<Invoice> invoicesOfYearAndClientFound=invoiceService.findInvoicesByClientAndYear(
																CLIENT_FIXTURE, YEAR_FIXTURE);
		assertThat(invoicesOfYearAndClientFound).isEqualTo(invoices);
	}
	
	@Test
	public void testFindInvoicesByClientAndYearWhenClientExistingInDatabase() {
		when(clientRepository.findById(CLIENT_FIXTURE.getId())).thenReturn(null);
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
	public void testAddInvoiceWhenClientExistingInDatabase() {
		Invoice invoiceToAdd=new Invoice(CLIENT_FIXTURE, new Date(), 10);
		when(clientRepository.findById(CLIENT_FIXTURE.getId())).thenReturn(CLIENT_FIXTURE);
		when(invoiceRepository.save(invoiceToAdd)).thenReturn(invoiceToAdd);
		Invoice invoiceAdded=invoiceService.addInvoice(invoiceToAdd);
		assertThat(invoiceAdded).isEqualTo(invoiceToAdd);
		verify(invoiceRepository).save(invoiceToAdd);
	}
	
	@Test
	public void testAddInvoiceWhenClientNotExistingInDatabase() {
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
		Invoice invoiceToRemove=new Invoice("1",CLIENT_FIXTURE, new Date(), 10);
		when(clientRepository.findById(CLIENT_FIXTURE.getId())).thenReturn(CLIENT_FIXTURE);
		when(invoiceRepository.findById(invoiceToRemove.getId())).thenReturn(invoiceToRemove);
		invoiceService.removeInvoice(invoiceToRemove);
		verify(invoiceRepository).delete(invoiceToRemove.getId());
	}
	
	@Test
	public void testRemoveInvoiceWhenInvoiceNotExistingInDatabase() {
		Invoice invoiceToRemove=new Invoice("1",CLIENT_FIXTURE, new Date(), 10);
		when(clientRepository.findById(CLIENT_FIXTURE.getId())).thenReturn(CLIENT_FIXTURE);
		when(invoiceRepository.findById(invoiceToRemove.getId())).thenReturn(null);
		try {
			invoiceService.removeInvoice(invoiceToRemove);
			fail("Expected a InvoiceNotFoundException to be thrown");
		}
		catch(InvoiceNotFoundException e) {
			assertThat("La fattura con id "+invoiceToRemove.getId()+" non è presente nel database")
						.isEqualTo(e.getMessage());
		}
	}
	
	@Test
	public void testRemoveInvoiceWhenClientNotExistingInDatabase() {
		Invoice invoiceToRemove=new Invoice("1",CLIENT_FIXTURE, new Date(), 10);
		when(clientRepository.findById(CLIENT_FIXTURE.getId())).thenReturn(null);
		try {
			invoiceService.removeInvoice(invoiceToRemove);
			fail("Expected a ClientNotFoundException to be thrown");
		}
		catch(ClientNotFoundException e) {
			assertThat("Il cliente con id "+CLIENT_FIXTURE.getId()+" non è presente nel database")
						.isEqualTo(e.getMessage());
		}
	}
}
