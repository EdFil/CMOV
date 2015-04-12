package pt.ulisboa.tecnico.cmov.simpleimagedownload;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;

/**
 * Created by edgar on 16-03-2015.
 */
public class DownloadImageTask extends AsyncTask<URL, Integer, Bitmap> {

    ImageView mImageView;
    Handler mHandler;

    public DownloadImageTask(ImageView imageView, Handler handler) {
        mImageView = imageView;
        mHandler = handler;
    }

    @Override
    protected Bitmap doInBackground(URL... inputUrls) {
        Bitmap image = null;
        try {
            sendMessage("Downloading Image");
            image = BitmapFactory.decodeStream(inputUrls[0].openStream());
        } catch (IOException e) {
            sendMessage("IO Exception");
            e.printStackTrace();
        }
        return image;
    }


    @Override
    protected void onPostExecute(Bitmap result) {
        sendMessage("Loading Image");
        mImageView.setImageBitmap(result);
        sendMessage("Done");
    }

    private void sendMessage(String what) {
            Bundle bundle = new Bundle();
            bundle.putString("status", what);
            Message message = new Message();
            message.setData(bundle);
            mHandler.sendMessage(message);
        }
}
