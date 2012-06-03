package eu.fbk.ict4g.samo.models;


public class Target {
	
	private long id;
	
	private int latE6, lngE6;
	
	private String name;
	private String street, city, state, zip, country;
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getStreet() {
		return street;
	}
	
	public void setStreet(String street) {
		this.street = street;
	}
	
	public String getCity() {
		return city;
	}
	
	public void setCity(String city) {
		this.city = city;
	}
	
	public String getState() {
		return state;
	}
	
	public void setState(String state) {
		this.state = state;
	}
	
	public String getZip() {
		return zip;
	}
	
	public void setZip(String zip) {
		this.zip = zip;
	}
	
	public String getCountry() {
		return country;
	}
	
	public void setCountry(String country) {
		this.country = country;
	}

	public int getLatE6() {
		return latE6;
	}

	public void setLatE6(int latE6) {
		this.latE6 = latE6;
	}

	public int getLngE6() {
		return lngE6;
	}

	public void setLngE6(int lngE6) {
		this.lngE6 = lngE6;
	}

	@Override
	public String toString() {
		return name;
	}
	
}
