package pt.ulisboa.tecnico.cmov.producerconsumerwithlooper;

import java.util.Random;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;


public class MainActivity extends ActionBarActivity {

    private Handler handler;

    private class Consumer extends Thread {

        @Override
        public void run() {

            Looper.prepare();

            handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    int number = msg.what;
                    if (number % 2 == 0) {
                        Log.d("Consumer", number + " is divisible by 2");
                    } else {
                        Log.d("Consumer", number + " is not divisible by 2");
                    }
                }
            };

            Looper.loop();
        }
    }

    private class Producer extends Thread {

        public Producer(String name) {
            super(name);
        }

        @Override
        public void run() {
            Random random = new Random();
            while (true) {
                int number = random.nextInt(100);
                Log.d("Producer " + getName(), Integer.toString(number));
                handler.sendEmptyMessage(number);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // launch the consumer
        new Consumer().start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }

        // launch the producers
        new Producer("A").start();
        new Producer("B").start();
    }
}