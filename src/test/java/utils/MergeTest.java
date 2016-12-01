package utils;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by GG on 01.12.16.
 */
public class MergeTest extends TestCase{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public MergeTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(MergeTest.class);
    }

    public void test_mergeSegments() throws IOException{

        int a = 0;
        int b = a+1;

        Utils utils = new Utils();

        String path = "./data/data_store";

        JSONArray A = new JSONArray(utils.readFileToString(path+"/"+a+".json"));
        ArrayList<String> indexA = utils.indexToArrayOfStrings(utils.readFileToString(path+"/"+a+".index"));

        JSONArray B = new JSONArray(utils.readFileToString(path+"/"+b+".json"));
        ArrayList<String> indexB = utils.indexToArrayOfStrings(utils.readFileToString(path+"/"+b+".index"));

        utils.mergeSegments(A,indexA,B,indexB);
        utils.writeToDataStore(A,indexA,path+"/"+a);
    }
}
