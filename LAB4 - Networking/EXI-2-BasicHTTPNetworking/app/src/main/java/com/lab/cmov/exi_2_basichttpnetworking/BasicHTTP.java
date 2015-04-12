package com.lab.cmov.exi_2_basichttpnetworking;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class BasicHTTP extends ActionBarActivity {


    TextView mTextView;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_http);

        mTextView = (TextView) findViewById(R.id.net_state_text);
        mTextView.setText("http://upload.wikimedia.org/wikipedia/commons/7/77/Small_stellated_dodecahedron.png");

        imageView = (ImageView) findViewById(R.id.image);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_basic_htt, menu);
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
        Bitmap bitmap;

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
                InputStream is = mConnection.getInputStream();
                bitmap = BitmapFactory.decodeStream(is);

                return readTextFromServer(mConnection);

            } catch (IOException e) {
                return "Error: " + e.getMessage();
            }
        }

        private String readTextFromServer(HttpURLConnection connection) throws IOException {
            InputStreamReader stream = null;
            try {
                stream = new InputStreamReader(connection.getInputStream());
                BufferedReader br = new BufferedReader(stream);
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();
                while (line != null) {
                    sb.append(line + "\n");
                    line = br.readLine();
                }
                return sb.toString();
            } finally {
                if (stream != null) {
                    stream.close();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {

            imageView.setImageBitmap(bitmap);
//            mTextView.setText("PAGE:" + result);
        }
    }
}
