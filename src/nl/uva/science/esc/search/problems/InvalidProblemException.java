package nl.uva.science.esc.search.problems;

/**
 * Exception to throw when trying to interpret a json problem definition
 * when the definition contains errors, i.e. it does not define a valid problem
 * of the expected problem type.
 * 
 * All the constructors from Exception are supported without change.
 * (Only the NAME of this exception type is different, so that I can catch it 
 * selectively.)
 * 
 * @author Wolter Kaper
 */
public class InvalidProblemException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7943157249686265415L;

	/**
	 * 
	 */
	public InvalidProblemException() {
	}

	/**
	 * @param arg0
	 */
	public InvalidProblemException(String arg0) {
		super(arg0);
	}

	/**
	 * @param cause
	 */
	public InvalidProblemException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public InvalidProblemException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public InvalidProblemException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
