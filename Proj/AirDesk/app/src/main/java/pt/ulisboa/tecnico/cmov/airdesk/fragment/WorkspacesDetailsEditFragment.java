package pt.ulisboa.tecnico.cmov.airdesk.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import pt.ulisboa.tecnico.cmov.airdesk.R;
import pt.ulisboa.tecnico.cmov.airdesk.core.tag.Tag;
import pt.ulisboa.tecnico.cmov.airdesk.core.user.User;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.Workspace;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.WorkspaceManager;
import pt.ulisboa.tecnico.cmov.airdesk.custom.PredicateLayout;

public class WorkspacesDetailsEditFragment extends Fragment {

    private static final int NUM_COLUMNS_PER_ROW = 2;
    boolean isLocal;

    EditText mNameInformationEdit;
    EditText mQuotaInformationEdit;
    Switch mPrivacyInformationSwitch;
    TextView mPrivateInformationText;
    TextView mPublicInformationText;
    EditText mNewTagInformationEdit;

    Button mAddTagButtonEdit;

    PredicateLayout mTagListLayout;
    TableLayout mTagsTableLayout;
    TableLayout mUsersTableLayout;

    LinearLayout quotaLayout;

    Workspace mWorkspace;

    public WorkspacesDetailsEditFragment() {}

    public static WorkspacesDetailsEditFragment newInstance() {
        return new WorkspacesDetailsEditFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View workspaceFragmentView = inflater.inflate(R.layout.fragment_workspace_details_edit, container, false);

        // Find Views
        mNameInformationEdit = (EditText) workspaceFragmentView.findViewById(R.id.nameInformationEdit);
        mQuotaInformationEdit = (EditText) workspaceFragmentView.findViewById(R.id.quotaInformationEdit);
        mPrivacyInformationSwitch = (Switch) workspaceFragmentView.findViewById(R.id.privateInformationSwitch);
        mPrivateInformationText = (TextView) workspaceFragmentView.findViewById(R.id.privateInformationText);
        mPublicInformationText = (TextView) workspaceFragmentView.findViewById(R.id.publicInformationText);

        quotaLayout = (LinearLayout) workspaceFragmentView.findViewById(R.id.quotaLayout);

        if(isLocal)
            quotaLayout.setVisibility(View.VISIBLE);
        else
            quotaLayout.setVisibility(View.GONE);

        mNewTagInformationEdit = (EditText) workspaceFragmentView.findViewById(R.id.newTagInformationEdit);
        mAddTagButtonEdit = (Button) workspaceFragmentView.findViewById(R.id.addTagButtonEdit);
        mAddTagButtonEdit.setVisibility(mWorkspace.isPrivate() ? View.INVISIBLE : View.VISIBLE);
        mNewTagInformationEdit.setVisibility(mWorkspace.isPrivate() ? View.INVISIBLE : View.VISIBLE);

        mTagListLayout = (PredicateLayout) workspaceFragmentView.findViewById(R.id.tagListEdit);

//        mTagsTableLayout = (TableLayout) workspaceFragmentView.findViewById(R.id.tagsTableEdit);
//        mTagsTableLayout.setStretchAllColumns(true);


        mUsersTableLayout = (TableLayout) workspaceFragmentView.findViewById(R.id.usersTableEdit);
        mUsersTableLayout.setStretchAllColumns(true);

        // Set initial values
        mNameInformationEdit.setText(mWorkspace.getName());
        mQuotaInformationEdit.setText(String.valueOf(mWorkspace.getMaxQuota()));
        mPrivacyInformationSwitch.setChecked(!mWorkspace.isPrivate());

        for(Tag tag : mWorkspace.getTags())
            addTagToTable(tag.getText());

        for(User user : mWorkspace.getUsers())
            addUserToTable(user.getEmail());

        // Set Listeners
        mPrivacyInformationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mAddTagButtonEdit.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
                mNewTagInformationEdit.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
            }
        });

        mPublicInformationText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPrivacyInformationSwitch.setChecked(true);
            }
        });

        mPrivateInformationText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPrivacyInformationSwitch.setChecked(false);
            }
        });

        mAddTagButtonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String tag = mNewTagInformationEdit.getText().toString().trim();

                if (tag.length() > 0) {
                    addTagToTable(tag);
                    WorkspaceManager.getInstance().addTagToWorkspace(tag, mWorkspace);
                    mNewTagInformationEdit.getText().clear();
                }
            }
        });

        return workspaceFragmentView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    public void sendWorkspaceDetails(Workspace workspace, boolean isLocal){
        mWorkspace = workspace;
        this.isLocal = isLocal;
    }

    public Workspace getEditedWorkspace() {
        String newWorkspaceName = mNameInformationEdit.getText().toString();
        Long newWorkspaceQuota = Long.parseLong(mQuotaInformationEdit.getText().toString().trim());
        boolean newWorkspacePrivacy = !mPrivacyInformationSwitch.isChecked();
        try {
            WorkspaceManager.getInstance().updateWorkspace(mWorkspace, newWorkspaceName, newWorkspaceQuota, newWorkspacePrivacy);
        }catch (Exception e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return mWorkspace;
    }

    private void addTagToTable(final String tag) {
        if (tag.length() > 0) {
            LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.tag_list_adapter, null);
            mTagListLayout.addView(view);
            ((TextView)view.findViewById(R.id.tagName)).setText(tag);
            view.findViewById(R.id.removeTagButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTagListLayout.removeView((LinearLayout) v.getParent());
                    WorkspaceManager.getInstance().removeTagFromWorkspace(tag, mWorkspace);

                }
            });
        }


//        TextView tagText = new TextView(getActivity());
//        tagText.setText(tag);
//        tagText.setTextAppearance(getActivity(), android.R.style.TextAppearance_DeviceDefault_Medium);
//        TableRow rowToInsertTag;
//        int numRows = mTagsTableLayout.getChildCount();
//        if(numRows == 0 || ((TableRow)mTagsTableLayout.getChildAt(numRows - 1)).getChildCount() >= NUM_COLUMNS_PER_ROW) {
//            rowToInsertTag = new TableRow(getActivity());
//            mTagsTableLayout.addView(rowToInsertTag);
//        } else {
//            rowToInsertTag = (TableRow)mTagsTableLayout.getChildAt(numRows - 1);
//        }
//        rowToInsertTag.addView(tagText, ActionBar.LayoutParams.WRAP_CONTENT);
    }

    private void addUserToTable(String tag) {
        TextView userText = new TextView(getActivity());
        userText.setText(tag);
        userText.setTextAppearance(getActivity(), android.R.style.TextAppearance_DeviceDefault_Medium);
        TableRow rowToInsertTag;
        int numRows = mUsersTableLayout.getChildCount();
        if(numRows == 0 || ((TableRow)mUsersTableLayout.getChildAt(numRows - 1)).getChildCount() >= NUM_COLUMNS_PER_ROW) {
            rowToInsertTag = new TableRow(getActivity());
            mUsersTableLayout.addView(rowToInsertTag);
        } else {
            rowToInsertTag = (TableRow)mUsersTableLayout.getChildAt(numRows - 1);
        }
        rowToInsertTag.addView(userText, ActionBar.LayoutParams.WRAP_CONTENT);
    }


}