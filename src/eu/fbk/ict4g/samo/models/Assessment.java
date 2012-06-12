package eu.fbk.ict4g.samo.models;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Assessment implements Parcelable {

	private long id;
	private long assessorId;
	private long campaignId;
	private long targetId;
	
	private boolean uploaded;
	
	private String name;
	private String assessorName;
	private String targetName;
	private String date;
	
	private List<Indicator> indicators;
	
	/**
	 * 
	 */
	public Assessment() {
		indicators =  new ArrayList<Indicator>();
	}

	public void addIndicator(Indicator indicator) {
		this.indicators.add(indicator);
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

	public boolean isUploaded() {
		return uploaded;
	}

	public void setUploaded(boolean uploaded) {
		this.uploaded = uploaded;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public long getAssessorId() {
		return assessorId;
	}

	public void setAssessorId(long assessorId) {
		this.assessorId = assessorId;
	}

	public long getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(long campaignId) {
		this.campaignId = campaignId;
	}

	public long getTargetId() {
		return targetId;
	}

	public void setTargetId(long targetId) {
		this.targetId = targetId;
	}

	public String getAssessorName() {
		return assessorName;
	}

	public void setAssessorName(String assessorName) {
		this.assessorName = assessorName;
	}

	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public List<Indicator> getIndicators() {
		return indicators;
	}

	public void setIndicators(List<Indicator> indicators) {
		for (Indicator indicator : indicators) {
			this.indicators.add(indicator);
		}
		//this.indicators = indicators;
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
		dest.writeLong(assessorId);
		dest.writeLong(campaignId);
		dest.writeLong(targetId);
		dest.writeInt(uploaded ? 1 : 0);
		dest.writeString(name);
		dest.writeString(assessorName);
		dest.writeString(targetName);
		dest.writeString(date);
	}
	
	public static final Parcelable.Creator<Assessment> CREATOR = 
			new Creator<Assessment>() {
				
				@Override
				public Assessment[] newArray(int size) {
					return new Assessment[size];
				}
				
				@Override
				public Assessment createFromParcel(Parcel source) {
					return new Assessment(source);
				}
			};
			
	public Assessment(Parcel in) {
		id = in.readLong();
		assessorId = in.readLong();
		campaignId = in.readLong();
		targetId = in.readLong();
		uploaded = in.readInt() == 1 ? true : false;
		name = in.readString();
		assessorName = in.readString();
		targetName = in.readString();
		date = in.readString();
	}	
	
}
