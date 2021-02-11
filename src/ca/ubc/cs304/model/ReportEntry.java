package ca.ubc.cs304.model;

public class ReportEntry {
    private String branch;
    private String vtName;
    private int count;
    private Double revenue;

    public ReportEntry(String branch, String vtName, int count, Double revenue) {
        this.branch = branch;
        this.vtName = vtName;
        this.count = count;
        this.revenue = revenue;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getVtName() {
        return vtName;
    }

    public void setVtName(String vtName) {
        this.vtName = vtName;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Double getRevenue() {
        return revenue;
    }

    public void setRevenue(Double revenue) {
        this.revenue = revenue;
    }
}
