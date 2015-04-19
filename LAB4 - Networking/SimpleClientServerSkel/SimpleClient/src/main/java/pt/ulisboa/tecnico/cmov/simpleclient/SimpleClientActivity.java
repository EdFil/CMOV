package pt.ulisboa.tecnico.cmov.simpleclient;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import pt.ulisboa.tecnico.cmov.simpleclient.fragment.CreateContactDialogFragment;
import pt.ulisboa.tecnico.cmov.simpleclient.fragment.DeleteContactDialogFragment;

public class SimpleClientActivity extends FragmentActivity {

	private EditText textField;
	private Button button;
	private String messsage;
    private ArrayAdapter<String> servicesListAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

        servicesListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        ListView listView = (ListView) findViewById(R.id.contact_services);
        listView.setAdapter(servicesListAdapter);

        servicesListAdapter.add("Create Contact");
        servicesListAdapter.add("Delete Contact");
        servicesListAdapter.add("List Contacts");

        listView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String request = (String) parent.getItemAtPosition(position);

                switch (request) {
                    case "Create Contact":
                        CreateContactDialogFragment fragCreate = CreateContactDialogFragment.newInstance(request);
                        fragCreate.show(getSupportFragmentManager(), "dialog");
                        break;
                    case "Delete Contact":
                        DeleteContactDialogFragment fragDel = DeleteContactDialogFragment.newInstance(request);
                        fragDel.show(getSupportFragmentManager(), "dialog");
                        break;
                    case "List Contacts":
                        listContactsRequest();
                        break;
                }

            }
        });

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

    public void createContactRequest(String contactName, String contactAddress, String contactNumber, String contactEmail) {

        JSONObject contact = new JSONObject();
        try {
            contact.put("request", "Create Contact");
            contact.put("name", contactName);
            contact.put("address", contactAddress);
            contact.put("phone_number", contactNumber);
            contact.put("email", contactEmail);
        } catch (Exception e) {
            Toast.makeText(this, "SimpleClientActivity : " + e.toString(), Toast.LENGTH_SHORT).show();
        }

        String jsonString = contact.toString();
        new ClientConnectorTask().execute(jsonString);
    }


    public void deleteContactRequest(String contactName) {

        JSONObject contact = new JSONObject();
        try {
            contact.put("request", "Delete Contact");
            contact.put("name", contactName);
        } catch (Exception e) {
            Toast.makeText(this, "SimpleClientActivity : " + e.toString(), Toast.LENGTH_SHORT).show();
        }

        String jsonString = contact.toString();
        new ClientConnectorTask().execute(jsonString);
    }


    public void listContactsRequest() {

        JSONObject contact = new JSONObject();
        try {
            contact.put("request", "List Contacts");
        } catch (Exception e) {
            Toast.makeText(this, "SimpleClientActivity : " + e.toString(), Toast.LENGTH_SHORT).show();
        }

        String jsonString = contact.toString();
        new ClientConnectorTask().execute(jsonString);
    }


    public class ClientConnectorTask extends AsyncTask<String, Void, Integer> {
        private Socket client;
        private PrintWriter printwriter;
        protected Integer doInBackground(String... strings) {
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
    }
}
