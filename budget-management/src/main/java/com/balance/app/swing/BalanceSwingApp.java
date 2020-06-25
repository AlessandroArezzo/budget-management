package com.balance.app.swing;

import java.awt.EventQueue;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.balance.controller.BalanceController;
import com.balance.service.ClientService;
import com.balance.service.ClientServiceTransactional;
import com.balance.service.InvoiceService;
import com.balance.service.InvoiceServiceTransactional;
import com.balance.transaction.TransactionManager;
import com.balance.transaction.mongodb.TransactionMongoManager;
import com.balance.view.swing.BalanceSwingView;
import com.mongodb.MongoClient;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(mixinStandardHelpOptions =true)
public class BalanceSwingApp implements Callable<Void>{
	
	@Option(names= { "--mongo-host" }, description= "MongoDB host address")
	private String mongoHost = "localhost";
	
	@Option(names= { "--mongo-port" }, description= "MongoDB host port")
	private int mongoPort = 27017;
	
	@Option(names= { "--db-name" }, description= "Database name")
	private String databaseName = "balance";
	
	@Option(names= { "--collection-clients-name" }, description= "Collection clients name")
	private String collectionClientsName = "client";
	
	@Option(names= { "--collection-invoices-name" }, description= "Collection invoices name")
	private String collectionInvoicesName = "invoice";
	
	
	public static void main(String[] args) {
		new CommandLine(new BalanceSwingApp()).execute(args);
	}

	@Override
	public Void call() throws Exception {
		EventQueue.invokeLater(() -> {
			try {
				TransactionManager transactionManager=new TransactionMongoManager(
						new MongoClient(mongoHost,mongoPort), databaseName, collectionClientsName,
						collectionInvoicesName);
				ClientService clientService=new ClientServiceTransactional(transactionManager);
				InvoiceService invoiceService=new InvoiceServiceTransactional(transactionManager);
				BalanceSwingView balanceView = new BalanceSwingView();
				BalanceController balanceController=new BalanceController(
						balanceView,clientService,invoiceService );
				balanceView.setBalanceController(balanceController);
				balanceView.setVisible(true);
				balanceController.initializeView();
			}
			catch(Exception e) {
				Logger.getLogger(getClass().getName())
				.log(Level.SEVERE,"Exception",e); 
			}
		});
		return null;
	}
}
