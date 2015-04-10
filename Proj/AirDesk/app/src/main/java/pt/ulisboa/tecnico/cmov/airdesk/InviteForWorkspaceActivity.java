package pt.ulisboa.tecnico.cmov.airdesk;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.adapter.UserListAdapter;
import pt.ulisboa.tecnico.cmov.airdesk.core.user.User;
import pt.ulisboa.tecnico.cmov.airdesk.core.user.UserManager;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.Workspace;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.WorkspaceManager;
import pt.ulisboa.tecnico.cmov.airdesk.util.Constants;

public class InviteForWorkspaceActivity extends ActionBarActivity {

    UserManager userManager;
    UserListAdapter mUserListAdapter;
    Workspace workspace;

    ListView mUsersList;
    List<User> usersToInvite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_for_workspace);

        setTitle("Invite for Workspace");

        Intent intent = getIntent();
        int workspaceIndex = intent.getIntExtra(Constants.WORKSPACE_INDEX, -1);
        workspace = WorkspaceManager.getInstance().getWorkspaceAtIndex(true, workspaceIndex);

        userManager = UserManager.getInstance();

        usersToInvite = getUsersToInvite(workspace);

        mUserListAdapter = new UserListAdapter(this, usersToInvite);

        mUsersList = (ListView) findViewById(R.id.usersListView);
        mUsersList.setAdapter(mUserListAdapter);

        mUsersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String userEmail = ((User) parent.getItemAtPosition(position)).getEmail();
                User user = userManager.getUserByEmail(userEmail);
                WorkspaceManager.getInstance().insertWorkspaceToForeignWorkspaces(workspace, user);
                usersToInvite.remove(user);
                mUserListAdapter.notifyDataSetChanged();
            }
        });
    }

    private List<User> getUsersToInvite(Workspace workspace) {
        List<User> list = new ArrayList<User>(userManager.getUsers());
        List<User> workspaceUsers = workspace.getUsers();
        for(int i = list.size()-1; i >= 0; i--){
            if (workspaceUsers.contains(list.get(i)))
                list.remove(list.get(i));
        }
            return list;
    }

    // I guess this activity doesn't need a menu, but I will not delete it yet
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_invite_for_workspace, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
