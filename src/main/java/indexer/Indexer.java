package indexer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.SimpleFSDirectory;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.Params;
import utils.Utils;
import java.io.File;
import java.io.IOException;


import static org.apache.lucene.index.IndexWriterConfig.OpenMode.CREATE;


/**
 * Created by GG on 02.12.16.
 */
public class Indexer {

    private String indexPath;
    private JSONArray data;
    private IndexWriter indexWriter;
    private Params p;
    private Utils utils;

    public Indexer(String indexPath, JSONArray data, Params p) {
        this.indexPath = indexPath;
        this.data = data;
        indexWriter = null;
        this.p = p;
        utils = new Utils();
    }

    public void createIndex() throws IOException {
        System.out.println(utils.delimiter());
        System.out.println("Starting indexing process ...");
        openIndex();
        addDocuments(data);
        finish();
    }

    @SuppressWarnings("Since15")
    public boolean openIndex() throws CorruptIndexException, LockObtainFailedException, IOException {
        try {
            File file = new File(indexPath);
            SimpleFSDirectory dir = new SimpleFSDirectory(file.toPath());
            Analyzer analyzer = new StandardAnalyzer();

            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
//            //Always overwrite the directory
            iwc.setOpenMode(CREATE);
            indexWriter = new IndexWriter(dir, iwc);

            return true;

        } catch (Exception e) {
            System.err.println("Error opening the index. " + e.getMessage());
        }
        return false;
    }

    private void addDocuments(JSONArray data) throws IOException {
        /**
         * Add documents to the index
         * field to save :
         * screen_name
         * id_str
         * created_at
         * favorite_count
         * hastags
         * text
         * user
         * retweet_count
         * url
         * tweetImg
         */
        System.out.println(data.length()+" document to index");

        for (int i = 0; i < data.length(); i++) {

            JSONObject obj = data.getJSONObject(i);
            Document doc = new Document();
            for(String field : obj.keySet()){
                if(!field.equals("retweet")){
                    Class type = obj.get(field).getClass();
                    if(type.equals(String.class))
                        doc.add(new StringField(field, (String)obj.get(field), Field.Store.YES));
                    else if(type.equals(Integer.class))
                        doc.add(new StringField(field, obj.get(field).toString(), Field.Store.YES));
                    else if(type.equals(JSONArray.class))
                        doc.add(new StringField(field, obj.get(field).toString(), Field.Store.YES));
                }
            }
            try {
                indexWriter.addDocument(doc);
            } catch (IOException ex) {
                System.err.println("Error adding documents to the index. " + ex.getMessage());
            }
        }
    }

    /**
     * Write the document to the index and close it
     */
    public void finish(){
        try {
            indexWriter.commit();
            indexWriter.close();
            System.out.println("Index writted succesfully.");
        } catch (IOException ex) {
            System.err.println("We had a problem closing the index: " + ex.getMessage());
        }
    }
}
