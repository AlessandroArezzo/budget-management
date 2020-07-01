package com.balance.view.swing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.balance.model.Client;
import com.balance.model.Invoice;

public class InvoiceTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	private transient List<Invoice> invoices;
    private static final String[] columnNames = {"Cliente", "Data", "Importo (€)"};


    public InvoiceTableModel() {
        this.invoices = new ArrayList<>(Arrays.asList());
    }
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return getValueAt(0, columnIndex).getClass();
    }

    @Override
    public int getRowCount() {
    	int result=0;
    	if(invoices!=null)
    		result=invoices.size();
        return result;
    }
    
    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object value="";
        Invoice invoice = invoices.get(rowIndex);
        if(columnIndex==0) 
        	value = invoice.getClient().getIdentifier();
        else if (columnIndex==1)
        	value = invoice.getDateInString();
        else if (columnIndex==2)
        	value = invoice.getRevenueInString();
        return value;
    }
    
    public void addElement(Invoice invoiceToAdd) {
    	invoices.add(invoiceToAdd);
    	Collections.sort(invoices);
    	fireTableDataChanged();
    }
    
    public void addElements(List<Invoice> invoicesToAdd) {
    	invoicesToAdd.stream().forEach(invoices::add);
    	Collections.sort(invoices);
    	fireTableDataChanged();
    }
    
    public Invoice getInvoiceAt(int row) {
    	if(row==-1)
    		return null;
        return invoices.get(row);
    }
    
    public int getRowInvoice(Invoice invoice) {
    	return invoices.indexOf(invoice);
    }
    
    public void removeElement(Invoice invoiceToRemove) {
    	invoices.remove(invoiceToRemove);
    	fireTableDataChanged();
    }

	public void removeAllElements() {
		invoices.clear();
		fireTableDataChanged();
	}
	
	public void removeInvoicesOfAClient(Client client) {
		int counter=0;
		while(counter<invoices.size()) {
			if(invoices.get(counter).getClient().equals(client)) 
				invoices.remove(counter);
			else 
				counter++;
		}
	}
}
