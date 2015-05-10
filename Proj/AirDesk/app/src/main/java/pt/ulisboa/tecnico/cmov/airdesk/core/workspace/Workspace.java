package pt.ulisboa.tecnico.cmov.airdesk.core.workspace;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.core.tag.Tag;
import pt.ulisboa.tecnico.cmov.airdesk.core.user.User;
import pt.ulisboa.tecnico.cmov.airdesk.manager.UserManager;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.exception.WorkspaceExceedsMaxSpaceException;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.exception.WorkspaceNameIsEmptyException;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.exception.WorkspaceNegativeQuotaException;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.exception.WorkspaceQuotaBelowUsedQuotaException;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.exception.WorkspaceQuotaIsZeroException;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.exception.WorkspaceRemoveOwnerException;
import pt.ulisboa.tecnico.cmov.airdesk.manager.WorkspaceManager;
import pt.ulisboa.tecnico.cmov.airdesk.manager.FileManager;

public abstract class Workspace {

    public static final String TAG = Workspace.class.getSimpleName();
    public static final String NAME_KEY = "name";
    public static final String OWNER_KEY = "owner";
    public static final String USED_QUOTA_KEY = "used_quota";
    public static final String MAX_QUOTA_KEY = "max_quota";
    public static final String IS_PRIVATE_KEY = "is_private";
    public static final String TAGS_KEY = "tags";
    public static final String USERS_KEY = "users";
    public static final String FILES_KEY = "files";

    private String mName;
    private User mOwner;
    private long mQuota;
    private boolean mIsPrivate;
    private long mDatabaseId;
    private List<String> mTags;
    private List<User> mUsers;
    private List<File> mFiles;
    private WorkspaceManager mWorkspaceManager;

    public Workspace(JSONObject jsonObject) throws JSONException {
        String workspaceName = jsonObject.getString(Workspace.NAME_KEY);
        User workspaceOwner = UserManager.getInstance().createUser(jsonObject.getJSONObject(Workspace.OWNER_KEY));
        long maxQuota = jsonObject.getLong(Workspace.MAX_QUOTA_KEY);
        boolean isPrivate = jsonObject.getBoolean(Workspace.IS_PRIVATE_KEY);
        List<String> tags = new ArrayList<>();
        JSONArray tagArray = jsonObject.getJSONArray(Workspace.TAGS_KEY);
        for(int i = 0; i < tagArray.length(); i++)
            tags.add(tagArray.getString(i));
        List<User> users = new ArrayList<>();
        JSONArray userArray = jsonObject.getJSONArray(Workspace.USERS_KEY);
        for(int i = 0; i < userArray.length(); i++)
            users.add(UserManager.getInstance().createUser(userArray.getJSONObject(i)));
        List<File> files = new ArrayList<>();
        JSONArray fileArray = jsonObject.getJSONArray(Workspace.FILES_KEY);
        for(int i = 0; i < fileArray.length(); i++)
            files.add(FileManager.fileNameToFile(WorkspaceManager.getInstance().getContext(), workspaceName, fileArray.getString(i)));

        init(-1, workspaceName, workspaceOwner, maxQuota, isPrivate, tags, users, files);
    }

    public Workspace(long workspaceId, String name, User owner, long quota, boolean isPrivate, Collection<String> tags, Collection<User> users, Collection<File> files){
        init(workspaceId, name, owner, quota, isPrivate, tags, users, files);
    }

    private void init(long workspaceId, String name, User owner, long quota, boolean isPrivate, Collection<String> tags, Collection<User> users, Collection<File> files) {
        setDatabaseId(workspaceId);
        setWorkspaceManager(WorkspaceManager.getInstance());
        setName(name);
        setOwner(owner);
        setQuota(quota);
        setIsPrivate(isPrivate);
        setTags(new ArrayList<>(tags));
        setUsers(new ArrayList<>(users));
        setFiles(new ArrayList<>(files));
    }

    // Getters
    public String getName() { return mName; }
    public User getOwner() { return mOwner; }
    public long getMaxQuota() { return mQuota; }
    public boolean isPrivate() { return mIsPrivate; }
    public long getDatabaseId() { return mDatabaseId; }
    public List<String> getTags() { return mTags; }
    public List<User> getUsers() { return mUsers; }
    public List<File> getFiles() { return mFiles; }
    public String getWorkspaceFolderName() { return getOwner().getDatabaseId() + "_" + getName(); }

    // Setters
    public void setName(String name) throws WorkspaceNameIsEmptyException, NullPointerException {
        if(name == null)
            throw new NullPointerException("Workspace cannot be null");
        if(name.isEmpty())
            throw new WorkspaceNameIsEmptyException();
        mName = name;
    }

    public void setOwner(User owner) throws NullPointerException{
        if(owner == null)
            throw new NullPointerException("Owner cannot be null");
        mOwner = owner;
    }

    public void setQuota(long quota) throws WorkspaceNegativeQuotaException{
        //TODO: Check when setting a quota bellow the used space
        if(quota < 0)
            throw new WorkspaceNegativeQuotaException();
        if(quota == 0)
            throw new WorkspaceQuotaIsZeroException();
        if(getFiles() != null && quota < getUsedQuota())
            throw new WorkspaceQuotaBelowUsedQuotaException(getMaxQuota());
        if(quota > mWorkspaceManager.getSpaceAvailableInternalStorage())
            throw new WorkspaceExceedsMaxSpaceException(mWorkspaceManager.getContext(), mWorkspaceManager.getSpaceAvailableInternalStorage());
        mQuota = quota;
    }

    public void setIsPrivate(boolean isPrivate) {
        mIsPrivate = isPrivate;
    }

    public void setDatabaseId(long databaseId){
        mDatabaseId = databaseId;
    }

    public void setTags(Collection<String> tags) {
        if(!isPrivate() && tags.isEmpty())
            Log.i(TAG, "Tags is empty in a public workspace");
        mTags = new ArrayList<String>(tags);
    }

    public void setUsers(Collection<User> users) {
        if(users == null)
            throw new NullPointerException("Users cannot be null");
        mUsers = new ArrayList<User>(users);
        if(!getUsers().contains(getOwner())){
            getUsers().add(getOwner());
        }
    }

    public void setFiles(Collection<File> files) {
        if(files == null)
            throw new NullPointerException("Files cannot be null");
        mFiles = new ArrayList<File>(files);
    }

    public void setWorkspaceManager(WorkspaceManager workspaceManager){
        if(workspaceManager == null)
            throw new NullPointerException("Workspace Manager cannot be null");
        mWorkspaceManager = workspaceManager;
    }

    // Class functions
    public void addTag(String tag) { mTags.add(tag); }
    public void removeTag(Tag tag) { mTags.remove(tag); }
    public void removeTagFromString(String tagName) { mTags.remove(tagName); }
    public boolean hasTag(String otherTag) {
        for(String tag : mTags)
            if(tag.equals(otherTag))
                return true;
        return false;
    }

    public boolean hasAllTags(Collection<String> tags) {
        for(String tag : tags)
            if(!hasTag(tag))
                return false;
        return true;
    }

    public void addUser(User user) { mUsers.add(user); }
    public void removeUser(User user) {
        if(user.equals(getOwner()))
            throw new WorkspaceRemoveOwnerException();
        mUsers.remove(user);
    }

    public long getUsedQuota(){
        long bytesUsed = 0;
        for(File file : mFiles)
            bytesUsed += file.length();
        return bytesUsed;
    }
    public void addFile(File file) { mFiles.add(file); }
    public void removeFile(File file) { mFiles.remove(file); }

    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(NAME_KEY, getName());
            jsonObject.put(OWNER_KEY, getOwner().toJson());
            jsonObject.put(MAX_QUOTA_KEY, getMaxQuota());
            jsonObject.put(USED_QUOTA_KEY, getUsedQuota());
            jsonObject.put(IS_PRIVATE_KEY, isPrivate());
            JSONArray tagsArray = new JSONArray();
            for(String tag : mTags)
                tagsArray.put(tag);
            jsonObject.put(TAGS_KEY, tagsArray);
            JSONArray usersArray = new JSONArray();
            for(User user : mUsers)
                usersArray.put(user.toJson());
            jsonObject.put(USERS_KEY, usersArray);
            JSONArray filesArray = new JSONArray();
            for(File file : mFiles){
                filesArray.put(file.getName());
            }
            jsonObject.put(FILES_KEY, filesArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }finally {
            return jsonObject;
        }
    }
}
