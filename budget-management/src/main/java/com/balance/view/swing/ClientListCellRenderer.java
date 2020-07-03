package com.balance.view.swing;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

public class ClientListCellRenderer extends DefaultListCellRenderer{

	private static final long serialVersionUID = 1L;

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
	
}
