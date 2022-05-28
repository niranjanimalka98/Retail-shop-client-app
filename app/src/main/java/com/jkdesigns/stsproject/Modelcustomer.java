package com.jkdesigns.stsproject;

/**
 * Created by corei3 on 04-07-2017.
 */

public class Modelcustomer {

    private String p03;
    private String p12;
    private String p13;
    private String p14;
    private String p15;
    private String p22;

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    private String log;

    String key;

    public Modelcustomer() {

    }

    public Modelcustomer(String p03, String p12, String p13, String p14, String p15, String p22, String key) {
        this.p03 = p03;
        this.p12 = p12;
        this.p13 = p13;
        this.p14 = p14;
        this.p15 = p15;
        this.p22 = p22;
        this.key = key;
        this.log = "";
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getP12() {
        return p12;
    }

    public void setP12(String p12) {
        this.p12 = p12;
    }

    public String getP13() {
        return p13;
    }

    public void setP13(String p13) {
        this.p13 = p13;
    }

    public String getP14() {
        return p14;
    }

    public void setP14(String p14) {
        this.p14 = p14;
    }

    public String getP15() {
        return p15;
    }

    public void setP15(String p15) {
        this.p15 = p15;
    }



    public String getP03() {
        return p03;
    }

    public void setP03(String p03) {
        this.p03 = p03;
    }

    public String getP22() {
        return p22;
    }

    public void setP22(String p22) {
        this.p22 = p22;
    }

}
