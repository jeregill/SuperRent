package ca.ubc.cs304.model;

public class Customer {

    private String dlicense;
    private String fName;
    private String lName;
    private String phone_num;
    private String card_num;

    public Customer(String dlicense, String fName, String lName, String phone_num, String card_num) {
        this.dlicense = dlicense;
        this.fName = fName;
        this.lName = lName;
        this.phone_num = phone_num;
        this.card_num = card_num;
    }

    public String getDlicense() {
        return dlicense;
    }

    public void setDlicense(String dlicense) {
        this.dlicense = dlicense;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getPhone_num() {
        return phone_num;
    }

    public void setPhone_num(String phone_num) {
        this.phone_num = phone_num;
    }

    public String getCard_num() {
        return card_num;
    }

    public void setCard_num(String card_num) {
        this.card_num = card_num;
    }
}
