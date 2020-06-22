package com.balance.model;

import java.util.Objects;

public class Client extends BaseEntity{
	private String identifier; // name and surname of the client or company name
	
	public Client(String id, String identifier) {
		super(id);
		this.identifier=identifier;
	}
	
	public Client(String identifier) {
		this.identifier=identifier;
	}
	
	public Client() {
		this.identifier="test";
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if (obj == null)
			return false;
		
		if (getClass() != obj.getClass())
			return false;
		
		Client other = (Client) obj;
		return Objects.equals(identifier, other.identifier);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(identifier);
	}
	
	@Override
	public String toString() {
		return identifier;
	}
}
