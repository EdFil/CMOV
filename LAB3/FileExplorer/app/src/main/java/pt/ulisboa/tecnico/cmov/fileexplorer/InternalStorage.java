package pt.ulisboa.tecnico.cmov.fileexplorer;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class InternalStorage extends Activity {

    private static final String LINE_SEP = System.getProperty("line.separator");

    private Handler mHandler;
    private EditText input;
    private TextView output;
    private Button write;
    private Button read;


    @Override
    public void onCreate(final Bundle icicle) {
        super.onCreate(icicle);
        this.setContentView(R.layout.internal_storage);

        this.input = (EditText) findViewById(R.id.internal_storage_input);
        this.output = (TextView) findViewById(R.id.internal_storage_output);

        this.write = (Button) findViewById(R.id.internal_storage_write_button);
        this.write.setOnClickListener(new OnClickListener() {
            public void onClick(final View v) {
                write();
            }
        });

        this.read = (Button) findViewById(R.id.internal_storage_read_button);
        this.read.setOnClickListener(new OnClickListener() {
            public void onClick(final View v) {
                read();
            }
        });

        mHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message inputMessage) {
                String handlerInput = inputMessage.getData().getString("input");
                String handlerOutput = inputMessage.getData().getString("output");
                String handlerToast = inputMessage.getData().getString("toast");

                if(handlerInput != null)
                    input.setText(handlerInput);
                if(handlerOutput != null)
                    output.setText(handlerOutput);
                if(handlerToast != null)
                    Toast.makeText(getApplicationContext(), handlerToast, Toast.LENGTH_SHORT).show();
            }
        };

    }

    private void write() {
        new Thread(new Runnable(){
            @Override
            public void run() {
                FileOutputStream fos = null;
                try {
                    // note that there are many modes you can use
                    fos = openFileOutput("test.txt", Context.MODE_PRIVATE);
                    fos.write(input.getText().toString().getBytes());
//                    Toast.makeText(getApplicationContext(), "File written", Toast.LENGTH_SHORT).show();

                    Bundle bundle = new Bundle();
                    bundle.putString("input", "");
                    bundle.putString("output", "");
                    bundle.putString("toast", "File written");
                    Message message = new Message();
                    message.setData(bundle);
                    mHandler.sendMessage(message);

//                    input.setText("");
//                    output.setText("");
                } catch (FileNotFoundException e) {
                    Log.e(Constants.LOG_TAG, "File not found", e);
                } catch (IOException e) {
                    Log.e(Constants.LOG_TAG, "IO problem", e);
                } finally {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        Log.d("FileExplorer", "Close error.");
                    }
                }
            }
        }).start();
    }

    private void read() {
        new Thread(new Runnable(){
            @Override
            public void run() {
                FileInputStream fis = null;
                Scanner scanner = null;
                StringBuilder sb = new StringBuilder();
                Bundle bundle = new Bundle();
                try {
                    fis = openFileInput("test.txt");
                    // scanner does mean one more object, but it's easier to work with
                    scanner = new Scanner(fis);
                    while (scanner.hasNextLine()) {
                        sb.append(scanner.nextLine() + LINE_SEP);
                    }
                    //Toast.makeText(getApplicationContext(), "File read", Toast.LENGTH_SHORT).show();
                    bundle.putString("toast", "File read");
                } catch (FileNotFoundException e) {
                    Log.e(Constants.LOG_TAG, "File not found", e);
                } finally {
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            Log.d("FileExplorer", "Close error.");
                        }
                    }
                    if (scanner != null) {
                        scanner.close();
                    }
                }

                bundle.putString("output", sb.toString());
                Message message = new Message();
                message.setData(bundle);
                mHandler.sendMessage(message);

//                output.setText(sb.toString());
            }
        }).start();

    }
}
