
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.JList;
import javax.swing.JComboBox;

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
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		JLabel lblManagement = new JLabel("GESTIONE RICAVI");
		GridBagConstraints gbc_lblManagement = new GridBagConstraints();
		gbc_lblManagement.insets = new Insets(0, 0, 5, 5);
		gbc_lblManagement.gridx = 6;
		gbc_lblManagement.gridy = 0;
		contentPane.add(lblManagement, gbc_lblManagement);
		
		JLabel lblClient = new JLabel("CLIENTI");
		GridBagConstraints gbc_lblClient = new GridBagConstraints();
		gbc_lblClient.insets = new Insets(0, 0, 5, 5);
		gbc_lblClient.gridx = 0;
		gbc_lblClient.gridy = 1;
		contentPane.add(lblClient, gbc_lblClient);
		
		JLabel lblInvoice = new JLabel("FATTURE");
		GridBagConstraints gbc_lblInvoice = new GridBagConstraints();
		gbc_lblInvoice.insets = new Insets(0, 0, 5, 5);
		gbc_lblInvoice.gridx = 6;
		gbc_lblInvoice.gridy = 2;
		contentPane.add(lblInvoice, gbc_lblInvoice);
		
		clientListModel=new DefaultListModel<>();
		comboboxYearsModel=new DefaultComboBoxModel<>();
	    comboboxYears = new JComboBox<>(comboboxYearsModel);
		comboboxYears.setName("yearsCombobox");
		GridBagConstraints gbc_comboboxYears = new GridBagConstraints();
		gbc_comboboxYears.insets = new Insets(0, 0, 5, 5);
		gbc_comboboxYears.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboboxYears.gridx = 6;
		gbc_comboboxYears.gridy = 3;
		contentPane.add(comboboxYears, gbc_comboboxYears);
		listClients = new JList<>(clientListModel);
		listClients.addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting() && listClients.getSelectedValue()!=null) {
				balanceController.allInvoicesByClientAndYear(
						listClients.getSelectedValue(), (int) comboboxYears.getSelectedItem());
				balanceController.annualClientRevenue(
						listClients.getSelectedValue(), (int) comboboxYears.getSelectedItem());
			}
		});
		listClients.setName("clientsList");
		listClients.setSize(new Dimension(300, 400));
		listClients.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		GridBagConstraints gbc_listClients = new GridBagConstraints();
		gbc_listClients.insets = new Insets(0, 0, 5, 5);
		gbc_listClients.fill = GridBagConstraints.BOTH;
		gbc_listClients.gridx = 0;
		gbc_listClients.gridy = 4;
		JScrollPane scrollPaneClientsList = new JScrollPane();
		scrollPaneClientsList.setPreferredSize(new Dimension(200, 100));
		scrollPaneClientsList.setViewportView(listClients);
		contentPane.add(scrollPaneClientsList,gbc_listClients);
		
		invoiceListModel=new DefaultListModel<>();
		listInvoices = new JList<>(invoiceListModel);
		listInvoices.setName("invoicesList");
		listInvoices.setSize(new Dimension(300, 400));
		listInvoices.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		GridBagConstraints gbc_listInvoices = new GridBagConstraints();
		gbc_listInvoices.insets = new Insets(0, 0, 5, 5);
		gbc_listInvoices.fill = GridBagConstraints.BOTH;
		gbc_listInvoices.gridx = 6;
		gbc_listInvoices.gridy = 4;
		JScrollPane scrollPaneInvoicesList = new JScrollPane();
		scrollPaneInvoicesList.setPreferredSize(new Dimension(200, 100));
		scrollPaneInvoicesList.setViewportView(listInvoices);
		contentPane.add(scrollPaneInvoicesList, gbc_listInvoices);
		
		comboboxClientsModel=new DefaultComboBoxModel<>();
		comboBoxClients = new JComboBox<>(comboboxClientsModel);
		comboBoxClients.setName("clientsCombobox");
		GridBagConstraints gbc_comboBoxClients = new GridBagConstraints();
		gbc_comboBoxClients.insets = new Insets(0, 0, 5, 5);
		gbc_comboBoxClients.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBoxClients.gridx = 6;
		gbc_comboBoxClients.gridy = 5;
		contentPane.add(comboBoxClients, gbc_comboBoxClients);
		
		lbClientError = new JLabel("");
		lbClientError.setName("labelClientErrorMessage");
		GridBagConstraints gbc_lbClientError = new GridBagConstraints();
		gbc_lbClientError.insets = new Insets(0, 0, 5, 5);
		gbc_lbClientError.gridx = 0;
		gbc_lbClientError.gridy = 6;
		contentPane.add(lbClientError, gbc_lbClientError);
		
		lblRevenue = new JLabel("");
		lblRevenue.setName("revenueLabel");
		GridBagConstraints gbc_lblRevenue = new GridBagConstraints();
		gbc_lblRevenue.insets = new Insets(0, 0, 0, 5);
		gbc_lblRevenue.gridx = 6;
		gbc_lblRevenue.gridy = 7;
		contentPane.add(lblRevenue, gbc_lblRevenue);
		
		
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

}
