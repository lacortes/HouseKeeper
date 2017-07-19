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
import com.application.cortesluis.housekeeper.data.MainUser;
import com.application.cortesluis.housekeeper.data.PaperDictionary;
import com.application.cortesluis.housekeeper.data.Task;
import com.application.cortesluis.housekeeper.data.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.paperdb.Paper;

/**
 * Created by luis_cortes on 7/18/17.
 */

public class CreateTaskFragment extends DialogFragment {
    private static final String FRAGMENT_TAG = "CreateTaskFragment";

    private EditText inputEditText;
    private Button cancelButton;
    private Button submitButton;

    public static CreateTaskFragment newInstance() {
        CreateTaskFragment frag = new CreateTaskFragment();
        return frag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.task_creation, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        inputEditText = (EditText) view.findViewById(R.id.task_creation_editText);
        inputEditText.requestFocus();
        inputEditText.addTextChangedListener(new TextWatcher() {
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

        submitButton = (Button) view.findViewById(R.id.task_creation_submit_Button);
        submitButton.setEnabled(false);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewTask(inputEditText.getText().toString().trim());
                dismiss();
            }
        });

        cancelButton = (Button) view.findViewById(R.id.task_creation_cancel_Button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        getDialog().setTitle("New Task");
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

    }

    private void createNewTask(String msg) {
        User mainUser = Paper.book().read(PaperDictionary.MAIN_USER.name(), new User());

        Date currentDate = new Date();

        Task task = new Task();
        task.setAuthorId(mainUser.getUid());
        task.setMsg(msg);
        task.setTimeStamp(currentDate.getTime());

        updateTaskList(task);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("tasks/"+mainUser.getUid()+"/"+ currentDate.getTime()).setValue(task);
    }

    private void updateTaskList(Task task) {
        Log.i(FRAGMENT_TAG, "updating list");
        List<Task> tasks = Paper.book().read(PaperDictionary.TASK_LIST.name(), new ArrayList<Task>());
        tasks.add(task);
        Paper.book().write(PaperDictionary.TASK_LIST.name(), tasks);
    }
}
