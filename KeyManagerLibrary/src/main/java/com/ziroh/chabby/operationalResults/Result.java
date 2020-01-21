package com.ziroh.chabby.operationalResults;


/**
 * This is the base class for all the results returned when an IO is performed with the device service
 */
public class Result   
{
	/**
	 * short message description, generally short description of the problem.
	 */
	public String short_msg = new String();
	/**
	 * get short message description
	 * @return short_msg
	 */
	public String getshort_msg() {
		return short_msg;
	}
	/**
	 * set short message description
	 * @param value short_msg is required
	 */
	public void setshort_msg(String value) {
		short_msg = value;
	}

	/**
	 * long message description, long description of the problem, may be stack trace.
	 */
	public String long_msg = new String();
	/**
	 * get long message description
	 * @return long_msg
	 */
	public String getlong_msg() {
		return long_msg;
	}

	/**
	 * set long message description
	 * @param value long_msg is required
	 */
	public void setlong_msg(String value) {
		long_msg = value;
	}

	/**
	 * error code. Success code is always 0
	 */
	public int error_code;
	/**
	 * get error code
	 * @return error_code
	 */
	public int geterror_code() {
		return error_code;
	}

	/**
	 * set error code
	 * @param value error_code is required
	 */
	public void seterror_code(int value) {
		error_code = value;
	}

	/**
	 * specific error message. 
	 * For all errors, this value is always failed.
	 */
	public String error_message = new String();
	/**
	 * get specific error message. 
	 * @return error_message
	 */
	public String geterror_message() {
		return error_message;
	}

	/**
	 * set specific error message. 
	 * @param value error_message is required
	 */
	public void seterror_message(String value) {
		error_message = value;
	}

}


