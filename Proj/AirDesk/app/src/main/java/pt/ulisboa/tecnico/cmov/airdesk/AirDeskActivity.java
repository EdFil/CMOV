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
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.Workspace;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.WorkspaceManager;
import pt.ulisboa.tecnico.cmov.airdesk.fragment.CreateWorkspaceFragment;
import pt.ulisboa.tecnico.cmov.airdesk.fragment.WorkspaceFragment;


public class AirDeskActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    public static final String TAG = AirDeskActivity.class.getSimpleName();
    public static final String WORKSPACE_MESSAGE = "Workspace_message";
    public static final String WORKSPACES_FOLDER_NAME = "workspaces";

    // Fragment managing the behaviors, interactions and presentation of the navigation drawer.
    private NavigationDrawerFragment mNavigationDrawerFragment;

    // Used to store the last screen title. For use in {@link #restoreActionBar()}.
    private CharSequence mTitle;

    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_desk);

        WorkspaceManager.initWorkspaceManager(getApplicationContext());

        // Ready the User
        pref = getApplicationContext().getSharedPreferences("UserPref", MODE_PRIVATE); // 0 - for private mode
        checkUserLogin();

        setSideDrawer();
    }

    @Override
    protected void onStart() {
        super.onStart();

        populateAccount(); //TODO devia ir para depois do login...apos garantir que o user esta na bd, populate!
        refreshList();
    }

    private void setSideDrawer() {

        // Adding of the header to the drawer list
        ListView listView = (ListView) findViewById(R.id.list_fragment);
        View header = getLayoutInflater().inflate(R.layout.drawer_header, null);
        listView.addHeaderView(header);

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
//        ListView listView = (ListView) findViewById(R.id.workspacesList);
//        listView.setAdapter(WorkspaceManager.getInstance().getWorkspaceAdapter());
//        // Registering context menu for the listView
//        registerForContextMenu(listView);
    }

    // This will be invoked when an item in the listView is long pressed
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_my_workspaces, menu);
    }

    // This will be invoked when a menu item is selected
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();

        switch(item.getItemId()){
            case R.id.menu_my_edit:
                Toast.makeText(this, "TODO Edit", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_my_details:
                Toast.makeText(this, "TODO Details", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_my_delete:
                Workspace selectedWorkspace = (Workspace)((ListView)info.targetView.getParent()).getAdapter().getItem(info.position);
                WorkspaceManager.getInstance().deleteWorkspace(selectedWorkspace);
                break;
            case R.id.menu_my_invite:
                Toast.makeText(this, "TODO Invite", Toast.LENGTH_SHORT).show();
                break;

        }

        return true;
    }

    public void onNewWorkspace(View view) {
        CreateWorkspaceFragment.newInstance().show(getFragmentManager(), "Create Workspace");
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

    public void refreshList(){
        // TODO: Fix this
        WorkspaceManager.getInstance().reloadWorkspaces();
//        mWorkspaceAdapter.sort(new Comparator<Workspace>() {
//            @Override
//            public int compare(Workspace lhs, Workspace rhs) {
//                return lhs.getName().compareToIgnoreCase(rhs.getName());
//            }
//        });
    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {
            // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (position) {
            case 0:
                break;
            case 1:
                fragmentManager.beginTransaction().replace(R.id.container, WorkspaceFragment.newInstance()).commit();
                break;
            default:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, PlaceholderFragment.newInstance(position))
                        .commit();

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


        if(item.getItemId() == R.id.delete_all){
                new AlertDialog.Builder(this)
                        .setTitle("Delete All")
                        .setMessage("Are you sure you want to delete all your workspaces?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                WorkspaceManager.getInstance().deleteAllWorkspaces();
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
