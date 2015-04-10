package pt.ulisboa.tecnico.cmov.simpleimagedownload;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadImageTask extends AsyncTask<String, Integer, Bitmap> {

    ImageView mImageView;
    TextView mTextView;

    public DownloadImageTask(ImageView imageView, TextView statusText) {
        mImageView = imageView;
        mTextView = statusText;
    }

    @Override
    protected Bitmap doInBackground(String... inputUrls) {
        Bitmap image = null;
        try {
            URL imageUrl = new URL(inputUrls[0], inputUrls[1], inputUrls[2]);
            image = BitmapFactory.decodeStream(imageUrl.openStream());
            if (image != null) {
                Log.i("DL", "Successfully retrieved file!");
            } else {
                Log.i("DL", "Failed decoding file from stream");
            }
        } catch (Exception e) {
            Log.i("DL", "Failed downloading file!");
            e.printStackTrace();
        }
        return image;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        mImageView.setImageBitmap(result);
        mTextView.setText("IMAGE DONE");
    }

}
