package com.cs.zsb.ieee.ieeescanner;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;



public class ScanActivity extends AppCompatActivity {

    // For Debugging
    private static final String TAG = "ScanActivity";

    // Constants determine which sound will be played
    private static final String CONFIRM = "confirm";
    private static final String OVER = "over";

    // Server Default attributes
    public static final String IP = "192.168.1.10";
    public static final String PORT = "8080";

    // Name of the attendant
    public static String mName;
    // If the attendant already here
    public static Boolean mAlreadyHere = false;

    // Which button the user pressed
    private int mButtonNumber = 0;

    // Current used ip, port
    private String mUsedIp = IP;
    private String mUsedPort = PORT;

    // The id of the attendant
    private String mId;

    // Server request to the server activity
    private static final int REQUEST_SERVER_NAME = 22;

    // Intent to the camera activity
    private IntentIntegrator qrScan;
    // Text of the scanned qr code
    String mQrScannedText;

    // Instances to get fom the server and file
    DataRestClient client = new DataRestClient();
    DataFileGet fileConfirm = new DataFileGet(ScanActivity.this);

    // References to the background view and edit text view that will show the name
    TextView mNameView;
    FrameLayout mFrameLayout;

    // Making toast variable to use it to make and cancel toasts
    Toast mToast;

    // media player to play sounds
    MediaPlayer mp = null;

    // Async task reference to stop on leaving
    AsyncTask mAsyncTask;

    //AnimationDrawable frameAnimation = null;

    @Override
    protected void onStart(){
        super.onStart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        Button mButtonScanCode;
        Button mButtonScanFile;

        mButtonScanCode = (Button) findViewById(R.id.button_scan_code);
        mButtonScanFile = (Button) findViewById(R.id.button_scan_file);

        mNameView = (TextView) findViewById(R.id.view_name);
        mFrameLayout = (FrameLayout) findViewById(R.id.layout_background);

        // Get the background, which has been compiled to an AnimationDrawable object.
        //frameAnimation = (AnimationDrawable) mFrameLayout.getBackground();

        // Make the intent with the camera
        qrScan = new IntentIntegrator(this);

        mButtonScanCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Start the scan using file

                mFrameLayout.setBackgroundResource(R.drawable.mutex);
                mNameView.setText("");

                mButtonNumber = 0;
                qrScan.initiateScan();

            }
        });

        mButtonScanFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Start the scan using file

                mFrameLayout.setBackgroundResource(R.drawable.mutex);
                mNameView.setText("");

                mButtonNumber = 1;
                qrScan.initiateScan();

            }
        });

        mFrameLayout.setOnLongClickListener(new View.OnLongClickListener () {

            @Override
            public boolean onLongClick(View v) {

                // Revert to normal screen on long press

                Log.d(TAG, "out");

                mFrameLayout.setBackgroundResource(R.drawable.mutex);

                return true;
               /*
                if(frameAnimation != null) {


                    if(!frameAnimation.isRunning()) {

                        Log.d(TAG, "in");

                        mNameView.setText("");


                        mFrameLayout.setBackgroundResource(R.drawable.anim_mutex_main);
                        frameAnimation = (AnimationDrawable) mFrameLayout.getBackground();

                        // Start the animation (looped playback by default).
                        frameAnimation.start();

                    }
                    else{

                        mNameView.setText("");

                        frameAnimation.stop();
                        mFrameLayout.setBackgroundResource(R.drawable.mutex);
                    }

                    return true;
                }
                return true;
                */
            }
        });


        if (savedInstanceState != null) {

            Log.e(TAG, "Save instance state");
            //mQrScannedText = savedInstanceState.getString(KEY_INDEX, null);

        }

    }

    // Make the menu with edit icon
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_server, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Start the server name activity if the edit icon was clicked
        switch (item.getItemId()) {
            case R.id.menu_item_enter_server:

                Intent i = new Intent(this, ServerName.class);
                startActivityForResult(i, REQUEST_SERVER_NAME);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }


    // Cancel the task if the the app was closed
    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (mAsyncTask != null)
            mAsyncTask.cancel(true);
    }

    // Determine what happen after the sub activity closed
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // The activity is the server name activity
        if (requestCode == REQUEST_SERVER_NAME) {

            if (data == null) {

                mUsedIp = IP;
                mUsedPort = PORT;

                Log.d(TAG, "dataI null");
                Log.d(TAG, mUsedIp);
                Log.d(TAG, mUsedPort);

                return;

            } else {

                mUsedIp = ServerName.getExtraIp(data);
                mUsedPort = ServerName.getExtraPort(data);

                Log.d(TAG, "dataI not null");
                Log.d(TAG, mUsedIp);
                Log.d(TAG, mUsedPort);

                return;
            }
        }

        // The activity is the camera scan
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {

            //if QRCode has nothing in it
            if (result.getContents() == null) {

                makeToast("Result Not Found");

            } else {

                // Use the server button_0 was pressed
                if (mButtonNumber == 0) {

                    mQrScannedText = result.getContents();
                    mId = mQrScannedText;

                    mAsyncTask = new ConfirmTask().execute();

                    // Use the file button_0 was pressed
                } else if (mButtonNumber == 1) {

                    Log.d(TAG, "Button_1 pressed");
                    mQrScannedText = result.getContents();
                    mId = mQrScannedText;

                    fileConfirm.read();
                    if (fileConfirm.getResult(mId)) {

                        mFrameLayout.setBackgroundResource(R.drawable.geek);
                        String show = "Welcome " + mName;
                        mNameView.setText(show);

                        makeToast("Accepted and can attend");
                        managerOfSound(CONFIRM);

                    } else if (mAlreadyHere) {

                        mFrameLayout.setBackgroundResource(R.drawable.student);
                        mNameView.setText(mName);

                        Log.d(TAG, "Already Here");
                        makeToast("Accepted but can't attend already here");
                        managerOfSound(OVER);

                    } else {

                        mFrameLayout.setBackgroundResource(R.drawable.student);
                        mNameView.setText(R.string.not_in_the_list);

                        makeToast("Not Accepted");
                        managerOfSound(OVER);
                    }
                }

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        //savedInstanceState.putString(KEY_INDEX, mQrScannedText);
    }

    private class ConfirmTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {

            return client.sendId(mId, mUsedIp, mUsedPort);
        }

        @Override
        protected void onPostExecute(String response) {

            // connection went as planned
            if (response != null) {

                Boolean confirmArray[] = client.confirmId(response);

                // Pasing went as planned
                if (confirmArray != null) {
                    Boolean accepted = confirmArray[0];
                    Boolean attended = confirmArray[1];

                    // Can attend
                    if (accepted && !attended) {
                        mFrameLayout.setBackgroundResource(R.drawable.geek);
                        String show = "Welcome " + mName;
                        mNameView.setText(show);

                        makeToast("Accepted and can attend");
                        managerOfSound(CONFIRM);


                        // Can't attend already here
                    } else if (accepted) {

                        mFrameLayout.setBackgroundResource(R.drawable.student);
                        mNameView.setText(mName);

                        makeToast("Accepted but can't attend already here");
                        managerOfSound(OVER);

                        // Can't attend not in the list
                    } else {

                        mFrameLayout.setBackgroundResource(R.drawable.student);
                        mNameView.setText(R.string.not_in_the_list);

                        makeToast("Not Accepted");
                        managerOfSound(OVER);
                    }

                } else {

                    // Parsing went Wrong

                    Log.d(TAG, "Nothing returned");
                    makeToast("Something Went Wrong \"Parsing\" just try again");
                }

            } else {

                // Something went wrong with the server

                Log.d(TAG, "Connection Wrong");
                makeToast("Something Went Wrong \"Connecting to server\"");

                mToast = Toast.makeText(ScanActivity.this, "if took long time check WIFI if not check server name, Connectivity", Toast.LENGTH_LONG);
                mToast.show(); // show current toast

            }
        }

    }

    // Make a toast and show it
    private void makeToast(String print) {
        if (mToast != null)
            mToast.cancel();  // cancel last toast

        mToast = Toast.makeText(this, print, Toast.LENGTH_SHORT);
        mToast.show();  // show current toast
    }


    // Choose sound and play it
    protected void managerOfSound(String theText) {


        if (mp != null) {

            mp.reset();
            mp.release();
        }

        if (theText.equals(CONFIRM))
            mp = MediaPlayer.create(this, R.raw.robot_confirm);

        else if (theText.equals(OVER))
            mp = MediaPlayer.create(this, R.raw.game_over);

        mp.start();

    }
}
