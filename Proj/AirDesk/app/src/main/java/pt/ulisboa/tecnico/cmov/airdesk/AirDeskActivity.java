package pt.ulisboa.tecnico.cmov.airdesk;

import android.app.Activity;
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

import java.io.File;
import java.util.ArrayList;

import pt.ulisboa.tecnico.cmov.airdesk.adapter.WorkspaceListAdapter;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.LocalWorkspace;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.Workspace;
import pt.ulisboa.tecnico.cmov.airdesk.util.FileManager;


public class AirDeskActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    public static final String TAG = AirDeskActivity.class.getSimpleName();
    public static final String WORKSPACE_MESSAGE = "Workspace_message";
    public static final String WORKSPACES_FOLDER_NAME = "workspaces";

    /**
     * Adapter of the list of Workspaces
     */
    private WorkspaceListAdapter mWorkspaceAdapter;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_desk);

        ListView listView = (ListView) findViewById(R.id.list_fragment);
        View header = getLayoutInflater().inflate(R.layout.drawer_header, null);
        listView.addHeaderView(header);

        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));


        checkUserLogin();
        populateAccount();


    }


    private void checkUserLogin() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("UserPref", 0); // 0 - for private mode

        if(!pref.contains("user_email")){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, LoginActivity.LOGIN_REQUEST);
        }
    }

    private void populateAccount() {
        ListView listView;//***************************************************************************************
        // WORKSPACES POPULATION
        //***************************************************************************************

        // Set up the workspace folder
        getDir(WORKSPACES_FOLDER_NAME, MODE_PRIVATE);
        FileManager.createFolder(this, "Workspace 1");
        FileManager.createFolder(this, "Workspace 2");
        FileManager.createFolder(this, "Workspace 3");
        FileManager.createFolder(this, "Workspace 4");
        FileManager.createFolder(this, "Workspace 5");
        FileManager.createFile(this, "Workspace 1", "File 1");


        mWorkspaceAdapter = new WorkspaceListAdapter(this, new ArrayList<Workspace>());
        File rootFolder = getDir(WORKSPACES_FOLDER_NAME, MODE_PRIVATE);
        String[] children = rootFolder.list();
        for (int i = 0; i < children.length; i++) {
            mWorkspaceAdapter.add(new LocalWorkspace(children[i], 10));
        }

        listView = (ListView) findViewById(R.id.workspacesList);
        listView.setAdapter(mWorkspaceAdapter);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(view.getContext(), WorkspaceActivity.class);
//                intent.putExtra(WORKSPACE_MESSAGE, position);
//                startActivity(intent);
//            }
//        });
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