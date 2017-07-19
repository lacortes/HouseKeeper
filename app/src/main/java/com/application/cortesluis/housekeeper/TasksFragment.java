package com.application.cortesluis.housekeeper;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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

import com.application.cortesluis.housekeeper.adapters.TaskListAdapter;
import com.application.cortesluis.housekeeper.data.FireDate;
import com.application.cortesluis.housekeeper.data.GroupInfo;
import com.application.cortesluis.housekeeper.data.PaperDictionary;
import com.application.cortesluis.housekeeper.data.Task;
import com.application.cortesluis.housekeeper.data.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.paperdb.Paper;

/**
 * Created by luis_cortes on 6/27/17.
 */

public class TasksFragment extends Fragment {
    private final static String FRAGMENT_TAG = "TasksFragment";
    private final String NEW_ANNOUNCEMENT = "new_announcement";

    private ListView listView;
    private TaskListAdapter taskListAdapter;
    private FirebaseDatabase firebaseDatabase;
    private TextView monthTextview;
    private TextView datesTextView;

    private User mainUser;

    private Toolbar toolbar;

    public static TasksFragment newInstance() {
        TasksFragment fragment = new TasksFragment();
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("test", "creating fragment two");
        View v = inflater.inflate(R.layout.tasks_layout, container, false);

        toolbar = (Toolbar) v.findViewById(R.id.tasks_toolbar);
        configToolbar();

        datesTextView = (TextView) v.findViewById(R.id.tasks_dates_textView);
        setDateTextView();

        monthTextview = (TextView) v.findViewById(R.id.tasks_month_textView);
        Date currentDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        int currentMonth = cal.get(Calendar.MONTH);

        monthTextview.setText(getMonthName(currentMonth));

        setMainUser();
        readInFromDB();

        List<Task> tasks = Paper.book().read(PaperDictionary.TASK_LIST.name(), new ArrayList<Task>());
        taskListAdapter = new TaskListAdapter(this.getContext(), R.layout.task_list_item, tasks);

        listView = (ListView) v.findViewById(R.id.tasks_listView);
        listView.setAdapter(taskListAdapter);
        taskListAdapter.setNotifyOnChange(true);

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.bills_action_items, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bills_menu_item_new_bill:
                showDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void readInFromDB() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase
                .getReference("tasks/"+mainUser.getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Task> tasks = new ArrayList<Task>();

                Log.i(FRAGMENT_TAG, "Reading in from DB!");
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Task task = data.getValue(Task.class);
                    tasks.add(task);
                }

                Paper.book().write(PaperDictionary.TASK_LIST.name(), tasks);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void configToolbar() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        toolbar.setTitle("Tasks");
        activity.setSupportActionBar(toolbar);

    }

    private void showDialog() {
        FragmentManager manager = getFragmentManager();
        CreateTaskFragment dialog = CreateTaskFragment.newInstance();
        dialog.show(manager, NEW_ANNOUNCEMENT);
        Log.i(FRAGMENT_TAG, "I'm back");
    }

    private String getMonthName(int monthIndex) {
        String[] months = {"January", "February", "March", "April", "May", "June", "July",
                "August", "September", "October", "November", "December"};
        return months[monthIndex];
    }

    private void setDateTextView() {
        GroupInfo groupInfo = Paper.book().read(PaperDictionary.GROUP_TURN.name(), new GroupInfo());
        FireDate fireDate = groupInfo.getDate();
        DateTime dateTime = new DateTime(fireDate.getYear(),
                fireDate.getMonth(), fireDate.getDay(), fireDate.getHour(), fireDate.getMin());
        int initDay = dateTime.getDayOfMonth();
        int initMonth = dateTime.getMonthOfYear();

        dateTime = dateTime.plusDays(groupInfo.getDays());
        int lastDay = dateTime.getDayOfMonth();

        Log.i(FRAGMENT_TAG, "UPDATING DATE");
        datesTextView.setText(String.format("%d - %d", initDay, lastDay-1));

    }

    private void setMainUser() {
        mainUser = Paper.book().read(PaperDictionary.MAIN_USER.name(), new User());
    }

}
