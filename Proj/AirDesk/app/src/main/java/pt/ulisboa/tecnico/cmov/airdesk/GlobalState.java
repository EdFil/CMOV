package pt.ulisboa.tecnico.cmov.airdesk;

import android.app.Application;

import pt.ulisboa.tecnico.cmov.airdesk.core.user.UserManager;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.WorkspaceManager;

/**
 * Created by edgar on 13-04-2015.
 */
public class GlobalState extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        WorkspaceManager.getInstance().initWorkspaceManager(this);
        UserManager.getInstance().initUserManager(this);
    }

    public void reloadManagers() {
        WorkspaceManager.getInstance().refreshWorkspaceLists();
//        UserManager.getInstance().refreshUserList();
    }
}
