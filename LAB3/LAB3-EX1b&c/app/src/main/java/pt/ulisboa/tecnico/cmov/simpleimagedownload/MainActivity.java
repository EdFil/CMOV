package pt.ulisboa.tecnico.cmov.simpleimagedownload;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

//    private Runnable imageDownloader = new Runnable() {
//
//        private void sendMessage(String what) {
//            Bundle bundle = new Bundle();
//            bundle.putString("status", what);
//            Message message = new Message();
//            message.setData(bundle);
//            mHandler.sendMessage(message);
//        }
//
//        public void run() {
//            try {
//                URL imageUrl = new URL("http", "android.com", "/images/froyo.png");
//                Bitmap image = BitmapFactory.decodeStream(imageUrl.openStream());
//                if (image != null) {
//                    sendMessage("Successfully retrieved file!");
//                } else {
//                    sendMessage("Failed decoding file from stream");
//                }
//            } catch (Exception e) {
//                sendMessage("Failed downloading file!");
//                e.printStackTrace();
//            }
//        }
//    };

    public Handler mHandler;

    public void startDownload(View source) throws MalformedURLException {
        DownloadImageTask task = new DownloadImageTask((ImageView)findViewById(R.id.imageView), mHandler);
        task.execute(new URL("http", "android.com", "/images/froyo.png"));
//        new Thread(imageDownloader, "Download thread").start();
//        TextView statusText = (TextView) findViewById(R.id.status);
//        statusText.setText("Download started...");
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message inputMessage) {
                String text = inputMessage.getData().getString("status");
                TextView statusText = (TextView) findViewById(R.id.status);
                statusText.setText(text);
            }

       };
    }
}