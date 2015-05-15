package pt.ulisboa.tecnico.cmov.airdesk;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.adapter.UserListAdapter;
import pt.ulisboa.tecnico.cmov.airdesk.core.user.User;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.LocalWorkspace;
import pt.ulisboa.tecnico.cmov.airdesk.manager.UserManager;
import pt.ulisboa.tecnico.cmov.airdesk.manager.WorkspaceManager;
import pt.ulisboa.tecnico.cmov.airdesk.service.InviteUserService;
import pt.ulisboa.tecnico.cmov.airdesk.tasks.AsyncResponse;
import pt.ulisboa.tecnico.cmov.airdesk.tasks.RequestTask;
import pt.ulisboa.tecnico.cmov.airdesk.util.Constants;

public class ManageAccessListActivity extends ActionBarActivity {

    public static final String TAG = ManageAccessListActivity.class.getSimpleName();

    UserListAdapter mUserWithNoAccess;
    UserListAdapter mUserWithAccess;

    LocalWorkspace mWorkspace;

    ListView mUserWithAccessList;
    ListView mUserNoAccessList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_for_workspace);

        Intent intent = getIntent();
        long workspaceIndex = intent.getLongExtra(Constants.WORKSPACE_ID_KEY, -1);
        mWorkspace = WorkspaceManager.getInstance().getLocalWorkspaceWithId(workspaceIndex);

        mUserNoAccessList = (ListView) findViewById(R.id.user_no_access_list);
        mUserWithAccessList = (ListView) findViewById(R.id.user_with_access_list);

        mUserWithNoAccess = new UserListAdapter(this, new ArrayList<User>());
        mUserNoAccessList.setAdapter(mUserWithNoAccess);

        mUserWithAccess = new UserListAdapter(this, new ArrayList<User>());
        mUserWithAccessList.setAdapter(mUserWithAccess);

        mUserNoAccessList.setOnItemClickListener(mUserNoAccessListOnItemClickListener);
        mUserWithAccessList.setOnItemClickListener(mUserWithAccessListOnItemClickListener);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mUserWithAccess.clear();
        mUserWithNoAccess.clear();

        List<String> emailList = mWorkspace.getAccessList();
        emailList.remove(UserManager.getInstance().getOwner().getEmail());

        for(String email : emailList) {
            User user = UserManager.getInstance().getUserByEmail(email);
            if(user != null)
                mUserWithAccess.add(user);
            else
                mUserWithAccess.add(new User(-1, email, "?"));
        }

        for(User onlineUser : UserManager.getInstance().getUsers()) {
            if(!mWorkspace.getAccessList().contains(onlineUser.getEmail()))
                mUserWithNoAccess.add(onlineUser);
        }
    }

    // ---------------------------
    // -------- Listeners --------
    // ---------------------------

    private AdapterView.OnItemClickListener mUserNoAccessListOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final User user = (User) parent.getItemAtPosition(position);

            Log.d(TAG, "OnItemCick " + user.getNick());

            // Create a request task for every device
            RequestTask task = new RequestTask(
                    user.getDevice().getVirtIp(),
                    Integer.parseInt(getString(R.string.port)),
                    InviteUserService.class,
                    mWorkspace.toJSON().toString());

            // Override the callback so we can process the result from the task
            task.mDelegate = new AsyncResponse() {
                @Override
                public void processFinish(String output) {
                    try {
                        // Get the response as a JSONObject
                        JSONObject response = new JSONObject(output);

                        // Check if has error
                        if (response.has(Constants.ERROR_KEY)) {
                            mWorkspace.removeAccessToUser(user.getEmail());
                            throw new Exception(response.getString(Constants.ERROR_KEY));
                        }

                        Toast.makeText(getApplicationContext(), response.getString(Constants.RESULT_KEY), Toast.LENGTH_SHORT).show();
                        WorkspaceManager.getInstance().addAccessToUser(user.getEmail(), mWorkspace);
                        mUserWithNoAccess.remove(user);
                        mUserWithAccess.add(user);

                    } catch (Exception e) {
                        // Show our error in a toast
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            };
            // Execute the task
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        }
    };

    private AdapterView.OnItemClickListener mUserWithAccessListOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            User user = (User) parent.getItemAtPosition(position);
            WorkspaceManager.getInstance().removeAccessToUser(user.getEmail(), mWorkspace);
            mUserWithNoAccess.add(user);
            mUserWithAccess.remove(user);
        }
    };

}
