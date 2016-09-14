package br.com.nopredio.util;

/**
 * Created by Andersom on 12/07/2016.
 */
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONfunctions {

    public static JSONObject getJSONfromURL(String urli) {
        InputStream is = null;
        String result = "";
        JSONObject jArray = null;

        // Download JSON data from URL
        try {
            java.net.URL url = new java.net.URL(urli);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

// read the response
            System.out.println("Response Code: " + conn.getResponseCode());
            InputStream in = new BufferedInputStream(conn.getInputStream());
            result = org.apache.commons.io.IOUtils.toString(in, "UTF-8");

        } catch (Exception e) {
            Log.e("log_tag", "Error in http connection " + e.toString());
        }


        try {

            jArray = new JSONObject(result);
        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }

        return jArray;
    }

    public static JSONObject sendJSONfromURL(Object objeto, String urli) {
        InputStream is = null;
        String result = "";
        JSONObject jArray = null;


        try{
            ObjectMapper mapper = new ObjectMapper();
            //Object to JSON in String
            String jsonInString = mapper.writeValueAsString(objeto);
            jArray = new JSONObject(jsonInString);

            URL url = new URL(urli);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            OutputStream os = conn.getOutputStream();
            os.write(jArray.toString().getBytes());
            os.flush();

            // read the response
            System.out.println("Response Code: " + conn.getResponseCode());
            InputStream in = new BufferedInputStream(conn.getInputStream());
            result = org.apache.commons.io.IOUtils.toString(in, "UTF-8");

            jArray = new JSONObject(result);
        } catch(JsonProcessingException e){
            Log.e("log_tag", "Error parsing data " + e.toString());
        } catch (Exception e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }


        return jArray;
    }
}