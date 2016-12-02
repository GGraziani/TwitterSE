import crawler.CrawlData;
import crawler.Crawler;
import org.json.JSONArray;
import utils.Params;
import utils.Utils;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by GG on 28.11.16.
 */
public class IndexerApp {

    private Crawler crawler;


    public IndexerApp(Params p) {

        try {
            crawler = new Crawler(p);
        } catch (IOException e){
            e.printStackTrace();
        }

    }

    public static void main(String []args) throws IOException {
        Utils utils = new Utils();
        System.out.println("Running IndexerApp...(built time: "+utils.getDate()+")"+utils.delimiter());

        String paramFile = "";

        try {

            paramFile = args[0];
            System.out.println("Initializing setting:\n\tParameters: "+paramFile);
        } catch(Exception e){
            System.out.println(" caught a " + e.getClass() + "\n with message: " + e.getMessage());
            System.exit(1);
        }

        Params p = utils.readParamsFromXMLFile(paramFile);
        IndexerApp app = new IndexerApp(utils.readParamsFromXMLFile(paramFile));

        CrawlData cd = app.crawler.crawl(); // crawl new data
        utils.merge(cd); // merge new data with the existing data and overwrite the Data Store
    }

}