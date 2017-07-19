package com.application.cortesluis.housekeeper.adapters;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.application.cortesluis.housekeeper.R;
import com.application.cortesluis.housekeeper.data.Announcement;

import java.util.List;

/**
 * Created by luis_cortes on 7/2/17.
 */

public class AnnouncementListAdapter extends ArrayAdapter<Announcement> {

    private Context context;
    private List<Announcement> announcements;

    public AnnouncementListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Announcement> objects) {
        super(context, resource, objects);
        this.context = context;
        this.announcements = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.announce_item, parent, false);

        TextView nameTextView = (TextView) view.findViewById(R.id.announcement_name_textView);
        nameTextView.setText(announcements.get(position).getAuthorName());

        TextView dateTextView = (TextView) view.findViewById(R.id.announcement_date_textView);
        dateTextView.setText(announcements.get(position).getDate());

        TextView msgTextView = (TextView) view.findViewById(R.id.announcement_msg_textView);
        msgTextView.setText(announcements.get(position).getMsg());

        return view;
    }
}
