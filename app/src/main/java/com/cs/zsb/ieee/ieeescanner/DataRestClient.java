package com.cs.zsb.ieee.ieeescanner;


import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;


class DataRestClient{

    private static final String TAG = "DataRestClient";

     String sendId(String id, String ip, String port){

        try {
            // Assign the url to the server where the get operation will be executed

            URL url = new URL("http://" + ip + ":" + port +"/check");
            //("http://192.168.1.3:8080/lang");

            //making connection with the server
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // making the connection last for one second
            // To make it stop if it couldn't connect
            conn.setConnectTimeout(3000);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setReadTimeout(3000);


            // By default it is GET request but we need a POST request here
            conn.setRequestMethod("POST");

            //add request header
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            // This is the string in jason format to be posted to the server
            String input = "{\"_id\":\"" + id + "\"}";

            // Sending post request to the server
            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.close();

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output; // the string that will get the values from the input stream
            StringBuilder response = new StringBuilder(); // This will have the full string in jason format

            // reading the full string and then will be put in "response" variable
            while ((output = br.readLine()) != null) {
                response.append(output);
            }

            conn.disconnect(); // Closing connection with the server
            return response.toString(); // returning the full string to get the values from it


        }catch (ConnectException ce){

            Log.e(TAG, "Error happened while connecting to the server\"Probably wrong server name\"", ce);
            return null; //returning null on failure
        }
        catch (IOException ioe) {

            // Getting error message that defines where the error happened
            Log.e(TAG, "Error happened while connecting to the server", ioe);
            return null; //returning null on failure
        }
         catch (Exception e){

             Log.e(TAG, "Error happened while connecting to the server ", e);
             return null; //returning null on failure
         }
    }


     Boolean[] confirmId(String result) {

        //Enter if the operation was success
        if (result != null){


            try {

                /*
                making a jason object of the returned string to extract the values from
                the string as it is in jason format
                */
                JSONObject jObj = new JSONObject(result);

                Boolean confirmArray[] = new Boolean[2];

                // Get the accepted and attended values
                confirmArray[0] = jObj.getBoolean("accepted");
                confirmArray[1] = jObj.getBoolean("attended");

                // Get the name
                ScanActivity.mName = jObj.getString("fullname");

                return confirmArray;


            } catch (JSONException je) {

                // Something went Wrong with parsing
                Log.e(TAG, "Failed to parse JSON", je);

                return  null;
            }

        }

        return null;
    }
}
