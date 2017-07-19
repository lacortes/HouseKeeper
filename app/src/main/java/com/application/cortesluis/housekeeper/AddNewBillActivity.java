package com.application.cortesluis.housekeeper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.application.cortesluis.housekeeper.data.Bill;
import com.application.cortesluis.housekeeper.data.BillStub;
import com.application.cortesluis.housekeeper.data.PaperDictionary;
import com.application.cortesluis.housekeeper.data.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.paperdb.Paper;

/**
 * Created by luis_cortes on 7/14/17.
 */

public class AddNewBillActivity extends AppCompatActivity implements DatePickerFragment.OnDateSelectedListener {
    private static final String ACTIVITY_TAG = "AddNewBillActivity";
    private static final String DIALOG_DATE = "DialogDate";

    private EditText billNameEditText;
    private EditText billAmountEditText;
    private TextView billDateTextView;

    private Button pickDateButton;
    private Button cancelButton;
    private Button submitButton;

    private Toolbar toolbar;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseUser firebaseUser;

    private String billName;
    private String billAmount;
    private boolean defaultDateChanged;

    private Date billDueDate;
    private List<User> allUsers;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bills_add_new_bill_layout);

        // Add support toolbar
        toolbar = (Toolbar) findViewById(R.id.bills_toolbar);
        configToolBar();

        billNameEditText = (EditText) findViewById(R.id.bills_add_bill_name_editText);
        billAmountEditText = (EditText) findViewById(R.id.bills_add_bill_amount_editText);

        billDateTextView = (TextView) findViewById(R.id.bills_add_due_date_textView);

        pickDateButton = (Button) findViewById(R.id.bills_add_pick_date_button);
        pickDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getSupportFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(new Date());
                dialog.show(manager, DIALOG_DATE);
            }
        });

        cancelButton = (Button) findViewById(R.id.bills_add_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               closeThisActivity();
            }
        });

        submitButton = (Button) findViewById(R.id.bills_add_submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                billName = billNameEditText.getText().toString().trim();
                billAmount = billAmountEditText.getText().toString().trim();

                if (!TextUtils.isEmpty(billName) && !TextUtils.isEmpty(billAmount)
                        && defaultDateChanged) {
                    Log.i(ACTIVITY_TAG, "CREATE A NEW BILL");
                    createBill();
                    closeThisActivity();
                } else {
                    Toast.makeText(AddNewBillActivity.this, "All Fields Must Be Filled Out!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        setDefaultDate();

    }

    @Override
    public void onDateSelected(Date date) {
        billDueDate = date;
        String dateToStr = DateFormat.getDateInstance().format(date);
        billDateTextView.setText(dateToStr);
        defaultDateChanged = true;
        Log.i(ACTIVITY_TAG, date.toString());
    }

    private void setDefaultDate() {
        Date currentDate = new Date();
        String strFormat = DateFormat.getDateInstance().format(currentDate);

        billDateTextView.setText(strFormat);
        defaultDateChanged = false;
    }

    private void createBill() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        Bill bill = new Bill();
        bill.setName(billName);
        bill.setAmount(Double.parseDouble(billAmount));
        bill.setBalance(Double.parseDouble(billAmount));
        bill.setBillId(UUID.randomUUID()+"");
        bill.setDate(billDueDate);
        bill.setOwner(firebaseUser.getUid());
        bill.setTimeStamp(billDueDate.getTime()+"");

        String year = getFieldFromDate(billDueDate, DateOptions.YEAR);

        // Get int representation of month
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(billDueDate);
        int monthNumber = calendar.get(Calendar.MONTH);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("bills/"+year+"/"+monthNumber+"/"+billDueDate.getTime()).setValue(bill);

        // Create a bill stub for each user
       createStubsForEachUser(bill.getBillId());

        Log.i(ACTIVITY_TAG, "Done!");

    }

    private String getFieldFromDate(Date date, DateOptions option) {
        String dateToString = DateFormat.getDateInstance().format(date);
        String[] tokens = dateToString.split(" ");

        if (option == DateOptions.MONTH)
            return tokens[0];
        else if (option == DateOptions.DATE)
            return tokens[1].substring(0,2); // Get rid of trailing apostrophe: ex: 14,
        else
            return tokens[2];

    }

    private void closeThisActivity() {
        startActivity(FeedActivity.newIntent(AddNewBillActivity.this, R.layout.bills_layout));
        finish();
    }

    private void configToolBar() {
        toolbar.setTitle("New Bill");
        setSupportActionBar(toolbar);
    }

    private void createStubsForEachUser(final String billId) {
        // Read in all user ids
//        final List<User> users = new ArrayList<>();

        Log.i(ACTIVITY_TAG, "Creating stubs");

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("users");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<User> list = new ArrayList<User>();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    User user = data.getValue(User.class);
                    list.add(user);
                    Log.i(ACTIVITY_TAG, user.getName());
                }

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                for (int i = 0; i < list.size(); i++) {
                    Log.i(ACTIVITY_TAG, "id's"+list.get(i).getUid());

                    BillStub billStub = new BillStub();
                    billStub.setBillId(billId);
                    billStub.setPaid(false);

                    Log.i(ACTIVITY_TAG, "Make Users");
                    reference.child("usersBills/"
                            +list.get(i).getUid()+"/"+billId).setValue(billStub);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public static Intent newIntent(Context packageContext) {
        Intent i = new Intent(packageContext, AddNewBillActivity.class);
        return i;
    }


    private enum DateOptions {
        DATE, MONTH, YEAR
    }
}
