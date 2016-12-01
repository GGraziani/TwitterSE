package utils;

import org.eclipse.jetty.util.IO;
import org.json.JSONArray;

import javax.xml.bind.JAXB;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    public ArrayList<String> readFileFromPath(String path) throws IOException {
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

    public String removeUrl(String commentstr) {
        String urlPattern = "((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern p = Pattern.compile(urlPattern,Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(commentstr);
        try{
            int i = 0;
            while (m.find()) {
                commentstr = commentstr.replaceAll(m.group(i),"").trim();
                i++;
            }

        } catch (Exception e){
            System.out.println("------->"+commentstr);
        }
        return commentstr;
    }

    public void writeToFile(JSONArray array, ArrayList<String> index, String Path) throws IOException {

        FileWriter json = new FileWriter(Path+".json");
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

        System.out.println("\tData Store: " + p.DS);
        System.out.println("\tSeed File: " + p.seed);
        System.out.println("\tOther Resources: " + p.lists);
        System.out.println("\tNumber of tweets/req: " + p.numOfTweets);

        return p;
    }

//    public ArrayList<String> readFileListFromFile(){
//        /*
//            Takes the name of a file (filename), which contains a list of files.
//            Returns an array of the filenames (to be indexed)
//         */
//
//        String filename = p.fileList;
//
//        ArrayList<String> files = new ArrayList<String>();
//
//        try {
//            BufferedReader br = new BufferedReader(new FileReader(filename));
//            try {
//                String line = br.readLine();
//                while (line != null){
//                    files.add(line);
//                    line = br.readLine();
//                }
//
//            } finally {
//                br.close();
//            }
//        } catch (Exception e){
//            System.out.println(" caught a " + e.getClass() +
//                    "\n with message: " + e.getMessage());
//        }
//        return files;
//    }

}

