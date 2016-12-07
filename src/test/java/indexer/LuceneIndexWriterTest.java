package indexer;

import apple.laf.JRSUIConstants;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.json.JSONArray;
import utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by GG on 01.12.16.
 */
public class LuceneIndexWriterTest extends TestCase{

    static final String INDEX_PATH = "./index";
    Utils utils = new Utils();


    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public LuceneIndexWriterTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(LuceneIndexWriterTest.class);
    }

//    public void testWriteIndex(){
//        try {
//            Indexer lw = new Indexer(INDEX_PATH, new JSONArray(utils.readFileToString("./data/data_store/ds1.json")));
//            lw.createIndex();
//            //Check the index has been created successfully
//            Directory indexDirectory = FSDirectory.open(new File(INDEX_PATH).toPath());
//
////            SimpleFSDirectory dir = new SimpleFSDirectory(new File(INDEX_PATH).toPath());
//            IndexReader indexReader = DirectoryReader.open(indexDirectory);
//            int numDocs = indexReader.numDocs();
//            assertEquals(numDocs, 1);
//            for ( int i = 0; i < numDocs; i++)
//            {
//                Document document = indexReader.document( i);
//                System.out.println( "d=" +document);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public void testQueryLucene1() throws IOException {
        Directory indexDirectory = FSDirectory.open(new File(INDEX_PATH).toPath());
        IndexReader indexReader = DirectoryReader.open(indexDirectory);
        final IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        Term t = new Term("favorite_count", "3");
        Query query = new TermQuery(t);
        TopDocs topDocs = indexSearcher.search(query, 10);
        System.out.println(topDocs.totalHits);
        assertEquals(1, topDocs.totalHits);
    }

    public static void testQueryLucene2() throws IOException{
        Directory indexDirectory = FSDirectory.open(new File(INDEX_PATH).toPath());
        IndexReader indexReader = DirectoryReader.open(indexDirectory);
        System.out.println(indexReader.numDocs());
        int numDocs = indexReader.numDocs();

//        for (int i=0; i<10; i++) {
//            Document doc = indexReader.document(i);
//            System.out.println(doc.get("hastags"));
////            String docId = doc.get("docId");
//        }
//
//        String searchString = "";
//
//        System.out.println("Searching for '" + searchString + "'");
//        Directory indexDirectory = FSDirectory.open(new File(INDEX_PATH).toPath());
//        IndexReader indexReader = DirectoryReader.open(indexDirectory);
//        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
//
//        Analyzer analyzer = new StandardAnalyzer();
//        QueryParser queryParser = new QueryParser(FIELD_CONTENTS, analyzer);
//        Query query = queryParser.parse(searchString);
//        Hits hits = indexSearcher.search(query);
//        System.out.println("Number of hits: " + hits.length());
//
//        Iterator<JRSUIConstants.Hit> it = hits.iterator();
//        while (it.hasNext()) {
//            Hit hit = it.next();
//            Document document = hit.getDocument();
//            String path = document.get(FIELD_PATH);
//            System.out.println("Hit: " + path);
//        }
//
    }
}

