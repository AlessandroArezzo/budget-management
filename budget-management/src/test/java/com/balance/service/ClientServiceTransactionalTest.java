package com.balance.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.AdditionalAnswers.answer;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.balance.exception.ClientNotFoundException;
import com.balance.model.Client;
import com.balance.repository.ClientRepository;
import com.balance.repository.InvoiceRepository;
import com.balance.repository.RepositoryFactory;
import com.balance.transaction.TransactionCode;
import com.balance.transaction.TransactionManager;

public class ClientServiceTransactionalTest {
	
	@InjectMocks
	private ClientServiceTransactional clientService;
	
	@Mock
	private TransactionManager transactionManager;
	
	@Mock
	private RepositoryFactory repositoryFactory;
	
	@Mock
	private ClientRepository clientRepository;
	
	@Mock
	private InvoiceRepository invoiceRepository;
	
	@Before
	public void init(){
		MockitoAnnotations.initMocks(this); 
		when(transactionManager.doInTransaction(
				any())).thenAnswer(answer((TransactionCode<?> code) -> code.apply(repositoryFactory)));
		doReturn(clientRepository).when(repositoryFactory).createClientRepository();
		doReturn(invoiceRepository).when(repositoryFactory).createInvoiceRepository();
	}
	
	@Test
	public void testFindAllClients() {
		List<Client> clients = Arrays.asList(new Client());
		when(clientRepository.findAll()).thenReturn(clients);
		assertThat(clientService.findAllClients())
			.isEqualTo(clients);
	}
	
	@Test
	public void testAddClient() {
		Client clientToAdd=new Client("test identifier");
		assertThat(clientService.addClient(clientToAdd)).isEqualTo(
			new Client("test identifier"));
		verify(clientRepository).save(clientToAdd);
	}
	
	@Test
	public void testDeleteClientWhenClientIsPresentInDatabase() {
		String idClientToRemove="1";
		when(clientRepository.findById(idClientToRemove)).thenReturn(new Client(idClientToRemove,"test identifier"));
		clientService.removeClient(idClientToRemove);
		InOrder inOrder = Mockito.inOrder(clientRepository, invoiceRepository);
		inOrder.verify(invoiceRepository).deleteAllInvoicesByClient(idClientToRemove);
		inOrder.verify(clientRepository).delete(idClientToRemove);
	}
	
	@Test
	public void testDeleteClientWhenClientIsNotPresentInDatabase() {
		String idClientToRemove="1";
		when(clientRepository.findById(idClientToRemove))
			.thenReturn(null);
		try {
			clientService.removeClient(idClientToRemove);
			fail("Excepted a ClientNotFoundException to be thrown");
		}
		catch(ClientNotFoundException e) {
			assertThat("Il cliente con id "+idClientToRemove+" non è presente nel database")
			.isEqualTo(e.getMessage());
		}
	}
	
}
