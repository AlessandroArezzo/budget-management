
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
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.JList;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JTextField;
import java.awt.Component;

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
	
	private JTextField textFieldDayNewInvoice;
	private JTextField textFieldMonthNewInvoice;
	private JTextField textFieldYearNewInvoice;
	private JTextField textFieldRevenueNewInvoice;
	private JButton btnNewInvoice;
	private JLabel lblInvoiceError;
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
		setSize(800,500);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		JLabel lblManagement = new JLabel("GESTIONE RICAVI");
		GridBagConstraints gbc_lblManagement = new GridBagConstraints();
		gbc_lblManagement.insets = new Insets(0, 0, 5, 5);
		gbc_lblManagement.gridx = 10;
		gbc_lblManagement.gridy = 0;
		contentPane.add(lblManagement, gbc_lblManagement);
		
		JLabel lblClient = new JLabel("CLIENTI");
		GridBagConstraints gbc_lblClient = new GridBagConstraints();
		gbc_lblClient.gridwidth = 5;
		gbc_lblClient.insets = new Insets(0, 0, 5, 5);
		gbc_lblClient.gridx = 0;
		gbc_lblClient.gridy = 1;
		contentPane.add(lblClient, gbc_lblClient);
		
		JLabel lblInvoice = new JLabel("FATTURE");
		GridBagConstraints gbc_lblInvoice = new GridBagConstraints();
		gbc_lblInvoice.insets = new Insets(0, 0, 5, 5);
		gbc_lblInvoice.gridx = 10;
		gbc_lblInvoice.gridy = 2;
		contentPane.add(lblInvoice, gbc_lblInvoice);
		
		clientListModel=new DefaultListModel<>();
		comboboxYearsModel=new DefaultComboBoxModel<>();
	    comboboxYears = new JComboBox<>(comboboxYearsModel);
		comboboxYears.setName("yearsCombobox");
		GridBagConstraints gbc_comboboxYears = new GridBagConstraints();
		gbc_comboboxYears.insets = new Insets(0, 0, 5, 5);
		gbc_comboboxYears.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboboxYears.gridx = 10;
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
				}
				else {
					balanceController.allInvoicesByYear(yearSelected);
				}
			}
		});
		listClients.setName("clientsList");
		listClients.setSize(new Dimension(300, 400));
		listClients.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		GridBagConstraints gbc_listClients = new GridBagConstraints();
		gbc_listClients.gridwidth = 6;
		gbc_listClients.insets = new Insets(0, 0, 5, 5);
		gbc_listClients.fill = GridBagConstraints.BOTH;
		gbc_listClients.gridx = 0;
		gbc_listClients.gridy = 4;
		JScrollPane scrollPaneClientsList = new JScrollPane();
		scrollPaneClientsList.setViewportView(listClients);
		contentPane.add(scrollPaneClientsList,gbc_listClients);
		listInvoices = new JList<>(invoiceListModel);
		listInvoices.setName("invoicesList");
		listInvoices.setSize(new Dimension(100, 400));
		listInvoices.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		GridBagConstraints gbc_listInvoices = new GridBagConstraints();
		gbc_listInvoices.gridwidth = 13;
		gbc_listInvoices.insets = new Insets(0, 0, 5, 5);
		gbc_listInvoices.fill = GridBagConstraints.BOTH;
		gbc_listInvoices.gridx = 10;
		gbc_listInvoices.gridy = 4;
		JScrollPane scrollPaneInvoicesList = new JScrollPane();
		scrollPaneInvoicesList.setPreferredSize(new Dimension(200, 100));
		scrollPaneInvoicesList.setViewportView(listInvoices);
		contentPane.add(scrollPaneInvoicesList, gbc_listInvoices);
		
		lblInvoiceError = new JLabel("");
		lblInvoiceError.setName("labelInvoiceErrorMessage");
		GridBagConstraints gbc_lblInvoiceErrorMessage = new GridBagConstraints();
		gbc_lblInvoiceErrorMessage.gridwidth = 13;
		gbc_lblInvoiceErrorMessage.insets = new Insets(0, 0, 5, 5);
		gbc_lblInvoiceErrorMessage.gridx = 10;
		gbc_lblInvoiceErrorMessage.gridy = 6;
		contentPane.add(lblInvoiceError, gbc_lblInvoiceErrorMessage);
		
		btnRemoveClient=new JButton("Rimuovi cliente");
		btnRemoveClient.setEnabled(false);
		btnRemoveClient.setName("btnRemoveClient");
		GridBagConstraints gbc_btnRemoveClient = new GridBagConstraints();
		gbc_btnRemoveClient.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnRemoveClient.insets = new Insets(0, 0, 5, 5);
		gbc_btnRemoveClient.gridx = 0;
		gbc_btnRemoveClient.gridy = 8;
		contentPane.add(btnRemoveClient, gbc_btnRemoveClient);
		
		btnRemoveClient.addActionListener(
				e -> balanceController.deleteClient(listClients.getSelectedValue())
			);
		
		JLabel lblnewInvoice=new JLabel("INSERISCI UNA NUOVA FATTURA");
		GridBagConstraints gbc_lblNewInvoice = new GridBagConstraints();
		gbc_lblNewInvoice.gridwidth = 13;
		gbc_lblNewInvoice.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewInvoice.gridx = 10;
		gbc_lblNewInvoice.gridy = 10;
		contentPane.add(lblnewInvoice, gbc_lblNewInvoice);
		
		JLabel lblClientNewInvoice=new JLabel("Cliente");
		GridBagConstraints gbc_lblClientNewInvoice = new GridBagConstraints();
		gbc_lblClientNewInvoice.insets = new Insets(0, 0, 5, 5);
		gbc_lblClientNewInvoice.gridx = 9;
		gbc_lblClientNewInvoice.gridy = 11;
		contentPane.add(lblClientNewInvoice, gbc_lblClientNewInvoice);
		comboboxClientsModel=new DefaultComboBoxModel<>();
		comboBoxClients = new JComboBox<>(comboboxClientsModel);
		comboBoxClients.setName("clientsCombobox");
		
		GridBagConstraints gbc_comboBoxClients = new GridBagConstraints();
		gbc_comboBoxClients.gridwidth = 13;
		gbc_comboBoxClients.insets = new Insets(0, 0, 5, 5);
		gbc_comboBoxClients.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBoxClients.gridx = 10;
		gbc_comboBoxClients.gridy = 11;
		contentPane.add(comboBoxClients, gbc_comboBoxClients);
		
		lblClientName=new JLabel("Identificativo");
		GridBagConstraints gbc_lblClientName = new GridBagConstraints();
		gbc_lblClientName.insets = new Insets(0, 0, 5, 5);
		gbc_lblClientName.gridx = 0;
		gbc_lblClientName.gridy = 12;
		contentPane.add(lblClientName, gbc_lblClientName);
		
		textFieldNewClient=new JTextField();
		textFieldNewClient.setName("textField_clientName");
		textFieldNewClient.setColumns(4);
		GridBagConstraints gbc_txtFieldClientName = new GridBagConstraints();
		gbc_txtFieldClientName.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtFieldClientName.gridwidth = 5;
		gbc_txtFieldClientName.insets = new Insets(0, 0, 5, 5);
		gbc_txtFieldClientName.gridx = 1;
		gbc_txtFieldClientName.gridy = 12;
		contentPane.add(textFieldNewClient, gbc_txtFieldClientName);
		
		JLabel lblDateNewInvoice=new JLabel("Data");
		lblDateNewInvoice.setMaximumSize(new Dimension(10, 16));
		lblDateNewInvoice.setMinimumSize(new Dimension(10, 16));
		lblDateNewInvoice.setAlignmentY(Component.TOP_ALIGNMENT);
		lblDateNewInvoice.setBorder(null);
		GridBagConstraints gbc_lblDateNewInvoice = new GridBagConstraints();
		gbc_lblDateNewInvoice.insets = new Insets(0, 0, 5, 5);
		gbc_lblDateNewInvoice.gridx = 9;
		gbc_lblDateNewInvoice.gridy = 12;
		contentPane.add(lblDateNewInvoice, gbc_lblDateNewInvoice);
		
		textFieldDayNewInvoice=new JTextField();
		textFieldDayNewInvoice.setMinimumSize(new Dimension(50, 26));
		textFieldDayNewInvoice.setName("textField_dayOfDateInvoice");
		GridBagConstraints gbc_txtFieldDayNewInvoice = new GridBagConstraints();
		gbc_txtFieldDayNewInvoice.insets = new Insets(0, 0, 5, 5);
		gbc_txtFieldDayNewInvoice.gridx = 10;
		gbc_txtFieldDayNewInvoice.gridy = 12;
		contentPane.add(textFieldDayNewInvoice, gbc_txtFieldDayNewInvoice);
		textFieldMonthNewInvoice=new JTextField();
		textFieldMonthNewInvoice.setName("textField_monthOfDateInvoice");
		
		JLabel lblSlashDate1NewInvoice=new JLabel("/");
		GridBagConstraints gbc_lblSlashDate1NewInvoice = new GridBagConstraints();
		gbc_lblSlashDate1NewInvoice.insets = new Insets(0, 0, 5, 5);
		gbc_lblSlashDate1NewInvoice.gridx = 11;
		gbc_lblSlashDate1NewInvoice.gridy = 12;
		contentPane.add(lblSlashDate1NewInvoice, gbc_lblSlashDate1NewInvoice);
		textFieldMonthNewInvoice.setName("textField_monthOfDateInvoice");
		
		GridBagConstraints gbc_txtFieldMonthNewInvoice = new GridBagConstraints();
		gbc_txtFieldMonthNewInvoice.gridwidth = 3;
		gbc_txtFieldMonthNewInvoice.insets = new Insets(0, 0, 5, 5);
		gbc_txtFieldMonthNewInvoice.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtFieldMonthNewInvoice.gridx = 12;
		gbc_txtFieldMonthNewInvoice.gridy = 12;
		contentPane.add(textFieldMonthNewInvoice, gbc_txtFieldMonthNewInvoice);
		
		JLabel lblSlashDate2NewInvoice=new JLabel("/");
		GridBagConstraints gbc_lblSlashDate2NewInvoice = new GridBagConstraints();
		gbc_lblSlashDate2NewInvoice.insets = new Insets(0, 0, 5, 5);
		gbc_lblSlashDate2NewInvoice.gridx = 15;
		gbc_lblSlashDate2NewInvoice.gridy = 12;
		contentPane.add(lblSlashDate2NewInvoice, gbc_lblSlashDate2NewInvoice);
		
		textFieldYearNewInvoice=new JTextField();
		textFieldYearNewInvoice.setName("textField_yearOfDateInvoice");
		GridBagConstraints gbc_txtFieldYearNewInvoice = new GridBagConstraints();
		gbc_txtFieldYearNewInvoice.gridwidth = 2;
		gbc_txtFieldYearNewInvoice.insets = new Insets(0, 0, 5, 0);
		gbc_txtFieldYearNewInvoice.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtFieldYearNewInvoice.gridx = 35;
		gbc_txtFieldYearNewInvoice.gridy = 12;
		contentPane.add(textFieldYearNewInvoice, gbc_txtFieldYearNewInvoice);
		
		btnNewClient=new JButton("Aggiungi cliente");
		btnNewClient.setEnabled(false);
		btnNewClient.setName("btnAddClient");
		GridBagConstraints gbc_btnNewClient = new GridBagConstraints();
		gbc_btnNewClient.gridwidth = 6;
		gbc_btnNewClient.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnNewClient.insets = new Insets(0, 0, 5, 5);
		gbc_btnNewClient.gridx = 0;
		gbc_btnNewClient.gridy = 13;
		contentPane.add(btnNewClient, gbc_btnNewClient);
		btnNewClient.addActionListener(
				e -> balanceController.newClient(new Client(textFieldNewClient.getText()))
			);
		
		JLabel lblRevenueNewInvoice=new JLabel("Importo (€)");
		GridBagConstraints gbc_lblRevenueNewInvoice = new GridBagConstraints();
		gbc_lblRevenueNewInvoice.insets = new Insets(0, 0, 5, 5);
		gbc_lblRevenueNewInvoice.gridx = 9;
		gbc_lblRevenueNewInvoice.gridy = 13;
		contentPane.add(lblRevenueNewInvoice, gbc_lblRevenueNewInvoice);
		
		textFieldRevenueNewInvoice=new JTextField();
		textFieldRevenueNewInvoice.setName("textField_revenueInvoice");
		GridBagConstraints gbc_txtFieldRevenueNewInvoice = new GridBagConstraints();
		gbc_txtFieldRevenueNewInvoice.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtFieldRevenueNewInvoice.insets = new Insets(0, 0, 5, 5);
		gbc_txtFieldRevenueNewInvoice.gridx = 10;
		gbc_txtFieldRevenueNewInvoice.gridy = 13;
		contentPane.add(textFieldRevenueNewInvoice, gbc_txtFieldRevenueNewInvoice);
		
		btnNewInvoice=new JButton("Aggiungi fattura");
		btnNewInvoice.setEnabled(false);
		btnNewInvoice.setName("btnAddInvoice");
		GridBagConstraints gbc_btnAddInvoice = new GridBagConstraints();
		gbc_btnAddInvoice.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnAddInvoice.insets = new Insets(0, 0, 0, 5);
		gbc_btnAddInvoice.gridx = 10;
		gbc_btnAddInvoice.gridy = 14;
		contentPane.add(btnNewInvoice, gbc_btnAddInvoice);
				
		btnShowAllInvoices = new JButton("Vedi tutte le fatture");
		btnShowAllInvoices.setVisible(false);
		GridBagConstraints gbc_btnShowAllInvoices = new GridBagConstraints();
		gbc_btnShowAllInvoices.insets = new Insets(0, 0, 5, 5);
		gbc_btnShowAllInvoices.gridx = 10;
		gbc_btnShowAllInvoices.gridy = 7;
		contentPane.add(btnShowAllInvoices, gbc_btnShowAllInvoices);
		
		lbClientError = new JLabel("");
		lbClientError.setName("labelClientErrorMessage");
		GridBagConstraints gbc_lbClientError = new GridBagConstraints();
		gbc_lbClientError.insets = new Insets(0, 0, 5, 5);
		gbc_lbClientError.gridx = 0;
		gbc_lbClientError.gridy = 9;
		contentPane.add(lbClientError, gbc_lbClientError);
		
		lblRevenue = new JLabel("");
		lblRevenue.setName("revenueLabel");
		GridBagConstraints gbc_lblRevenue = new GridBagConstraints();
		gbc_lblRevenue.insets = new Insets(0, 0, 5, 5);
		gbc_lblRevenue.gridx = 10;
		gbc_lblRevenue.gridy = 9;
		contentPane.add(lblRevenue, gbc_lblRevenue);
		
		lblNewClient=new JLabel("INSERISCI UN NUOVO CLIENTE");
		GridBagConstraints gbc_lblNewClient = new GridBagConstraints();
		gbc_lblNewClient.gridwidth = 7;
		gbc_lblNewClient.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewClient.gridx = 0;
		gbc_lblNewClient.gridy = 10;
		contentPane.add(lblNewClient, gbc_lblNewClient);
		
		comboboxYears.addActionListener(
					e -> {
					if(comboboxYears.getSelectedIndex()!=-1) {
						int yearSelected=(int) comboboxYears.getSelectedItem();
						Client clientSelected=listClients.getSelectedValue();
						if(clientSelected==null) {
					    	balanceController.allInvoicesByYear(yearSelected);
						}
						else {
							balanceController.allInvoicesByClientAndYear(clientSelected, yearSelected);
						}					
					}
			});
		
		
		btnNewInvoice.addActionListener(
				e -> {
					int yearOfDate=Integer.parseInt(textFieldYearNewInvoice.getText());
					int monthOfYear=Integer.parseInt(textFieldMonthNewInvoice.getText());
					int dayOfMonth=Integer.parseInt(textFieldDayNewInvoice.getText());
					double revenueOfInvoice=Double.parseDouble(textFieldRevenueNewInvoice.getText());
					try {
						if(yearOfDate<CURRENT_YEAR-100 || yearOfDate>CURRENT_YEAR) {
							throw new DateTimeException("Wrong year");
						}
						LocalDate localDate = LocalDate.of( yearOfDate, monthOfYear, dayOfMonth);
						Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
						
						Client clientOfInvoice=(Client) comboBoxClients.getSelectedItem();
						balanceController.newInvoice(new Invoice(clientOfInvoice,
								date,revenueOfInvoice));
						resetInvoiceErrorLabel();
						resetTextBoxAndComboBoxNewInvoice();
					}
					catch(DateTimeException ex) {
						lblInvoiceError.setText("La data "+dayOfMonth+"/"+monthOfYear+"/"+yearOfDate+" non è corretta");
						resetTextBoxDateNewInvoice();
						btnNewInvoice.setEnabled(false);
					}
				}
				);
		
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
		
		KeyAdapter btnAddInvoiceEnabler= new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				btnNewInvoice.setEnabled(
						!textFieldDayNewInvoice.getText().trim().isEmpty() &&
						!textFieldMonthNewInvoice.getText().trim().isEmpty() &&
						!textFieldYearNewInvoice.getText().trim().isEmpty() &&
						!textFieldRevenueNewInvoice.getText().trim().isEmpty() &&
						comboBoxClients.getSelectedIndex()!=-1
				); 
			}
		};
		
		textFieldNewClient.addKeyListener(btnAddClientEnabler);
		textFieldDayNewInvoice.addKeyListener(btnAddInvoiceEnabler);
		textFieldMonthNewInvoice.addKeyListener(btnAddInvoiceEnabler);
		textFieldYearNewInvoice.addKeyListener(btnAddInvoiceEnabler);
		textFieldRevenueNewInvoice.addKeyListener(btnAddInvoiceEnabler);
		comboBoxClients.addKeyListener(btnAddInvoiceEnabler);
		textFieldDayNewInvoice.addKeyListener(  new KeyAdapter() {
				@Override
		        public void keyTyped(KeyEvent e) {
					char ch = e.getKeyChar();
		            if (!isNumber(ch) || textFieldDayNewInvoice.getText().length() >= 2 )
		                e.consume();
		        }
			});
		
		textFieldMonthNewInvoice.addKeyListener(  new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				char ch = e.getKeyChar();
	            if (!isNumber(ch) || textFieldMonthNewInvoice.getText().length() >= 2 )
	                e.consume();
	        }
		});
		textFieldYearNewInvoice.addKeyListener(  new KeyAdapter() {
			@Override
	        public void keyTyped(KeyEvent e) {
				char ch = e.getKeyChar();
	            if (!isNumber(ch) || textFieldYearNewInvoice.getText().length() >= 4) 
	                e.consume();
	        }
		});
		textFieldRevenueNewInvoice.addKeyListener(  new KeyAdapter() {
			@Override
	        public void keyTyped(KeyEvent e) {
				char ch = e.getKeyChar();
	            if (!isNumber(ch) && !isDot(ch))
	                e.consume();
	        }
		});
	}
	
	private boolean isNumber(char ch){
        return ch >= '0' && ch <= '9';
    }
	
	private boolean isDot(char ch) {
		return ch == '.';
	}
	
	

	@Override
	public void showClients(List<Client> clients) {
		clientListModel.removeAllElements();
		clients.stream().forEach(clientListModel::addElement);
		clients.stream().forEach(comboboxClientsModel::addElement);
	}


	@Override
	public void showInvoices(List<Invoice> invoices) {
		if((int) comboboxYearsModel.getSelectedItem()!=CURRENT_YEAR && invoices.isEmpty() && 
				listClients.getSelectedValue()==null) {
			balanceController.yearsOfTheInvoices();
		}
		else {
			invoiceListModel.removeAllElements();
			invoices.stream().forEach(invoiceListModel::addElement);
			this.setLabelTotalRevenue();
		}
	}


	@Override
	public void setChoiceYearInvoices(List<Integer> yearsOfTheInvoices) {
		comboboxYearsModel.removeAllElements();
		List<Integer> yearsToAddInModel=new ArrayList<>(yearsOfTheInvoices);
		if(!yearsToAddInModel.contains(CURRENT_YEAR)) {
			yearsToAddInModel.add(CURRENT_YEAR);
		}
		yearsToAddInModel.stream().forEach(comboboxYearsModel::addElement); 
		setCurrentYearSelected();
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
	
	private void setLabelTotalRevenue() {
		int yearSelected=(int) comboboxYears.getSelectedItem();
		Client clientSelected=listClients.getSelectedValue();
		double totalRevenue=0;
		for(int i=0;i<invoiceListModel.getSize();i++) {
			totalRevenue+=invoiceListModel.getElementAt(i).getRevenue();
		}
		if (clientSelected==null) {
			lblRevenue.setText("Il ricavo totale del "+yearSelected+" è di "+String.format("%.2f", totalRevenue)+"€");
		}
		else {
			lblRevenue.setText("Il ricavo totale delle fatture del cliente "+clientSelected.getIdentifier()+" "
					+ "nel "+yearSelected+" è di "+String.format("%.2f", totalRevenue)+"€");
		}
	}

	private void setCurrentYearSelected() {
		comboboxYearsModel.setSelectedItem(CURRENT_YEAR);
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
	
	private void resetInvoiceErrorLabel() {
		lblInvoiceError.setText("");
	}
	
	private void resetTextBoxNewClient() {
		textFieldNewClient.setText("");
		btnNewClient.setEnabled(false);
	}
	
	private void resetTextBoxAndComboBoxNewInvoice() {
		resetTextBoxDateNewInvoice();
		textFieldRevenueNewInvoice.setText("");
		comboBoxClients.setSelectedIndex(-1);
		btnNewInvoice.setEnabled(false);
	}
	
	private void resetTextBoxDateNewInvoice() {
		textFieldDayNewInvoice.setText("");
		textFieldMonthNewInvoice.setText("");
		textFieldYearNewInvoice.setText("");
	}

	@Override
	public void invoiceAdded(Invoice invoiceToAdd) {
		int yearSelected=(int) comboboxYears.getSelectedItem();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(invoiceToAdd.getDate());
		int yearOfInvoice=calendar.get(Calendar.YEAR);
		if(yearOfInvoice==yearSelected) {
			invoiceListModel.addElement(invoiceToAdd);
			this.setLabelTotalRevenue();
		}
		if(comboboxYearsModel.getIndexOf(yearOfInvoice)==-1) {
			comboboxYearsModel.addElement(yearOfInvoice);
		}
		resetInvoiceErrorLabel();
	}

	@Override
	public void removeInvoicesOfClient(Client client) {
		for(int i=0;i<invoiceListModel.getSize();i++) {
			if(invoiceListModel.getElementAt(i).getClient()==client) {
				invoiceListModel.remove(i--);
			}
		}
		if(invoiceListModel.getSize()==0 && listClients.getSelectedValue()==null) {
			balanceController.yearsOfTheInvoices();
		}
		
	}

}
