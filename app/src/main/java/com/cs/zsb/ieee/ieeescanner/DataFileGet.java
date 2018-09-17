package com.cs.zsb.ieee.ieeescanner;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
//import java.io.FileInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


class DataFileGet {

    private static final String TAG = "DataFileGet";


    private Context mContext;
    private Toast mToast;

    private String allId = null;

    private boolean check = true;

     DataFileGet(Context context){
        this.mContext = context;
    }

     void read() {


         Log.d(TAG, "Read");

         if(check) {

             BufferedReader reader = null;

             try {


                 reader = new BufferedReader(
                         new InputStreamReader(mContext.getAssets().open("query_result.json")));

                 // do reading, loop until end of file reading
                 String output;
                 StringBuilder response;

                 response = new StringBuilder(); // This will have the full string in jason format

                 while ((output = reader.readLine()) != null) {
                       response.append(output);

                 }

                 allId = response.toString();

                 check = false;

                 FileOutputStream fileOutput = mContext.openFileOutput("attended.json", Context.MODE_APPEND);
                 OutputStreamWriter outputWriter = new OutputStreamWriter(fileOutput);
                 outputWriter.close();


             } catch (IOException e) {
                 Log.e(TAG, "IOException", e);

             } finally {

                 if (reader != null) {

                     try {

                         reader.close();

                     } catch (IOException e) {

                         Log.e(TAG, "IOException", e);
                     }
                 }
             }
         }
    }

     boolean getResult(String id){

         Log.d(TAG, "Get Rresult");

         if(!readExternal(id)) {

             try {

                 JSONArray jsonArray = new JSONArray(allId);

                 for (int i = 0; i < jsonArray.length(); ++i) {
                     JSONObject obj = jsonArray.getJSONObject(i);

                     String x = obj.getString("_id");

                     if (x.equals(id)) {

                         ScanActivity.mName = obj.getString("fullname");

                         Log.d(TAG, ScanActivity.mName);

                         writeExternal(x);

                         ScanActivity.mAlreadyHere = false;

                         return true;
                     }
                 }

                 return false;

             } catch (JSONException je) {

                 check = true;
                 Log.e(TAG, "Failed to parse JSON", je);
                 makeToast("\"Something Went Wrong \\\"Parsing\\\" just try again\"");


                 return false;
             }
         }else{

             Log.d(TAG, "Returned");
             return false;
         }
    }


     private boolean readExternal(String id){

         File file = new File(mContext.getFilesDir() + "\\attended.csv");

         if(file.exists()) {

             try {

                 String csvFilename = mContext.getFilesDir() + "\\attended.csv";
                 CSVReader csvReader = new CSVReader(new FileReader(csvFilename));
                 String[] row;

                 while ((row = csvReader.readNext()) != null) {

                     if (id.equals(row[0])) {

                         ScanActivity.mName = row[1];
                         ScanActivity.mAlreadyHere = true;

                         return true;
                     }
                 }

                 Log.e(TAG, "readExternal");
                 csvReader.close();

                 ScanActivity.mAlreadyHere = false;
                 return false;


             } catch (IOException ioe) {

                 Log.d(TAG, "IOExFileRead", ioe);
                 makeToast("\"Something Went Wrong \\\"Reading File\\\" just try again\"");

                 ScanActivity.mAlreadyHere = false;
                 return false;

             }
         }

         return false;
     }



     private void writeExternal(String id){

         Log.e(TAG, "writeExternal");
         try {

             String csv = mContext.getFilesDir() + "\\attended.csv";
             CSVWriter writer = new CSVWriter(new FileWriter(csv, true));

             String [] person = (id + "#" + ScanActivity.mName).split("#");

             writer.writeNext(person);

             writer.close();

         } catch (IOException ioe) {

             Log.d(TAG, "IOExFileWrite", ioe);
             makeToast("\"Something Went Wrong \\\"Writing to file\\\" just try again\"");

         }
     }

    private void makeToast(String print){

        if (mToast != null)
            mToast.cancel(); // cancel last toast

        mToast = Toast.makeText(mContext, print, Toast.LENGTH_SHORT);
        mToast.show(); // show current toast
    }

}
