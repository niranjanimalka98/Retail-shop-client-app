package com.jkdesigns.stsproject;

/**
 * Created by corei3 on 04-07-2017.
 */

public class Modelexpense {

    private String p03;
    private String p18;
    private String b07;

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    private String log;

    String key;

    public Modelexpense() {

    }

    public Modelexpense(String p3, /*String p12,*/ String b7, String p18, String key) {
        this.p03 = p3;
        //this.p12 = p12;
        this.p18 = p18;
        this.b07 = b7;
        //this.p15 = p15;
        this.key = key;
        this.log = "";
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    /*public String getP12() {
        return p12;
    }

    public void setP12(String p12) {
        this.p12 = p12;
    }*/

    public String getP18() {
        return p18;
    }

    public void setP18(String p18) {
        this.p18 = p18;
    }

    public String getB07() {
        return b07;
    }

    public void setB07(String b7) {
        this.b07 = b7;
    }

    public String getP15() {
        return p15;
    }

    public void setP15(String p15) {
        this.p15 = p15;
    }

    private String p15;

    public String getP03() {
        return p03;
    }

    public void setP03(String p3) {
        this.p03 = p3;
    }

}
