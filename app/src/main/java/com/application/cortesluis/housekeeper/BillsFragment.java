package com.application.cortesluis.housekeeper;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.application.cortesluis.housekeeper.adapters.BillMainListAdapter;
import com.application.cortesluis.housekeeper.data.Bill;
import com.application.cortesluis.housekeeper.data.BillStub;
import com.application.cortesluis.housekeeper.data.PaperDictionary;
import com.application.cortesluis.housekeeper.data.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.paperdb.Paper;

/**
 * Created by luis_cortes on 6/27/17.
 */

public class BillsFragment extends Fragment {
    private static final String FRAGMENT_TAG = "BillsFragment";

    private ListView listView;
    private BillMainListAdapter listViewAdapter;

    private TextView monthNameTextView;

    private Toolbar toolbar;
    private FirebaseDatabase firebaseDatabase;

    public static BillsFragment newInstance() {
        BillsFragment fragment = new BillsFragment();
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
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.i("test", "creating fragment three");
        View v = inflater.inflate(R.layout.bills_layout, container, false);

        // Add support toolbar
        toolbar = (Toolbar) v.findViewById(R.id.bills_toolbar);
        configToolBar();

        monthNameTextView = (TextView) v.findViewById(R.id.bills_month_name_textView);

        listView = (ListView) v.findViewById(R.id.bills_main_listView);

        Date currentDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        int currentMonth = cal.get(Calendar.MONTH);
        int currentYear = cal.get(Calendar.YEAR);

        // Set Name of Month for View
        monthNameTextView.setText(getMonthName(currentMonth));

        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase
                .getReference("bills/"+currentYear+"/"+currentMonth);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Bill> bills = new ArrayList<Bill>();

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Bill bill = data.getValue(Bill.class);
                    bills.add(bill);
                    Log.i(FRAGMENT_TAG, "Bil Name: "+bill.getName());
                }

                // Error caused by context issue
                try {
                    listViewAdapter = new BillMainListAdapter(
                            getContext(), R.layout.bills_main_list_item, bills);
                    listView.setAdapter(listViewAdapter);
                } catch (NullPointerException e) {
                    Log.i(FRAGMENT_TAG, "Caught the Error!");

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        loadBillStubs();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Bill bill = (Bill) parent.getAdapter().getItem(position);
                Log.i(FRAGMENT_TAG, bill.getName());

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Update "+bill.getName()+" paid status.")
                        .setPositiveButton("Toggle Pay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                updatePaidStatus(bill);
                                listViewAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

            }
        });

        return v;
    }


    private void updatePaidStatus(Bill bill) {
        List<BillStub> stubs = Paper.book()
                .read(PaperDictionary.STUB_LIST.name(), new ArrayList<BillStub>());


        Log.i(FRAGMENT_TAG, "Updating bill paid status!");
        int index = 0;
        for (int i = 0; i < stubs.size(); i++) {
            BillStub billStub = stubs.get(i);

            Log.i(FRAGMENT_TAG, "Check bill: "+billStub.getBillId()+" === "+bill.getBillId());

            // Compare bill stubs id with bill id to find a match
            if (billStub.getBillId().equals(bill.getBillId())) {
                // Update stubs list
                stubs.get(i).setPaid(!billStub.isPaid());
                Log.i(FRAGMENT_TAG, "Updating bill id: "+billStub.getBillId());
                index = i;
            }
        }

        // Update Fields
        DatabaseReference databaseReference =
                FirebaseDatabase.getInstance().getReference();
        String userId = Paper.book().read(PaperDictionary.MAIN_USER.name(), new User()).getUid();

        // Make a map of stubs
        Map<String, Object> stubsMap = new HashMap<>();

        BillStub stub = stubs.get(index);
        stubsMap.put("billId", stub.getBillId());
        stubsMap.put("paid", stub.isPaid());

        Map<String, Object> update = new HashMap<>();
        update.put("usersBills/"+userId+"/"+bill.getBillId()+"/", stubsMap);

        databaseReference.updateChildren(update);

        loadBillStubs();
    }

    // Get a list of bill stubs pertaining to the user
    private void loadBillStubs() {
        User mainUser = Paper.book().read(PaperDictionary.MAIN_USER.name(), new User());

        DatabaseReference databaseReference = FirebaseDatabase
                .getInstance().getReference("usersBills/"+mainUser.getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                List<BillStub> stubs = new ArrayList<BillStub>();

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    BillStub readIn = data.getValue(BillStub.class);

                    Log.i(FRAGMENT_TAG, "Reading in stub: "+readIn.getBillId());

                    stubs.add(readIn);

                }
                Paper.book().write(PaperDictionary.STUB_LIST.name(), stubs);
                List<BillStub> check = Paper.book().read(PaperDictionary.STUB_LIST.name(), new ArrayList<BillStub>());

                Log.i(FRAGMENT_TAG, "Stub Size: "+stubs.size());
                Log.i(FRAGMENT_TAG, "**********");
                for (int i = 0; i < check.size(); i++) {
                    Log.i(FRAGMENT_TAG, check.get(i).getBillId() + " Paid: "+check.get(i).isPaid());
                }
                Log.i(FRAGMENT_TAG, "**********");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



    private String getMonthName(int monthIndex) {
        String[] months = {"January", "February", "March", "April", "May", "June", "July",
            "August", "September", "October", "November", "December"};
        return months[monthIndex];
    }

    private void configToolBar() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);

        toolbar.setTitle("Bills");
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
                startActivity(AddNewBillActivity.newIntent(getActivity()));
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
