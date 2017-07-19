package com.application.cortesluis.housekeeper.data;

import android.content.Context;

import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by luis_cortes on 7/16/17.
 */

public class BillStubBank {
    private static BillStubBank billStubBank;

    private Map<String,BillStub> stubMap;
    private Context context;

    public static BillStubBank get(Context context) {
        if (billStubBank == null) {
            billStubBank = new BillStubBank(context);
        }
        return billStubBank;
    }

    private BillStubBank(Context context) {
        this.context = context.getApplicationContext();
        stubMap = new HashMap<>();
    }

    public void addBillStub(String key, BillStub billStub) {
        stubMap.put(key, billStub);
    }

    public Map<String,BillStub> getStubs() {
        return this.stubMap;
    }

}
