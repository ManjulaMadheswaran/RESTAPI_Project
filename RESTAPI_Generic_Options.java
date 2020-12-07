package com.Implementation;

import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.json.simple.JSONObject;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class RESTAPI_Generic_Options {
    @Test
    void bddtest_GET1(){
        Long MAX_TIMEOUT = 2000l;
        ResponseSpecBuilder resBuilder = new ResponseSpecBuilder();
        resBuilder.expectResponseTime(Matchers.lessThan(MAX_TIMEOUT));
        RestAssured.responseSpecification = resBuilder.build();

        Response response = (Response) given()
                .get("https://reqres.in/api/users?delay=3")
                .then()
                .statusCode(200)
                .body("data.id[1]",equalTo(8))
                .body("data.first_name",hasItems("Michael"))
                .log().all(); // logs complete data
        System.out.println(response.getTime());

    }

    @Test
    public void test1_post(){
        JSONObject jsonObj= new JSONObject() ;
        jsonObj.put("name","Manju");
        jsonObj.put("job","Engineer");
        System.out.println(jsonObj);

        //when some serialization not happening error
        System.out.println(jsonObj.toJSONString());

        given()
                .header("Content-Type","application/json")
                .contentType(ContentType.JSON) //just for expecting json data
                .accept(ContentType.JSON) //just for expecting json data
                .body(jsonObj.toJSONString())
                .when()
                .post("https://reqres.in/api/users")
                .then()
                .statusCode(201);
    }

    @Test
    public void test1_put(){
        JSONObject jsonObj= new JSONObject() ;
        jsonObj.put("name","Aravind");
        jsonObj.put("job","Engineer");

        System.out.println(jsonObj);

        //when some serialization not happening error
        System.out.println(jsonObj.toJSONString());

        given()
                .header("Content-Type","application/json")
                .contentType(ContentType.JSON) //just for expecting json data
                .accept(ContentType.JSON) //just for expecting json data
                .body(jsonObj.toJSONString())
                .when()
                .put("https://reqres.in/api/users/2")
                .then()
                .statusCode(200)
                .log().all();
    }

    @Test
    public void test1_patch(){
        JSONObject jsonObj= new JSONObject() ;
        jsonObj.put("name","Aravind");
        jsonObj.put("job","Engineer");

        System.out.println(jsonObj);

        //when some serialization not happening error
        System.out.println(jsonObj.toJSONString());

        given()
                .header("Content-Type","application/json")
                .contentType(ContentType.JSON) //just for expecting json data
                .accept(ContentType.JSON) //just for expecting json data
                .body(jsonObj.toJSONString())
                .when()
                .patch("https://reqres.in/api/users/2")
                .then()
                .statusCode(200)
                .log().all();
    }
    @Test
    public void test1_delete(){
        when()
                .patch("https://reqres.in/api/users/2")
                .then()
                .statusCode(200) //204 is for no content
                .log().all();
    }


}


/* RestAssuredConfig config = RestAssured.config()
        .httpClient(HttpClientConfig.httpClientConfig()
                .setParam(CoreConnectionPNames.CONNECTION_TIMEOUT, 3000)
                .setParam(CoreConnectionPNames.SO_TIMEOUT, 3000));
*/
//validations
//refer sample restassured documentation site.
//https://github.com/rest-assured/rest-assured/wiki/Usage

// given().config(config)
// .header("content-Type","application/json")