package utils;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

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
        return new TestSuite(DataStoreTest.class);
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
    public void test_retweets() throws IOException {
        Utils utils = new Utils();

        JSONArray A = new JSONArray(utils.readFileToString("./data/data_store/ds.json"));
        int a = 0;
        for(int i = 0 ; i<A.length();i++){
//            System.out.println(A.getJSONObject(i).get("retweet"));
            if (A.getJSONObject(i).get("retweet").toString().equals("false")){
                a++;
//                System.out.println("True");
            }
        }
        System.out.println(a+"/"+A.length());
    }

    public void test_deleteRetweets() throws IOException {
        Utils utils = new Utils();
        JSONArray A = new JSONArray(utils.readFileToString("./data/data_store/ds.json"));
        ArrayList<String> indexA = utils.indexToArrayOfStrings(utils.readFileToString("./data/data_store/ds.index"));

        int i = 0;

        while(i<indexA.size()){

            if(A.getJSONObject(i).get("retweet").equals(true)){
                A.remove(i);
                indexA.remove(i);
            } else{
                i++;
            }
        }
//        utils.writeToDataStore(A, indexA, "./data/data_store/ds"); // write the new data
    }

    public void test_hastags() throws IOException {
        Utils utils = new Utils();

        JSONArray A = new JSONArray(utils.readFileToString("./data/data_store/ds.json"));

        for(int i = 0 ; i<A.length();i++){

            try{
                A.getJSONObject(i).put("tweetImg",  A.getJSONObject(i).get("tweetImg").toString());
                System.out.println(i+": succesfull");
            } catch (Exception e) {
                System.out.println(i+": failed");
            }

//            JSONArray obj = A.getJSONObject(i).getJSONObject("entities").getJSONArray("hashtags");
//
//            JSONArray ht = new JSONArray();
//
//            for (int x = 0; x < obj.length(); x++) {
//                ht.put(obj.getJSONObject(x).get("text").toString());
//            }
//            A.getJSONObject(i).put("hastags", ht);
//
//            try {
//                String tweetImg = A.getJSONObject(i).getJSONObject("entities").getJSONArray("media").getJSONObject(0).get("media_url").toString();
//                A.getJSONObject(i).put("tweetImg", tweetImg);
//            } catch (Exception e) {
//            }
//            try {
//                A.getJSONObject(i).remove("extended_entities");
//            } catch (Exception e) {
//            }
//            try {
//                A.getJSONObject(i).remove("entities");
//            } catch (Exception e) {
//            }
        }
        test_writeFile(A, "data/data_store/ds.json");
    }


    public void test_tweetsImgs() throws IOException {
        Utils utils = new Utils();

        JSONArray data = new JSONArray(utils.readFileToString("./data/data_store/ds.json"));
        System.out.println(utils.getDate()+")"+utils.delimiter());

        int a = 890*9;
        for (int i = a; i < data.length() && i < a + 900; i++) {
            JSONObject obj = data.getJSONObject(i);
            String id = obj.get("id_str").toString();

            String url = "https://api.twitter.com/1.1/statuses/show.json?id="+id;

            Hashtable<String,String> token = new Hashtable<String, String>(){{put("Authorization","Bearer AAAAAAAAAAAAAAAAAAAAAKZcyAAAAAAAOnuY8gZEDgfWINEIaVJsYOmQfCg%3DWHBCTyjQMeJf5SKNi6fYEOR2jP5sJ0UeGi3IbS09E8TkqToBeD");}};
            try {
                HttpReq req = new HttpReq("GET", url, token);
                JSONObject ciao = req.reqIds();
                String user_img = ciao.getJSONObject("user").get("profile_image_url").toString().replace("_normal","");
                obj.put("user_img",user_img);
            }catch (Exception e){
                System.err.println("Error with: "+obj.get("url"));
            }
        }
        test_writeFile(data, "data/data_store/ds.json");
    }


    private void test_writeFile(JSONArray A, String path) throws IOException {
        FileWriter json = new FileWriter(path);
        try {
            json.write(A.toString());
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            json.flush();
            json.close();
        }
    }
}
