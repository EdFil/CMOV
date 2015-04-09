package pt.ulisboa.tecnico.cmov.airdesk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.Workspace;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.WorkspaceManager;
import pt.ulisboa.tecnico.cmov.airdesk.fragment.FilesFragment;
import pt.ulisboa.tecnico.cmov.airdesk.fragment.NavigationDrawerFragment;
import pt.ulisboa.tecnico.cmov.airdesk.fragment.NewFileFragment;
import pt.ulisboa.tecnico.cmov.airdesk.fragment.NewWorkspaceFragment;
import pt.ulisboa.tecnico.cmov.airdesk.fragment.WorkspacesFragment;
import pt.ulisboa.tecnico.cmov.airdesk.util.Constants;

public class AirDeskActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
                   NewFileFragment.OnNewFileFragmentListener,
                   NewWorkspaceFragment.OnNewWorkspaceFragmentListener {

    public static final String TAG = AirDeskActivity.class.getSimpleName();

    // Fragment managing the behaviors, interactions and presentation of the navigation drawer.
    private NavigationDrawerFragment mNavigationDrawerFragment;

    // Used to store the last screen title. For use in {@link #restoreActionBar()}.
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_desk);

        // Init the manager of the workspaces so it has the context of the application
        WorkspaceManager.initWorkspaceManager(getApplicationContext());
        setNavigationDrawer();
    }

    public void updateActionBarTitle() {
        getSupportActionBar().setTitle(mTitle);
    }

    private void setNavigationDrawer() {
        // Adding of the header to the drawer list
        ListView listView = (ListView) findViewById(R.id.list_fragment);
        View header = getLayoutInflater().inflate(R.layout.drawer_header, null);
        listView.addHeaderView(header);

        refreshNickEmailAfterLogin();

        // Get the reference of the Drawer Fragment.
        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    private void refreshNickEmailAfterLogin() {
        // Update the nickname and the email of the user in the drawer
        SharedPreferences pref = getSharedPreferences("UserPref", MODE_PRIVATE);
        TextView nick = (TextView) findViewById(R.id.nick);
        TextView email = (TextView) findViewById(R.id.email);
        nick.setText(pref.getString("user_nick", "Nickname"));
        email.setText(pref.getString("user_email", "Email"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        switch(requestCode) {
            case Constants.LOGIN_REQUEST:
                if (resultCode == RESULT_OK)
                    refreshNickEmailAfterLogin();
                else if (resultCode == RESULT_CANCELED)
                    finish();
                break;
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
            // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (position) {
            // Account information : Nickname and Email
            case 0:
                break;
            // MyWorkspaces : Owned workspace list fragment
            case 1:
                fragmentManager.beginTransaction().replace(R.id.container, WorkspacesFragment.newInstance()).commit();
                break;
            // Foreign Workspaces : Workspaces accessible but not owned
            case 2:
                fragmentManager.beginTransaction().replace(R.id.container, WorkspacesFragment.newInstance()).commit();
                break;
            // Search Workspace
            case 3:
                break;
            case 5:
                // Returns the result to the AirDeskActivity
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.putExtra(Constants.LOG_OUT_MESSAGE, true);
                startActivityForResult(intent, Constants.LOGIN_REQUEST);
            default:
                fragmentManager.beginTransaction().replace(R.id.container, PlaceholderFragment.newInstance(position)).commit();
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
            case 4:
                mTitle = getString(R.string.title_section4);
                break;
            case 5:
                mTitle = getString(R.string.title_section5);
                break;
        }
    }

    // Called to recreate action bar
    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.air_desk, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        if (item.getItemId() == R.id.refresh) {
            WorkspacesFragment workspacesFrag = (WorkspacesFragment) getSupportFragmentManager().findFragmentById(R.id.container);
            if(workspacesFrag != null)
                workspacesFrag.updateWorkspaceList();
            else {
                FilesFragment filesFragment = (FilesFragment) getSupportFragmentManager().findFragmentById(R.id.container);
                if (filesFragment != null)
                    filesFragment.updateFileList();
            }
            return true;
        }

        if(item.getItemId() == R.id.delete_all) {
                new AlertDialog.Builder(this)
                        .setTitle("Delete All")
                        .setMessage("Are you sure you want to delete all your workspaces?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                WorkspacesFragment workspacesFrag = (WorkspacesFragment) getSupportFragmentManager().findFragmentById(R.id.container);
                                if (workspacesFrag != null)
                                    workspacesFrag.deleteAllWorkspaces();
                                Toast.makeText(getBaseContext(), "DELETED", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
        }

        return super.onOptionsItemSelected(item);
    }

    //////////////////////////////////////////////////////////
    // Methods dealing with FILE FRAGMENTS
    //////////////////////////////////////////////////////////

    @Override
    public void updateFileList() {
        FilesFragment filesFrag = (FilesFragment) getSupportFragmentManager().findFragmentById(R.id.container);

        if (filesFrag != null)
            filesFrag.updateFileList();
    }

    @Override
    public void updateWorkspaceList(Workspace workspace) {
        WorkspacesFragment workspaceFrag = (WorkspacesFragment) getSupportFragmentManager().findFragmentById(R.id.container);

        if (workspaceFrag != null)
            workspaceFrag.addWorkspace(workspace);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_workspaces, container, false);
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((AirDeskActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }
}
