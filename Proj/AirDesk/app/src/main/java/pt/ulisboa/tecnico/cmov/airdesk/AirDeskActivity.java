package pt.ulisboa.tecnico.cmov.airdesk;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import pt.ulisboa.tecnico.cmov.airdesk.adapter.WorkspaceListAdapter;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.LocalWorkspace;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.Workspace;
import pt.ulisboa.tecnico.cmov.airdesk.dialogFragment.CreateWorkspace;
import pt.ulisboa.tecnico.cmov.airdesk.util.FileManager;


public class AirDeskActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    public static final String TAG = AirDeskActivity.class.getSimpleName();
    public static final String WORKSPACE_MESSAGE = "Workspace_message";
    public static final String WORKSPACES_FOLDER_NAME = "workspaces";

    // Adapter of the list of Workspaces.
    private WorkspaceListAdapter mWorkspaceAdapter;

    // Fragment managing the behaviors, interactions and presentation of the navigation drawer.
    private NavigationDrawerFragment mNavigationDrawerFragment;

    // Used to store the last screen title. For use in {@link #restoreActionBar()}.
    private CharSequence mTitle;

    SharedPreferences pref;

    public WorkspaceListAdapter getWorkspaceListAdapter () {return mWorkspaceAdapter;}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_desk);

        // Ready the User
        pref = getApplicationContext().getSharedPreferences("UserPref", MODE_PRIVATE); // 0 - for private mode
        checkUserLogin();
        Log.d("NICK NAME : ", pref.getString("user_nick", "Ridiculo"));

        setSideDrawer();
        populateAccount(); // goes to LoginActivity
    }

    private void setSideDrawer() {

        // Adding of the header to the drawer list
        ListView listView = (ListView) findViewById(R.id.list_fragment);
        View header = getLayoutInflater().inflate(R.layout.drawer_header, null);
        listView.addHeaderView(header);

        // Adding of the footer to the drawer list
        listView = (ListView) findViewById(R.id.list_fragment);
        View footer = getLayoutInflater().inflate(R.layout.drawer_logout, null);
        listView.addFooterView(footer);

        updateNickEmail();

        // Get the reference of the Drawer Fragment.
        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    private void updateNickEmail() {
        // Update the nickname and the email of the user in the drawer
        TextView nick = (TextView) findViewById(R.id.nick);
        TextView email = (TextView) findViewById(R.id.email);
        nick.setText(pref.getString("user_nick", "Nickname"));
        email.setText(pref.getString("user_email", "Email"));
    }




    private void checkUserLogin() {
        if(!pref.contains("user_email")){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, LoginActivity.LOGIN_REQUEST);
        }
    }

    private void populateAccount() {
        mWorkspaceAdapter = new WorkspaceListAdapter(this, new ArrayList<Workspace>());
//        File rootFolder = getDir(WORKSPACES_FOLDER_NAME, MODE_PRIVATE);
//        String[] children = rootFolder.list();
//        for (int i = 0; i < children.length; i++) {
//            mWorkspaceAdapter.add(new LocalWorkspace(children[i], 10));
//        }

        ListView listView = (ListView) findViewById(R.id.workspacesList);
        listView.setAdapter(mWorkspaceAdapter);
    }

    public void onNewWorkspace(View view) {
        CreateWorkspace.newInstance().show(getFragmentManager(), "Create Workspace");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == LoginActivity.LOGIN_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                updateNickEmail();
            }
            if (resultCode == RESULT_CANCELED) {
                finish();
                //checkUserLogin();
            }
        }
    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {
            // update the main content by replacing fragments
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, PlaceholderFragment.newInstance(position))
                    .commit();
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
        }
    }

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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_air_desk, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((AirDeskActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
