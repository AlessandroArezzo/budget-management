package com.balance.view.swing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;

import com.balance.model.Client;
import com.balance.model.Invoice;

public class InvoiceTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	private List<Invoice> invoices;
    public static final String[] columnNames = {"Cliente", "Data", "Importo (€)"};


    public InvoiceTableModel() {
        this.invoices = new ArrayList<>(Arrays.asList());
    }
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (getRowCount() > 0 && getValueAt(0, columnIndex) != null) {
            return getValueAt(0, columnIndex).getClass();
        }
        return super.getColumnClass(columnIndex);
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
        switch (columnIndex) {
            case 0:
                value = invoice.getClient().getIdentifier();
                break;
            case 1:
                value = invoice.getDateInString();
                break;
            case 2:
                value = ""+invoice.getRevenue();
                break;
        }

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
    
    public void remove(int row) {
    	invoices.remove(row);
    	fireTableDataChanged();
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
