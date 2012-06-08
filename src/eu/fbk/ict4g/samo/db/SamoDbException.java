package eu.fbk.ict4g.samo.db;

/**
 * Thrown when something goes wrong with the SAMo DB
 * 
 * @author pietro
 *
 */
public class SamoDbException extends Exception {

	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	public SamoDbException() {
		// TODO Auto-generated constructor stub
	}

	public SamoDbException(String detailMessage) {
		super(detailMessage);
		// TODO Auto-generated constructor stub
	}

	public SamoDbException(Throwable throwable) {
		super(throwable);
		// TODO Auto-generated constructor stub
	}

	public SamoDbException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
		// TODO Auto-generated constructor stub
	}

}
