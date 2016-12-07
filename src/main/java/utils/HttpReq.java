package utils;

import java.io.*;
import java.net.*;
import java.util.Hashtable;
import java.util.Map;
import org.json.*;

/**
 * Created by GG on 25.11.16.
 */
public class HttpReq {

    private URL url;
    private String method;
    private Hashtable<String,String> headers;
    private HttpURLConnection connection;

    public HttpReq(String method, String url, Hashtable<String,String> headers){
        this.method = method;
        try{
            this.url = new URL(url);
        } catch (MalformedURLException e){
            e.printStackTrace();
        }
        this.headers = new Hashtable<String,String>();
        this.headers.putAll(headers);
        try{
            setConnection();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

//    public void newReq(String url){
//        try{
//            this.url = new URL(url);
//            setConnection();
//        } catch (MalformedURLException e){
//            e.printStackTrace();
//        } catch (IOException e){
//            e.printStackTrace();
//        }
//    }

    private void setConnection() throws IOException{
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);

        for (Map.Entry<String, String> h: headers.entrySet()) {
            String k = h.getKey();
            String v = h.getValue();
            connection.setRequestProperty(k,v);
        }
    }

    private String sendRequest() throws IOException {
        int responseCode = connection.getResponseCode();
        System.out.println("\t- Sending request to " + url+" ... status code " + responseCode);

        BufferedReader buff = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));

        String inputLine;
        StringBuilder res = new StringBuilder();

        while ((inputLine = buff.readLine()) != null) {
            res.append(inputLine);
        }
        buff.close();
//        System.out.println("Res:  "+res.toString());
        connection.disconnect();

        return res.toString();
    }

    public JSONArray reqTweets() throws IOException {
        String res = sendRequest();
        JSONObject obj = new JSONObject(res);
        return obj.getJSONArray("statuses");
    }

    public String reqOther() throws IOException {
        return sendRequest();
    }
    public JSONObject reqIds() throws IOException {
        String res = sendRequest();
        return new JSONObject(res);
    }

}