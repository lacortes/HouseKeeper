package com.application.cortesluis.housekeeper.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.application.cortesluis.housekeeper.R;
import com.application.cortesluis.housekeeper.data.Bill;
import com.application.cortesluis.housekeeper.data.BillStub;
import com.application.cortesluis.housekeeper.data.PaperDictionary;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.paperdb.Paper;

/**
 * Created by luis_cortes on 7/1/17.
 */

public class BillMainListAdapter extends ArrayAdapter<Bill> {
    private Context context;
    private List<Bill> bills;

    TextView billPaidStatus;

    public BillMainListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Bill> objects) {
        super(context, resource, objects);
        this.context = context;
        this.bills = objects;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.bills_main_list_item, parent, false);

        TextView dueDateTextView = (TextView) view.findViewById(R.id.bill_main_due_date_textView);
        dueDateTextView.setText("Due on "+getFormattedDate( bills.get(position).getDate() ));

        TextView billNameTextView = (TextView) view.findViewById(R.id.bill_main_name_textView);
        billNameTextView.setText(bills.get(position).getName());
//
        billPaidStatus = (TextView) view.findViewById(R.id.bill_main_paid_status_textView);
        isBillPaid(bills.get(position));

        TextView billTotal = (TextView) view.findViewById(R.id.bill_main_total_textView);
        billTotal.setText( "$"+formatTotal( bills.get(position).getAmount() ) );

        TextView billEach = (TextView) view.findViewById(R.id.bill_main_each_textView);
        billEach.setText("$"+splitBill(bills.get(position).getAmount()) );


        return view;

    }

    private String getFormattedDate(Date date) {
        String dateToString = DateFormat.getDateInstance().format(date);
        String[] tokens = dateToString.split(",");

        // Return in form of i.e. Jul 17
        return tokens[0];
    }

    private String formatTotal(double amount) {
        return String.format("%.2f",amount);
    }

    private String splitBill(double total) {
        int splitBy = Paper.book().read(PaperDictionary.USER_COUNT.name());
        double amount =  total / splitBy;
        return String.format("%.2f", amount);
    }

    private void isBillPaid(Bill bill) {
        List<BillStub> billStubs = Paper.book().
                read(PaperDictionary.STUB_LIST.name(), new ArrayList<BillStub>());
        Log.i("TEST", "Checking\n********");
        for (int i = 0; i < billStubs.size(); i++) {
            Log.i("TEST", billStubs.get(i).getBillId()+" === "+bill.getBillId());

            if (billStubs.get(i).getBillId().equals(bill.getBillId())) {
                Log.i("TEST", "Found a match");
                Log.i("TEST", "Paid: "+billStubs.get(i).isPaid()+"");

                if (billStubs.get(i).isPaid()) {
                    Log.i("TEST", "Set to paid");
                    billPaidStatus.setText(R.string.bill_paid);
                    billPaidStatus.setTextColor(Color.RED);
                }
            }
        }
        Log.i("TEST", "*****************");
    }


}
