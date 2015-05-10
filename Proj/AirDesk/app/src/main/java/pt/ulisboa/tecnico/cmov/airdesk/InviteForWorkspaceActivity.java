package pt.ulisboa.tecnico.cmov.airdesk;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;

import pt.ulisboa.tecnico.cmov.airdesk.adapter.UserListAdapter;
import pt.ulisboa.tecnico.cmov.airdesk.core.user.User;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.Workspace;
import pt.ulisboa.tecnico.cmov.airdesk.manager.UserManager;
import pt.ulisboa.tecnico.cmov.airdesk.manager.WorkspaceManager;
import pt.ulisboa.tecnico.cmov.airdesk.service.GetUserService;
import pt.ulisboa.tecnico.cmov.airdesk.service.InviteUserService;
import pt.ulisboa.tecnico.cmov.airdesk.tasks.AsyncResponse;
import pt.ulisboa.tecnico.cmov.airdesk.tasks.BroadcastTask;
import pt.ulisboa.tecnico.cmov.airdesk.tasks.RequestTask;
import pt.ulisboa.tecnico.cmov.airdesk.util.Constants;

public class InviteForWorkspaceActivity extends ActionBarActivity {

    public static final String TAG = InviteForWorkspaceActivity.class.getSimpleName();
    UserListAdapter mUserListAdapter;
    Workspace mWorkspace;

    ListView mUsersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_for_workspace);

        Intent intent = getIntent();
        int workspaceIndex = intent.getIntExtra(Constants.WORKSPACE_INDEX, -1);
        mWorkspace = WorkspaceManager.getInstance().getWorkspaceAtIndex(true, workspaceIndex);

        mUsersList = (ListView) findViewById(R.id.user_list);

        mUserListAdapter = new UserListAdapter(this, new ArrayList<User>());
        mUsersList.setAdapter(mUserListAdapter);

        mUsersList.setOnItemClickListener(mUserListOnItemClickListener);
    }

    @Override
    protected void onStart() {
        super.onStart();

        for(User onlineUser : UserManager.getInstance().getUsers()) {
            if(!mWorkspace.getUsers().contains(onlineUser))
                mUserListAdapter.add(onlineUser);
        }
    }

    // ---------------------------
    // -------- Listeners --------
    // ---------------------------

    private AdapterView.OnItemClickListener mUserListOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final User user = (User) parent.getItemAtPosition(position);

            mWorkspace.addUser(user);

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
                            mWorkspace.removeUser(user);
                            throw new Exception(response.getString(Constants.ERROR_KEY));
                        }

                        Toast.makeText(getApplicationContext(), response.getString(Constants.RESULT_KEY), Toast.LENGTH_SHORT).show();
                        mUserListAdapter.remove(user);

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

}
