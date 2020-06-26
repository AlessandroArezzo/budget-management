
package com.balance.view.swing;

import java.awt.Dimension;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import com.balance.controller.BalanceController;
import com.balance.model.Client;
import com.balance.model.Invoice;
import com.balance.view.BalanceView;

import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.JList;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JTextField;

public class BalanceSwingView extends JFrame implements BalanceView {

	private static final long serialVersionUID = 1L;
	private static final int CURRENT_YEAR=Calendar.getInstance().get(Calendar.YEAR);

	private transient BalanceController balanceController;
	
	private JPanel contentPane;
	private JList<Client> listClients;
	private DefaultListModel<Client> clientListModel;
	private JList<Invoice> listInvoices;
	private DefaultListModel<Invoice> invoiceListModel;
	private JComboBox<Integer> comboboxYears;
	private DefaultComboBoxModel<Integer> comboboxYearsModel;
	private JLabel lblRevenue;
	private JLabel lbClientError;
	private JComboBox<Client> comboBoxClients;
	private DefaultComboBoxModel<Client> comboboxClientsModel;
	private JButton btnShowAllInvoices;
	private JLabel lblNewClient;
	private JLabel lblClientName;
	private JTextField textFieldNewClient;
	private JButton btnNewClient;
	private JButton btnRemoveClient;

	public void setBalanceController(BalanceController balanceController) {
		this.balanceController = balanceController;
	}

	/**
	 * Create the frame.
	 */
	public BalanceSwingView() {
		setTitle("Budget Management View");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setBounds(100, 100, 450, 300);
		setResizable(false);
		setSize(500,600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		JLabel lblManagement = new JLabel("GESTIONE RICAVI");
		GridBagConstraints gbc_lblManagement = new GridBagConstraints();
		gbc_lblManagement.insets = new Insets(0, 0, 5, 5);
		gbc_lblManagement.gridx = 9;
		gbc_lblManagement.gridy = 0;
		contentPane.add(lblManagement, gbc_lblManagement);
		
		JLabel lblClient = new JLabel("CLIENTI");
		GridBagConstraints gbc_lblClient = new GridBagConstraints();
		gbc_lblClient.gridwidth = 4;
		gbc_lblClient.insets = new Insets(0, 0, 5, 5);
		gbc_lblClient.gridx = 0;
		gbc_lblClient.gridy = 1;
		contentPane.add(lblClient, gbc_lblClient);
		
		JLabel lblInvoice = new JLabel("FATTURE");
		GridBagConstraints gbc_lblInvoice = new GridBagConstraints();
		gbc_lblInvoice.insets = new Insets(0, 0, 5, 5);
		gbc_lblInvoice.gridx = 9;
		gbc_lblInvoice.gridy = 2;
		contentPane.add(lblInvoice, gbc_lblInvoice);
		
		clientListModel=new DefaultListModel<>();
		comboboxYearsModel=new DefaultComboBoxModel<>();
	    comboboxYears = new JComboBox<>(comboboxYearsModel);
		comboboxYears.setName("yearsCombobox");
		GridBagConstraints gbc_comboboxYears = new GridBagConstraints();
		gbc_comboboxYears.insets = new Insets(0, 0, 5, 5);
		gbc_comboboxYears.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboboxYears.gridx = 9;
		gbc_comboboxYears.gridy = 3;
		contentPane.add(comboboxYears, gbc_comboboxYears);
		
		invoiceListModel=new DefaultListModel<>();
		listClients = new JList<>(clientListModel);
		listClients.addListSelectionListener(e -> {
			btnShowAllInvoices.setVisible(
					listClients.getSelectedIndex() != -1);
			btnRemoveClient.setEnabled(
					listClients.getSelectedIndex() != -1); 
			if (!e.getValueIsAdjusting() && comboboxYears.getSelectedItem()!=null) {
				int yearSelected=(int) comboboxYears.getSelectedItem();
				if(listClients.getSelectedValue()!=null ) {
					balanceController.allInvoicesByClientAndYear(
							listClients.getSelectedValue(), yearSelected);
					balanceController.annualClientRevenue(
							listClients.getSelectedValue(), yearSelected);
				}
				else {
					balanceController.allInvoicesByYear(yearSelected);
					balanceController.annualRevenue(yearSelected);
				}
			}
		});
		listClients.setName("clientsList");
		listClients.setSize(new Dimension(300, 400));
		listClients.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		GridBagConstraints gbc_listClients = new GridBagConstraints();
		gbc_listClients.insets = new Insets(0, 0, 5, 5);
		gbc_listClients.fill = GridBagConstraints.BOTH;
		gbc_listClients.gridx = 1;
		gbc_listClients.gridy = 4;
		JScrollPane scrollPaneClientsList = new JScrollPane();
		scrollPaneClientsList.setViewportView(listClients);
		contentPane.add(scrollPaneClientsList,gbc_listClients);
		listInvoices = new JList<>(invoiceListModel);
		listInvoices.setName("invoicesList");
		listInvoices.setSize(new Dimension(300, 400));
		listInvoices.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		GridBagConstraints gbc_listInvoices = new GridBagConstraints();
		gbc_listInvoices.insets = new Insets(0, 0, 5, 5);
		gbc_listInvoices.fill = GridBagConstraints.BOTH;
		gbc_listInvoices.gridx = 9;
		gbc_listInvoices.gridy = 4;
		JScrollPane scrollPaneInvoicesList = new JScrollPane();
		scrollPaneInvoicesList.setPreferredSize(new Dimension(200, 100));
		scrollPaneInvoicesList.setViewportView(listInvoices);
		contentPane.add(scrollPaneInvoicesList, gbc_listInvoices);
		
		comboboxClientsModel=new DefaultComboBoxModel<>();
		
		btnShowAllInvoices = new JButton("Vedi tutte le fatture");
		btnShowAllInvoices.setVisible(false);
		GridBagConstraints gbc_btnShowAllInvoices = new GridBagConstraints();
		gbc_btnShowAllInvoices.insets = new Insets(0, 0, 5, 5);
		gbc_btnShowAllInvoices.gridx = 9;
		gbc_btnShowAllInvoices.gridy = 5;
		contentPane.add(btnShowAllInvoices, gbc_btnShowAllInvoices);
		comboBoxClients = new JComboBox<>(comboboxClientsModel);
		comboBoxClients.setName("clientsCombobox");
		GridBagConstraints gbc_comboBoxClients = new GridBagConstraints();
		gbc_comboBoxClients.insets = new Insets(0, 0, 5, 5);
		gbc_comboBoxClients.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBoxClients.gridx = 9;
		gbc_comboBoxClients.gridy = 6;
		contentPane.add(comboBoxClients, gbc_comboBoxClients);
		
		btnRemoveClient=new JButton("Rimuovi cliente");
		btnRemoveClient.setEnabled(false);
		btnRemoveClient.setName("btnRemoveClient");
		GridBagConstraints gbc_btnRemoveClient = new GridBagConstraints();
		gbc_btnRemoveClient.gridwidth = 2;
		gbc_btnRemoveClient.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnRemoveClient.insets = new Insets(0, 0, 0, 5);
		gbc_btnRemoveClient.gridx = 1;
		gbc_btnRemoveClient.gridy = 7;
		contentPane.add(btnRemoveClient, gbc_btnRemoveClient);
		
		lbClientError = new JLabel("");
		lbClientError.setName("labelClientErrorMessage");
		GridBagConstraints gbc_lbClientError = new GridBagConstraints();
		gbc_lbClientError.insets = new Insets(0, 0, 5, 5);
		gbc_lbClientError.gridx = 0;
		gbc_lbClientError.gridy = 8;
		contentPane.add(lbClientError, gbc_lbClientError);
		
		lblRevenue = new JLabel("");
		lblRevenue.setName("revenueLabel");
		GridBagConstraints gbc_lblRevenue = new GridBagConstraints();
		gbc_lblRevenue.insets = new Insets(0, 0, 5, 5);
		gbc_lblRevenue.gridx = 9;
		gbc_lblRevenue.gridy = 8;
		contentPane.add(lblRevenue, gbc_lblRevenue);
		
		lblNewClient=new JLabel("INSERISCI UN NUOVO CLIENTE");
		GridBagConstraints gbc_lblNewClient = new GridBagConstraints();
		gbc_lblNewClient.gridwidth = 9;
		gbc_lblNewClient.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblNewClient.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewClient.gridx = 0;
		gbc_lblNewClient.gridy = 9;
		contentPane.add(lblNewClient, gbc_lblNewClient);
		
		lblClientName=new JLabel("Identificativo");
		GridBagConstraints gbc_lblClientName = new GridBagConstraints();
		gbc_lblClientName.gridwidth = 5;
		gbc_lblClientName.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblClientName.insets = new Insets(0, 0, 0, 5);
		gbc_lblClientName.gridx = 0;
		gbc_lblClientName.gridy = 10;
		contentPane.add(lblClientName, gbc_lblClientName);
		
		textFieldNewClient=new JTextField();
		textFieldNewClient.setName("textField_clientName");
		textFieldNewClient.setColumns(4);
		GridBagConstraints gbc_txtFieldClientName = new GridBagConstraints();
		gbc_txtFieldClientName.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtFieldClientName.gridwidth = 3;
		gbc_txtFieldClientName.insets = new Insets(0, 0, 0, 5);
		gbc_txtFieldClientName.gridx = 4;
		gbc_txtFieldClientName.gridy = 10;
		contentPane.add(textFieldNewClient, gbc_txtFieldClientName);
		
		btnNewClient=new JButton("Aggiungi cliente");
		btnNewClient.setEnabled(false);
		btnNewClient.setName("btnAddClient");
		GridBagConstraints gbc_btnNewClient = new GridBagConstraints();
		gbc_btnNewClient.gridwidth = 2;
		gbc_btnNewClient.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnNewClient.insets = new Insets(0, 0, 0, 5);
		gbc_btnNewClient.gridx = 1;
		gbc_btnNewClient.gridy = 12;
		contentPane.add(btnNewClient, gbc_btnNewClient);
		
		comboboxYears.addActionListener(
					e -> {
					int yearSelected=(int) comboboxYears.getSelectedItem();
					Client clientSelected=listClients.getSelectedValue();
					if(clientSelected==null) {
				    	balanceController.allInvoicesByYear(yearSelected);
				    	balanceController.annualRevenue(yearSelected);
					}
					else {
						balanceController.allInvoicesByClientAndYear(clientSelected, yearSelected);
						balanceController.annualClientRevenue(clientSelected, yearSelected);
						}
					
			});
		
		btnShowAllInvoices.addActionListener(
				e -> {
					btnShowAllInvoices.setVisible(false);
					listClients.clearSelection();
				});
		
		KeyAdapter btnAddClientEnabler= new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				btnNewClient.setEnabled(
						!textFieldNewClient.getText().trim().isEmpty()
				); 
			}
		};
		btnNewClient.addActionListener(
				e -> balanceController.newClient(new Client(textFieldNewClient.getText()))
			);
		
		btnRemoveClient.addActionListener(
				e -> balanceController.deleteClient(listClients.getSelectedValue())
			);
		textFieldNewClient.addKeyListener(btnAddClientEnabler);
	}


	@Override
	public void showClients(List<Client> clients) {
		clients.stream().forEach(clientListModel::addElement);
		clients.stream().forEach(comboboxClientsModel::addElement);
	}


	@Override
	public void showInvoices(List<Invoice> invoices) {
		invoiceListModel.removeAllElements();
		invoices.stream().forEach(invoiceListModel::addElement); 
	}


	@Override
	public void setAnnualTotalRevenue(int year, double totalRevenue) {
		lblRevenue.setText("Il ricavo totale del "+year+" è di "+String.format("%.2f", totalRevenue)+"€");
	}

	@Override
	public void setChoiceYearInvoices(List<Integer> yearsOfTheInvoices) {
		List<Integer> yearsToAddInModel=new ArrayList<>(yearsOfTheInvoices);
		if(!yearsToAddInModel.contains(CURRENT_YEAR)) {
			yearsToAddInModel.add(CURRENT_YEAR);
		}
		yearsToAddInModel.stream().forEach(comboboxYearsModel::addElement); 
	}
	
	public DefaultComboBoxModel<Integer> getComboboxYearsModel() {
		return comboboxYearsModel;
	}
	
	public DefaultListModel<Invoice> getInvoiceListModel() {
		return invoiceListModel;
	}
	
	public DefaultListModel<Client> getClientListModel() {
		return clientListModel;
	}
	
	public DefaultComboBoxModel<Client> getComboboxClientsModel() {
		return comboboxClientsModel;
	}

	@Override
	public void setYearSelected(int year) {
		if(comboboxYearsModel.getIndexOf(year)!=-1) {
			comboboxYearsModel.setSelectedItem(year);
		}
	}

	@Override
	public void setAnnualClientRevenue(Client client, int year, double clientRevenue) {
		lblRevenue.setText("Il ricavo totale delle fatture del cliente "+client.getIdentifier()+" "
				+ "nel "+year+" è di "+String.format("%.2f", clientRevenue)+"€");
	}

	@Override
	public void clientRemoved(Client clientToRemove) {
		clientListModel.removeElement(clientToRemove);
		comboboxClientsModel.removeElement(clientToRemove);
	}

	public void showClientError(String message, Client client) {
		lbClientError.setText(message+": "+client.getIdentifier());
	}

	@Override
	public void clientAdded(Client clientToAdd) {
		clientListModel.addElement(clientToAdd);
		comboboxClientsModel.addElement(clientToAdd);
		resetTextBoxNewClient();
		resetClientErrorLabel();
	}
	
	private void resetClientErrorLabel() {
		lbClientError.setText("");
	}
	
	private void resetTextBoxNewClient() {
		textFieldNewClient.setText("");
		btnNewClient.setEnabled(false);
	}

}
