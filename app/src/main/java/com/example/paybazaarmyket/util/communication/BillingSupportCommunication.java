package com.example.paybazaarmyket.util.communication;


import com.example.paybazaarmyket.util.IabResult;

public interface BillingSupportCommunication {
    void onBillingSupportResult(int response);
    void remoteExceptionHappened(IabResult result);
}
