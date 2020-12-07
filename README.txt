OBJECTIVE:
Create a robust framework to validate REST API's

Pre-requisites:( all latest)
1) Java
2) IntelliJ / Eclipse
3) Maven
4) TestNG

PROGRAM STRUCTURE:
RestAPIDefinitions.java - All API's are defined in this file 
PostCode_Operations.java - Common java file to invoke the API calls 
PostCode_Main.java - @Test Main class where tests are defined
postCode.xml - suite file to input dynamic parameters and execute the test
postCode.xls - Input from the user

INPUT:
To provide an input please go to PostCode.xls and search for the comment

OUTPUT:
For now we are just printing the output in console
Please refer the path :
1) HTML report : /FeatureSpace-Task/test-output/PostCode Operations/UK Posal tCode Operations.html
2) console output : /FeatureSpace-Task/test-output/PostCode Operations/consolelog.txt

To Run:
Run via suite
1) Right click on the xml -> Exit vm arguments
2) set vm arguments : -Dtestng.dtd.http=true ( if its intelliJ -Make it as in teh brackets[ -ea -Dtestng.dtd.http=true ]
3) Trigger Run

Ref TimeOut :
/*RequestConfig configData = RequestConfig.custom()
			  .setConnectTimeout(timeout * 1000)
			  .setConnectionRequestTimeout(timeout * 1000)
			  .setSocketTimeout(timeout * 1000).build();
			CloseableHttpClient client =
			  HttpClientBuilder.create().setDefaultRequestConfig(config).build();*/

			/*RestAssuredConfig config = RestAssured.config()
					.httpClient(HttpClientConfig.httpClientConfig()
							.setParam(CoreConnectionPNames.CONNECTION_TIMEOUT, 1000)
							.setParam(CoreConnectionPNames.SO_TIMEOUT, 1000));*/

			/*HttpParams httpParams = httpClient.getParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, timeout * 1000);
			HttpConnectionParams.setSoTimeout(httpParams, timeout * 1000);*/


				//RequestSpecification httpRequest=given();
				//Response response = httpRequest.config(config).request(Method.GET));


