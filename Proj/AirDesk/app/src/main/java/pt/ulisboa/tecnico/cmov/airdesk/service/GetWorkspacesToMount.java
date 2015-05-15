package pt.ulisboa.tecnico.cmov.airdesk.service;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.LocalWorkspace;
import pt.ulisboa.tecnico.cmov.airdesk.manager.UserManager;
import pt.ulisboa.tecnico.cmov.airdesk.manager.WorkspaceManager;
import pt.ulisboa.tecnico.cmov.airdesk.util.Constants;
import pt.ulisboa.tecnico.cmov.airdesk.util.Util;

public class GetWorkspacesToMount implements AirDeskService {

    public static final String TAG = GetWorkspacesToMount.class.getSimpleName();

    @Override
    public JSONObject execute(JSONArray jsonArray) {
        // Parse the array to get the arguments
        String userEmail = (String) jsonArray.remove(0);
        String[] arguments = Util.parseArguments(jsonArray);

        List<LocalWorkspace> workspaceList = WorkspaceManager.getInstance().getLocalWorkspacesToMount(userEmail, arguments);

        JSONArray response = new JSONArray();
        for(LocalWorkspace workspace : workspaceList) {
            response.put(workspace.toJSON());
        }

        Log.d(TAG, response.toString());

        try {
            return new JSONObject().put(Constants.WORKSPACE_LIST_KEY, response);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }
}
