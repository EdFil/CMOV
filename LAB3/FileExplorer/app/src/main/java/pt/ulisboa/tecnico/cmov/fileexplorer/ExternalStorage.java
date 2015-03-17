package pt.ulisboa.tecnico.cmov.fileexplorer;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class ExternalStorage extends Activity {

    // wrap some operations that are likely to be needed in more than one place in FileUtil

    private EditText input;
    private TextView output;
    private Button write;
    private Button read;
    private Handler mHandler;

    @Override
    public void onCreate(final Bundle icicle) {
        super.onCreate(icicle);
        this.setContentView(R.layout.external_storage);

        this.input = (EditText) findViewById(R.id.external_storage_input);
        this.output = (TextView) findViewById(R.id.external_storage_output);

        this.write = (Button) findViewById(R.id.external_storage_write_button);
        this.write.setOnClickListener(new OnClickListener() {
            public void onClick(final View v) {
                write();
            }
        });

        this.read = (Button) findViewById(R.id.external_storage_read_button);
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bundle bundle = new Bundle();
                if (FileUtil.isExternalStorageWritable()) {
                    File dir = FileUtil.getExternalFilesDirAllApiLevels(getPackageName());
                    File file = new File(dir, "test.txt");
                    FileUtil.writeStringAsFile(input.getText().toString(), file);
                    bundle.putString("toast", "File written");
                    bundle.putString("input", "");
                    bundle.putString("output", "");
//                    Toast.makeText(this, "File written", Toast.LENGTH_SHORT).show();
//                    input.setText("");
//                    output.setText("");
                } else {
                    bundle.putString("toast", "External storage not writable");
//                    Toast.makeText(this, "External storage not writable", Toast.LENGTH_SHORT).show();
                }
                Message message = new Message();
                message.setData(bundle);
                mHandler.sendMessage(message);

            }
        }).start();

    }

    private void read() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bundle bundle = new Bundle();
                if (FileUtil.isExternalStorageReadable()) {
                    File dir = FileUtil.getExternalFilesDirAllApiLevels(getPackageName());
                    File file = new File(dir, "test.txt");
                    if (file.exists() && file.canRead()) {
                        bundle.putString("output", FileUtil.readFileAsString(file));
                        bundle.putString("toast", "File read");
//                        output.setText(FileUtil.readFileAsString(file));
//                        Toast.makeText(this, "File read", Toast.LENGTH_SHORT).show();
                    } else {
                        bundle.putString("toast", "Unable to read file: " + file.getAbsolutePath());
//                        Toast.makeText(this, "Unable to read file: " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    bundle.putString("toast", "External storage not readable");
//                    Toast.makeText(this, "External storage not readable", Toast.LENGTH_SHORT).show();
                }
                Message message = new Message();
                message.setData(bundle);
                mHandler.sendMessage(message);
            }
        }).start();
    }
}

