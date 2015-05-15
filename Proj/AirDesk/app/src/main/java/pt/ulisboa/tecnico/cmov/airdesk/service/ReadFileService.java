package pt.ulisboa.tecnico.cmov.airdesk.service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.ulisboa.tecnico.cmov.airdesk.core.file.LocalFile;
import pt.ulisboa.tecnico.cmov.airdesk.core.file.MyFile;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.LocalWorkspace;
import pt.ulisboa.tecnico.cmov.airdesk.manager.WorkspaceManager;
import pt.ulisboa.tecnico.cmov.airdesk.tasks.ReadFileTask;
import pt.ulisboa.tecnico.cmov.airdesk.util.Constants;

/**
 * Created by edgar on 06-05-2015.
 */
public class ReadFileService implements AirDeskService {

    @Override
    public JSONObject execute(JSONArray arguments) {
        JSONObject response = new JSONObject();
        try {
            String workspaceName = arguments.getString(0);
            String fileName = arguments.getString(1);
            LocalWorkspace workspace = WorkspaceManager.getInstance().getLocalWorkspaceWithName(workspaceName);
            if(workspace == null)
                throw new JSONException("Cannot find workspace with name \"" + workspaceName + "\"");
            final LocalFile file = workspace.getFileByName(fileName);
            if(file == null)
                throw new JSONException("Cannot find file with name \"" + fileName + "\"");
            ReadFileTask task = new ReadFileTask();
            task.execute(file.getFile());
            String content = task.get();

            response.put(MyFile.CONTENT_KEY, content);
        } catch (JSONException e) {
                response.put(Constants.ERROR_KEY, e.getMessage());
        } finally {
            return response;
        }
    }

}
