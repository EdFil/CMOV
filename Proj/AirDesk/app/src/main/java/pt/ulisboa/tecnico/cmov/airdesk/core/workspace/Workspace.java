package pt.ulisboa.tecnico.cmov.airdesk.core.workspace;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import pt.ulisboa.tecnico.cmov.airdesk.core.tag.Tag;
import pt.ulisboa.tecnico.cmov.airdesk.core.user.User;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.exception.WorkspaceAddTagToPrivateException;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.exception.WorkspaceExceedsMaxSpaceException;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.exception.WorkspaceNameIsEmptyException;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.exception.WorkspaceNegativeQuotaException;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.exception.WorkspacePublicNoTagsException;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.exception.WorkspaceQuotaIsZeroException;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.exception.WorkspaceRemoveOwnerException;

public class Workspace {

    private String mName;
    private User mOwner;
    private long mQuota;
    private boolean mIsPrivate;
    private long mDatabaseId;
    private HashSet<Tag> mTags;
    private HashSet<User> mUsers;
    private HashSet<File> mFiles;
    private WorkspaceManager mWorkspaceManager;

    public Workspace(String name, User owner, long quota, boolean isPrivate, Tag[] tags, User[] users, File[] files, WorkspaceManager workspaceManager){
        setWorkspaceManager(workspaceManager);
        setName(name);
        setOwner(owner);
        setQuota(quota);
        setIsPrivate(isPrivate);
        setTags(new HashSet<Tag>(Arrays.asList(tags)));
        setUsers(new HashSet<User>(Arrays.asList(users)));
        setFiles(new HashSet<File>(Arrays.asList(files)));
    }

    // Getters
    public String getName() { return mName; }
    public User getOwner() { return mOwner; }
    public long getQuota() { return mQuota; }
    public boolean isPrivate() { return mIsPrivate; }
    public long getDatabaseId() { return mDatabaseId; }
    public HashSet<Tag> getTags() { return mTags; }
    public HashSet<User> getUsers() { return mUsers; }
    public HashSet<File> getFiles() { return mFiles; }

    // Setters
    public void setName(String name) throws WorkspaceNameIsEmptyException, NullPointerException {
        if(name == null)
            throw new NullPointerException("Workspace cannot be null");
        if(name.isEmpty())
            throw new WorkspaceNameIsEmptyException();
        // TODO: Check if name already exists
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
        long maxQuota = mWorkspaceManager.getSpaceAvailableInternalStorage();
        if(quota > maxQuota)
            throw new WorkspaceExceedsMaxSpaceException(mWorkspaceManager.getContext(), maxQuota);
        mQuota = quota;
    }

    public void setIsPrivate(boolean isPrivate) {
        mIsPrivate = isPrivate;
    }

    public void setDatabaseId(long databaseId){
        mDatabaseId = databaseId;
    }

    public void setTags(Collection<Tag> tags) {
        if(isPrivate())
            throw new WorkspaceAddTagToPrivateException();
        if(tags == null)
            throw new NullPointerException("Tags is null");
        if(tags.isEmpty())
            throw new WorkspacePublicNoTagsException();
        mTags = new HashSet<Tag>(tags);
    }

    public void setUsers(Collection<User> users) {
        if(users == null)
            throw new NullPointerException("Users cannot be null");
        mUsers = new HashSet<User>(users);
        if(!getUsers().contains(getOwner())){
            getUsers().add(getOwner());
        }
    }

    public void setFiles(Collection<File> files) {
        if(files == null)
            throw new NullPointerException("Files cannot be null");
        mFiles = new HashSet<File>(files);
    }

    public void setWorkspaceManager(WorkspaceManager workspaceManager){
        if(workspaceManager == null)
            throw new NullPointerException("Workspace Manager cannot be null");
        mWorkspaceManager = workspaceManager;
    }

    // Class functions
    public void addTag(Tag tag) { mTags.add(tag); }
    public void removeTag(Tag tag) { mTags.remove(tag); }

    public void addUser(User user) { mUsers.add(user); }
    public void removeUser(User user) {
        if(user.equals(getOwner()))
            throw new WorkspaceRemoveOwnerException();
        mUsers.remove(user);
    }

    public void addFile(File file) { mFiles.add(file); }
    public void removeFile(File file) { mFiles.remove(file); }
    
}
