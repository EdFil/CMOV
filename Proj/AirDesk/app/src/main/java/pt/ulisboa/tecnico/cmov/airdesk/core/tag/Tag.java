package pt.ulisboa.tecnico.cmov.airdesk.core.tag;

import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.Workspace;

/**
 * Created by edgar on 31-03-2015.
 */
public class Tag {

    private Workspace mWorkspace;
    private String mText;

    public Tag(String text) {
        mText = text;
    }

    // Getters
    public Workspace getWorkspace() { return mWorkspace; }
    public String getText() { return mText; }

    // Setters
    public void setText(String text) { mText = text; }

    @Override
    public String toString(){
        return "Tag '" + mText + "'.";
    }

}
