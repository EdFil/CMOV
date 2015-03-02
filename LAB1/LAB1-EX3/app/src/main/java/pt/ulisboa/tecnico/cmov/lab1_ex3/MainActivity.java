package pt.ulisboa.tecnico.cmov.lab1_ex3;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    private static String TAG = MainActivity.class.getSimpleName();

    ArrayList<String> mTodoList;
    ArrayAdapter<String> mArrayAdapter;

    EditText mEditText;
    ListView mListView;
    Button mSendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Create list
        mTodoList = new ArrayList<String>();
        mTodoList.add("Wake Up.");
        mTodoList.add("Eat Breakfast.");
        mTodoList.add("Brush Teeth.");
        //mTodoList.add("Go to work.");
        //mTodoList.add("Sleep at work.");
        //mTodoList.add("Lunch.");
        //mTodoList.add("Go to the bathroom");
        //mTodoList.add("Work for a while");
        //mTodoList.add("Facebook break");
        //mTodoList.add("Twitter break");
        //mTodoList.add("Youtube break");
        //mTodoList.add("Get caught by boss");
        //mTodoList.add("Quit job.");
        //mTodoList.add("Give up on life");

        //Create Array adapter (context, XML Resource, data array)
        mArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mTodoList);

        //Get the listView a list view and bind our adapter to it
        mListView = (ListView) findViewById(R.id.todoListView);
        mListView.setAdapter(mArrayAdapter);

        //Get our text box
        mEditText = (EditText) findViewById(R.id.editText);

        //Get out button and add a click listener
        mSendButton = (Button) findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //If has text, add to adapter and clear text box
                if(mEditText.getText().length() > 0) {
                    mArrayAdapter.add(mEditText.getText().toString());
                    mEditText.getText().clear();
                }
            }
        });
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
