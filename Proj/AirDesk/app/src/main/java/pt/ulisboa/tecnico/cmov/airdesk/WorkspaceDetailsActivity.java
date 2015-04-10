package pt.ulisboa.tecnico.cmov.airdesk;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.Workspace;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.WorkspaceManager;
import pt.ulisboa.tecnico.cmov.airdesk.fragment.FilesFragment;
import pt.ulisboa.tecnico.cmov.airdesk.fragment.WorkspacesDetailsEditFragment;
import pt.ulisboa.tecnico.cmov.airdesk.fragment.WorkspacesDetailsFragment;
import pt.ulisboa.tecnico.cmov.airdesk.util.Constants;

public class WorkspaceDetailsActivity extends ActionBarActivity {

    public static final String EDIT_MODE = "view_mode";
    public static final String IS_LOCAL_WS = "ws_type";

    boolean mEditMode;
    boolean ws_type;
    FragmentManager fragmentManager = getSupportFragmentManager();
    Workspace workspace;
    WorkspacesDetailsEditFragment editDetailsFragment;
    WorkspacesDetailsFragment detailsFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workspace_details_wfragments);

        Intent intent = getIntent();
        mEditMode = intent.getBooleanExtra(EDIT_MODE, false);

        ws_type = intent.getBooleanExtra(IS_LOCAL_WS, true);

        int workspaceIndex = intent.getIntExtra(Constants.WORKSPACE_INDEX, -1);
        workspace = WorkspaceManager.getInstance().getWorkspaceAtIndex(ws_type, workspaceIndex);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction;
        if(mEditMode){
            transaction = fragmentManager.beginTransaction();
            editDetailsFragment = WorkspacesDetailsEditFragment.newInstance();
            transaction.replace(R.id.details_container, editDetailsFragment);
            // Commit the transaction
            transaction.commit();

            editDetailsFragment.sendWorkspaceDetails(workspace, ws_type);
        } else {
            transaction = fragmentManager.beginTransaction();
            detailsFragment = WorkspacesDetailsFragment.newInstance();
            transaction.replace(R.id.details_container, detailsFragment);
            // Commit the transaction
            transaction.commit();

            detailsFragment.sendWorkspaceDetails(workspace);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_workspace_details, menu);
        MenuItem edit = (menu.findItem(R.id.edit));
        if(mEditMode){
            edit.setIcon(android.R.drawable.ic_menu_info_details);
        } else {
            edit.setIcon(android.R.drawable.ic_menu_edit);
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
            editDetailsFragment = WorkspacesDetailsEditFragment.newInstance();
            fragmentManager.beginTransaction().replace(R.id.details_container, editDetailsFragment).commit();
            editDetailsFragment.sendWorkspaceDetails(workspace, ws_type);
        } else {
            item.setIcon(android.R.drawable.ic_menu_edit);

            workspace = editDetailsFragment.getEditedWorkspace();

            detailsFragment = WorkspacesDetailsFragment.newInstance();
            fragmentManager.beginTransaction().replace(R.id.details_container, detailsFragment).commit();
            detailsFragment.sendWorkspaceDetails(workspace);
        }
    }
}
