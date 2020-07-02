package com.balance.view.swing;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class InvoiceTable extends JTable{
	private static final long serialVersionUID = 1L;
	public InvoiceTable(InvoiceTableModel invoiceTableModel) {
		super(invoiceTableModel);
	}
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
}
