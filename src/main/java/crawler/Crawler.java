package crawler;

/**
 * Created by GG on 24.11.16.
 */

import org.apache.oro.text.regex.Util;
import org.json.*;
import utils.HttpReq;
import utils.Utils;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

import static com.sun.tools.javac.jvm.ByteCodes.ret;
import static java.awt.SystemColor.text;


public class Crawler {

    private Utils utils;
    private final Hashtable<String,String> token = new Hashtable<String, String>(){{put("Authorization","Bearer AAAAAAAAAAAAAAAAAAAAAKZcyAAAAAAAOnuY8gZEDgfWINEIaVJsYOmQfCg%3DWHBCTyjQMeJf5SKNi6fYEOR2jP5sJ0UeGi3IbS09E8TkqToBeD");}};
    private ArrayList<String> Hashtags;
    private ArrayList<String> badWords;
    private ArrayList<String> newHashtags;
    private ArrayList<String> hashtagsToRemove;
    private ArrayList<String> keyWords;

    public Crawler(String seedPath, String badWordsPath, String keyWordsPath) throws IOException {
        utils = new Utils();
        System.out.println();
        Hashtags = utils.readFileFromPath(seedPath);
        badWords = utils.readFileFromPath(badWordsPath);
        keyWords = utils.readFileFromPath(keyWordsPath);
        newHashtags = new ArrayList<String>();
        hashtagsToRemove = new ArrayList<String>();

    }

    public CrawlData crawl() throws IOException {

        JSONArray data = new JSONArray();
        ArrayList<String> indexData = new ArrayList<String>();

        for (String hashtag: Hashtags) {
            System.out.println("\nNew Query: "+hashtag);
            String url = "https://api.twitter.com/1.1/search/tweets.json?q=%23"+hashtag+"&count=100";
            HttpReq req = new HttpReq("GET", url, token);


            JSONArray tweets = req.reqTweets();
            System.out.println("n° of tweets: "+tweets.length());
            normalizeData(tweets, data, indexData, hashtag);


            System.out.println("\n"+"--------------------------------------------------------------------------------");
        }
//        System.out.println(data);
        System.out.println(indexData.size());

        return new CrawlData(data, indexData);
    }

    private void normalizeData(JSONArray tweets, JSONArray data, ArrayList<String> indexData, String hashtag){

        for (int i = 0; i < tweets.length(); i++) {
            JSONObject tweet = (JSONObject) tweets.get(i);

            if(!contains_obscenity(tweet)){
                if(isTopicRelated(tweet, hashtag)){
                    JSONObject obj = newObj(tweet);

                    data.put(obj);
                    indexData.add(obj.get("id_str").toString());

                    if(obj.get("retweet").equals(true)){
//                System.out.println("RE-TWEET FOUND");

                        JSONObject obj2 = newObj(tweet.getJSONObject("retweeted_status"));

                        int j = contains(obj2, indexData);
//                    System.out.println(j);
                        if( j > - 1){ // avoid duplicates
//                         modify existing obj with new data
                        } else{ // add original tweet
                            data.put(obj2);
                            indexData.add(obj2.get("id_str").toString());
                        }
                    }
//                    getNewHashtags(obj);
                } else {
//                    System.out.println("Not topic related: "+tweet.get("text"));
                }
            }
            else {
//                System.out.println("Obscenity Detected: " + tweet.get("text"));
            }
        }
//        System.out.println(data.toString());
//        System.out.println(indexData.toString());
    }

    private JSONObject newObj(JSONObject tweet){
        JSONObject obj = new JSONObject();
        obj.put("id_str", tweet.get("id_str"));
        obj.put("created_at", tweet.get("created_at"));
        obj.put("user", tweet.getJSONObject("user").get("name"));
        obj.put("screen_name", tweet.getJSONObject("user").get("screen_name"));
        obj.put("text", tweet.get("text"));
        obj.put("favorite_count", tweet.get("favorite_count"));
        obj.put("retweet_count", tweet.get("retweet_count"));
        obj.put("entities",tweet.getJSONObject("entities"));
        if(tweet.has("extended_entities"))
            obj.put("extended_entities",tweet.getJSONObject("extended_entities"));
        obj.put("retweet", isRetweet(tweet));
        return obj;
    }

    private int contains(JSONObject tweet, ArrayList<String> indexData){
        return indexData.indexOf(tweet.get("id_str").toString());
    }

    private boolean isRetweet(JSONObject tweet){
        return tweet.has("retweeted_status");
    }

    private boolean contains_obscenity(JSONObject tweet){
        String text = tweet.get("text").toString().toLowerCase();
        return findMatch(text, badWords);
    }

    private boolean isTopicRelated(JSONObject tweet, String hashtag){
        String text = tweet.get("text").toString().toLowerCase();
        text = utils.removeUrl(text);
        if(tweet.has("retweeted_status")){
            text = text.split(":")[1];
        }
        text = text.replace("#"+hashtag.toLowerCase(), "");
        if(text.length() < 10) return true;

        return findMatch(text, keyWords);
    }

    private boolean findMatch(String tweet, ArrayList<String> list){

        for (String s: list) {
            if(tweet.contains(s.toLowerCase())){
//                System.out.println("MATCH:"+s);
                return true;
            }
        }
        return false;
    }

//    private void getNewHashtags(JSONObject obj){
//       JSONArray ht = obj.getJSONObject("entities").getJSONArray("hashtags");
//        System.out.println("----HASHTAGS"+ht);
////        for (int i = 0; i < ht.length(); i++) {
//////            System.out.println(ht.getJSONObject(i).get("text"));
////        }
//    }

}