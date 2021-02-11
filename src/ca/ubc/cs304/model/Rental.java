package ca.ubc.cs304.model;

public class Rental {

    private int rent_id;
    private int conf_num;
    private String v_license;
    private String from_date;
    private String to_date;

    public Rental(int rent_id, int conf_num, String v_license, String from_date, String to_date) {
        this.rent_id = rent_id;
        this.conf_num = conf_num;
        this.v_license = v_license;
        this.from_date = from_date;
        this.to_date = to_date;
    }

    public int getRent_id() {
        return rent_id;
    }

    public void setRent_id(int rent_id) {
        this.rent_id = rent_id;
    }

    public int getConf_num() {
        return conf_num;
    }

    public void setConf_num(int conf_num) {
        this.conf_num = conf_num;
    }

    public String getV_license() {
        return v_license;
    }

    public void setV_license(String v_license) {
        this.v_license = v_license;
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
}
