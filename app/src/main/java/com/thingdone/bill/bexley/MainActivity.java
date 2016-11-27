package com.thingdone.bill.bexley;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {


    private ProgressBar pb;
    private String LOG_TAG = "BEXLEY";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private float mDownX;
    private float mDownY;
    private final float SCROLL_THRESHOLD = 10;
    private boolean isOnClick;
    private TextView myTextView;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        float deltax, deltay;
        switch (ev.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mDownX = ev.getX();
                mDownY = ev.getY();
                Log.i(LOG_TAG, "Down ");
                isOnClick = true;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (isOnClick) {
                    Log.i(LOG_TAG, "onClick ");
                    //TODO onClick code
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isOnClick && (Math.abs(mDownX - ev.getX()) > SCROLL_THRESHOLD || Math.abs(mDownY - ev.getY()) > SCROLL_THRESHOLD)) {
                    Log.i(LOG_TAG, "movement detected");
                    isOnClick = false;
                }
                if(!isOnClick) {
                    float newx = ev.getX();
                    float newy = ev.getY();
                    deltax = ((mDownX - newx));
                    deltay = ((mDownY - newy));
                    mDownX = newx;
                    mDownY = newy;
                    mouseMove(deltax, deltay);
                    Log.i(LOG_TAG, "Delta " + deltax + " " + deltay);
                }
                break;
            default:
                break;
        }
        return true;
    }
    // Reads an InputStream and converts it to a String.
    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

    private String downloadUrl(String myurl, JSONObject payload) throws IOException, JSONException {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(1000 /* milliseconds */);
            conn.setConnectTimeout(1500 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty("Content-Type","application/json");
            // Starts the query
            conn.connect();

            OutputStreamWriter out = new   OutputStreamWriter(conn.getOutputStream());
            out.write(payload.toString());
            out.close();


            int response = conn.getResponseCode();
            Log.d("BEXLEY", "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(is, len);
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (null != is) {
                is.close();
            }
        }
    }

    private String command(JSONObject payload) throws IOException, JSONException{
        return downloadUrl("http://192.168.150.111:5000/cmd", payload);
    }

    private void mouseMove(float x, float y){
        try {
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("cmd", "mouse");
            jsonParam.put("x", x);
            jsonParam.put("y", y);
            command(jsonParam);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void keyPress(String keys){
        try {
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("cmd", "keys");
            jsonParam.put("keys", keys);
            //jsonParam.put("y", y);
            command(jsonParam);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mouseClick(int keys){
        try {
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("cmd", "click");
            jsonParam.put("keys", keys);
            //jsonParam.put("y", y);
            command(jsonParam);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button buttonTVOff = (Button) findViewById(R.id.buttonTVOff);
        Button buttonLeft = (Button) findViewById(R.id.buttonLeft);
        Button buttonRight = (Button) findViewById(R.id.buttonRight);
        myTextView = (TextView)findViewById(R.id.textView);
        StrictMode.ThreadPolicy policy = new StrictMode.
                ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        buttonTVOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte data[] = new  byte[1024];
                long start, end;
                // TODO Auto-generated method stub
                Log.d(LOG_TAG, "Button Clicked");
                myTextView.setText("Checking...");
                try {
                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("cmd", "test");
                    jsonParam.put("description", "Real");
                    jsonParam.put("enable", "true2");
                    start = System.currentTimeMillis();
                    command(jsonParam);
                    end = System.currentTimeMillis();
                    myTextView.setText("Server Online "+(end-start) + "ms");
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    myTextView.setText("Server Offline");
                    e.printStackTrace();
                }
                Log.d("BEXLEY", "Done");
            }
        });

        buttonLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mouseClick(1);
            }
        });

        buttonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mouseClick(2);
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        char mykey = (char)event.getUnicodeChar(event.getMetaState());
        //String tmp = mykey;
        Log.i(LOG_TAG, "key detected " + keyCode + " " + (char)event.getUnicodeChar(event.getMetaState()));
        keyPress(String.valueOf(mykey));
        //event.isShiftPressed()
        switch (keyCode) {
            case KeyEvent.KEYCODE_D:

                return true;
            case KeyEvent.KEYCODE_F:

                return true;
            case KeyEvent.KEYCODE_J:

                return true;
            case KeyEvent.KEYCODE_K:

                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }
}
