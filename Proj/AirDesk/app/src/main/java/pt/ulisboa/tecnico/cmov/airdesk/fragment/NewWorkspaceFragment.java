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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.R;
import pt.ulisboa.tecnico.cmov.airdesk.core.tag.Tag;
import pt.ulisboa.tecnico.cmov.airdesk.core.user.UserManager;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.WorkspaceManager;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.exception.WorkspaceException;
import pt.ulisboa.tecnico.cmov.airdesk.custom.AddTagsLayout;
import pt.ulisboa.tecnico.cmov.airdesk.custom.PredicateLayout;
import pt.ulisboa.tecnico.cmov.airdesk.custom.QuotaValueLayout;
import pt.ulisboa.tecnico.cmov.airdesk.util.Constants;

public class NewWorkspaceFragment extends DialogFragment {

    List<String> mTagCache;

    Button cancelButton, createButton;
    EditText workspaceNameText, newTagText;
    Switch privacySwitch;
    ViewSwitcher mTagViewSwitcher;
    PredicateLayout mTagListLayout;
    AddTagsLayout mAddTagsLayout;
    QuotaValueLayout mQuotaValueLayout;
    TextView tags;

    OnNewWorkspaceFragmentListener mCallback;

    // AirDeskActivity must implement this interface
    public interface OnNewWorkspaceFragmentListener {
        public void updateWorkspaceList();
    }

    public static NewWorkspaceFragment newInstance() {
        return new NewWorkspaceFragment();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        mTagCache = new ArrayList<String>();

        final View view = inflater.inflate(R.layout.fragment_new_workspace, container, false);
        view.getContext();
        cancelButton = (Button)view.findViewById(R.id.cancelWorkspaceDialog);
        createButton = (Button) view.findViewById(R.id.createWorkspaceDialog);
        newTagText = (EditText)view.findViewById(R.id.newTag);
        workspaceNameText = (EditText) view.findViewById(R.id.newWorkspaceName);
        privacySwitch = (Switch) view.findViewById(R.id.privateSwitch);
        tags = (TextView) view.findViewById(R.id.tags);
        mTagListLayout = (PredicateLayout) view.findViewById(R.id.tagList);
        mTagViewSwitcher = (ViewSwitcher) view.findViewById(R.id.tagViewSwitcher);
        mAddTagsLayout = (AddTagsLayout) view.findViewById(R.id.addTagsView);
        mQuotaValueLayout = (QuotaValueLayout) view.findViewById(R.id.quotaLayout);



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

        // OnChangeListener for switch
        privacySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            // b = isPrivate
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mTagViewSwitcher.showNext();
            }
        });


        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // To force the focus change of the edit text, before processing the button click
                EditText mTagsEditText = (EditText) view.findViewById(R.id.newTag);
                mTagsEditText.clearFocus();

                String workspaceName = workspaceNameText.getText().toString().trim();

                long workspaceQuota = mQuotaValueLayout.getQuota();

                List<String> tags = mAddTagsLayout.getAllTags();

                try {
                    // Create workspace with associated user (owner) in database
                    WorkspaceManager.getInstance().addLocalWorkspace(workspaceName, UserManager.getInstance().getOwner(), workspaceQuota, !privacySwitch.isChecked(), tags);
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
