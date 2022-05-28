package com.jkdesigns.stsproject;

/**
 * Created by corei3 on 19-07-2017.
 */

public class ListModel {

    private String med;
    private String p03;
    private String pr;
    private String qty;
    private String amount;
    private String batch;
    private String expdate;
    private String key;
    private String orgqty;
    private String p06;
    private String p07;
    private String p11;
    private String p04;
    private String p18;
    private String p19;
    private String p20;
    private String p21;

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    private String unit;

    public ListModel(String med, String p3, String pr, String qty, String amount, String batch, String expdate, String key,
                     String orgqty, String unit, String p6, String p7, String p11, String p4, String p18, String p19, String p20, String p21) {

        this.med = med;
        this.p03 = p3;
        this.amount = amount;
        this.qty = qty;
        this.pr = pr;
        this.batch = batch;
        this.expdate = expdate;
        this.key = key;
        this.orgqty = orgqty;
        this.unit = unit;
        this.p06 = p6;
        this.p07 = p7;
        this.p11 = p11;
        this.p04 = p4;
        this.p18 = p18;
        this.p19 = p19;
        this.p20 = p20;
        this.p21 = p21;
    }

    public String getOrgqty() {
        return orgqty;
    }

    public void setOrgqty(String orgqty) {
        this.orgqty = orgqty;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getExpdate() {
        return expdate;
    }

    public void setExpdate(String expdate) {
        this.expdate = expdate;
    }

    public String getMed() {
        return med;
    }

    public void setMed(String med) {
        this.med = med;
    }

    public String getP03() {
        return p03;
    }

    public void setP03(String p3) {
        this.p03 = p3;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getPr() {
        return pr;
    }

    public void setPr(String pr) {
        this.pr = pr;
    }

    public String getP06() {
        return p06;
    }

    public void setP06(String p6) {
        this.p06 = p6;
    }

    public String getP07() {
        return p07;
    }

    public void setP07(String p7) {
        this.p07 = p7;
    }

    public String getP11() {
        return p11;
    }

    public void setP11(String p11) {
        this.p11 = p11;
    }

    public String getP04() {
        return p04;
    }

    public void setP04(String p4) {
        this.p04 = p4;
    }

    public String getP18() {
        return p18;
    }

    public void setP18(String p18) {
        this.p18 = p18;
    }

    public String getP19() {
        return p19;
    }

    public void setP19(String p19) {
        this.p19 = p19;
    }

    public String getP20() {
        return p20;
    }

    public void setP20(String p20) {
        this.p20 = p20;
    }

    public String getP21() {
        return p21;
    }

    public void setP21(String p21) {
        this.p21 = p21;
    }

}
