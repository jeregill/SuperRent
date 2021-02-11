package ca.ubc.cs304.model;

public class Reservation {

    private int conf_num;
    private int branch_num;
    private String vtname;
    private String cust_license;
    private String from_date;
    private String to_date;

    public int getConf_num() {
        return conf_num;
    }

    public void setConf_num(int conf_num) {
        this.conf_num = conf_num;
    }

    public int getBranch_num() {
        return branch_num;
    }

    public void setBranch_num(int branch_num) {
        this.branch_num = branch_num;
    }

    public String getVtname() {
        return vtname;
    }

    public void setVtname(String vtname) {
        this.vtname = vtname;
    }

    public String getCust_license() {
        return cust_license;
    }

    public void setCust_license(String cust_license) {
        this.cust_license = cust_license;
    }

    public String getFrom_date() {
        return from_date;
    }

    public void setFrom_date(String from_date) {
        this.from_date = from_date;
    }

    public String getTo_date() {
        return to_date;
    }

    public void setTo_date(String to_date) {
        this.to_date = to_date;
    }

    public Reservation(int conf_num, int branch_num, String vtname, String cust_license, String from_date, String to_date) {
        this.conf_num = conf_num;
        this.branch_num = branch_num;
        this.vtname = vtname;
        this.cust_license = cust_license;
        this.from_date = from_date;
        this.to_date = to_date;
    }
}
