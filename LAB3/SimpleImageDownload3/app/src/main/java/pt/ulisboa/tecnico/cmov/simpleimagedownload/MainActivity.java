package pt.ulisboa.tecnico.cmov.simpleimagedownload;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    private Runnable imageDownloader = new Runnable() {

        public void run() {
            try {
                URL imageUrl = new URL("http", "android.com", "/images/froyo.png");
                Bitmap image = BitmapFactory.decodeStream(imageUrl.openStream());
                if (image != null) {
                    Log.i("DL", "Successfully retrieved file!");
                } else {
                    Log.i("DL", "Failed decoding file from stream");
                }
            } catch (Exception e) {
                Log.i("DL", "Failed downloading file!");
                e.printStackTrace();
            }
        }


    };

    public void startDownload2(View source) {
        new Thread(imageDownloader, "Download thread").start();
        TextView statusText = (TextView) findViewById(R.id.status);
        statusText.setText("Download started...");
    }

    public void startDownload(View source) {

        ImageView imageView = (ImageView) findViewById(R.id.imageD);
        TextView textView = (TextView) findViewById(R.id.status);
        new DownloadImageTask(imageView, textView).execute("http", "android.com", "/images/froyo.png");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}