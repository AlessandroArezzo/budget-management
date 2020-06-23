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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.balance.model.Client;
import com.balance.repository.ClientRepository;
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
	
	@Before
	public void init(){
		MockitoAnnotations.initMocks(this); 
		when(transactionManager.doInTransaction(
				any())).thenAnswer(answer((TransactionCode<?> code) -> code.apply(repositoryFactory)));
		doReturn(clientRepository).when(repositoryFactory).createRepository(TypeRepository.CLIENT);
	}
	
	@Test
	public void testFindAllClients() {
		List<Client> clients = Arrays.asList(new Client());
		when(clientRepository.findAll()).thenReturn(clients);
		assertThat(clientService.findAllClients())
			.isEqualTo(clients);
	}
	
}
