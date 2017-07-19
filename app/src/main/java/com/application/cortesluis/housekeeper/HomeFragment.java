package com.application.cortesluis.housekeeper;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.application.cortesluis.housekeeper.adapters.AnnouncementListAdapter;
import com.application.cortesluis.housekeeper.data.Announcement;
import com.application.cortesluis.housekeeper.data.AnnouncementBank;
import com.application.cortesluis.housekeeper.data.FireDate;
import com.application.cortesluis.housekeeper.data.GroupInfo;
import com.application.cortesluis.housekeeper.data.MainUser;
import com.application.cortesluis.housekeeper.data.PaperDictionary;
import com.application.cortesluis.housekeeper.data.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

/**
 * Created by luis_cortes on 6/27/17.
 */

public class HomeFragment extends Fragment {
    private static final String ARG_USER_ID = "user_id";
    private final String FRAGMENT_TAG = "HomeFragment";
    private final String NEW_ANNOUNCEMENT = "new_announcement";

    private ListView listView;
    private AnnouncementBank announcements;
    private AnnouncementListAdapter announcementListAdapter;
    private Toolbar toolbar;
    private TextView noAnnounceTextView;

    private FirebaseDatabase firebaseDatabase;
    private String userId;
    private MainUser mainUser;

    public static HomeFragment newInstance() {
//        Bundle args = new Bundle();
//        args.putString(ARG_USER_ID, uid);

        HomeFragment fragment = new HomeFragment();
//        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
//        userId = getArguments().getString(ARG_USER_ID);
        setHasOptionsMenu(true); // Allows option menu for toolbar to be created

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("test", "creating fragment one");
        View v = inflater.inflate(R.layout.home_layout, container, false);

        toolbar = (Toolbar) v.findViewById(R.id.home_toolbar);
        configToolbar();

        listView = (ListView) v.findViewById(R.id.announce_listView);

        noAnnounceTextView = (TextView) v.findViewById(R.id.home_no_announce_textView);


        User user = Paper.book().read(PaperDictionary.MAIN_USER.name(), new User());
        Log.i(FRAGMENT_TAG, "Paper: "+user.getName());

        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("announcements");
        if (databaseReference != null) { // reference will be null if first time creating announcements

            Log.i(FRAGMENT_TAG, "database reference not null");

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<Announcement> announcementList = new ArrayList<Announcement>();

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Announcement announcement = ds.getValue(Announcement.class);
                        announcementList.add(announcement);
                    }

                    try {
                        announcementListAdapter = new AnnouncementListAdapter(getActivity(),
                                R.layout.announce_item, announcementList);
                        listView.setAdapter(announcementListAdapter);
//                    Log.i(FRAGMENT_TAG, listView.getAdapter().getCount()+"");
                    } catch (Exception e) {
                        Log.e(FRAGMENT_TAG, "ERROR!!");
                    }
                    if (listView.getAdapter().getCount() > 0)
                        updateView();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        updateTurnToClean();

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.home_action_items, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_announcement:
                showDialog();
                return true;

            case R.id.menu_item_logout:
                // Sign out user
                FirebaseAuth.getInstance().signOut();
                startActivity(LoginActivity.newIntent(getActivity()));
                return true;

            case R.id.menu_item_about:
                showUserInfoDialog();
                return true;

            case R.id.menu_item_create_groups:
                showGroupsInfoDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void configToolbar() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);

        toolbar.setTitle("Home");
    }

    private void updateTurnToClean() {
        DateTime currentDate = new DateTime();

        GroupInfo groupInfo = Paper.book().read(PaperDictionary.GROUP_TURN.name(), new GroupInfo());
        FireDate fireDate = groupInfo.getDate();
        DateTime readDate = new DateTime(fireDate.getYear(), fireDate.getMonth(),
                fireDate.getDay(), fireDate.getHour(), fireDate.getMin());
        readDate = readDate.plusDays(groupInfo.getDays());

        if (readDate.isAfter( currentDate.toInstant() )) {
            Log.i(FRAGMENT_TAG, "READ DATE IS AFTER");


        }

    }

    private void showDialog() {
        FragmentManager manager = getFragmentManager();
        PostAnnouncementFragment dialog = PostAnnouncementFragment.newInstance();
        dialog.show(manager, NEW_ANNOUNCEMENT);
    }


    private void showUserInfoDialog() {
        User user = Paper.book().read(PaperDictionary.MAIN_USER.name(), new User());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("User Info")
                .setMessage("Name: "+user.getName()+"\n"+"Email: "+user.getEmail())
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }


    private void showGroupsInfoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Groups")
                .setMessage("Group 1: Luis and Karly"
                        +"\nGroup 2: Brenda and Danny"
                        +"\nGroup 3: Geo and Xavier")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void updateView() {
        noAnnounceTextView.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
    }

}
