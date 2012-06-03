package eu.fbk.ict4g.samo.service;

/**
 * Thrown when something goes wrong with the BringTheFood Service
 * 
 * @author pietro
 *
 */
public class SamoServiceException extends Exception {

	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	public SamoServiceException() {
		// TODO Auto-generated constructor stub
	}

	public SamoServiceException(String detailMessage) {
		super(detailMessage);
		// TODO Auto-generated constructor stub
	}

	public SamoServiceException(Throwable throwable) {
		super(throwable);
		// TODO Auto-generated constructor stub
	}

	public SamoServiceException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
		// TODO Auto-generated constructor stub
	}

}
