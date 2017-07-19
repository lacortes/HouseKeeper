package com.application.cortesluis.housekeeper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.BundleCompat;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by luis_cortes on 7/14/17.
 */

public class DatePickerFragment extends AppCompatDialogFragment {
    private static final String ARG_DATE = "date";

    private DatePicker datePicker;

    OnDateSelectedListener callBack;

    public static DatePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    // Help pass data with Activity
    public interface OnDateSelectedListener {
        public void onDateSelected(Date date);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            callBack = (OnDateSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Date date = (Date) getArguments().getSerializable(ARG_DATE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date, null);

        datePicker = (DatePicker) v.findViewById(R.id.dialog_date_picker);
        datePicker.init(year, month, day, null);

        return new android.support.v7.app.AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle("Date Picker")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int year = datePicker.getYear();
                        int month = datePicker.getMonth();
                        int day = datePicker.getDayOfMonth();
                        Date date = new GregorianCalendar(year, month, day).getTime();

                        callBack.onDateSelected(date);
                    }
                })
                .create();
    }
}