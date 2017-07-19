package com.application.cortesluis.housekeeper;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.application.cortesluis.housekeeper.data.MainUser;
import com.application.cortesluis.housekeeper.data.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {
    private final String ACTIVITY_TAG = "LoginActivity";

    private EditText emailLoginEditText;
    private TextView incorrectTextView;
    private EditText passwordEditText;
    private Button loginButton;
    private Button signupButton;
    private ProgressDialog progressDialog;

    private boolean emailTextAvailable = false;
    private boolean passwordTextAvailable = false;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        progressDialog = new ProgressDialog(LoginActivity.this);

        incorrectTextView = (TextView) findViewById(R.id.incorrect_textView);
        incorrectTextView.setVisibility(View.INVISIBLE);

        // Enable login button if there is text to show
        emailLoginEditText  = (EditText) findViewById(R.id.email_login_editText);
        emailLoginEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    loginButton.setEnabled(true);
                    emailTextAvailable = true;
                    incorrectTextView.setVisibility(View.INVISIBLE);
                } else {
                    loginButton.setEnabled(false);
                    emailTextAvailable = false;
                    Toast.makeText(LoginActivity.this, "Enter email !", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        passwordEditText = (EditText) findViewById(R.id.password_editText);
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    loginButton.setEnabled(true);
                    passwordTextAvailable = true;
                    incorrectTextView.setVisibility(View.INVISIBLE);
                } else {
                    loginButton.setEnabled(false);
                    passwordTextAvailable = false;
//                    Toast.makeText(LoginActivity.this, "Enter password!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setEnabled(false);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!emailTextAvailable || !passwordTextAvailable)
                    return;

                // Progress Dialog
                progressDialog.setTitle("Logging In");
                progressDialog.setMessage("You are being logged in!");
                progressDialog.show();
                progressDialog.setCanceledOnTouchOutside(false);


                final String email = emailLoginEditText.getText().toString();
                String pass = passwordEditText.getText().toString();
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signInWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = firebaseAuth.getCurrentUser();

                                    progressDialog.dismiss();

                                    Intent i = FeedActivity.newIntent(LoginActivity.this, R.layout.home_layout);
                                    startActivity(i);
                                    finish();
                                } else {
                                    progressDialog.hide();
                                    Log.e(ACTIVITY_TAG, "Failed to sign in.");
                                    incorrectTextView.setVisibility(View.VISIBLE);
//                                    Toast.makeText(LoginActivity.this, "Incorrect email/password. Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });

        signupButton = (Button) findViewById(R.id.sign_up_button);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(SignUpActivity.newIntent(LoginActivity.this));
            }
        });

    }

    public static Intent newIntent(Context packageContext) {
        Intent i = new Intent(packageContext, LoginActivity.class);
        return i;
    }

}
