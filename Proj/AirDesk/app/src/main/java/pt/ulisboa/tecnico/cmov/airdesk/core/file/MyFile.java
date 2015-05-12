package pt.ulisboa.tecnico.cmov.airdesk.core.file;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.Workspace;

/**
 * Created by edgar on 11-05-2015.
 */
public class MyFile {

    public static final String VERSION_KEY = "version";
    public static final String NAME_KEY = "name";

    private int mVersion;
    private boolean isLocked;
    private Workspace mWorkspace;
    private String mName;
    private File mFile;

    public MyFile(Workspace workspace, String name, int version) {
        mWorkspace = workspace;
        mVersion = version;
        mName = name;
        isLocked = false;
    }

    public int getVersion() { return mVersion; }
    public boolean isLocked() { return isLocked; }
    public Workspace getWorkspace() { return mWorkspace; }
    public File getFile() { return mFile; }
    public String getName() { return mName; }


    public synchronized boolean open() {
        if(isLocked)
            return false;
        isLocked = true;
        return true;
    }

    public synchronized boolean close() {
        if(!isLocked)
            return false;
        isLocked = false;
        return true;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(NAME_KEY, getFile().getName());
            jsonObject.put(VERSION_KEY, getVersion());
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            return jsonObject;
        }

    }
}
