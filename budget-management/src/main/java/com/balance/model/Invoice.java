package com.balance.model;

import java.util.Date;
import java.util.Objects;

public class Invoice extends BaseEntity{
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

	public double getRevenue() {
		return revenue;
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
				&& Objects.equals(revenue, other.revenue)
				&& Objects.equals(id, other.id);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id, date, client, revenue);
	}
	
	@Override
	public String toString() {
		return "id="+id+", cliente="+client.getIdentifier()+","
				+ " data="+date.toString()+","
				+ " ricavo="+revenue+"€";
	}
}
