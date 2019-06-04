import java.util.Scanner;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import java.io.InputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.json.JSONArray;


import javax.net.ssl.HttpsURLConnection;

public class application {

    public static void main(String[] args) throws Exception  {

        String expresion = "";
        String username = "";
        String password = "";
        boolean auth = false;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the webshop console client.");
        System.out.println("=== Menu:");
        System.out.println("login");
        System.out.println("register %USERNAME%");
        System.out.println("exit");

        // loop while exit is false
        while(!expresion.contains("exit")){
            String input = scanner.nextLine();
//            System.out.println(input);
            String[] inputList = input.split(" ");
            expresion = inputList[0];
            if(expresion.contains("login")){
                System.out.println("username:");
                username = scanner.nextLine();
                System.out.println("password:");
                password = scanner.nextLine();
                HttpResponse response = sendPost("http://localhost:51243/api/login", "{\"username\":\""+username+"\",\"password\":\""+password+"\"} " );

                HttpEntity entity = response.getEntity();

                // Read the contents of an entity and return it as a String.
                String content = EntityUtils.toString(entity);
//                System.out.println(content);

                JSONObject json = new JSONObject(content);
                String inventory =  json.get("inventory").toString();
                for (String s : inventory.split(";")){
                    System.out.println(s);
                }
//                JSONArray key = json.names ();
//                for (int i = 0; i < key.length (); ++i) {
//                    String keys = key.getString (i);
//                    String value = json.getString (keys);
//                    System.out.println(value);
//                }
                if(response.getStatusLine().getStatusCode() == 201){
                    auth =  true;
                    System.out.println("login was a succese");
                    System.out.println("type main to continue");
                }

            } else if(expresion.contains("register")){
                System.out.println("registered " + inputList[1] );
                HttpResponse response = sendPost("http://localhost:51243/api/users", "{\"username\":\""+inputList[1]+"\"} " );
                HttpEntity entity = response.getEntity();
                String content = EntityUtils.toString(entity);
//


                if(response.getStatusLine().getStatusCode() == 201){
                    JSONObject json = new JSONObject(content);
                    JSONArray key = json.names ();
                    for (int i = 0; i < key.length (); ++i) {
                        String keys = key.getString (i);
                        String value = json.get(keys).toString();
                        System.out.println(keys +" = " + value);
                    }
                } else {
                    System.out.println("server response : " + content);
                }

                // Read the contents of an entity and return it as a String.

            } else if(expresion.contains("exit")){
                System.out.println("========  exit  ========");
            } else if(expresion.contains("main") && auth ==  true){
                while(!expresion.contains("exit")) {

                    loadMenu();

                    getUser(username, password);

                    System.out.println("===");

                    input = scanner.nextLine();
                    System.out.println(input);
                    inputList = input.split(" ");
                    expresion = inputList[0];

                    if(expresion.contains("buy")){
                        HttpResponse response = sendPost("http://localhost:51243/api/buy", "{\"username\":\""+username +"\",\"producttitle\":\""+inputList[1]+"\"} "  );
                        HttpEntity entity = response.getEntity();

                        // Read the contents of an entity and return it as a String.
                        String content = EntityUtils.toString(entity);
                        System.out.println(content);


                    } else if(expresion.contains("refresh")){
                        System.out.println("refreshed your content");
                    }

                }
            }
        }

    }

    private static void getUser(String username, String password) throws Exception{
        HttpResponse response = sendPost("http://localhost:51243/api/login", "{\"username\":\""+ username +"\",\"password\":\""+ password +"\"} " );
        HttpEntity entity = response.getEntity();

        // Read the contents of an entity and return it as a String.
        String content = EntityUtils.toString(entity);
        System.out.println("=== your items:");
        JSONObject json = new JSONObject(content);
        String inventory =  json.get("inventory").toString();
        for (String s : inventory.split(";")){
            System.out.println(s);
        }
    }

    private static void loadMenu() throws Exception{

        System.out.println("========  Webwinkel  ========");
        System.out.println("Menu:");
        System.out.println("buy %ITEM%");
        System.out.println("refresh");

        HttpResponse response = sendGet("http://localhost:51243/api/products" );
        System.out.println("=== items to buy:");
        HttpEntity entity = response.getEntity();

        // Read the contents of an entity and return it as a String.
        String content = EntityUtils.toString(entity);
//        System.out.println(content);

        JSONArray jsonarray = new JSONArray(content);
       // JSONArray jsonarray = json.getJSONArray(0);
        for (int i = 0; i < jsonarray.length (); ++i) {
            JSONObject item = jsonarray.getJSONObject(i);
            JSONArray key = item.names ();
            String s = "";
            for (int ii = 0; ii < key.length (); ++ii) {
                String keys = key.getString (ii);
                String value = item.get(keys).toString();
                s += keys +" = " + value +" | ";
            }
            System.out.println(s);

        }

    }
    // HTTP GET request
    private static HttpResponse sendGet(String url) throws Exception {
        HttpClient httpClient = HttpClientBuilder.create().build(); //Use this instead
        HttpGet request = new HttpGet(url);

        request.addHeader("content-type", "application/json");
        HttpResponse response = httpClient.execute(request);
        return response;

    }

    // HTTP POST request
    private static HttpResponse sendPost(String url, String par) throws Exception {
        HttpClient httpClient = HttpClientBuilder.create().build(); //Use this instead
        StringEntity params = new StringEntity(par);
        HttpPost request = new HttpPost(url);

        request.addHeader("content-type", "application/json");
        request.setEntity(params);
        HttpResponse response = httpClient.execute(request);
        return response;

    }


}

