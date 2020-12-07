/**
 * 
 */
package com.Common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Manjula Madheswaran
 * Common Rest API Call File ( which has all the API's used in this project )
 * keywords within curly braces is to get the dynamic value 
 */

public class RestAPIDefinitions {
	 
	//Defined the logger
	public String loggerName = "automation";
	public Logger logger = LoggerFactory.getLogger(loggerName);
    
	// Status Response Code with result parameter true / false 
	public static final String sucessCode = "200" ;
	public static final String failureFactor = "false" ;
	

	// GET API's
	public static final String postCode = "http://postcodes.io/postcodes/{POSTCODE}";
	public static final String postCode_Validate = "http://postcodes.io/postcodes/{POSTCODE}/validate";
	public static final String postCode_Nearest = "http://postcodes.io/postcodes/{POSTCODE}/nearest";
	public static final String postCode_autoComplete = "http://postcodes.io/postcodes/{POSTCODE}/autocomplete";
	
}
