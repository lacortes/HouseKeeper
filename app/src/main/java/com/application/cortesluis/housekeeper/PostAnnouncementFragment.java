package com.application.cortesluis.housekeeper;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.application.cortesluis.housekeeper.data.Announcement;
import com.application.cortesluis.housekeeper.data.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by luis_cortes on 7/5/17.
 */

public class PostAnnouncementFragment extends DialogFragment {
    private static final String ARG_USER_ID = "user_id";

    private EditText inputEditTextView;
    private Button submitButton;
    private Button cancelButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    private String userName;

    public PostAnnouncementFragment() {
        // Empty constructor
    }

    public static PostAnnouncementFragment newInstance() {
        PostAnnouncementFragment frag = new PostAnnouncementFragment();
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.create_announcement, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = firebaseAuth.getInstance();
        firebaseDatabase = firebaseDatabase.getInstance();

        setMainUser();

        inputEditTextView = (EditText) view.findViewById(R.id.input_EditText);
        inputEditTextView.requestFocus();
        inputEditTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    // Characters are available, excluding white space
                    submitButton.setEnabled(true);
                } else {
                    submitButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Nothing
            }
        });

        submitButton = (Button) view.findViewById(R.id.submit_post_Button);
        submitButton.setEnabled(false);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("TEST", "Post submitted");
                createNewAnnouncement(inputEditTextView.getText().toString().trim());
                dismiss();
            }
        });

        cancelButton = (Button) view.findViewById(R.id.cancel_post_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        getDialog().setTitle("New Announcement");
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    private void createNewAnnouncement(String msg) {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        Date currentDate = new Date();
        String dateToStr = DateFormat.getDateInstance().format(currentDate);

        Announcement announcement = new Announcement();
        announcement.setMsg(msg);
        announcement.setAuthorId(firebaseUser.getUid());
        announcement.setDate(dateToStr);
        announcement.setTimeStamp(currentDate.getTime());
        announcement.setAuthorName(userName);

        Log.i("TEST", "Name: "+userName);

        DatabaseReference databaseReference = firebaseDatabase.getReference();
        databaseReference.child("announcements/"+currentDate.getTime()).setValue(announcement);

//        Log.i("TAG", currentDate.toString());
//        Log.i("TAG", dateToStr);
//        Log.i("TAG", currentDate.getTime()+"");
    }

    private void setMainUser() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        Log.i("TEST", "ID: "+firebaseUser.getUid());
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().
                getReference("users/"+firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                userName = user.getName();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
