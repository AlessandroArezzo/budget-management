package com.balance.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class Invoice extends BaseEntity implements Comparable<Invoice>{
	private Date date;
	private double revenue;
	
	private Client client;
	
	public Invoice(String id, Client client, Date date, double revenue) {
		super(id);
		this.client=client;
		this.date=date;
		this.revenue=revenue;
	}
	
	public Invoice(Client client, Date date, double revenue) {
		this.client=client;
		this.date=date;
		this.revenue=revenue;
	}
	
	public Invoice() {
		this.client=null;
		this.date=null;
		this.revenue=0;
	}

	public Client getClient() {
		return client;
	}
	
	public Date getDate() {
		return date;
	}
	
	public String getDateInString() {
		final DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		return formatter.format(this.date);
	}

	public double getRevenue() {
		return revenue;
	}
	
	public String getRevenueInString() {
		return String.format("%.2f", revenue);
	}
	
	public void setClient(Client client) {
		this.client = client;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if (obj == null)
			return false;
		
		if (getClass() != obj.getClass())
			return false;
		
		Invoice other = (Invoice) obj;
		return  Objects.equals(client, other.client)
				&& Objects.equals(date, other.date)
				&& Objects.equals(revenue, other.revenue);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(date, client, revenue);
	}
	
	@Override
	public String toString() {
		return "Cliente="+client.getIdentifier()+","
				+ " Data="+getDateInString()+","
				+ " Ricavo="+revenue+"€";
	}
	
	@Override
    public int compareTo(Invoice invoiceToCompare) {
        return this.date.compareTo(invoiceToCompare.getDate());
    }

}
