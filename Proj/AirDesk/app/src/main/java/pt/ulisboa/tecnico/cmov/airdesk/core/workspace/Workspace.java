package pt.ulisboa.tecnico.cmov.airdesk.core.workspace;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.core.file.MyFile;
import pt.ulisboa.tecnico.cmov.airdesk.core.user.User;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.exception.WorkspaceNameIsEmptyException;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.exception.WorkspaceNegativeQuotaException;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.exception.WorkspaceQuotaIsZeroException;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.exception.WorkspaceRemoveOwnerException;
import pt.ulisboa.tecnico.cmov.airdesk.manager.WorkspaceManager;

public abstract class Workspace {

    public static final String TAG = Workspace.class.getSimpleName();
    public static final String NAME_KEY = "name";
    public static final String OWNER_KEY = "owner";
    public static final String USED_QUOTA_KEY = "used_quota";
    public static final String MAX_QUOTA_KEY = "max_quota";
    public static final String IS_PRIVATE_KEY = "is_private";
    public static final String TAGS_KEY = "tags";
    public static final String ACCESS_LIST_KEY = "users";
    public static final String FILES_KEY = "files";

    private String mName;
    private User mOwner;
    private long mQuota;
    private boolean mIsPrivate;
    private long mDatabaseId;
    private List<String> mTags;
    private List<String> mAccessList;
    private WorkspaceManager mWorkspaceManager;


    public Workspace(long workspaceId, String name, User owner, long quota, boolean isPrivate, Collection<String> tags, Collection<String> users){
        setDatabaseId(workspaceId);
        setWorkspaceManager(WorkspaceManager.getInstance());
        setName(name);
        setOwner(owner);
        setQuota(quota);
        setIsPrivate(isPrivate);
        setTags(new ArrayList<>(tags));
        setAccessList(new ArrayList<>(users));
    }

    // Getters
    public String getName() { return mName; }
    public User getOwner() { return mOwner; }
    public long getMaxQuota() { return mQuota; }
    public boolean isPrivate() { return mIsPrivate; }
    public long getDatabaseId() { return mDatabaseId; }
    public List<String> getTags() { return mTags; }
    public List<String> getAccessList() { return mAccessList; }
    public String getWorkspaceFolderName() { return getOwner().getEmail().replaceAll("@|\\.", "_") + "_" + getName(); }

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
        if(quota < 0)
            throw new WorkspaceNegativeQuotaException();
        if(quota == 0)
            throw new WorkspaceQuotaIsZeroException();
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

    public void setAccessList(Collection<String> users) {
        if(users == null)
            throw new NullPointerException("Users cannot be null");
        mAccessList = new ArrayList<String>(users);
        if(!getAccessList().contains(getOwner().getEmail())){
            getAccessList().add(getOwner().getEmail());
        }
    }

    public void setWorkspaceManager(WorkspaceManager workspaceManager){
        if(workspaceManager == null)
            throw new NullPointerException("Workspace Manager cannot be null");
        mWorkspaceManager = workspaceManager;
    }

    // Class functions
    public void addTag(String tag) { mTags.add(tag); }
    public void removeTag(String tag) { mTags.remove(tag); }
    public boolean hasTag(String otherTag) {
        for(String tag : mTags)
            if(tag.equals(otherTag))
                return true;
        return false;
    }

    public boolean containsAtLeastOneTag(Collection<String> tags) {
        for(String tag : tags)
            if(hasTag(tag))
                return true;
        return false;
    }

    public abstract MyFile getFileByName(String name);

    public void addAccessToUser(String user) { mAccessList.add(user); }
    public void removeAccessToUser(String user) {
        if(user.equals(getOwner().getEmail()))
            throw new WorkspaceRemoveOwnerException();
        mAccessList.remove(user);
    }
}
