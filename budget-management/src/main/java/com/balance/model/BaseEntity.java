package com.balance.model;

public abstract class BaseEntity {
	protected String id;
	
	public BaseEntity(String id) {
		this.id=id;
	}
	
	public BaseEntity() { }
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BaseEntity other = (BaseEntity) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
