package pt.ulisboa.tecnico.cmov.todo;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;


import java.util.ArrayList;


public class TODO extends ActionBarActivity {

    //Create a an arraylist to hold all the todo items
    ArrayList<String> todoList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);



        //Create reference to the EditText and the ListView in the main layout
        ListView listView = (ListView) findViewById(R.id.TODOS);
        final EditText editText = (EditText) findViewById(R.id.insert_TODO);

        //Create an ArrayAdaptor object to be able to bind ArrayLists to ListViews
        final ArrayAdapter<String> arrayAdaptor = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, todoList);
        listView.setAdapter(arrayAdaptor);

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    if(keyCode == KeyEvent.KEYCODE_ENTER)
                    {
                        //add item in the EditText to the todo list
                        todoList.add(0, editText.getText().toString());

                        //Update the view by notifying the ArrayAdapter of the data changes in the todoList
                        arrayAdaptor.notifyDataSetChanged();
                        editText.getText().clear();
                        return true;
                    }
                    return false;
                }
               return false;
            }
        });




    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_todo, menu);
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
