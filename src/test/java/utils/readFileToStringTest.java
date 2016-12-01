package utils;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;
import org.json.*;


/**
 * Created by GG on 01.12.16.
 */
public class readFileToStringTest extends TestCase {

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public readFileToStringTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(readFileToStringTest.class);
    }


    public String test_readFileToString(String url) throws IOException{
        Utils utils = new Utils();
        String str = utils.readFileToString(url);
//        System.out.println(str);
        return str;
    }

//    public void test_addUrl() throws IOException{
//        String url = "data/data_store/16.json";
//        String str = test_readFileToString(url);
//        JSONArray arr = new JSONArray(str);
//        for(int x= 0; x< arr.length();x++){
//            JSONObject js = arr.getJSONObject(x);
//            arr.getJSONObject(x).put("url","https://Twitter.com/"+js.get("screen_name").toString()+"/status/"+js.get("id_str"));
//        }
////        System.out.println(arr.toString());
//        test_writeToFile(arr,url);
//    }

    public void test_writeToFile(JSONArray array, String Path) throws IOException {

        FileWriter json = new FileWriter(Path);
        try {
            json.write(array.toString());
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            json.flush();
            json.close();
        }
    }


}
