package utils;

import crawler.CrawlData;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.xml.bind.JAXB;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by GG on 28.11.16.
 */
public class Utils {

    public Params p;

    public Utils(){
        // Nothing to do
    }

    public ArrayList<String> readFileToArrayList(String path) throws IOException {
        BufferedReader bufReader = new BufferedReader(new FileReader(path));
        ArrayList<String> lines = new ArrayList<String>();
        String line = bufReader.readLine();
        while (line != null) {
            lines.add(line);
            line = bufReader.readLine();
        }
        bufReader.close();

        return lines;
    }

    public int removeUrl(String commentstr) {
        String str = commentstr;
        String urlPattern = "((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern p = Pattern.compile(urlPattern,Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(str);
        try{
            int i = 0;
            while (m.find()) {
                str = str.replaceAll(m.group(i),"").trim();
                i++;
            }

        } catch (Exception e){

        }
        return str.length();
    }

    public void writeToDataStore(JSONArray array, ArrayList<String> index, String Path) throws IOException {

        FileWriter json = new FileWriter(Path+".json");
//        System.out.println(Path+".json");
        FileWriter indexFile = new FileWriter(Path+".index");
        try {
            json.write(array.toString());
            indexFile.write(index.toString());
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            json.flush();
            indexFile.flush();
            json.close();
            indexFile.close();
        }
    }

    public String readFileToString(String path) throws IOException {
        BufferedReader bufReader = new BufferedReader(new FileReader(path));
        String str = "";
        String line = bufReader.readLine();
        while (line != null) {
            str+=line;
            line = bufReader.readLine();
        }
        bufReader.close();
        return str;
    }

    public int getNumberOfFiles(String Path){
        File dir = new File(Path);
        File[] list = dir.listFiles();
        return list.length;
    }

    public String getDate(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public Params readParamsFromXMLFile(String indexParamFile){
        try {
            p = JAXB.unmarshal(new File(indexParamFile), Params.class);
        } catch (Exception e){
            System.out.println(" caught a " + e.getClass() +
                    "\n with message: " + e.getMessage());
            System.exit(1);
        }

        System.out.println("\t- Data Store: " + p.DS);
        System.out.println("\t- Seed File: " + p.seed);
        System.out.println("\t- Other Resources: " + p.lists);
        System.out.println("\t- Number of tweets/req: " + p.numOfTweets+delimiter());
        return p;
    }

    public String delimiter(){
        return "\n--------------------------------------------------------------------------------";
    }

    public void mergeSegments(JSONArray A, ArrayList<String> indexA, JSONArray B, ArrayList<String> indexB){

//        int ot = 0;
        for (int i=0; i < B.length(); i++){
            String curr = indexB.get(i);
            int x;
            if((x = indexA.indexOf(curr)) != -1){
                updateObject(A.getJSONObject(x), B.getJSONObject(i));
            } else{
                if (B.getJSONObject(i).get("retweet").toString().equals("false")){
//                    ot++;
                }
                A.put(B.get(i));
                indexA.add(curr);
            }
        }
//        System.out.println(ot);
    }

    public void updateObject(JSONObject A, JSONObject B){

        if( Integer.parseInt(A.get("favorite_count").toString()) < Integer.parseInt(B.get("favorite_count").toString())){
            A.put("favorite_count", B.get("favorite_count"));
        }

        if( Integer.parseInt(A.get("retweet_count").toString()) < Integer.parseInt(B.get("retweet_count").toString())){
            A.put("retweet_count", B.get("retweet_count"));
        }
    }


    public ArrayList<String> indexToArrayOfStrings(String s){
        s = s.substring(1,s.length()-1);

        String[] array = s.split(", ");

        return new ArrayList<String>(Arrays.asList(array));
    }

    public JSONArray merge(CrawlData cd) throws IOException {
        JSONArray DS = new JSONArray(readFileToString(p.DS+"/ds.json"));
        ArrayList<String> indexDS = indexToArrayOfStrings(readFileToString(p.DS+"/ds.index"));
        int oldSize = indexDS.size();
        mergeSegments(DS,indexDS,cd.data,cd.indexData);
        int actualSize = indexDS.size();
        System.out.println("Total added to DS: "+(actualSize-oldSize));

        writeToDataStore(DS, indexDS, p.DS+"/ds"); // write the new data
        return DS;
    }
}

