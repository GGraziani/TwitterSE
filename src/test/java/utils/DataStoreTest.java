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
public class DataStoreTest extends TestCase{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public DataStoreTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(MergeTest.class);
    }

    public void test_id_objRelation() throws IOException{
        Utils utils = new Utils();

        JSONArray A = new JSONArray(utils.readFileToString("./data/data_store/ds.json"));
        ArrayList<String> indexA = utils.indexToArrayOfStrings(utils.readFileToString("./data/data_store/ds.index"));

        for(int i = 0; i<A.length();i++){
            if (A.getJSONObject(i).get("id_str").toString().equals(indexA.get(i))){
//                System.out.println("True");
            } else{
                System.out.println(A.getJSONObject(i).get("id_str")+"   :   "+indexA.get(i));
            }
        }

    }
}
