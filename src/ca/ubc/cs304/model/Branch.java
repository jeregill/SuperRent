package ca.ubc.cs304.model;

/**
 * The intent for this class is to update/store information about a single branch
 */
public class Branch {
	private String city;
	private int num;

	public Branch(String city, int num) {
		this.city = city;
		this.num = num;
	}


	public String getCity() {
		return city;
	}

	public int getNum() { return num; }
}


