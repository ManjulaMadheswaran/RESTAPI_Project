package com.Implementation;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.APIOperations.PostCode_Operations;

/**
 * @author Manjula Madheswaran Implementation Main File
 */
public class PostCode_Main {

// Variable declaration and Initialisation
	public String loggerName = "automation";
	public Logger logger = LoggerFactory.getLogger(loggerName);

	public String testData = "", testDatasets = "";
	protected PostCode_Operations postCodeOperation;
	static String cookie = "";

	LinkedHashMap<String, LinkedHashMap<String, String>> testDataMap = new LinkedHashMap<String, LinkedHashMap<String, String>>();
	LinkedHashMap<String, List<LinkedHashMap<String, String>>> testSuiteMap = new LinkedHashMap<String, List<LinkedHashMap<String, String>>>();

	@BeforeClass
	@Parameters({ "testData", "testDataSets", "level" })
	public void configuration(String testData, String testDataSets, String level) throws Exception {
		//testData and testDataSets and level are the parameters to pass the input value via excel

		postCodeOperation = new PostCode_Operations();

		List<String> testData_Arguments = postCodeOperation.splitStringWithDelimiter(testData, ":");
		List<List<String>> testData_Array = postCodeOperation.readExcelSheet(testData_Arguments.get(0),testData_Arguments.get(1));
		this.testDataMap = postCodeOperation.testData_createMap(testData_Array, level, testDataSets);
	}

	// Inputs are given here
	@DataProvider(name = "postCode")
	public Object[][] postCodeValidations() throws Exception {
		 return postCodeOperation.testData_createDataProvider(testDataMap, "postCode");
		//return new Object[][] { { "CB30FA" },{"IG11"} ,{ "CB30FA00000"},{ "CB3  0FA" },{"CB30FA "} ,{ " CB30FA"},{"xx"}};
	}

	/**
	 * Validating postal code of UK
	 * @throws Exception
	 */
	@Test(dataProvider = "postCode")
	//public void postCodeValidations(String postCode) throws Exception {
	public void postCodeValidations(String mappedTestCode, Map<String, String> parameters) throws Exception, InterruptedException
	{
		// Variable declaration and Initialisation
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		logger.debug("\n>> API  - Starts >>" + "Method - " + methodName + "\n");

		boolean flag = false;
		String failureMessage = "";
		Map<String, Object> getPostCodeDetails = new HashMap<String, Object>();
		try {

			// Fetching input details from input sheet
			logger.info("\n********************************************\n");
			String postCode = parameters.get("PostCode");
			logger.info("User given postCode : " + postCode);
			
			postCode = postCode.replace(" ", "");

			// Fetch post code data
			String[] postCodeActions = { "Validate", "postCode", "Nearest", "autoComplete"};
			
			//Validate API
			getPostCodeDetails = postCodeOperation.getPostCodeDetails(postCode, postCodeActions[0]);
			logger.debug("getPostCodeDetails ->" + getPostCodeDetails);

			// Invoke Validated API
			if (getPostCodeDetails.containsKey("status") && getPostCodeDetails.get("status").toString().contains("success")) {
				getPostCodeDetails = postCodeOperation.getPostCodeDetails(postCode, postCodeActions[1]);
				logger.debug("getPostCodeDetails to validate ->" + getPostCodeDetails);

				logger.info("\n\n========== Valid Postal Code Details below : ==============\n");
				logger.info("PostCode : " + getPostCodeDetails.get("postcode").toString());
				logger.info("Country : " + getPostCodeDetails.get("country").toString());
				logger.info("Region : " + getPostCodeDetails.get("region").toString());
			
				// Invoke Nearest PostCodes API
				if (getPostCodeDetails.size() != 0) {
					logger.info("\n\n========== Nearest Valid Postal Codes ========");
					getPostCodeDetails = postCodeOperation.getPostCodeDetails(postCode, postCodeActions[2]);
					logger.debug("getPostCodeDetails to validate ->" + getPostCodeDetails);
					for (int i = 0; i < getPostCodeDetails.size(); i++) {

						logger.debug("getPostCodeDetails to validate ->" + getPostCodeDetails);

						Map<String, Object> nearestData = (Map<String, Object>) getPostCodeDetails.get(String.valueOf(i));
						logger.debug("getPostCodeDetails to nearestData ->" + nearestData);

						logger.info("----------------");
						logger.info("PostCode : " + nearestData.get("postcode").toString());
						logger.info("Country : " + nearestData.get("country").toString());
						logger.info("Region : " + nearestData.get("region").toString());
					
						flag=true;
					}
				}
			}else {
				//autocomplete post code suggestions
				getPostCodeDetails = postCodeOperation.getPostCodeDetails(postCode, postCodeActions[3]);
				logger.debug("getPostCodeDetails to validate ->" + getPostCodeDetails);
				
				logger.info("----------------");
				logger.info("Your postCode is not quite right.Please look for the suggested postCodes matching your entry:");
				
				logger.info("Suggested PostCode : " );
				for(Map.Entry entry:getPostCodeDetails.entrySet()){
				    logger.info(entry.getKey() + " : " + entry.getValue());
				    flag=true;
				}
			}

		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
			throw new SkipException("Generic exception observed during the run ->" + e);
		}

		logger.debug(">> API  - Ends >>" + "Method - " + methodName + "\n\n");
		Assert.assertEquals(flag, true, failureMessage);

	}

}
