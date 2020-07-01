
package com.balance.view.swing;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.table.TableCellRenderer;

import com.balance.controller.BalanceController;
import com.balance.model.Client;
import com.balance.model.Invoice;
import com.balance.view.BalanceView;

import javax.swing.JLabel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.swing.JList;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Color;
import java.awt.Font;

public class BalanceSwingView extends JFrame implements BalanceView {

	private static final long serialVersionUID = 1L;
	private static final int CURRENT_YEAR=Calendar.getInstance().get(Calendar.YEAR);
	private static final String fontText="Lucida Grande";
	
	private transient BalanceController balanceController;
	
	private JPanel contentPane;
	private JList<Client> listClients;
	private DefaultListModel<Client> clientListModel;
	private JTable tableInvoices;
	private InvoiceTableModel invoiceTableModel;
	private JComboBox<Integer> comboboxYears;
	private DefaultComboBoxModel<Integer> comboboxYearsModel;
	private JLabel lblRevenue;
	private JTextPane paneClientError;
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
	private JTextPane paneInvoiceError;
	private JButton btnRemoveInvoice;
	
	public void setBalanceController(BalanceController balanceController) {
		this.balanceController = balanceController;
	}

	/**
	 * Create the frame.
	 */
	public BalanceSwingView() {
		setPreferredSize(new Dimension(850, 700));
		setTitle("Budget Management View");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setBackground(Color.WHITE);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 855, 700);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel panel_clientManagement = new JPanel();
		panel_clientManagement.setBackground(new Color(23, 35, 51));
		panel_clientManagement.setBounds(0, 52, 304, 626);
		contentPane.add(panel_clientManagement);
		panel_clientManagement.setLayout(null);
		
		JPanel panel_clientTitle = new JPanel();
		panel_clientTitle.setForeground(Color.WHITE);
		panel_clientTitle.setBounds(0, 0, 304, 58);
		panel_clientTitle.setBackground(new Color(47, 73, 106));
		panel_clientManagement.add(panel_clientTitle);
		panel_clientTitle.setLayout(null);
		
		JLabel lblClientsTitle = new JLabel("CLIENTI");
		lblClientsTitle.setFont(new Font(fontText, Font.BOLD, 17));
		lblClientsTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblClientsTitle.setBounds(0, 0, 304, 58);
		lblClientsTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblClientsTitle.setForeground(Color.WHITE);
		panel_clientTitle.add(lblClientsTitle);
		
		JScrollPane scrollPaneClientsList = new JScrollPane();
		scrollPaneClientsList.setBorder(null);
		scrollPaneClientsList.setBounds(0, 58, 304, 301);
		panel_clientManagement.add(scrollPaneClientsList);
		clientListModel=new DefaultListModel<>();
		listClients = new JList<>(clientListModel);
		listClients.setFont(new Font(fontText, Font.PLAIN, 14));
		listClients.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		listClients.setBorder(null);
		listClients.setName("clientsList");
		listClients.setSelectionBackground(Color.LIGHT_GRAY);
		listClients.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listClients.setBackground(new Color(220, 228, 239));
		listClients.setFixedCellHeight(35);
		scrollPaneClientsList.setViewportView(listClients);
		
		listClients.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index,
                      boolean isSelected, boolean cellHasFocus) {
            	super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                 setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                 if (index % 2 == 0) setBackground(new Color(203, 214, 231));
                 if(isSelected) {
                	 setForeground(Color.white);
                	 setBackground(new Color(23, 35, 51));
                 }
                 return this;
            }
        });
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
		
		btnRemoveClient = new JButton("Rimuovi cliente");
		btnRemoveClient.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnRemoveClient.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		btnRemoveClient.setEnabled(false);
		btnRemoveClient.setFont(new Font(fontText, Font.BOLD, 14));
		btnRemoveClient.setForeground(Color.WHITE);
		btnRemoveClient.setBounds(10, 369, 288, 29);
		panel_clientManagement.add(btnRemoveClient);
		btnRemoveClient.addActionListener(
				e -> balanceController.deleteClient(listClients.getSelectedValue())
			);
		
		paneClientError = new JTextPane();
		paneClientError.setText("");
		paneClientError.setName("paneClientErrorMessage");
		paneClientError.setForeground(new Color(255, 102, 102));
		paneClientError.setBackground(new Color(23, 35, 51));
		paneClientError.setBounds(10, 410, 288, 43);
		panel_clientManagement.add(paneClientError);
		
		JPanel panel_newClient = new JPanel();
		panel_newClient.setBounds(0, 477, 304, 143);
		panel_newClient.setBackground(new Color(23, 35, 51));
		panel_clientManagement.add(panel_newClient);
		panel_newClient.setLayout(null);
		
		lblNewClient = new JLabel("NUOVO CLIENTE");
		lblNewClient.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewClient.setBounds(6, 18, 292, 17);
		lblNewClient.setFont(new Font(fontText, Font.PLAIN, 14));
		lblNewClient.setForeground(Color.WHITE);
		panel_newClient.add(lblNewClient);
		
		JSeparator separator = new JSeparator();
		separator.setForeground(Color.WHITE);
		separator.setBounds(133, 69, 159, 17);
		panel_newClient.add(separator);
		
		textFieldNewClient = new JTextField();
		textFieldNewClient.setFont(new Font(fontText, Font.BOLD, 13));
		textFieldNewClient.setHorizontalAlignment(SwingConstants.CENTER);
		textFieldNewClient.setForeground(Color.WHITE);
		textFieldNewClient.setName("textField_clientName");
		textFieldNewClient.setBorder(null);
		textFieldNewClient.setBounds(133, 58, 165, 16);
		textFieldNewClient.setBackground(new Color(23, 35, 51));
		textFieldNewClient.setCaretColor(Color.white);
		panel_newClient.add(textFieldNewClient);
		textFieldNewClient.setColumns(10);
		
		lblClientName = new JLabel("Identificativo");
		lblClientName.setHorizontalAlignment(SwingConstants.CENTER);
		lblClientName.setFont(new Font(fontText, Font.PLAIN, 14));
		lblClientName.setForeground(Color.WHITE);
		lblClientName.setBounds(6, 58, 115, 22);
		panel_newClient.add(lblClientName);
		
		btnNewClient = new JButton("Aggiungi cliente");
		btnNewClient.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnNewClient.setEnabled(false);
		btnNewClient.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		btnNewClient.setForeground(Color.WHITE);
		btnNewClient.setFont(new Font(fontText, Font.BOLD, 14));
		btnNewClient.setBounds(12, 98, 286, 29);
		btnNewClient.setName("btnAddClient");
		panel_newClient.add(btnNewClient);
		
		btnNewClient.addActionListener(
				e -> balanceController.newClient(new Client(textFieldNewClient.getText()))
			);
		
		JSeparator separator_2 = new JSeparator();
		separator_2.setForeground(Color.WHITE);
		separator_2.setBounds(10, 460, 286, 17);
		panel_clientManagement.add(separator_2);
		
		JPanel panel_invoiceManagement = new JPanel();
		panel_invoiceManagement.setBackground(Color.WHITE);
		panel_invoiceManagement.setBounds(302, 52, 553, 626);
		contentPane.add(panel_invoiceManagement);
		panel_invoiceManagement.setLayout(null);
		
		JPanel panel_revenueLabel = new JPanel();
		panel_revenueLabel.setBorder(null);
		panel_revenueLabel.setBounds(0, 30, 551, 88);
		panel_revenueLabel.setBackground(new Color(245,245,245));
		panel_invoiceManagement.add(panel_revenueLabel);
		panel_revenueLabel.setLayout(null);
		
		lblRevenue = new JLabel("");
		lblRevenue.setFont(new Font(fontText, Font.PLAIN, 14));
		lblRevenue.setForeground(new Color(89, 89, 89));
		lblRevenue.setHorizontalAlignment(SwingConstants.CENTER);
		lblRevenue.setBounds(2, 0, 549, 88);
		lblRevenue.setName("revenueLabel");
		panel_revenueLabel.add(lblRevenue);
		
		JPanel panel_invoiceViewAndAdd = new JPanel();
		panel_invoiceViewAndAdd.setBackground(Color.WHITE);
		panel_invoiceViewAndAdd.setBounds(27, 130, 515, 418);
		panel_invoiceManagement.add(panel_invoiceViewAndAdd);
		panel_invoiceViewAndAdd.setLayout(null);
		
		comboboxYearsModel=new DefaultComboBoxModel<>();
	    comboboxYears = new JComboBox<>(comboboxYearsModel);
		comboboxYears.setBounds(302, 0, 101, 27);
		panel_invoiceViewAndAdd.add(comboboxYears);
		comboboxYears.setFont(new Font(fontText, Font.BOLD, 16));
		comboboxYears.setName("yearsCombobox");
		comboboxYears.setBorder(null);
		comboboxYears.setBackground(new Color(245,245,245));
		
		JScrollPane scrollPaneInvoicesList = new JScrollPane();
		scrollPaneInvoicesList.setFont(new Font(fontText, Font.PLAIN, 14));
		scrollPaneInvoicesList.setBackground(new Color(255, 255, 255));
		scrollPaneInvoicesList.setBorder(new EmptyBorder(0, 0, 0, 0));
		scrollPaneInvoicesList.setBounds(0, 31, 403, 234);
		panel_invoiceViewAndAdd.add(scrollPaneInvoicesList);
		
		invoiceTableModel=new InvoiceTableModel();
		tableInvoices = new JTable(invoiceTableModel) {
			private static final long serialVersionUID = 1L;
			@Override
            public Component prepareRenderer(
                    TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (isRowSelected(row)) {
                    c.setBackground(Color.black);
                } else {
                	c.setBackground(row%2==0 ? Color.white : new Color(247, 247, 247));
                }
                return c;
            }
		};
		tableInvoices.setBorder(new EmptyBorder(0, 0, 0, 0));
		tableInvoices.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableInvoices.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
		tableInvoices.setName("invoicesTable");
		scrollPaneInvoicesList.setViewportView(tableInvoices);
		tableInvoices.getSelectionModel().addListSelectionListener(e -> 
			btnRemoveInvoice.setEnabled(tableInvoices.getSelectedRow() != -1));

		btnRemoveInvoice = new JButton();
		btnRemoveInvoice.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnRemoveInvoice.setHorizontalTextPosition(SwingConstants.CENTER);
		btnRemoveInvoice.setText("<html><center>Rimuovi<br>fattura</center></html>");
		btnRemoveInvoice.setBounds(401, 146, 114, 121);
		panel_invoiceViewAndAdd.add(btnRemoveInvoice);
		btnRemoveInvoice.setEnabled(false);
		btnRemoveInvoice.setFont(new Font(fontText, Font.BOLD, 14));
		btnRemoveInvoice.addActionListener(
				e -> balanceController.deleteInvoice(invoiceTableModel.getInvoiceAt(tableInvoices.getSelectedRow()))
			);
		
		btnShowAllInvoices = new JButton("<html><center>Vedi tutte<br>le fatture</center></html>");
		btnShowAllInvoices.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnShowAllInvoices.setVisible(false);
		btnShowAllInvoices.setHorizontalTextPosition(SwingConstants.CENTER);
		btnShowAllInvoices.setBounds(401, 23, 114, 121);
		panel_invoiceViewAndAdd.add(btnShowAllInvoices);
		btnShowAllInvoices.setFont(new Font(fontText, Font.BOLD, 14));
		
		JPanel panel_newInvoice = new JPanel();
		panel_newInvoice.setBounds(0, 264, 515, 154);
		panel_invoiceViewAndAdd.add(panel_newInvoice);
		panel_newInvoice.setBorder(null);
		panel_newInvoice.setBackground(new Color(245,245,245));
		panel_newInvoice.setLayout(null);
		
		JLabel lblNewInvoice = new JLabel("NUOVA FATTURA");
		lblNewInvoice.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewInvoice.setFont(new Font(fontText, Font.PLAIN, 14));
		lblNewInvoice.setBounds(6, 6, 503, 22);
		panel_newInvoice.add(lblNewInvoice);
		
		JLabel lblClientNewInvoice = new JLabel("Cliente");
		lblClientNewInvoice.setFont(new Font(fontText, Font.PLAIN, 14));
		lblClientNewInvoice.setBounds(33, 47, 84, 16);
		panel_newInvoice.add(lblClientNewInvoice);
		
		comboboxClientsModel=new DefaultComboBoxModel<>();
		comboBoxClients = new JComboBox<>(comboboxClientsModel);
		comboBoxClients.setName("clientsCombobox");
		comboBoxClients.setBounds(99, 43, 193, 27);
		panel_newInvoice.add(comboBoxClients);
		
		JLabel lblDateNewInvoice = new JLabel("Data");
		lblDateNewInvoice.setFont(new Font(fontText, Font.PLAIN, 14));
		lblDateNewInvoice.setBounds(33, 91, 84, 16);
		panel_newInvoice.add(lblDateNewInvoice);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setForeground(Color.BLACK);
		separator_1.setBounds(124, 103, 33, 12);
		panel_newInvoice.add(separator_1);
		
		JLabel lblSlashDate1NewInvoice = new JLabel("/");
		lblSlashDate1NewInvoice.setFont(new Font(fontText, Font.PLAIN, 16));
		lblSlashDate1NewInvoice.setBounds(159, 93, 8, 16);
		panel_newInvoice.add(lblSlashDate1NewInvoice);
		
		JSeparator separator_1_1 = new JSeparator();
		separator_1_1.setForeground(Color.BLACK);
		separator_1_1.setBounds(167, 103, 33, 12);
		panel_newInvoice.add(separator_1_1);
		
		JLabel lblSlashDate2NewInvoice = new JLabel("/");
		lblSlashDate2NewInvoice.setFont(new Font(fontText, Font.PLAIN, 16));
		lblSlashDate2NewInvoice.setBounds(201, 93, 8, 16);
		panel_newInvoice.add(lblSlashDate2NewInvoice);
		
		JSeparator separator_1_1_1 = new JSeparator();
		separator_1_1_1.setForeground(Color.BLACK);
		separator_1_1_1.setBounds(211, 103, 57, 12);
		panel_newInvoice.add(separator_1_1_1);
		
		textFieldDayNewInvoice = new JTextField();
		textFieldDayNewInvoice.setHorizontalAlignment(SwingConstants.CENTER);
		textFieldDayNewInvoice.setName("textField_dayOfDateInvoice");
		textFieldDayNewInvoice.setBorder(null);
		textFieldDayNewInvoice.setBounds(125, 88, 31, 22);
		textFieldDayNewInvoice.setBackground(new Color(245,245,245));
		panel_newInvoice.add(textFieldDayNewInvoice);
		textFieldDayNewInvoice.setColumns(10);
		
		textFieldMonthNewInvoice = new JTextField();
		textFieldMonthNewInvoice.setHorizontalAlignment(SwingConstants.CENTER);
		textFieldMonthNewInvoice.setName("textField_monthOfDateInvoice");
		textFieldMonthNewInvoice.setColumns(10);
		textFieldMonthNewInvoice.setBorder(null);
		textFieldMonthNewInvoice.setBounds(169, 88, 31, 22);
		textFieldMonthNewInvoice.setBackground(new Color(245,245,245));
		panel_newInvoice.add(textFieldMonthNewInvoice);
		
		textFieldYearNewInvoice = new JTextField();
		textFieldYearNewInvoice.setHorizontalAlignment(SwingConstants.CENTER);
		textFieldYearNewInvoice.setName("textField_yearOfDateInvoice");
		textFieldYearNewInvoice.setColumns(10);
		textFieldYearNewInvoice.setBorder(null);
		textFieldYearNewInvoice.setBounds(212, 88, 56, 22);
		textFieldYearNewInvoice.setBackground(new Color(245,245,245));
		panel_newInvoice.add(textFieldYearNewInvoice);
		
		JLabel lblRevenueNewInvoice = new JLabel("Importo");
		lblRevenueNewInvoice.setFont(new Font(fontText, Font.PLAIN, 14));
		lblRevenueNewInvoice.setBounds(33, 129, 84, 16);
		panel_newInvoice.add(lblRevenueNewInvoice);
		
		JSeparator separator_1_1_1_1 = new JSeparator();
		separator_1_1_1_1.setForeground(Color.BLACK);
		separator_1_1_1_1.setBounds(118, 140, 150, 12);
		panel_newInvoice.add(separator_1_1_1_1);
		
		btnNewInvoice = new JButton("Aggiungi fattura");
		btnNewInvoice.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnNewInvoice.setEnabled(false);
		btnNewInvoice.setBorder(new LineBorder(new Color(23, 35, 51), 1, true));
		btnNewInvoice.setFont(new Font(fontText, Font.BOLD, 14));
		btnNewInvoice.setBounds(335, 53, 180, 101);
		panel_newInvoice.add(btnNewInvoice);
		
		textFieldRevenueNewInvoice = new JTextField();
		textFieldRevenueNewInvoice.setHorizontalAlignment(SwingConstants.CENTER);
		textFieldRevenueNewInvoice.setName("textField_revenueInvoice");
		textFieldRevenueNewInvoice.setColumns(10);
		textFieldRevenueNewInvoice.setBorder(null);
		textFieldRevenueNewInvoice.setBounds(118, 125, 150, 22);
		textFieldRevenueNewInvoice.setBackground(new Color(245,245,245));
		panel_newInvoice.add(textFieldRevenueNewInvoice);
		
		JLabel lblRevenueNewInvoice_1 = new JLabel("€");
		lblRevenueNewInvoice_1.setFont(new Font(fontText, Font.PLAIN, 14));
		lblRevenueNewInvoice_1.setBounds(270, 129, 15, 16);
		panel_newInvoice.add(lblRevenueNewInvoice_1);
		
		paneInvoiceError = new JTextPane();
		paneInvoiceError.setText("");
		paneInvoiceError.setForeground(new Color(255, 51, 51));
		paneInvoiceError.setName("paneInvoiceErrorMessage");
		paneInvoiceError.setBounds(27, 560, 514, 48);
		panel_invoiceManagement.add(paneInvoiceError);
		
		JPanel panel_headBar = new JPanel();
		panel_headBar.setBackground(new Color(23, 35, 51));
		panel_headBar.setBounds(0, 0, 855, 53);
		contentPane.add(panel_headBar);
		
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
					Date date = Date.from(localDate.atStartOfDay(ZoneId.of("Z")).toInstant());
					Client clientOfInvoice=(Client) comboBoxClients.getSelectedItem();
					resetInvoiceErrorLabel();
					resetTextBoxAndComboBoxNewInvoice();
					balanceController.newInvoice(new Invoice(clientOfInvoice,
							date,revenueOfInvoice));
				}
				catch(DateTimeException ex) {
					paneInvoiceError.setText("La data "+dayOfMonth+"/"+monthOfYear+"/"+yearOfDate+" non è corretta");
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
		comboboxClientsModel.removeAllElements();
		Collections.sort(clients);
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
			invoiceTableModel.removeAllElements();
			invoiceTableModel.addElements(invoices);
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
		Collections.sort(yearsToAddInModel);
		Collections.reverse(yearsToAddInModel);
		yearsToAddInModel.stream().forEach(comboboxYearsModel::addElement); 
		setCurrentYearSelected();
	}
	
	public DefaultComboBoxModel<Integer> getComboboxYearsModel() {
		return comboboxYearsModel;
	}
	
	public InvoiceTableModel getInvoiceTableModel() {
		return invoiceTableModel;
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
		int numberOfInvoices=invoiceTableModel.getRowCount();
		if(numberOfInvoices==0 && clientSelected==null) {
			lblRevenue.setText("Non sono presenti fatture per il "+yearSelected);
		}
		else if(numberOfInvoices==0 && clientSelected!=null) {
			lblRevenue.setText("Non sono presenti fatture del "+yearSelected+" per il cliente "+clientSelected.getIdentifier());
		}
		else {
			double totalRevenue=0;
			for(int i=0;i<numberOfInvoices;i++) {
				totalRevenue+=invoiceTableModel.getInvoiceAt(i).getRevenue();
			}
			if (clientSelected==null) {
				lblRevenue.setText("Il ricavo totale del "+yearSelected+" è di "+String.format("%.2f", totalRevenue)+"€");
			}
			else {
				lblRevenue.setText("Il ricavo totale delle fatture del cliente "+clientSelected.getIdentifier()+" "
						+ "nel "+yearSelected+" è di "+String.format("%.2f", totalRevenue)+"€");
			}
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
		paneClientError.setText(message+": "+client.getIdentifier());
	}

	@Override
	public void clientAdded(Client clientToAdd) {
		Client clientSelectedList=listClients.getSelectedValue();
		Client clientSelectedCombobox=(Client) comboBoxClients.getSelectedItem();
		List<Client> clients=new ArrayList<>();
		for(int index=0;index<clientListModel.getSize();index++)
			clients.add(clientListModel.getElementAt(index));
		clients.add(clientToAdd);
		showClients(clients);
		if(clientSelectedList!=null) {
			listClients.setSelectedValue(clientSelectedList, true);
		}
		if(clientSelectedCombobox!=null) {
			comboBoxClients.setSelectedItem(clientSelectedCombobox);
		}
		resetTextBoxNewClient();
		resetClientErrorLabel();
	}
	
	private void resetClientErrorLabel() {
		paneClientError.setText("");
	}
	
	private void resetInvoiceErrorLabel() {
		paneInvoiceError.setText("");
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
			Invoice invoiceSelectedInTable=invoiceTableModel.getInvoiceAt(tableInvoices.getSelectedRow());
			invoiceTableModel.addElement(invoiceToAdd);
			if(invoiceSelectedInTable!=null) {
				int indexToSelected=invoiceTableModel.getRowInvoice(invoiceSelectedInTable);
				tableInvoices.setRowSelectionInterval(indexToSelected,indexToSelected);
			}
			setLabelTotalRevenue();
		}
		else if(comboboxYearsModel.getIndexOf(yearOfInvoice)==-1) {
			addYearsInOrderInCombobox(yearOfInvoice);
		}
		resetInvoiceErrorLabel();
	}
	
	private void addYearsInOrderInCombobox(int yearToAdd) {
		int yearSelected=(int) comboboxYearsModel.getSelectedItem();
		comboboxYearsModel.addElement(yearToAdd);
		List<Integer> yearsInCombobox=new ArrayList<>();
		for(int index=0;index<comboboxYearsModel.getSize();index++)
			yearsInCombobox.add(comboboxYearsModel.getElementAt(index));
		Collections.sort(yearsInCombobox);
		Collections.reverse(yearsInCombobox);
		comboboxYearsModel.removeAllElements();
		yearsInCombobox.stream().forEach(comboboxYearsModel::addElement); 
		comboboxYearsModel.setSelectedItem(yearSelected);
	}

	@Override
	public void removeInvoicesOfClient(Client client) {
		invoiceTableModel.removeInvoicesOfAClient(client);
		if(invoiceTableModel.getRowCount()==0 && listClients.getSelectedValue()==null) {
			balanceController.yearsOfTheInvoices();
		}
		else if (listClients.getSelectedValue()==null){
			this.setLabelTotalRevenue();
		}
	}

	@Override
	public void invoiceRemoved(Invoice invoiceToRemove) {
		invoiceTableModel.removeElement(invoiceToRemove);
		if(invoiceTableModel.getRowCount()==0) {
			balanceController.yearsOfTheInvoices();
		}
		else{
			this.setLabelTotalRevenue();
		}
	}

	@Override
	public void showInvoiceError(String message, Invoice invoice) {
		paneInvoiceError.setText(message+": "+invoice.toString());
	}

}
