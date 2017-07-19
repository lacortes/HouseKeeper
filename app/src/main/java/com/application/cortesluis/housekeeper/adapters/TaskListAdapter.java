package com.application.cortesluis.housekeeper.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.application.cortesluis.housekeeper.R;
import com.application.cortesluis.housekeeper.data.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luis_cortes on 7/1/17.
 */

public class TaskListAdapter extends ArrayAdapter<Task> {

    private Context context;
    private List<Task> tasks;

    public TaskListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Task> objects) {
        super(context, resource, objects);
        this.context = context;
        this.tasks = objects;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.task_list_item, parent, false);

        TextView nameTextView = (TextView) view.findViewById(R.id.task_list_item_textView);
        nameTextView.setText(tasks.get(position).getMsg());

        return view;

    }
}
