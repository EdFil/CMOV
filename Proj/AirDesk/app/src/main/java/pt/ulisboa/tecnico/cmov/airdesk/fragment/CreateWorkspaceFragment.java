package pt.ulisboa.tecnico.cmov.airdesk.fragment;

import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.R;
import pt.ulisboa.tecnico.cmov.airdesk.adapter.TagListAdapter;
import pt.ulisboa.tecnico.cmov.airdesk.adapter.WorkspaceListAdapter;
import pt.ulisboa.tecnico.cmov.airdesk.core.tag.Tag;
import pt.ulisboa.tecnico.cmov.airdesk.core.user.User;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.WorkspaceManager;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.exception.WorkspaceException;

/**
 * Created by edgar on 30-03-2015.
 */
public class CreateWorkspaceFragment extends DialogFragment {

    List<String> mTagCache;
    TagListAdapter mTagListAdapter;
    WorkspaceListAdapter mWorkspaceListAdapter;

    Button cancelButton, createButton, addTagButton;
    EditText workspaceNameText, quotaValueText, newTagText;
    Switch privacySwitch;
    ListView tagList;


    public static CreateWorkspaceFragment newInstance() {
        return new CreateWorkspaceFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        mTagCache = new ArrayList<String>();
        getDialog().setTitle("Create new Workspace");
        final View view = inflater.inflate(R.layout.fragment_create_workspace, container, false);

        addTagButton = (Button)view.findViewById(R.id.addTagButton);
        cancelButton = (Button)view.findViewById(R.id.cancelWorkspaceDialog);
        createButton = (Button) view.findViewById(R.id.createWorkspaceDialog);
        newTagText = (EditText)view.findViewById(R.id.newTag);
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

        workspaceNameText.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isLetterOrDigit(source.charAt(i)) && !Character.isSpaceChar(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }
        }});


        mTagListAdapter = new TagListAdapter(view.getContext(), new ArrayList<String>());
        tagList.setAdapter(mTagListAdapter);
        tagList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                ArrayAdapter<String> list = (ArrayAdapter<String>) parent.getAdapter();
//                list.remove(list.getItem(position));
                mTagListAdapter.remove(mTagListAdapter.getItem(position));
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
                ArrayList<Tag> tags = new ArrayList<Tag>();
                for(int i = 0; i < tagList.getChildCount(); i++)
                    tags.add(new Tag(((TextView)((LinearLayout)tagList.getChildAt(i)).findViewById(R.id.tagName)).getText().toString()));
                try {
                    SharedPreferences pref = v.getContext().getSharedPreferences("UserPref", v.getContext().MODE_PRIVATE);
                    User owner = new User(pref.getString("user_email", null), pref.getString("user_nick", null));
                    WorkspaceManager.getInstance().addNewWorkspace(workspaceName, owner, (long)workspaceQuota, !privacySwitch.isChecked(), tags);
                    Toast.makeText(view.getContext(), "Workspace created", Toast.LENGTH_SHORT).show();
                    dismiss();
                } catch (WorkspaceException e) {
                    Toast.makeText(view.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}
