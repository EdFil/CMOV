package pt.ulisboa.tecnico.cmov.lab3_ex1;

import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    Button mStartButton;
    Button mStopButton;
    TextView mTextView;


    Thread mTimeTickerThread;
    Runnable mTimeTicker = new Runnable() {



        @Override
        public void run() {
            int mCounter = 0;
            while(true) {
                Log.d("Thread", "Counter = " + mCounter++);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStartButton = (Button) findViewById(R.id.startButton);
        mStopButton = (Button) findViewById(R.id.stopButton);
        mTextView = (TextView) findViewById(R.id.textView);

        mTextView.setText(String.valueOf(0));
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
            //mTimeTickerThread.interrupt();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClickStart(View view){
        if(mTimeTickerThread != null)
            mTimeTickerThread.interrupt();

        mTimeTickerThread = new Thread(mTimeTicker, "Time Thread");
        mTimeTickerThread.start();
    }

    public void onClickStop(View view){
        if(!mTimeTickerThread.isInterrupted())
            mTimeTickerThread.interrupt();
    }
}
