package pt.ulisboa.tecnico.cmov.airdesk.service;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.Workspace;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.WorkspaceManager;
import pt.ulisboa.tecnico.cmov.airdesk.util.Constants;
import pt.ulisboa.tecnico.cmov.airdesk.util.Util;

/**
 * Created by edgar on 06-05-2015.
 */
public class GetWorkspacesWithTagsService implements AirDeskService {

    public static final String TAG = GetWorkspacesWithTagsService.class.getSimpleName();

    @Override
    public JSONObject execute(JSONArray jsonArray) {
        // Parse the array to get the arguments
        String[] tagValues = Util.parseArguments(jsonArray);

        List<Workspace> workspaceList = WorkspaceManager.getInstance().getWorkspacesWithTags(tagValues);

        JSONArray response = new JSONArray();
        for(Workspace workspace : workspaceList) {
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
