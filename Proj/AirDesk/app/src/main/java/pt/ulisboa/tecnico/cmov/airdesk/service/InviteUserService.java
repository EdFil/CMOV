package pt.ulisboa.tecnico.cmov.airdesk.service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.ForeignWorkspace;
import pt.ulisboa.tecnico.cmov.airdesk.manager.WorkspaceManager;
import pt.ulisboa.tecnico.cmov.airdesk.util.Constants;

public class InviteUserService implements AirDeskService {

    @Override
    public JSONObject execute(JSONArray arguments) {
        JSONObject object = new JSONObject();
        try {
            JSONObject foreignWorkspaceInfo = new JSONObject(arguments.getString(0));
            ForeignWorkspace workspace = WorkspaceManager.getInstance().mountForeignWorkspace(foreignWorkspaceInfo);
            // TODO: Add workspace to foreign
            object.put(Constants.RESULT_KEY, "OK");
        } catch (JSONException e) {
            object.put(Constants.ERROR_KEY, e.getMessage());
        } finally {
            return object;
        }
    }

}
