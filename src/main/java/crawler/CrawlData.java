package crawler;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by GG on 28.11.16.
 */
public class CrawlData {

    public JSONArray data;
    public ArrayList<String> indexData;
    public ArrayList<String> newHashtags;

    public CrawlData(JSONArray data, ArrayList<String> indexData){

        this.data = data;
        this.indexData = new ArrayList<String>();
        this.indexData.addAll(indexData);
    }
}
