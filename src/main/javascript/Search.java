import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by GG on 05.12.16.
 */
public class Search {

    private ArrayList<String> query;
    private IndexReader indexReader;
    private ArrayList<String> stopword = new ArrayList<String>(Arrays.asList("a", "an","and", "are","as", "at", "be",
            "by","for","from","has","he","in","is","it","its","of","on","that","the","to","was","were","will","with"));

    public Search(String []args) throws IOException {
        String[] str = args[0].substring(1,args[0].length()-1).split(" ");

        query = new ArrayList<String>();

        for(String s : str){
            query.add(s);
        }
        Directory indexDirectory = FSDirectory.open(new File("../../../index").toPath()); // if called from js server

//        query.add("#Trump");
//        query.add("is");
//        query.add("the");
//        query.add("president");
//        query.add("of");
//        query.add("the");
//        query.add("USA");
//        Directory indexDirectory = FSDirectory.open(new File("./index").toPath()); // for tests
        indexReader = DirectoryReader.open(indexDirectory);
    }

    public void stdout(String message) {
        System.out.println(message);
    }

    private JSONArray SearchIndex() throws IOException {
        int indexSize = indexReader.numDocs();
        ArrayList<String> copyQuery = new ArrayList<String>();
        for(String s : query){
            if(! stopword.contains(s.toLowerCase())){
                copyQuery.add(s);   // copy the initial query removing stopwords
            }
        }
        JSONArray results = new JSONArray();
        for (int i=0; i<indexSize; i++) {
            JSONObject obj = parseDoc(indexReader.document(i));
            JSONArray matches = new JSONArray();
            for (String s: copyQuery){
                if((obj.get("user").toString()+obj.get("screen_name").toString()+obj.get("text").toString()).toLowerCase().contains(s.toLowerCase())){
                    try {
//                        matches.put(new JSONObject("{ match :'"+s+"}"));
                        matches.put(s);
                    } catch (Exception e){}
                }
            }
            if (matches.length()>0){
                obj.put("matches", matches);
                double likes = (double) Integer.parseInt(obj.get("favorite_count").toString());
                double retweets = (double) Integer.parseInt(obj.get("retweet_count").toString());
                int matchPow = matches.length()*matches.length();
                obj.put("score", matchPow+((likes+retweets)/10000));
                results.put(obj);
            }
        }
        return results;
    }

    private JSONObject parseDoc(Document doc){
        JSONObject obj = new JSONObject();
        obj.put("screen_name", doc.getField("screen_name").stringValue());
        obj.put("id_str", doc.getField("id_str").stringValue());
        obj.put("created_at", doc.getField("created_at").stringValue());
        obj.put("favorite_count", Integer.parseInt(doc.getField("favorite_count").stringValue()));
        obj.put("hashtags", doc.getField("hashtags").stringValue());
        obj.put("text", doc.getField("text").stringValue());
        obj.put("user", doc.getField("user").stringValue());
        obj.put("retweet_count", Integer.parseInt(doc.getField("retweet_count").stringValue()));
        obj.put("url", doc.getField("url").stringValue());
        try {
            obj.put("tweetImg", doc.getField("tweetImg").stringValue());
        } catch (Exception e) {}
        try {
            obj.put("user_img", doc.getField("user_img").stringValue());
        } catch (Exception e) {}
        return obj;
    }

    public static void main(String []args) throws IOException {
        Search search = new Search(args);

        JSONArray res = search.SearchIndex();
        search.stdout(""+res.toString());
    }
}