package com.cs.zsb.ieee.ieeescanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;


public class ServerName extends AppCompatActivity {

    // Used for debugging
    private static final String TAG = "ServerName";

    // Indexes for recreating activity
    private static  final String KEY_INDEX_0 = "index0";
    private static  final String KEY_INDEX_1 = "index1";

    // Stings to index intent of this activity and the main activity(Scan activity)
    private static final String EXTRA_IP = "com.cs.zsb.ieee.ieeescanner.ip";
    private static final String EXTRA_PORT = "com.cs.zsb.ieee.ieeescanner.port";

    // Intent to send back to main activity(scan activity) with the values of ip, port
    private Intent data = new Intent ();

    // Represent the current ip, port all the time
    private String mIp_text;
    private String mPort_text;

    // Views reference for ip and port
    EditText ip;
    EditText port;


    // Get the ip from the intent outside of the class
    public static String getExtraIp(Intent result) {
        return result.getStringExtra(EXTRA_IP);
    }

    // Get the port from the intent outside of the class
    public static String getExtraPort(Intent result) {
        return result.getStringExtra(EXTRA_PORT);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server_name);

        ip = (EditText) findViewById(R.id.ip_text);
        port = (EditText) findViewById(R.id.port_text);

        // Initialize ip, port with the default value
        mIp_text = ScanActivity.IP;
        mPort_text = ScanActivity.PORT;

        ip.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String text;
                text = s.toString().replaceAll("\\s","");

                Log.d(TAG,"IP text " + text);


                if (!text.equals("")) {

                    mIp_text = text;
                    data.putExtra(EXTRA_IP, mIp_text);

                    mPort_text = port.getText().toString().replaceAll("\\s","");

                    if(!mPort_text.equals("")) {
                        data.putExtra(EXTRA_PORT, mPort_text);
                        setResult(RESULT_OK, data);
                    }

                    else{
                        mPort_text = ScanActivity.PORT;
                        data.putExtra(EXTRA_PORT, mPort_text);
                        setResult(RESULT_OK, data);
                    }
                }
                else{
                    mIp_text = ScanActivity.IP;

                    if(mPort_text.equals(""))
                        mPort_text = ScanActivity.PORT;

                    data.putExtra(EXTRA_IP, mIp_text);
                    data.putExtra(EXTRA_PORT, mPort_text);

                    setResult(RESULT_OK, data);
                }
            }
        });

        port.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String text;
                text = s.toString().replaceAll("\\s","");

                Log.d(TAG,"Port text " + text);

                if (!text.equals("")) {

                    mPort_text = text;
                    data.putExtra(EXTRA_PORT, mPort_text);

                    mIp_text = ip.getText().toString().replaceAll("\\s","");
                    if(!mIp_text.equals("")) {

                        data.putExtra(EXTRA_IP, mIp_text);
                        setResult(RESULT_OK, data);
                    }
                    else{

                        mIp_text = ScanActivity.IP;
                        data.putExtra(EXTRA_IP, mIp_text);
                        setResult(RESULT_OK, data);
                    }

                }else{

                    mPort_text = ScanActivity.PORT;

                    if(mIp_text.equals(""))
                        mIp_text = ScanActivity.IP;

                    data.putExtra(EXTRA_IP, mIp_text);
                    data.putExtra(EXTRA_PORT, mPort_text);

                    setResult(RESULT_OK, data);
                }

            }
        });

        if(savedInstanceState != null){

            mIp_text = savedInstanceState.getString(KEY_INDEX_0, "");
            mPort_text = savedInstanceState.getString(KEY_INDEX_1, "");

            ip.setText(mIp_text);
            port.setText(mPort_text);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){

        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putString(KEY_INDEX_0, mIp_text);
        savedInstanceState.putString(KEY_INDEX_1, mPort_text);
    }

}
