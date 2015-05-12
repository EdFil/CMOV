package pt.ulisboa.tecnico.cmov.airdesk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.R;
import pt.ulisboa.tecnico.cmov.airdesk.core.subscription.Subscription;

public class SubscriptionListAdapter extends ArrayAdapter<Subscription> {


    public SubscriptionListAdapter(Context context, List<?> subscriptions) {
        super(context, 0, (List<Subscription>) subscriptions);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Subscription subscription = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_subscription, parent, false);

        // Lookup view for data population
        TextView subscriptionTitle = (TextView) convertView.findViewById(R.id.itemName);

        // Populate the data into the template view using the data object
        subscriptionTitle.setText(subscription.getName());

        // Return the completed view to render on screen
        return convertView;
    }

}
