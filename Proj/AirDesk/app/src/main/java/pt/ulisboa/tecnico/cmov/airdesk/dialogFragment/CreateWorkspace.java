package pt.ulisboa.tecnico.cmov.airdesk.dialogFragment;

import android.app.DialogFragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.AirDeskActivity;
import pt.ulisboa.tecnico.cmov.airdesk.R;
import pt.ulisboa.tecnico.cmov.airdesk.adapter.TagListAdapter;
import pt.ulisboa.tecnico.cmov.airdesk.adapter.WorkspaceListAdapter;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.LocalWorkspace;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.Workspace;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.exception.WorkspaceException;

/**
 * Created by edgar on 30-03-2015.
 */
public class CreateWorkspace extends DialogFragment {

    List<String> mTagCache;
    TagListAdapter mTagListAdapter;
    WorkspaceListAdapter mWorkspaceListAdapter;

    Button cancelButton, createButton, addTagButton;
    EditText workspaceNameText, quotaValueText, newTagText;
    Switch privacySwitch;
    ListView tagList;


    public static CreateWorkspace newInstance() {
        return new CreateWorkspace();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mTagCache = new ArrayList<String>();
        getDialog().setTitle("Create new Workspace");
        final View view = inflater.inflate(R.layout.create_workspace_dialog, container, false);

        addTagButton = (Button)view.findViewById(R.id.addTagButton);
        newTagText = (EditText)view.findViewById(R.id.newTag);
        cancelButton = (Button)view.findViewById(R.id.cancelWorkspaceDialog);
        createButton = (Button) view.findViewById(R.id.createWorkspaceDialog);
        workspaceNameText = (EditText) view.findViewById(R.id.newWorkspaceName);
        quotaValueText = (EditText) view.findViewById(R.id.editQuota);
        privacySwitch = (Switch) view.findViewById(R.id.privateSwitch);
        tagList = (ListView) view.findViewById(R.id.tagList);

        addTagButton.setVisibility(View.INVISIBLE);
        newTagText.setVisibility(View.INVISIBLE);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });



        mTagListAdapter = new TagListAdapter(view.getContext(), new ArrayList<String>());
        tagList.setAdapter(mTagListAdapter);
        tagList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayAdapter<String> list = (ArrayAdapter<String>) parent.getAdapter();
                list.remove(list.getItem(position));
                return true;
            }
        });

        // OnChangeListener for switch
        privacySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                addTagButton.setVisibility(b ? View.VISIBLE : View.INVISIBLE);
                newTagText.setVisibility(b ? View.VISIBLE : View.INVISIBLE);
                if(!b) {
                    mTagCache.clear();
                    for(int i = 0; i < mTagListAdapter.getCount(); i++){
                        mTagCache.add(mTagListAdapter.getItem(i));
                    }
                    mTagListAdapter.clear();

                }
                else{
                    mTagListAdapter.addAll(mTagCache);
                }

            }
        });

        addTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText tagString = (EditText) v.getRootView().findViewById(R.id.newTag);
                String tag = tagString.getText().toString().trim();
                if (tag.length() > 0) {
                    mTagListAdapter.add(tag);
                    tagString.getText().clear();
                }
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String workspaceName = workspaceNameText.getText().toString().trim();
                int workspaceQuota = Integer.parseInt(quotaValueText.getText().toString());
                try {
                    Workspace workspace = new LocalWorkspace(v.getContext(), workspaceName, workspaceQuota, privacySwitch.isChecked(), null);
                    Toast.makeText(view.getContext(), "Workspace created", Toast.LENGTH_SHORT).show();
                    dismiss();

                    mWorkspaceListAdapter = ((AirDeskActivity)getActivity()).getWorkspaceListAdapter();
                    mWorkspaceListAdapter.add(workspace);
                } catch (WorkspaceException e) {
                    Toast.makeText(view.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}
