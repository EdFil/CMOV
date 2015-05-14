package pt.ulisboa.tecnico.cmov.airdesk.core.workspace;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.core.file.LocalFile;
import pt.ulisboa.tecnico.cmov.airdesk.core.file.MyFile;
import pt.ulisboa.tecnico.cmov.airdesk.core.user.User;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.exception.WorkspaceExceedsMaxSpaceException;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.exception.WorkspaceQuotaBelowUsedQuotaException;
import pt.ulisboa.tecnico.cmov.airdesk.manager.WorkspaceManager;

/**
 * Created by edgar on 20-03-2015.
 */
public class LocalWorkspace extends Workspace {

    private List<LocalFile> mFiles;

    public LocalWorkspace(String name, User owner, long quota, boolean isPrivate, Collection<String> tags) {
        this(-1, name, owner, quota, isPrivate, tags, Collections.EMPTY_LIST, Collections.EMPTY_LIST);
    }

    public LocalWorkspace(long workspaceId, String name, User owner, long quota, boolean isPrivate) {
        this(workspaceId, name, owner, quota, isPrivate, Collections.EMPTY_LIST, Collections.EMPTY_LIST, Collections.EMPTY_LIST);
    }

    public LocalWorkspace(long workspaceId, String name, User owner, long quota, boolean isPrivate, Collection<String> tags, Collection<String> users, Collection<LocalFile> files) {
        super(workspaceId, name, owner, quota, isPrivate, tags, users);
        setFiles(files);
    }

    public void addFile(LocalFile file) {
        mFiles.add(file);
    }

    public void removeFile(LocalFile file) {
        mFiles.remove(file);
    }

    public LocalFile getFileByName(String name) {
        for(LocalFile file : mFiles)
            if(file.getName().equals(name))
                return file;
        return null;
    }

    public List<LocalFile> getFiles() {
        return mFiles;
    }

    public void setFiles(Collection<LocalFile> files) {
        mFiles = new ArrayList<>(files);
    }

    public long getUsedQuota(){
        long bytesUsed = 0;
        for(MyFile file : mFiles)
            bytesUsed += file.getFile().length();
        return bytesUsed;
    }


    public void setQuota(long quota) {
        if(getFiles() != null && quota < getUsedQuota())
            throw new WorkspaceQuotaBelowUsedQuotaException(getMaxQuota());
        if(quota > WorkspaceManager.getInstance().getSpaceAvailableInternalStorage())
            throw new WorkspaceExceedsMaxSpaceException(
                    WorkspaceManager.getInstance().getContext(),
                    WorkspaceManager.getInstance().getSpaceAvailableInternalStorage());
        super.setQuota(quota);
    }


    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(NAME_KEY, getName());
            jsonObject.put(OWNER_KEY, getOwner().toJson());
            jsonObject.put(MAX_QUOTA_KEY, getMaxQuota());
            jsonObject.put(USED_QUOTA_KEY, getUsedQuota());
            jsonObject.put(IS_PRIVATE_KEY, isPrivate());
            JSONArray tagsArray = new JSONArray();
            for(String tag : getTags())
                tagsArray.put(tag);
            jsonObject.put(TAGS_KEY, tagsArray);
            JSONArray usersArray = new JSONArray();
            for(String user : getAccessList())
                usersArray.put(user);
            jsonObject.put(ACCESS_LIST_KEY, usersArray);
            JSONArray filesArray = new JSONArray();
            for(LocalFile file : mFiles){
                filesArray.put(file.toJson());
            }
            jsonObject.put(FILES_KEY, filesArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }finally {
            return jsonObject;
        }
    }

}
