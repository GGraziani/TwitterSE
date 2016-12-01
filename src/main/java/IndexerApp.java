import crawler.CrawlData;
import crawler.Crawler;
import utils.Params;
import utils.Utils;

import java.io.IOException;

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
        System.out.println("Running IndexerApp...(built time: "+utils.getDate()+")");

        String paramFile = "";

        try {
            paramFile = args[0];
            System.out.println("\nInitializing setting:\n\tParameters: "+paramFile);
        } catch(Exception e){
            System.out.println(" caught a " + e.getClass() + "\n with message: " + e.getMessage());
            System.exit(1);
        }
        Params p = utils.readParamsFromXMLFile(paramFile);
        IndexerApp app = new IndexerApp(p);

        CrawlData cd = app.crawler.crawl();
//        int name = utils.getNumberOfFiles("./data/data_store")/2;
//        System.out.println("Filename = " + name + ".");
//        utils.writeToFile(cd.data, cd.indexData, "./data/data_store/"+name);
    }
}