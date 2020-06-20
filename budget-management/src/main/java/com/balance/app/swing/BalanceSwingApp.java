package com.balance.app.swing;

import java.awt.EventQueue;

import com.balance.view.swing.BalanceSwingView;

public class BalanceSwingApp {
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					BalanceSwingView frame = new BalanceSwingView();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
