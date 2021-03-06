package pt.ulisboa.tecnico.cmov.airdesk.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.R;
import pt.ulisboa.tecnico.cmov.airdesk.core.subscription.Subscription;
import pt.ulisboa.tecnico.cmov.airdesk.custom.AddTagsLayout;
import pt.ulisboa.tecnico.cmov.airdesk.manager.UserManager;

public class EditSubscriptionFragment extends DialogFragment {

    OnEditSubscriptionFragmentListener mCallback;

    Button cancelButton, mountButton;
    AddTagsLayout mAddTagsLayout;
    EditText subscriptionName;

    Subscription mSubscription;

    // AirDeskActivity must implement this interface
    public interface OnEditSubscriptionFragmentListener {
        void updateSubscriptionList();
    }

    public static EditSubscriptionFragment newInstance() {
        return new EditSubscriptionFragment();
    }

    public void setSubscription(Subscription subscription) {
        mSubscription = subscription;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        View subscribeFragmentView = inflater.inflate(R.layout.fragment_mount_workspaces, container, false);

        // Get references to all the views
        subscriptionName = (EditText) subscribeFragmentView.findViewById(R.id.newSubscriptionName);
        subscriptionName.setText(mSubscription.getName());

        mAddTagsLayout = (AddTagsLayout) subscribeFragmentView.findViewById(R.id.sub_workspace_tags);
        mAddTagsLayout.setAllTags(mSubscription.getTags());

        mountButton = (Button) subscribeFragmentView.findViewById(R.id.ok_button);
        cancelButton = (Button)subscribeFragmentView.findViewById(R.id.cancel_button);


        // Setup the on click listeners
        mountButton.setOnClickListener(mMountButtonOnClickListener);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return subscribeFragmentView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnEditSubscriptionFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnHeadlineSelectedListener");
        }
    }

    // Searches all public workspaces with supplied tags
    private View.OnClickListener mMountButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            // Add the subscription to the subscription list of the user
            try {

                // Get and convert to String[] all tags, to send as arguments of service (String...)
                List<String> tagList = mAddTagsLayout.getAllTags();
                String[] tags = new String[tagList.size()];
                tagList.toArray(tags);

                // If no tags were added exit
                if(tags == null || tags.length == 0) {
                    Toast.makeText(getActivity(), "No tags were added.", Toast.LENGTH_SHORT).show();
                    return;
                }

                UserManager.getInstance().editSubscription(mSubscription, subscriptionName.getText().toString(), tags);

                // CALL back to refresh subscription list on the SubscriptionFragment through the AirDeskActivity
                mCallback.updateSubscriptionList();

                // Close dialog fragment
                dismiss();
            } catch (Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    };
}
