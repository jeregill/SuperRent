package ca.ubc.cs304.model;

public class Vehicle {

    private String vlicense;
    private String make;
    private String model;
    private String color;
    private String year;
    private Integer odometer;
    private String vtName;
    private String status;
    private String city;

    public Vehicle(String vlicense, String make, String model, String color, String year, Integer odometer,
                   String vtName, String status, String city) {
        this.vlicense = vlicense;
        this.make = make;
        this.model = model;
        this.color = color;
        this.year = year;
        this.odometer = odometer;
        this.vtName = vtName;
        this.status = status;
        this.city = city;
    }

    public String getVlicense() {
        return vlicense;
    }

    public void setVlicense(String vlicense) {
        this.vlicense = vlicense;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public Integer getOdometer() {
        return odometer;
    }

    public void setOdometer(Integer odometer) {
        this.odometer = odometer;
    }

    public String getVtName() {
        return vtName;
    }

    public void setVtName(String vtName) {
        this.vtName = vtName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
