package pt.ulisboa.tecnico.cmov.airdesk.custom;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.R;
import pt.ulisboa.tecnico.cmov.airdesk.util.Constants;

/**
 * Created by edgar on 18-04-2015.
 */
public class QuotaValueLayout extends LinearLayout {

    EditText mQuotaEditText;
    Spinner mSuffixSpinner;

    public QuotaValueLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(getContext(), R.layout.view_quota_layout, this);

        mQuotaEditText =  (EditText) findViewById(R.id.editQuota);
        mSuffixSpinner = (Spinner) findViewById(R.id.mSpinner);

        List<String> sizeSuffix = new ArrayList<String>();
        sizeSuffix.add(Constants.BYTE);
        sizeSuffix.add(Constants.KILOBYTE);
        sizeSuffix.add(Constants.MEGABYTE);

        mSuffixSpinner.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, sizeSuffix));
        mSuffixSpinner.setOnItemSelectedListener(new SpinnerOnSelectedItemListener());

    }

    // -----------------
    // --- Functions ---
    // -----------------

    public long getQuota() {
        return convertFileSize(
                Long.parseLong(mQuotaEditText.getText().toString()),
                (String)mSuffixSpinner.getSelectedItem(),
                Constants.BYTE);
    }

    public long convertFileSize(long value, String previousSuffix, String currentSuffix) {
        if(!previousSuffix.equals(currentSuffix)){
            if(previousSuffix.equals(Constants.BYTE)){
                if(currentSuffix.equals(Constants.KILOBYTE))
                    return value / 1024l;
                if (currentSuffix.equals(Constants.MEGABYTE))
                    return value / 1024l /1024l;
            } else if (previousSuffix.equals(Constants.KILOBYTE)) {
                if(currentSuffix.equals(Constants.BYTE))
                    return value * 1024l;
                if (currentSuffix.equals(Constants.MEGABYTE))
                    return value / 1024l;
            } else {
                if(currentSuffix.equals(Constants.BYTE))
                    return value * 1024l * 1024l;
                if (currentSuffix.equals(Constants.KILOBYTE))
                    return value * 1024l;
            }
        }
        return value;
    }

    // -----------------
    // --- Listeners ---
    // -----------------

    private final class SpinnerOnSelectedItemListener implements AdapterView.OnItemSelectedListener {
        private String lastSelectedOption = Constants.BYTE;
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String currentSelectedOption = (String) parent.getItemAtPosition(position);
            // Only convert if it has a number in the EditText box
            if(mQuotaEditText.getText().length() != 0) {
                long quotaValue = Long.parseLong(mQuotaEditText.getText().toString());
                mQuotaEditText.setText(String.valueOf(convertFileSize(quotaValue, lastSelectedOption, currentSelectedOption)));
            }
            lastSelectedOption = currentSelectedOption;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) { /* Empty */ }
    }

}
