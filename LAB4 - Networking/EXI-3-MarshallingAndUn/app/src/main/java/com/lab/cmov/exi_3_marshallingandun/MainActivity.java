package com.lab.cmov.exi_3_marshallingandun;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    TextView mTextView;
//    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.net_state_text);
//        mTextView.setText("http://api.openweathermap.org/data/2.5/weather?q=Lisbon,pt");
        mTextView.setText("http://api.openweathermap.org/data/2.5/weather?q=Lisbon,pt&mode=xml");

//        imageView = (ImageView) findViewById(R.id.image);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void downloadPage(View view) {

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadWebpageTask().execute(mTextView.getText().toString());
        } else {
            mTextView.setText("No network connection available.");
        }
    }

    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {

        private HttpURLConnection mConnection;
        private String currentWindSpeed = "Current Wind Speed : ";
//        Bitmap bitmap;

        @Override
        protected String doInBackground(String... urls) {
            try {

                URL url = new URL(urls[0]);
                mConnection = (HttpURLConnection) url.openConnection();
                mConnection.setReadTimeout(10000 /* milliseconds */);
                mConnection.setConnectTimeout(15000 /* milliseconds */);
                mConnection.setRequestMethod("GET");
                mConnection.setDoInput(true);

                mConnection.connect();
                int statusCode = mConnection.getResponseCode();
                if (statusCode != HttpURLConnection.HTTP_OK) {
                    return "Error: Failed getting update notes";
                }
//                InputStream is = mConnection.getInputStream();
//                bitmap = BitmapFactory.decodeStream(is);

//                return readTextFromServer(mConnection);

                XMLParser xmlParser = new XMLParser();

                List<XMLParser.Current> currents = null;

                InputStream stream= null;
                try{
                    stream = mConnection.getInputStream();
                    currents = xmlParser.parse(stream);
                // Makes sure that the InputStream is closed after the app is
                // finished using it.
                } finally {
                    if (stream != null) {
                        stream.close();
                    }
                }

                for(XMLParser.Current current : currents)
                    currentWindSpeed += current.speed;

            } catch (IOException e) {
                return "Error: " + e.getMessage();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }

            return currentWindSpeed;
        }

        @Override
        protected void onPostExecute(String result) {

//            JSON WAY OF MARSHALLING AND UNMARSHALLING
//            String textReturn;
//
//            String speed = "";
//            String deg = "";
//            try {
//                JSONObject jsonWhether = new JSONObject(result);
//                JSONObject jsonWind = jsonWhether.getJSONObject("wind");
//                speed = jsonWind.getString("speed");
//                deg = jsonWind.getString("deg");
//            } catch (Exception e) {
//                Toast.makeText(getBaseContext(), "OnPostExecute: JSON Object Problem", Toast.LENGTH_SHORT).show();
//            }
//
////            imageView.setImageBitmap(bitmap);
//            textReturn = "Wind\n" + "Speed: " + speed + "\n" + "DEG: " + deg;


            mTextView.setText("PAGE:\n" + result);
        }
    }
}
