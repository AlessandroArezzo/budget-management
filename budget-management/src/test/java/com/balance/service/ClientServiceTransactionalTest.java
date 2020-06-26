package com.balance.service;

import static org.assertj.core.api.Assertions.assertThat;
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

import com.balance.model.Client;
import com.balance.repository.ClientRepository;
import com.balance.repository.InvoiceRepository;
import com.balance.repository.RepositoryFactory;
import com.balance.repository.TypeRepository;
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
		doReturn(clientRepository).when(repositoryFactory).createRepository(TypeRepository.CLIENT);
		doReturn(invoiceRepository).when(repositoryFactory).createRepository(TypeRepository.INVOICE);
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
	public void testDeleteClient() {
		String idClientToRemove="1";
		clientService.removeClient(idClientToRemove);
		InOrder inOrder = Mockito.inOrder(clientRepository, invoiceRepository);
		inOrder.verify(invoiceRepository).deleteAllInvoicesByClient(idClientToRemove);
		inOrder.verify(clientRepository).delete(idClientToRemove);
	}
}
