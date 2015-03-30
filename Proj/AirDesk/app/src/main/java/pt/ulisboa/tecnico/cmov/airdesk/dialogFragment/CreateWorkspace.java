package pt.ulisboa.tecnico.cmov.airdesk.dialogFragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.ViewSwitcher;

import pt.ulisboa.tecnico.cmov.airdesk.R;

/**
 * Created by edgar on 30-03-2015.
 */
public class CreateWorkspace extends DialogFragment {

    public static CreateWorkspace newInstance() {
        return new CreateWorkspace();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle("Create new Workspace");
        final View view = inflater.inflate(R.layout.create_workspace_dialog, container, false);

        // OnChangeListener for switch
        ((Switch)view.findViewById(R.id.privateSwitch)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                ViewSwitcher vs = (ViewSwitcher) compoundButton.getRootView().findViewById(R.id.viewSwitcher);
                vs.showNext();
            }
        });

        ((Button)view.findViewById(R.id.cancelWorkspaceDialog)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }
}
