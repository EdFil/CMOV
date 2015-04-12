package pt.ulisboa.tecnico.cmov.simpleclient;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class SimpleClientActivity extends Activity {

	private EditText textField;
	private Button button;
	private String messsage;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		textField = (EditText) findViewById(R.id.editText1);
		button = (Button) findViewById(R.id.button1);

		//Button press event listener
		button.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				messsage = textField.getText().toString();
//				textField.setText("Implement me!");
				// send the data to the server
                new ClientConnectorTask().execute(messsage);
			}
		});
	}

    public class ClientConnectorTask extends AsyncTask<String, Void, Integer> {
        private Socket client;
        private PrintWriter printwriter;
        protected Integer doInBackground(String...strings) {
            // validate input parameters
            if (strings.length <= 0) {
                return 0;
            }
            // connect to the server and send the message
            try {
                client = new Socket("10.0.2.2", 4444);
                printwriter = new PrintWriter(client.getOutputStream(),true);
                printwriter.write(strings[0]);
                printwriter.flush();
                printwriter.close();
                client.close();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return 0;
        }
        protected void onPostExecute(Long result) {
            return;
        }
    }
}
