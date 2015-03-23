package pt.ulisboa.tecnico.cmov.cmov_proj.core.user;

import android.provider.ContactsContract;

import java.util.Comparator;
import java.util.HashSet;

import pt.ulisboa.tecnico.cmov.cmov_proj.core.workspace.ForeignWorkspace;
import pt.ulisboa.tecnico.cmov.cmov_proj.core.workspace.LocalWorkspace;

public class User {

    private String mEmail;
    private String mNick;
    private HashSet<LocalWorkspace> mLocalWorkspaces;
    private HashSet<ForeignWorkspace> mForeignWorkspaces;

    public User(String email, String nick){
        mEmail = email;
        mNick = nick;
        mLocalWorkspaces = new HashSet<LocalWorkspace>();
        mForeignWorkspaces = new HashSet<ForeignWorkspace>();
    }

    public String getEmail() { return mEmail; }
    public String getNick() { return mNick; }

    @Override
    public boolean equals(Object o) {
        if(o instanceof User){
            User user = (User) o;
            return user.getEmail().equals(getEmail());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return mEmail.hashCode();
    }

    @Override
    public String toString(){
        return "Email = " + getEmail() + ", Nick = " + getNick();
    }

}
