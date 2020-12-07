package com.APIOperations;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.hamcrest.Matchers;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.*;
import org.testng.Assert;
import org.testng.SkipException;

import com.Common.RestAPIDefinitions;

//import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;

/**
 * @author Manjula Madheswaran
 * API Invoking File
 */
public class PostCode_Operations {
	
	// Variable declaration and Initialisation
	public static String loggerName = "automation";
    public static Logger logger=LoggerFactory.getLogger(loggerName);
	
	//constructor
	public PostCode_Operations() {
		//object instantiation
	}
	
	/**
	* GET Operation - To fetch post code details
	* @param postCode, postCodeActionType
	* @return Retrieve Post Code
	*/

	public Map<String, Object> getPostCodeDetails(String postCode,String postCodeActionType)
	{
	
	//Variable Declaration and Initialisation

	String methodName =Thread.currentThread().getStackTrace()[1].getMethodName();
	logger.debug("\n>>CallingMethod - Starts >>" +"Method - " + methodName +"\n\n");

	Map<String, Object> resultantData = new HashMap<String, Object>();
	Map<String, Object> nearest = new HashMap<String, Object>();
	String url = "",responseBody="",failureMessage="";

	try
	{
		if(postCodeActionType.equalsIgnoreCase("Validate")) {
			url = RestAPIDefinitions.postCode+"/validate";
		}else if(postCodeActionType.equalsIgnoreCase("postCode")) {
			url = RestAPIDefinitions.postCode;
		}else if(postCodeActionType.equalsIgnoreCase("Nearest")) {
			url = RestAPIDefinitions.postCode+"/nearest";
		}else if(postCodeActionType.equalsIgnoreCase("autoComplete")) {
			url = RestAPIDefinitions.postCode+"/autocomplete";
		}else {
			Assert.fail("Invalid postCodeActionType options. Expected : Validate / postCode / Nearest , Retrieved : "+postCodeActionType);
		}
		
		url=url.replace("{POSTCODE}",postCode);
	
		//URL to fetch the expected Data
		logger.info("API CALL -> "+ url);
	
		//Performing GET Operation
		String response = sendHttpRequest(url, "GET");
	
		 JSONParser parser = new JSONParser();
		 JSONObject jsonData = (JSONObject) parser.parse(response);
		 logger.debug("Fetched Response Code : "+jsonData);
		 logger.debug("Fetched Response Code : "+jsonData.get("status"));
		
		if(RestAPIDefinitions.sucessCode.equalsIgnoreCase(jsonData.get("status").toString()))
		{
			if(jsonData.get("result")!= null) {
			responseBody = jsonData.get("result").toString();
			logger.debug("Fetched Response Body : "+responseBody);
		
			if(responseBody.equalsIgnoreCase("true")) {
				logger.info("\n\n=========== Validation is Successful ==============\n");
				resultantData.put("status", "success");
			}else if(responseBody.equalsIgnoreCase("false")) {
				resultantData.put("Status", "Failure");
				logger.info("Validation is failed for given API :"+url+" Status Code: "+jsonData.get("status").toString()+" Response :"+responseBody);
			}else {
				if(!responseBody.equalsIgnoreCase(null) || !responseBody.isEmpty()) {
					if(responseBody.contains("[")){
						List<Object> nearestData = (List<Object>) jsonData.get("result");
						for (int i = 0; i < nearestData.size(); i++) {            
							logger.debug("Nearest ->"+nearestData.get(i));
							nearest.put(String.valueOf(i), nearestData.get(i));
						}
						resultantData.putAll(nearest);
					}else
						resultantData.putAll((Map<String,Object>) jsonData.get("result"));
				}else {
					Assert.fail("Invalid response retrieved for API :\n"+url+"\n Status Code: "+jsonData.get("status").toString()+"\n Response :"+responseBody);
				}
			}	}else {
				    logger.info("Invalid Post Code - "+postCode );
					Assert.fail("Invalid Post Code , retrieved response for API :\n"+url+"\n Status Code: "+jsonData.get("status").toString()+"\n Response :"+responseBody);
				}
				
			}
		else {
			failureMessage="Retrieved failed status code for given API :\n"+url+"\n Status Code: "+jsonData;
			logger.info(failureMessage);
			Assert.fail(failureMessage);
		}
	}
	catch(Exception ex) {
		ex.printStackTrace();
		throw new SkipException("Exception found in the common function while fetching the data"+ex.getMessage());
	}
	    logger.debug("\n>>CallingMethod - Ends >>" +"Method - " + methodName +"\n\n");
		return resultantData;
	}
	
	/**
	 * API Calls - To Perform REST Operations - GET / POST / PUT / DELETE
	 * @param url
	 * @param actionType
	 * @return
	 */
	public static String sendHttpRequest(String url,String actionType)
	{
		//Variable Declarations and Initialisations
		String methodName =Thread.currentThread().getStackTrace()[1].getMethodName();
		logger.debug("\n>>CallingMethod - Starts >>" +"Method - " + methodName );
		String resultResponse="";
		try {
			Long MAX_TIMEOUT = 2000l;
			ResponseSpecBuilder resBuilder = new ResponseSpecBuilder();
			resBuilder.expectResponseTime(Matchers.lessThan(MAX_TIMEOUT));
			RestAssured.responseSpecification = resBuilder.build();

			if(actionType.equalsIgnoreCase("GET")) {
				Response response = (Response) given().get(url);
				System.out.println("Time Taken for the api call in ms :"+response.getTime());

				resultResponse =  response.getBody().asString();
				logger.info("Retrieved Http response:"+resultResponse);	

			}else {
				//To do POST or PUT or DELETE actionTypes .
			}	
		}
		catch(Exception ex) {
			throw new SkipException("Found Generic Exception while performing http operation: " + ex.getMessage());
		}
		logger.debug("\n>>CallingMethod - Ends >>" +"Method - " + methodName );
		return resultResponse;		
	}

	public List<String> splitStringWithDelimiter(String data, String delimiter){
		List<String> dataSplit = new ArrayList<String>();
		String[] split=data.split(delimiter);
		dataSplit.add(split[0]);
		dataSplit.add(split[1]);

	return dataSplit;
	}

	public synchronized List<List<String>> readExcelSheet(String workBookPath, String workSheet) throws IOException {
		List<List<String>> excelData = new ArrayList<List<String>>();
		HSSFWorkbook workbook = null;
		FileInputStream file = null;
		try
		{
			file = new FileInputStream(new File(workBookPath));
			if(workBookPath.endsWith(".xls")){
				//Create Workbook instance holding reference to .xlsx file
				workbook = new HSSFWorkbook(file);
			}else{
			   Assert.fail("WorkBook " + workBookPath + "has unSupported extension.Should be .xls");
			}

			/* Get first/desired sheet from the workbook */
			HSSFSheet sheet = workbook.getSheet(workSheet);

			//Iterate through each rows one by one
			Iterator<Row> rowIterator = sheet.iterator();
			while (rowIterator.hasNext())
			{
				//List to iterate and store the row data
				List<String> rowData = new ArrayList<String>();

				Row row = rowIterator.next();
				logger.debug("Row -->"+row);

				//For each row, iterate through all the columns
				Iterator<Cell> cellIterator = row.cellIterator();
				DataFormatter df = new DataFormatter();
				logger.debug("cellIterator -->"+cellIterator);
				while (cellIterator.hasNext())
				{
					Cell cell = cellIterator.next();
					logger.debug("cell -->"+cell);
					//Check the cell type and format accordingly
					if(cell!=null){
						if(cell.getCellType().equals(0)){
							rowData.add(df.formatCellValue(cell));
						}else {
							rowData.add(cell.getStringCellValue().trim());
						}
					}else{
						// if the cell has null value then add empty string
						rowData.add("");
					}
				}
				if(rowData.size() != 0){
					excelData.add(rowData);
				}
				logger.debug("excelData--->"+excelData);
			}
			file.close();
		}
		catch (FileNotFoundException Fe)
		{
			Fe.printStackTrace();
			throw new SkipException("Exception - Failed to read excel workbookPath ["+workBookPath+":"+workSheet+"]. Please verify.");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new SkipException("Exception while reading workbookPath ["+workBookPath+":"+workSheet+"]. Please verify");
		}

		if(workbook != null){
			workbook.close();
		}
		if(file !=null){
			file.close();
		}

		return excelData;
}

	/**
	 * API - To Create testData Map
	 * @param testData
	 * @param testLevel
	 * @param dataSets
	 * @return
	 */

	public LinkedHashMap<String, LinkedHashMap<String, String>> testData_createMap(List<List<String>> testData, String testLevel, String dataSets)
	{
		String methodName =Thread.currentThread().getStackTrace()[1].getMethodName();
		logger.debug(">>CallingMethod - Starts >>" +"Method - " + methodName );

		LinkedHashMap<String, LinkedHashMap<String, String>> testDataMap = new LinkedHashMap<String, LinkedHashMap<String, String>>();
		try
		{
			int numberOfRows = testData.size(); //Number of rows
			int startRow = 1;
			List<String> headerRow1 = testData.get(0); //Get header row 1.
			List<String> headerRowWithLabels = new ArrayList<String>();
			List<String> listOfDataSets = new ArrayList<String>();

			headerRowWithLabels = headerRow1;

			// DataSet does not contains all [ In future will write enhancement for regular expressions support ]
			if(!dataSets.equalsIgnoreCase("ALL"))
			{
				listOfDataSets = Arrays.asList(dataSets.split(","));
				logger.debug("Test Data Sets to be executed: " + listOfDataSets);
			}

			//DataSet contains All
			testData_All(startRow, numberOfRows,testData,listOfDataSets,testLevel,dataSets, headerRowWithLabels, testDataMap);
			logger.debug("Test data map contains the following keys: " + testDataMap.keySet());

			logger.debug(">>CallingMethod - Ends >>" +"Method - " + methodName );
		}
		catch(Exception e)
		{
			throw new SkipException("Exception while creating the test data map. Stack trace follows...", e);
		}
		return testDataMap;
	}

	/**
	 * Is string empty?
	 * @param data	String to be checked.
	 * @return true if string is empty, else false.
	 */

	public boolean isStringEmpty(String data)
	{
		boolean dataEmpty = false;
		try
		{
			if(data == null || data.equals(""))
				dataEmpty = true;
			else
				dataEmpty = false;
		}
		catch(Exception e)
		{
			logger.error("Exception while checking if the following string is empty " + data + "\n. Stack trace follows...");
			logger.error("Stack trace:", e);
		}
		return dataEmpty;
	}

	/**
	 * TO get the test data ( Non regex )
	 * @param startRow
	 * @param numberOfRows
	 * @param testData
	 * @param listOfDataSets
	 * @param testLevel
	 * @param dataSets
	 * @param headerRowWithLabels
	 * @param testDataMap
	 */

	public void testData_All(int startRow,int numberOfRows,List<List<String>> testData,List<String> listOfDataSets, String testLevel, String dataSets,List<String> headerRowWithLabels,LinkedHashMap<String, LinkedHashMap<String, String>> testDataMap)
	{
		String methodName =Thread.currentThread().getStackTrace()[1].getMethodName();
		logger.debug(">>CallingMethod - Starts >>" +"Method - " + methodName );

		for(int row = startRow; row < numberOfRows; row++) //Get for each data set (row)
		{
			List<String> testDataRow = testData.get(row);
			logger.debug("testDataRow->"+testDataRow);

			LinkedHashMap<String, String> testDataPerDataSet = new LinkedHashMap<String, String>();
			String dataSet = testDataRow.get(0); //Data Set
			logger.debug("dataSet->"+dataSet);

			String testDataRow_testLevel = testDataRow.get(1); //Test Level
			logger.debug("testDataRow_testLevel->"+testDataRow_testLevel);
			//logger.debug("Processing row with data set " + dataSet + " and test level " + testDataRow_testLevel);

			if(isStringEmpty(dataSet))
			{
				logger.error("Found a row with data set value empty. Skipping it for now. Please correct it in the test data sheet.");
				continue;
			}

			if(!testDataRow_testLevel.contains(testLevel))
			{
				//logger.debug("Data set " + dataSet + " is ignored. Test level " + testLevel + " doesn't match with the supported test levels [" + testDataRow_testLevel + "] of the data set.");
				continue;
			}

			if(!listOfDataSets.isEmpty() && !listOfDataSets.contains(dataSet))
			{
				//logger.debug("Data set " + dataSet + " is ignored since it is not among the data sets specified for usage: " + dataSets);
				continue;
			}

			int numberOfColumns = headerRowWithLabels.size();
			logger.debug("Number of columns --> " + numberOfColumns);

			for(int column = 2; column < numberOfColumns; column++)
			{
				String label = headerRowWithLabels.get(column); //Get label from 0th row of that column.
				if(!testDataPerDataSet.containsKey(label)) {//Add only if it already doesn't contain an entry for the same label.
					testDataPerDataSet.put(label, removeSpecialCharacters(testDataRow.get(column)));
				    logger.debug("testDataPerDataSet->"+testDataPerDataSet);}
				else
					logger.warn("Label " + label + " is duplicated for the data set " + dataSet + ". Please correct the test data sheet.");
			}

			if(!testDataMap.containsKey(dataSet)) // Add only if it already doesn't contain a map for the same data set
				testDataMap.put(dataSet, testDataPerDataSet);
			    logger.debug("testDataMap->"+testDataMap);
		}
		logger.debug(">>CallingMethod - Ends >>" +"Method - " + methodName );
	}

	/**
	 * Remove special characters from the string - Starting and Trailing spaces, carriage returns, line and tab characters.
	 * @param data	String
	 * @return Processed String
	 */
	public String removeSpecialCharacters(String data)
	{
		try
		{
			if(!isStringEmpty(data))
			{
				data = data.trim();
				data = data.replaceAll("\n", "");
				data = data.replaceAll("\t", "");
				data = data.replaceAll("\r", "");
			}
		}
		catch(Exception e)
		{
			logger.error("Exception while trimming special characters for: " + data + ". \nStack trace follows:\n", e);
			data = "AUTOMATION_ERROR";
		}
		return data;
	}

	/**
	 * Create data provider for the test suite.
	// * @param testSuite
	 * @return 2 dimensional array
	 */

	public Object[][] testData_createDataProvider(LinkedHashMap<String, LinkedHashMap<String, String>> testData, String internalTestCode)
	{
		Object[][] testData_dataProvider = null;
		try
		{
			testData_dataProvider = new Object[testData.size()][2];
			String testDataSet = "",mappedTestCode = "";
			int row = 0;

			for(Map.Entry<String, LinkedHashMap<String, String>> testCase : testData.entrySet())
			{
				testDataSet = testCase.getKey(); //Test Data Set name
				logger.debug("Test Data Set-->"+testDataSet);

				mappedTestCode = testDataSet + ":" + internalTestCode + "_" + (row + 1);
				logger.debug("Test Data Set " + testDataSet + " mapping not found. Returning a default code " + mappedTestCode);

				testData_dataProvider[row][0] = mappedTestCode;
				testData_dataProvider[row][1] = testCase.getValue();
				row++;
			}
			logger.debug("Created a data provider of " + row + "test case(s).");

		}
		catch(Exception e)
		{
			logger.error("Detected exception while creating the data provider.", e);
		}
		return testData_dataProvider;
	}

}
