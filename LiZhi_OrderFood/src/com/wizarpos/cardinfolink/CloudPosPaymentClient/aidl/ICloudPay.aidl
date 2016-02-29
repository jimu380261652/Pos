package com.wizarpos.cardinfolink.CloudPosPaymentClient.aidl;

interface ICloudPay{
    String payCash(String jsonData);
    String transact(String jsonData);
    String getPayInfo(String jsonData);
    String getPOSInfo(String jsonData);
    String getYunAccountServerInfo(String jsonData);
}