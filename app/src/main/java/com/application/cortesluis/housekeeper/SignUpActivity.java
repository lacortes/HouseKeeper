package com.application.cortesluis.housekeeper;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.application.cortesluis.housekeeper.data.FireDate;
import com.application.cortesluis.housekeeper.data.GroupInfo;
import com.application.cortesluis.housekeeper.data.PaperDictionary;
import com.application.cortesluis.housekeeper.data.User;
import com.application.cortesluis.housekeeper.data.UserType;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;

import io.paperdb.Paper;

/**
 * Created by luis_cortes on 7/5/17.
 */

public class SignUpActivity extends AppCompatActivity {
    private final String ACTIVITY_TAG = "SignUpActivity";
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    private EditText emailEditText;
    private EditText userFirstNameEditText;
    private EditText userLastNameEditText;
    private EditText passwordEditText;
    private CheckBox adminCheckBox;

    private ProgressDialog progressDialog;

    private Button submitButton;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_layout);

        firebaseAuth = firebaseAuth.getInstance();
        firebaseDatabase = firebaseDatabase.getInstance();

        progressDialog = new ProgressDialog(SignUpActivity.this);

        emailEditText = (EditText) findViewById(R.id.sign_up_email);

        userFirstNameEditText = (EditText) findViewById(R.id.sign_up_first_name);

        userLastNameEditText = (EditText) findViewById(R.id.sign_up_last_name);

        passwordEditText = (EditText) findViewById(R.id.sign_up_password);

        adminCheckBox = (CheckBox) findViewById(R.id.sign_up_admin_checkBox);

        submitButton = (Button) findViewById(R.id.sign_up_submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String firstName = userFirstNameEditText.getText().toString().trim();
                String lastName = userLastNameEditText.getText().toString().trim();


                if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)
                        || !TextUtils.isEmpty(firstName) || !TextUtils.isEmpty(lastName))
                    createUser();
            }
        });


       backButton = (Button) findViewById(R.id.sign_up_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = LoginActivity.newIntent(SignUpActivity.this);
                startActivity(i);
            }
        });
    }

    private void createUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String firstName = userFirstNameEditText.getText().toString().trim();
        String lastName = userLastNameEditText.getText().toString().trim();

        final String name = firstName+" "+lastName;

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                            Log.i(ACTIVITY_TAG, "New user Created");

                            User user = new User();
                            user.setName(name);
                            user.setEmail(emailEditText.getText().toString().trim());
                            user.setUid(currentUser.getUid());

                            Paper.book().write(PaperDictionary.MAIN_USER.name(), user);

                            if (adminCheckBox.isChecked()) {
                                Log.i(ACTIVITY_TAG, "Creating admin");
                                user.setType(UserType.ADMIN);
                                Paper.book().write(PaperDictionary.ADMIN_USER.name(), user.getName());

                                setCleanSchedule();

                            } else {
                                user.setType(UserType.MEMBER);
                            }

                            Log.i(ACTIVITY_TAG, "Creating member");

                            // Progress Dialog
                            progressDialog.setTitle("Logging In");
                            progressDialog.setMessage("You are being logged in!");
                            progressDialog.show();
                            progressDialog.setCanceledOnTouchOutside(false);


                            DatabaseReference reference = firebaseDatabase.getReference();
                            reference.child("users/" + currentUser.getUid()).setValue(user);

                            progressDialog.dismiss();

                            Intent intent = FeedActivity.newIntent(SignUpActivity.this, R.layout.home_layout);
                            startActivity(intent);
                        } else {
                            Log.i(ACTIVITY_TAG, "FAILED TO CREATE USER");
                        }
                    }
                });
    }

    private void setCleanSchedule() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Log.i(ACTIVITY_TAG, "SETTING FIRST GROUP");

        GroupInfo groupInfo = new GroupInfo();
        groupInfo.setGroup(1);
        groupInfo.setDays(7);

        FireDate fireDate = new FireDate();
        fireDate.setYear(2017);
        fireDate.setMonth(7);
        fireDate.setDay(16);
        fireDate.setHour(8);
        fireDate.setHour(0);

        groupInfo.setDate(fireDate);

        Paper.book().write(PaperDictionary.GROUP_TURN.name(), groupInfo);

        databaseReference.child("clean/1").setValue(groupInfo);

    }

    public static Intent newIntent(Context packageContext) {
        Intent i = new Intent(packageContext, SignUpActivity.class);
        return i;
    }

}
