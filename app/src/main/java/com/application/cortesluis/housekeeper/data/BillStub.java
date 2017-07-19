package com.application.cortesluis.housekeeper.data;

/**
 * Created by luis_cortes on 7/15/17.
 */

public class BillStub {
    private String billId;
    private boolean paid;


    public BillStub() {

    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }
}
