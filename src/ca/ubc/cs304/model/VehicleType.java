package ca.ubc.cs304.model;

public class VehicleType {
    private String vtname;
    private int wrate;
    private int drate;
    private int hrate;
    private int krate;

    public VehicleType(String vtname, int wrate, int drate, int hrate, int krate) {
        this.vtname = vtname;
        this.wrate = wrate;
        this.drate = drate;
        this.hrate = hrate;
        this.krate = krate;
    }

    public String getVtname() {
        return vtname;
    }

    public void setRent_id(String vtname) {
        this.vtname = vtname;
    }

    public int getWrate() {
        return wrate;
    }

    public void setWrate(int wrate) {
        this.wrate = wrate;
    }

    public int getDrate() {
        return drate;
    }

    public void setDrate(int drate) {
        this.drate = drate;
    }

    public int getHrate() {
        return hrate;
    }

    public void setHrate(int hrate) {
        this.hrate = hrate;
    }

    public int getKrate() {
        return krate;
    }

    public void setKrate(int krate) {
        this.krate = krate;
    }
}
