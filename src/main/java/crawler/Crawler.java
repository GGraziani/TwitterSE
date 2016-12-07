package crawler;

/**
 * Created by GG on 24.11.16.
 */

import org.json.*;
import utils.HttpReq;
import utils.Params;
import utils.Utils;

import java.io.IOException;
import java.util.*;


public class Crawler {

    private Utils utils;
    private Params p;
    private final Hashtable<String,String> token = new Hashtable<String, String>(){{put("Authorization","Bearer AAAAAAAAAAAAAAAAAAAAAKZcyAAAAAAAOnuY8gZEDgfWINEIaVJsYOmQfCg%3DWHBCTyjQMeJf5SKNi6fYEOR2jP5sJ0UeGi3IbS09E8TkqToBeD");}};
    private ArrayList<String> Hashtags;
    private ArrayList<String> badWords;
    private ArrayList<String> newHashtags;
    private ArrayList<String> hashtagsToRemove;
    private ArrayList<String> keyWords;
    private int count;
    private int tweetsCount;

//    public Crawler(String seedPath, String badWordsPath, String keyWordsPath) throws IOException {
//        utils = new Utils();
//        System.out.println();
//        Hashtags = utils.readFileToArrayList(seedPath);
//        badWords = utils.readFileToArrayList(badWordsPath);
//        keyWords = utils.readFileToArrayList(keyWordsPath);
//        newHashtags = new ArrayList<String>();
//        hashtagsToRemove = new ArrayList<String>();
//    }

    public Crawler(Params p) throws IOException {
        utils = new Utils();
        this.p = p;
        Hashtags = utils.readFileToArrayList(p.seed);
        badWords = utils.readFileToArrayList(p.lists+"/badwords");
        keyWords = utils.readFileToArrayList(p.lists+"/key_words");
        count = p.numOfTweets;
        newHashtags = new ArrayList<String>();
        hashtagsToRemove = new ArrayList<String>();
    }

    public CrawlData crawl() throws IOException {
        JSONArray data = new JSONArray();
        ArrayList<String> indexData = new ArrayList<String>();
        int c=0;

        for (String hashtag: Hashtags) {
            tweetsCount = 0;
            System.out.println("Crawling element "+c+++": #"+hashtag);
            String url = "https://api.twitter.com/1.1/search/tweets.json?q=%23"+hashtag+"&count="+count;

            HttpReq req = new HttpReq("GET", url, token);

            JSONArray tweets = req.reqTweets();

            normalizeData(tweets, data, indexData, hashtag);
        }
//        System.out.println("New Hastags:"+newHashtags.toString());
//        System.out.println("TO REMOOOVE:"+hashtagsToRemove);

        System.out.println("\nTotal retrieved after filtering (no duplicates): "+indexData.size());

        return new CrawlData(data, indexData);
    }

    private void normalizeData(JSONArray tweets, JSONArray data, ArrayList<String> indexData, String hashtag){
        System.out.println("\t- Retrieved: "+tweets.length());
        ArrayList<String> nht = new ArrayList<String>();

        for (int i = 0; i < tweets.length(); i++) {
            JSONObject tweet = (JSONObject) tweets.get(i);

            if(!contains_obscenity(tweet)){
                if(isTopicRelated(tweet, hashtag)){
                    tweetsCount++;
                    JSONObject obj;

                    if (isRetweet(tweet)){
                        obj = newObj(tweet.getJSONObject("retweeted_status"), hashtag);
                    } else{
                        obj = newObj(tweet, hashtag);
                    }

                    int j = containsObj(obj, indexData); // object already in data store?
                    if( j > - 1){
                        utils.updateObject(data.getJSONObject(j),obj); // modify existing obj with new data
                    } else{ // add original tweet
                        data.put(obj);
                        indexData.add(obj.get("id_str").toString());
                    }

//                    for (String s:getNewHashtags(obj)){
//                        addNoDuplicate(s, nht);
//                    }
                }
            }
        }
        System.out.println("\t- After filtering: "+tweetsCount);

//        int alpha = (((tweetsCount*100)/tweets.length()) + ((tweets.length()*100)/count ))/2;
//        if( alpha > 50 ){
//            for (String s : nht){
//                addNoDuplicate(s, newHashtags);
//            }
//        } else{
//            System.out.println("---->>>>NO GOOOD");
//            hashtagsToRemove.add(hashtag);
//        }

    }

    private JSONObject newObj(JSONObject tweet, String hashtag){
        JSONObject obj = new JSONObject();
        obj.put("id_str", tweet.get("id_str"));
        obj.put("created_at", tweet.get("created_at"));
        obj.put("user", tweet.getJSONObject("user").get("name"));
        obj.put("user_img",tweet.getJSONObject("user").get("profile_image_url").toString().replace("_normal",""));
        try {
            obj.put("tweetImg", tweet.getJSONObject("entities").getJSONArray("media").getJSONObject(0).get("media_url").toString());
        } catch (Exception e){
//            System.out.println("tweetImg not found");
        }
        obj.put("screen_name", tweet.getJSONObject("user").get("screen_name"));
        obj.put("text", tweet.get("text"));
        obj.put("favorite_count", tweet.get("favorite_count"));
        obj.put("retweet_count", tweet.get("retweet_count"));

        JSONArray hts = tweet.getJSONObject("entities").getJSONArray("hashtags");
        JSONArray ht = new JSONArray();
        for (int x = 0; x < hts.length(); x++) {
            ht.put(hts.getJSONObject(x).get("text").toString());
        }
        if (!ht.toList().contains(hashtag)){
            ht.put(hashtag);
        }
        obj.put("hashtags",ht);

        obj.put("url","https://Twitter.com/"+tweet.getJSONObject("user").get("screen_name").toString()+"/status/"+tweet.get("id_str"));
        obj.put("retweet", isRetweet(tweet));

        return obj;
    }

    private int containsObj(JSONObject tweet, ArrayList<String> indexData){
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
        int urlLenght = text.length() - utils.removeUrl(text);
        if(tweet.has("retweeted_status")){
            text = text.split(":")[1];
        }
        text = text.replace("#"+hashtag.toLowerCase(), "");
        if((text.length()-urlLenght) < 10) return true;

        return findMatch(text, keyWords);
    }

    private boolean findMatch(String tweet, ArrayList<String> list){

        for (String s: list) {
            if(tweet.contains(s.toLowerCase())){
                return true;
            }
        }
        return false;
    }

    private boolean findMatch2(String tweet, ArrayList<String> list){

        for (String s: list) {
            if(s.toLowerCase().contains(tweet)){
                return true;
            }
        }
        return false;
    }

//    private ArrayList<String> getNewHashtags(JSONObject obj){
//        ArrayList<String> nht = new ArrayList<String>();
//        JSONArray hts = obj.getJSONObject("entities").getJSONArray("hashtags");
//        String ht;
//        for (int i = 0; i < hts.length(); i++) {
//            ht = hts.getJSONObject(i).get("text").toString();
//            addNoDuplicate(ht, nht);
//        }
//        return nht;
//    }
//
//    private void addNoDuplicate(String s, ArrayList<String> arr){
//        if (!findMatch2(s.toLowerCase(), arr)){
//            arr.add(s);
//        }
//    }
}