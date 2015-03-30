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

import pt.ulisboa.tecnico.cmov.airdesk.R;
import pt.ulisboa.tecnico.cmov.airdesk.adapter.TagListAdapter;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.LocalWorkspace;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.Workspace;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.exception.WorkspaceException;

/**
 * Created by edgar on 30-03-2015.
 */
public class CreateWorkspace extends DialogFragment {

    List<String> mTagCache;
    TagListAdapter mTagListAdapter;

    public static CreateWorkspace newInstance() {
        return new CreateWorkspace();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mTagCache = new ArrayList<String>();
        getDialog().setTitle("Create new Workspace");
        final View view = inflater.inflate(R.layout.create_workspace_dialog, container, false);

        ((Button)view.findViewById(R.id.cancelWorkspaceDialog)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mTagListAdapter = new TagListAdapter(view.getContext(), new ArrayList<String>());
        mTagListAdapter.add("ASKDKASMD");
        ((ListView)view.findViewById(R.id.tagList)).setAdapter(mTagListAdapter);
        ((ListView)view.findViewById(R.id.tagList)).setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayAdapter<String> list = (ArrayAdapter<String>) parent.getAdapter();
                list.remove(list.getItem(position));
                return true;
            }
        });

        // OnChangeListener for switch
        ((Switch)view.findViewById(R.id.privateSwitch)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                view.findViewById(R.id.addTagButton).setVisibility(b ? View.VISIBLE : View.INVISIBLE);
                view.findViewById(R.id.newTag).setVisibility(b ? View.VISIBLE : View.INVISIBLE);
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

        ((Button)view.findViewById(R.id.addTagButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText tagString = (EditText)v.getRootView().findViewById(R.id.newTag);
                String tag = tagString.getText().toString().trim();
                if(tag.length() > 0) {
                    mTagListAdapter.add(tag);
                    tagString.getText().clear();
                }
            }
        });

        ((Button)view.findViewById(R.id.createWorkspaceDialog)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String workspaceName = ((EditText)v.getRootView().findViewById(R.id.newWorkspaceName)).getText().toString().trim();
                int workspaceQuota = Integer.parseInt(((EditText)v.getRootView().findViewById(R.id.editQuota)).getText().toString());
                try {
                    Workspace workspace = new LocalWorkspace(v.getContext(), workspaceName, workspaceQuota, ((Switch) view.findViewById(R.id.privateSwitch)).isChecked(), null);
                    dismiss();
                } catch (WorkspaceException e) {
                    Toast.makeText(view.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
//                if(workspaceName.isEmpty()){
//                    Toast.makeText(v.getContext(), "Workspace name is empty", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                // Get workspace quota from editText
//
//                if(workspaceQuota > 100) {
//                    Toast.makeText(v.getContext(), "Quota is too big (>100)", Toast.LENGTH_SHORT).show();
//                    return;
//                }



            }
        });

        return view;
    }
}
