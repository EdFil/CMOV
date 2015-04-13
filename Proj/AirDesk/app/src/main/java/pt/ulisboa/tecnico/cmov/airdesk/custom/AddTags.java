package pt.ulisboa.tecnico.cmov.airdesk.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.airdesk.R;
import pt.ulisboa.tecnico.cmov.airdesk.core.tag.Tag;

public class AddTags extends LinearLayout {

    private PredicateLayout mTagsLayout;
    private EditText mTagsEditText;


    public AddTags(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(getContext(), R.layout.view_add_tags, this);

        mTagsEditText = (EditText) findViewById(R.id.newTag);
        mTagsEditText.setOnKeyListener(new TagsEditTextKeyListener());
        mTagsEditText.setOnFocusChangeListener(new TagsEditTextFocusChange());

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

    private final class TagsEditTextKeyListener implements OnKeyListener {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if(keyCode == KeyEvent.KEYCODE_SPACE) {
                addTag(mTagsEditText.getText().toString());
                mTagsEditText.getText().clear();
                return true;
            }
            if(keyCode == KeyEvent.KEYCODE_ENTER){
                addTag(mTagsEditText.getText().toString());
                mTagsEditText.getText().clear();
                return true;
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
