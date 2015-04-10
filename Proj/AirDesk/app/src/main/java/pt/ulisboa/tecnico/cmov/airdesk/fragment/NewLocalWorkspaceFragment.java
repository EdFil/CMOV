package pt.ulisboa.tecnico.cmov.airdesk.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.R;
import pt.ulisboa.tecnico.cmov.airdesk.adapter.TagListAdapter;
import pt.ulisboa.tecnico.cmov.airdesk.core.tag.Tag;
import pt.ulisboa.tecnico.cmov.airdesk.core.user.UserManager;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.WorkspaceManager;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.exception.WorkspaceException;

public class NewLocalWorkspaceFragment extends DialogFragment {

    List<String> mTagCache;
    TagListAdapter mTagListAdapter;

    Button cancelButton, createButton, addTagButton;
    EditText workspaceNameText, quotaValueText, newTagText;
    Switch privacySwitch;
    ListView tagList;
    TextView tags;

    OnNewWorkspaceFragmentListener mCallback;

    // AirDeskActivity must implement this interface
    public interface OnNewWorkspaceFragmentListener {
        public void updateWorkspaceList();
    }

    public static NewLocalWorkspaceFragment newInstance() {
        return new NewLocalWorkspaceFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        mTagCache = new ArrayList<String>();

        final View view = inflater.inflate(R.layout.fragment_new_workspace, container, false);
        view.getContext();
        addTagButton = (Button)view.findViewById(R.id.addTagButton);
        cancelButton = (Button)view.findViewById(R.id.cancelWorkspaceDialog);
        createButton = (Button) view.findViewById(R.id.createWorkspaceDialog);
        newTagText = (EditText)view.findViewById(R.id.newTag);
        workspaceNameText = (EditText) view.findViewById(R.id.newWorkspaceName);
        quotaValueText = (EditText) view.findViewById(R.id.editQuota);
        privacySwitch = (Switch) view.findViewById(R.id.privateSwitch);
        tags = (TextView) view.findViewById(R.id.tags);
        tagList = (ListView) view.findViewById(R.id.tagList);

        addTagButton.setVisibility(View.INVISIBLE);
        newTagText.setVisibility(View.INVISIBLE);
        tags.setVisibility(View.INVISIBLE);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        // TODO : no teclado, quando selecionado a opcao sugerida pelo dicionario, não aceita
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


        // OnChangeListener for switch
        privacySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            // b = isPrivate
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                // if private hide, if public make visible
                addTagButton.setVisibility(b ? View.VISIBLE : View.INVISIBLE);
                newTagText.setVisibility(b ? View.VISIBLE : View.INVISIBLE);
                tags.setVisibility(b ? View.VISIBLE : View.INVISIBLE);

                if(!b) {

                    // if the switch is switched to Private, clear list from adapter
                    // but save on mTagCache (auxiliary list)
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

                ArrayList<Tag> tags = new ArrayList<>();

                for(int i = 0; i < tagList.getChildCount(); i++)
                    tags.add(new Tag(((TextView)(tagList.getChildAt(i)).findViewById(R.id.tagName)).getText().toString()));

                try {
                    // Create workspace with associated user (owner) in database
                    WorkspaceManager.getInstance().addLocalWorkspace(workspaceName, UserManager.getInstance().getOwner(), (long) workspaceQuota, !privacySwitch.isChecked(), tags);
                    mCallback.updateWorkspaceList();
                    Toast.makeText(view.getContext(), "Workspace created", Toast.LENGTH_SHORT).show();

                    // Close dialog fragment
                    dismiss();
                } catch (WorkspaceException e) {
                    Toast.makeText(view.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnNewWorkspaceFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnHeadlineSelectedListener");
        }
    }
}
