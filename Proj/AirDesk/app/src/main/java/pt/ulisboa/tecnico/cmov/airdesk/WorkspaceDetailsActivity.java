package pt.ulisboa.tecnico.cmov.airdesk;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ClipData;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.io.File;

import pt.ulisboa.tecnico.cmov.airdesk.core.tag.Tag;
import pt.ulisboa.tecnico.cmov.airdesk.core.user.User;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.Workspace;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.WorkspaceManager;
import pt.ulisboa.tecnico.cmov.airdesk.fragment.WorkspacesEditDetailsFragment;
import pt.ulisboa.tecnico.cmov.airdesk.fragment.WorkspacesFragment;
import pt.ulisboa.tecnico.cmov.airdesk.fragment.WorkspacesViewDetailsFragment;

public class WorkspaceDetailsActivity extends ActionBarActivity {

    private static final int NUM_COLUMNS_PER_ROW = 4;

    public static final String WORKSPACE_INDEX_TAG = "worspace_index";
    public static final String EDIT_MODE = "view_mode";

    boolean mEditMode;
    FragmentManager fragmentManager = getSupportFragmentManager();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workspace_details_wfragments);

        int workspaceIndex = getIntent().getIntExtra(WORKSPACE_INDEX_TAG, -1);
        mEditMode = getIntent().getBooleanExtra(EDIT_MODE, false);

        Workspace workspace = WorkspaceManager.getInstance().getWorkspaceAtIndex(workspaceIndex);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mEditMode){
            fragmentManager.beginTransaction().replace(R.id.details_container, WorkspacesEditDetailsFragment.newInstance()).commit();
        } else {
            fragmentManager.beginTransaction().replace(R.id.details_container, WorkspacesViewDetailsFragment.newInstance()).commit();
        }
    }

//    private void addTagToTable(String tag) {
//        TextView tagText = new TextView(this);
//        tagText.setText(tag);
//        tagText.setTextAppearance(this, android.R.style.TextAppearance_DeviceDefault_Medium);
//        TableRow rowToInsertTag;
//        int numRows = mTagsTableLayout.getChildCount();
//        if(numRows == 0 || ((TableRow)mTagsTableLayout.getChildAt(numRows - 1)).getChildCount() >= NUM_COLUMNS_PER_ROW) {
//            rowToInsertTag = new TableRow(this);
//            mTagsTableLayout.addView(rowToInsertTag);
//        } else {
//            rowToInsertTag = (TableRow)mTagsTableLayout.getChildAt(numRows - 1);
//        }
//        rowToInsertTag.addView(tagText, ActionBar.LayoutParams.WRAP_CONTENT);
//    }
//
//    private void addUserToTable(String tag) {
//        TextView userText = new TextView(this);
//        userText.setText(tag);
//        userText.setTextAppearance(this, android.R.style.TextAppearance_DeviceDefault_Medium);
//        TableRow rowToInsertTag;
//        int numRows = mUsersTableLayout.getChildCount();
//        if(numRows == 0 || ((TableRow)mUsersTableLayout.getChildAt(numRows - 1)).getChildCount() >= NUM_COLUMNS_PER_ROW) {
//            rowToInsertTag = new TableRow(this);
//            mUsersTableLayout.addView(rowToInsertTag);
//        } else {
//            rowToInsertTag = (TableRow)mUsersTableLayout.getChildAt(numRows - 1);
//        }
//        rowToInsertTag.addView(userText, ActionBar.LayoutParams.WRAP_CONTENT);
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_workspace_details, menu);
        MenuItem item = (menu.findItem(R.id.edit));
        if(mEditMode){
            item.setIcon(android.R.drawable.ic_menu_info_details);
        } else {
            item.setIcon(android.R.drawable.ic_menu_edit);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar wil
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.edit) {
            mEditMode = !mEditMode;
            updateView(item);
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateView(MenuItem item){
        if(mEditMode){
            item.setIcon(android.R.drawable.ic_menu_info_details);
            fragmentManager.beginTransaction().replace(R.id.details_container, WorkspacesEditDetailsFragment.newInstance()).commit();
        } else {
            item.setIcon(android.R.drawable.ic_menu_edit);
            fragmentManager.beginTransaction().replace(R.id.details_container, WorkspacesViewDetailsFragment.newInstance()).commit();
        }
    }
}
