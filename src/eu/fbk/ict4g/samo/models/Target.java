package eu.fbk.ict4g.samo.models;

import android.os.Parcel;
import android.os.Parcelable;


public class Target implements Parcelable {

	private long id;

	private int latE6, lngE6;
	private double lat, lng;

	private String name;
	private String street, city, state, zip, country;

	public Target(){
		// empty constructor;
	}
	
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

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
		this.latE6 = (int) (lat * 1E6);
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
		this.lngE6 = (int) (lng * 1E6);
	}

	public int getLatE6() {
		return latE6;
	}

	public void setLatE6(int latE6) {
		this.latE6 = latE6;
		this.lat = (double) ((1.0 * latE6) / 1E6);
	}

	public int getLngE6() {
		return lngE6;
	}

	public void setLngE6(int lngE6) {
		this.lngE6 = lngE6;
		this.lng = (double) ((1.0 * lngE6) / 1E6);
	}

	@Override
	public String toString() {
		return name;
	}

	// Parcelable Stuff

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);

		dest.writeString(name);
		dest.writeString(street);
		dest.writeString(city);
		dest.writeString(state);
		dest.writeString(zip);
		dest.writeString(country);

		dest.writeInt(latE6);
		dest.writeInt(lngE6);
		
		dest.writeDouble(lat);
		dest.writeDouble(lng);

	}

	public static final Parcelable.Creator<Target> CREATOR = 
			new Creator<Target>() {

		@Override
		public Target[] newArray(int size) {
			return new Target[size];
		}

		@Override
		public Target createFromParcel(Parcel source) {
			return new Target(source);
		}
	};

	protected Target(Parcel in) {
		id = in.readLong();

		name = in.readString();
		street = in.readString();
		city = in.readString();
		state = in.readString();
		zip = in.readString();
		country = in.readString();

		latE6 = in.readInt();
		lngE6 = in.readInt();
		
		lat = in.readDouble();
		lng = in.readDouble();
	}
}
