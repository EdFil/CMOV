package pt.ulisboa.tecnico.cmov.airdesk.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import pt.ulisboa.tecnico.cmov.airdesk.AirDeskActivity;
import pt.ulisboa.tecnico.cmov.airdesk.R;
import pt.ulisboa.tecnico.cmov.airdesk.adapter.SubscriptionListAdapter;
import pt.ulisboa.tecnico.cmov.airdesk.manager.UserManager;
import pt.ulisboa.tecnico.cmov.airdesk.manager.WorkspaceManager;

public class SubscriptionsFragment extends Fragment {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public static final String TAG = SubscriptionsFragment.class.getSimpleName();

    WorkspaceManager manager;

    SubscriptionListAdapter mSubscriptionListAdapter;

    public SubscriptionsFragment() {}

    public static SubscriptionsFragment newInstance(int sectionNumber) {
        SubscriptionsFragment fragment = new SubscriptionsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = WorkspaceManager.getInstance();
        mSubscriptionListAdapter = new SubscriptionListAdapter(getActivity(), UserManager.getInstance().getSubscriptionList());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View subscriptionFragmentView = inflater.inflate(R.layout.fragment_subscriptions, container, false);

        ListView listView = (ListView) subscriptionFragmentView.findViewById(R.id.subscriptionWorkspacesList);
        listView.setAdapter(mSubscriptionListAdapter);

        // When selecting a workspace replaces this fragment for the FilesFragment
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // TODO : CALL SUBSCRIPTION ACTIVITY
            }
        });

        // Register the list of Files for the ContextMenu
        registerForContextMenu(listView);

        // Setup create new workspace button
        Button newSubscriptionButton = (Button) subscriptionFragmentView.findViewById(R.id.newSubscriptionButton);
        newSubscriptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View buttonView) {
                NewSubscriptionFragment.newInstance().show(getActivity().getFragmentManager(), NewSubscriptionFragment.class.getSimpleName());
            }
        });

        return subscriptionFragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AirDeskActivity) getActivity()).updateActionBarTitle();
        updateSubscriptionList();
    }

    // This will be invoked when an item in the listView is long pressed
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.menu_context_subscriptions, menu);
    }

    // This will be invoked when a menu item is selected
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        Intent intent;
        switch(item.getItemId()){
            case R.id.menu_my_delete:
                // TODO : DELETE SUBSCRIPTION ON USERMANAGER
                UserManager.getInstance().deleteSubscription(info.position);
                updateSubscriptionList();
                break;
        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_air_desk, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        if (item.getItemId() == R.id.refresh_workspaces) {
            updateSubscriptionList();
            return true;
        }

        if(item.getItemId() == R.id.delete_all_workspaces) {
           return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((AirDeskActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
    }

    public void updateSubscriptionList() {
        mSubscriptionListAdapter.notifyDataSetChanged();
    }
}