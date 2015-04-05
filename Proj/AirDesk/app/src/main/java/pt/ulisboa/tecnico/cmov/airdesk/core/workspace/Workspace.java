package pt.ulisboa.tecnico.cmov.airdesk.core.workspace;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.core.tag.Tag;
import pt.ulisboa.tecnico.cmov.airdesk.core.user.User;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.exception.WorkspaceExceedsMaxSpaceException;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.exception.WorkspaceNameIsEmptyException;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.exception.WorkspaceNegativeQuotaException;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.exception.WorkspacePublicNoTagsException;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.exception.WorkspaceQuotaIsZeroException;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.exception.WorkspaceRemoveOwnerException;

public class Workspace implements Parcelable {

    private String mName;
    private User mOwner;
    private long mQuota;
    private boolean mIsPrivate;
    private long mDatabaseId;
    private List<Tag> mTags;
    private List<User> mUsers;
    private List<File> mFiles;
    private WorkspaceManager mWorkspaceManager;

    public Workspace(String name, User owner, long quota, boolean isPrivate, Collection<Tag> tags, Collection<User> users, Collection<File> files, WorkspaceManager workspaceManager){
        setWorkspaceManager(workspaceManager);
        setName(name);
        setOwner(owner);
        setQuota(quota);
        setIsPrivate(isPrivate);
        setTags(new ArrayList<Tag>(tags));
        setUsers(new ArrayList<User>(users));
        setFiles(new ArrayList<File>(files));
    }

    // Getters
    public String getName() { return mName; }
    public User getOwner() { return mOwner; }
    public long getQuota() { return mQuota; }
    public boolean isPrivate() { return mIsPrivate; }
    public long getDatabaseId() { return mDatabaseId; }
    public List<Tag> getTags() { return mTags; }
    public List<User> getUsers() { return mUsers; }
    public List<File> getFiles() { return mFiles; }

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
        if(!isPrivate() && tags.isEmpty())
            throw new WorkspacePublicNoTagsException();
        mTags = new ArrayList<Tag>(tags);
        for(Tag tag : mTags){
            tag.setWorkspace(this);
        }
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





    // ---------------------
    // Stuff for Parcelable
    // ---------------------


    private Workspace(Parcel in){
        mName = in.readString();
        mOwner = in.readParcelable(User.class.getClassLoader());
        mQuota = in.readLong();
        mIsPrivate = in.readInt() == 1;
        mDatabaseId = in.readLong();
        in.readList((mTags = new ArrayList<Tag>()), Tag.class.getClassLoader());
        in.readList((mUsers = new ArrayList<User>()), User.class.getClassLoader());
        in.readList((mFiles = new ArrayList<File>()), File.class.getClassLoader());
        mWorkspaceManager = WorkspaceManager.getInstance();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeParcelable(mOwner, PARCELABLE_WRITE_RETURN_VALUE);
        dest.writeLong(mQuota);
        dest.writeInt(mIsPrivate ? 1 : 0);
        dest.writeLong(mDatabaseId);
        dest.writeList(mTags);
        dest.writeList(mUsers);
        dest.writeList(mFiles);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Workspace> CREATOR = new Parcelable.Creator<Workspace>() {
        public Workspace createFromParcel(Parcel in) {
            return new Workspace(in);
        }

        public Workspace[] newArray(int size) {
            return new Workspace[size];
        }
    };
}
