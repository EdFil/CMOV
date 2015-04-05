package pt.ulisboa.tecnico.cmov.airdesk.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.R;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.Workspace;
import pt.ulisboa.tecnico.cmov.airdesk.core.workspace.WorkspaceManager;

public class NewFileFragment extends DialogFragment {

    Workspace mWorkspace;

    Button cancelButton, newButton;
    EditText newFileNameText;

    public static NewFileFragment newInstance() {
        return new NewFileFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_new_file, container, false);

//        getDialog().setTitle("Create new Workspace");
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//        getDialog().setCanceledOnTouchOutside(true);
//        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.Base_Theme_AppCompat_Dialog_FixedSize);

        // Get the WORKSPACE selected in order to retrieve the respective files
        Bundle bundle = getArguments();
        mWorkspace = bundle.getParcelable("Workspace");

//        getDialog().setTitle("Create new Workspace");

        view.getContext();

        newFileNameText = (EditText) view.findViewById(R.id.newFileName);

        cancelButton = (Button)view.findViewById(R.id.cancelFile);
        newButton = (Button) view.findViewById(R.id.createFile);

        cancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fileName = newFileNameText.getText().toString().trim();
                    // Create workspace with associated user (owner) in database
                WorkspaceManager.getInstance().addFileToWorkspace(fileName, mWorkspace);
                Toast.makeText(v.getContext(), "File created", Toast.LENGTH_SHORT).show();

                // Close dialog fragment
                dismiss();

            }
        });


        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Update the list of files
        WorkspaceManager.getInstance().updateFileList();

    }
}
