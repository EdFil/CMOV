package pt.ulisboa.tecnico.cmov.airdesk;

import android.app.ExpandableListActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkspaceDetailsActivity extends ActionBarActivity {

    public static final int LOGIN_REQUEST = 1;
    private static final String NAME = "NAME";
    private static final String IS_EVEN = "IS_EVEN";

    private String group[] = {"Tags" , "Users", "Files"};
    private String[][] child = { { "Tag 1", "Tag 2" }, { "User 1", "User 2" }, { "File 1", "File 2" } };

    ExpandableListView mExpandableListView;
    ExpandableListAdapter mTagListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workspace_details);
        setTitle("[Workspace name here]");

        Bundle bundle = getIntent().getExtras();

        mExpandableListView = (ExpandableListView) findViewById(R.id.expandableTagsList);

        List<Map<String, String>> groupData = new ArrayList<Map<String, String>>();
        List<List<Map<String, String>>> childData = new ArrayList<List<Map<String, String>>>();
        for (int i = 0; i < group.length; i++) {
            Map<String, String> curGroupMap = new HashMap<String, String>();
            groupData.add(curGroupMap);
            curGroupMap.put(NAME, group[i]);

            List<Map<String, String>> children = new ArrayList<Map<String, String>>();
            for (int j = 0; j < child[i].length; j++) {
                Map<String, String> curChildMap = new HashMap<String, String>();
                children.add(curChildMap);
                curChildMap.put(NAME, child[i][j]);
            }
            childData.add(children);
        }

        // Set up our adapter
        mTagListAdapter = new SimpleExpandableListAdapter(this, groupData,
                android.R.layout.simple_selectable_list_item,
                new String[] { NAME }, new int[] { android.R.id.text1 },
                childData, android.R.layout.simple_expandable_list_item_2,
                new String[] { NAME }, new int[] { android.R.id.text1 });
        mExpandableListView.setAdapter(mTagListAdapter);
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
