package eu.fbk.ict4g.samo.models;

import android.os.Parcel;
import android.os.Parcelable;
import eu.fbk.ict4g.samo.utils.SAMoLog;

public class Indicator implements Parcelable {
	
	private long id;
	
	private String name;
	private String type;
	private String value;
	
	public static final String TYPE_TEXT = "TEXT";
	public static final String TYPE_STAR = "STAR";
	public static final String TYPE_YESNO = "YESNO";
	public static final String TYPE_PERCENT = "PERCENT";
	public static final String TYPE_NUMBER = "NUMBER";

	public Indicator() {
		this.id = 0;
		this.name = "";
		this.type = "";
		this.value = "";
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		SAMoLog.d(this.getClass().getSimpleName() + " " + this.name, "Value is " + value);
		this.value = value;
	}

	@Override
	public String toString() {
		return name + ": " + value;
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
		dest.writeString(type);
		dest.writeString(value);
	}
	
	public static final Parcelable.Creator<Indicator> CREATOR = 
			new Creator<Indicator>() {
				
				@Override
				public Indicator[] newArray(int size) {
					return new Indicator[size];
				}
				
				@Override
				public Indicator createFromParcel(Parcel source) {
					return new Indicator(source);
				}
			};
			
	public Indicator(Parcel in) {
		id = in.readLong();
		name = in.readString();
		type = in.readString();
		value = in.readString();
	}
}
