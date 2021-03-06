package pt.ulisboa.tecnico.cmov.simpleclient.fragment;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.simpleclient.R;
import pt.ulisboa.tecnico.cmov.simpleclient.SimpleClientActivity;

/**
 * Created by Diogo on 18-Apr-15.
 */
public class CreateContactDialogFragment extends DialogFragment {

    EditText name;
    EditText address;
    EditText number;
    EditText email;

    Button cancelButton;
    Button okButton;

    public static CreateContactDialogFragment newInstance(String title) {
        CreateContactDialogFragment frag = new CreateContactDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.new_contact, container, false);

        name = (EditText)view.findViewById(R.id.name);
        address = (EditText)view.findViewById(R.id.address);
        number = (EditText)view.findViewById(R.id.number);
        email = (EditText)view.findViewById(R.id.email);
        cancelButton = (Button) view.findViewById(R.id.cancelButton);
        okButton = (Button) view.findViewById(R.id.okButton);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contactName = name.getText().toString();
                String contactAddress = address.getText().toString();
                String contactNumber = number.getText().toString();
                String contactEmail = email.getText().toString();

                ((SimpleClientActivity)getActivity()).createContactRequest(contactName, contactAddress, contactNumber, contactEmail);
                dismiss();
            }
        });

        return view;
    }
}