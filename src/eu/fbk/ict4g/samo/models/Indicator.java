package eu.fbk.ict4g.samo.models;

public class Indicator {
	
	private long id;
	
	private String name;
	private String type;
	private String value;
	
	public static final String TYPE_TEXT = "TEXT";
	public static final String TYPE_STAR = "STAR";
	public static final String TYPE_YESNO = "YESNO";
	public static final String TYPE_PERCENT = "PERCENT";
	public static final String TYPE_NUMBER = "NUMBER";

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
		this.value = value;
	}

	@Override
	public String toString() {
		return name + ": " + value;
	}

	
}
