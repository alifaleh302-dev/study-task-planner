package com.example.studyplanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

/**
 * Spinner adapter that shows the list of categories in the add and edit forms.
 * It is a plain {@link ArrayAdapter} so the category name is used both for the
 * collapsed spinner and for the drop-down rows.
 */
public class CategorySpinnerAdapter extends ArrayAdapter<Category> {

    private final List<Category> categories;

    public CategorySpinnerAdapter(Context context, List<Category> categories) {
        super(context, android.R.layout.simple_spinner_item, categories);
        this.categories = categories;
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return buildRow(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return buildRow(position, convertView, parent);
    }

    private View buildRow(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(getContext())
                    .inflate(android.R.layout.simple_spinner_item, parent, false);
        }
        TextView text = view.findViewById(android.R.id.text1);
        text.setText(categories.get(position).getName());
        return view;
    }
}
