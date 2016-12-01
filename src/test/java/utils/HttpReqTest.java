package utils;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.*;
import java.util.Hashtable;
import org.json.*;

/**
 * Created by GG on 25.11.16.
 */

public class HttpReqTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public HttpReqTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(HttpReqTest.class);
    }

    public void test_TwitterCheckResponse() throws IOException {
        String method = "GET";
        String url = "https://api.twitter.com/1.1/search/tweets.json?q=%23Trump&count=100";
        Hashtable<String,String> headers = new Hashtable<String, String>();
        headers.put("Authorization","Bearer AAAAAAAAAAAAAAAAAAAAAKZcyAAAAAAAOnuY8gZEDgfWINEIaVJsYOmQfCg%3DWHBCTyjQMeJf5SKNi6fYEOR2jP5sJ0UeGi3IbS09E8TkqToBeD");

        HttpReq httpRec = new HttpReq(method, url, headers);
        JSONArray objArray = httpRec.reqTweets();
    }

    public void test_getImage() throws IOException{
        String method = "GET";
        String url = "https://www.nasa.gov/sites/default/files/styles/image_card_4x3_ratio/public/thumbnails/image/pia20645_main.jpg?itok=dLn7SngD";
        HttpReq httpRec = new HttpReq(method, url, new Hashtable<String, String>());
        System.out.println(httpRec.reqOther());
    }
}