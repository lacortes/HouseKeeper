package com.application.cortesluis.housekeeper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.application.cortesluis.housekeeper.data.MainUser;
import com.application.cortesluis.housekeeper.data.PaperDictionary;
import com.application.cortesluis.housekeeper.data.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Period;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

/**
 * Created by luis_cortes on 6/27/17.
 */

public class FeedActivity extends AppCompatActivity {
    private static final String EXTRA_USER_ID
            = "com.application.cortesluis.housekeeper.user_id";
    private static final String EXTRA_FRAG_LAYOUT
            = "com.application.cortesluis.housekeeper.fragment_layout";
    private static final String ACTIVITY_TAG = "FeedActivity";

    private final String FEED_ACTIVITY = "FeedActivity";
    private BottomNavigationView bottomNavView;
    private String extraUserId;
    private int extraFragmentLayoutResourceId;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser == null) {
            startActivity(LoginActivity.newIntent(FeedActivity.this));
            finish();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        firebaseAuth = FirebaseAuth.getInstance();


        // Retrieve User Id from login activity
//        extraUserId = getIntent().getStringExtra(EXTRA_USER_ID);


        setMainUser();
        loadUsers();

        DateTime dateTime = new DateTime();
        Log.i(ACTIVITY_TAG, "DATE: "+dateTime.toString());
        dateTime =  dateTime.plusDays(6);
        Log.i(ACTIVITY_TAG, "DATE ADDED: "+dateTime.toString());

        DateTime dt = new DateTime(2005, 3, 26, 12, 0);
        Log.i(ACTIVITY_TAG, "Custom : "+dt.toString());
        DateTime plusPeriod = dt.plus(Period.days(1));
        DateTime plusDuration = dt.plus(new Duration(24L*60L*60L*1000L));
        Log.i(ACTIVITY_TAG, "Custom ADDED: "+dt.toString());

        // Retrieve Layout Resource Id, put home_layout as default
        extraFragmentLayoutResourceId =
                getIntent().getIntExtra(EXTRA_FRAG_LAYOUT, R.layout.home_layout);

        bottomNavView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;

                switch(item.getItemId()) {
                    case R.id.action_item1 :
                        fragment = HomeFragment.newInstance();
                        Log.i("test", "pressed one");
                        break;
                    case R.id.action_item2 :
                        fragment = TasksFragment.newInstance();
                        break;
                    case R.id.action_item3 :
                        fragment = BillsFragment.newInstance();
                        break;
                }
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.commit();
                return true;
            }
        });

        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        Fragment openingFragment = determineLayout(extraFragmentLayoutResourceId);

        transaction.replace(R.id.fragment_container, openingFragment);
        transaction.commit();
    }

    private Fragment determineLayout(int layoutId) {
        switch (layoutId) {
            case R.layout.home_layout:
                return HomeFragment.newInstance();
            case R.layout.tasks_layout:
                return TasksFragment.newInstance();
            case R.layout.bills_layout:
                return BillsFragment.newInstance();
            default:
                return HomeFragment.newInstance();
        }
    }

    private void setMainUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference  = null;
        try {
            databaseReference = FirebaseDatabase.getInstance().
                    getReference("users/" + user.getUid());
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
//                Log.i("TEST", "MAIN USER: "+user.getName());
                    if (user != null) {
                        Paper.book().write(PaperDictionary.MAIN_USER.name(), user);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    startActivity(LoginActivity.newIntent(FeedActivity.this));
                    finish();
                }
            });
        } catch (Exception e) {
            startActivity(LoginActivity.newIntent(FeedActivity.this));
            finish();
        }

    };

    private void loadUsers() {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().
                getReference("users/");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<User> users = new ArrayList<User>();
                int count = 0;
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    User user = data.getValue(User.class);
                    users.add(user);
                    count++;
                }
                Paper.book().write(PaperDictionary.ALL_USERS.name(), users);
                Paper.book().write(PaperDictionary.USER_COUNT.name(), count);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static Intent newIntent(Context packageContext, int layoutId) {
        Intent i = new Intent(packageContext, FeedActivity.class);
//        i.putExtra(EXTRA_USER_ID, uid);
        i.putExtra(EXTRA_FRAG_LAYOUT, layoutId);
        return i;
    }

}
