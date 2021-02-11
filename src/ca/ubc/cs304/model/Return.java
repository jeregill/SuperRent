package ca.ubc.cs304.model;

public class Return {

    private int rent_id;
    private int odometer;
    private int full_tank;
    private String to_date;
    private int price;

    public Return(int rent_id, int odometer, int full_tank, String to_date, int price) {
        this.rent_id = rent_id;
        this.odometer = odometer;
        this.full_tank = full_tank;
        this.price = price;
        this.to_date = to_date;
    }

    public int getRent_id() {
        return rent_id;
    }

    public void setRent_id(int rent_id) {
        this.rent_id = rent_id;
    }

    public int getOdometer() {
        return odometer;
    }

    public void setOdometer(int odometer) {
        this.odometer = odometer;
    }

    public int getFull_tank() {
        return full_tank;
    }

    public void setFull_tank(int full_tank) {
        this.full_tank = full_tank;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getTo_date() {
        return to_date;
    }

    public void setTo_date(String to_date) {
        this.to_date = to_date;
    }
}
