package pt.ulisboa.tecnico.cmov.airdesk.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.R;
import pt.ulisboa.tecnico.cmov.airdesk.core.tag.Tag;
import pt.ulisboa.tecnico.cmov.airdesk.util.Constants;

public class AddTags extends LinearLayout {

    private PredicateLayout mTagsLayout;
    private EditText mTagsEditText;


    public AddTags(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(getContext(), R.layout.view_add_tags, this);

        mTagsEditText = (EditText) findViewById(R.id.newTag);
        mTagsEditText.addTextChangedListener(new TagsEditTextWatcher());
        mTagsEditText.setOnKeyListener(new TagsEditTextKeyListener());
        mTagsEditText.setOnFocusChangeListener(new TagsEditTextFocusChange());
        mTagsEditText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        mTagsLayout = (PredicateLayout) findViewById(R.id.tagList);

        TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.AddTags,
                0, 0
        );
        TextView title = (TextView) findViewById(R.id.tags);
        if(a.getBoolean(R.styleable.AddTags_titleVisible, false)) {
            title.setText(a.getString(R.styleable.AddTags_titleText));
        } else {
            ((LinearLayout)getChildAt(0)).removeView(title);
        }
    }


    // -----------------
    // --- Functions ---
    // -----------------

    public void addTag(String tag) {
        if(mTagsLayout.getChildCount() == Constants.MAX_TAGS_PER_WORKSPACE) {
            Toast.makeText(getContext(), "Max number of tags reached", Toast.LENGTH_SHORT).show();
            return;
        }

        // If tag already exists we do not add it
        for(int i = 0; i < mTagsLayout.getChildCount(); i++)
            if((((TextView)((LinearLayout)mTagsLayout.getChildAt(i)).getChildAt(0)).getText().toString()).equals(tag)) {
                return;
            }
        // Add tag to PredicateLayout
        if (tag.length() > 0) {
            View view = inflate(getContext(), R.layout.tag_list_adapter, null);
            mTagsLayout.addView(view);
            ((TextView)view.findViewById(R.id.tagName)).setText(tag);
            // Set the on click listener to remove itself from the PredicateLayout
            view.findViewById(R.id.removeTagButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTagsLayout.removeView((LinearLayout)v.getParent());
                }
            });
        }
    }

    // Gets all tags fro th PredicateLayout
    public List<Tag> getAllTags() {
        List<Tag> tags = new ArrayList<>();
        for(int i = 0; i < mTagsLayout.getChildCount(); i++)
            tags.add(new Tag(((TextView)((LinearLayout)mTagsLayout.getChildAt(i)).getChildAt(0)).getText().toString()));
        return tags;
    }

    // -----------------
    // --- Listeners ---
    // -----------------

    private final class TagsEditTextWatcher implements TextWatcher {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        public void onTextChanged(CharSequence s, int start, int before, int count) {}
        @Override
        public void afterTextChanged(Editable s) {
            if(s.length() > 0) {
                if(s.charAt(s.length() - 1) == ' ') {
                    addTag(s.toString().trim());
                    s.clear();
                }
            }
        }
    }

    private final class TagsEditTextKeyListener implements OnKeyListener {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if(event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    addTag(mTagsEditText.getText().toString());
                    mTagsEditText.getText().clear();
                    return true;
                }
            }
            return false;
        }
    }

    private final class TagsEditTextFocusChange implements OnFocusChangeListener {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(!hasFocus) {
                addTag(mTagsEditText.getText().toString());
                mTagsEditText.getText().clear();
            }
        }
    }
}
