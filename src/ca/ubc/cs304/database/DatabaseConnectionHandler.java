package ca.ubc.cs304.database;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ca.ubc.cs304.model.*;


/**
 * This class handles all database related transactions
 */
public class DatabaseConnectionHandler {
	private static final String ORACLE_URL = "jdbc:oracle:thin:@localhost:1522:stu";
	private static final String EXCEPTION_TAG = "[EXCEPTION]";
	private static final String WARNING_TAG = "[WARNING]";

	private Connection connection = null;

	// THESE FUNCTIONS ARE TO SET UP THE CONNECTION
	
	public DatabaseConnectionHandler() {
		try {
			// Load the Oracle JDBC driver
			// Note that the path could change for new drivers
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		}
	}


	// Login screen (given to us)
	public boolean login(String username, String password) {
		try {
			if (connection != null) {
				connection.close();
			}

			connection = DriverManager.getConnection(ORACLE_URL, username, password);
			connection.setAutoCommit(false);
			System.out.println("\nConnected to Oracle!");
			return true;
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			return false;
		}
	}

	// Rollback connection (given to me)
	private void rollbackConnection() {
		try  {
			connection.rollback();
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		}
	}

	// Close the connection
	public void close() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		}
	}


	// QUERIES FOR THE PROJECT

	// Query to View Vehicles
	public List queryViewVehicles(String vtType, String city, String to, String from) {
		ArrayList<Vehicle> result = new ArrayList<>();


		// Dynamically create SQL query; if null, simple leave as 1=1
		String locClause = (city != null) ? "B.city =" + singleQuote(city): "1 = 1";
		String typeClause = (vtType != null) ? "V.vtName =" + singleQuote(vtType): "1 = 1";
		String fromDateClause = (from != null) ? "R.from_Date > " + toDate(from): "1 = 1";
		String toDateClause = (to != null) ? "R.to_date < " + toDate(to): "1 = 1";

		String selectClause = "SELECT V.vlicense, V.make, V.model, V.color, V.year, V.odometer,V.vtname, V.status, V.branch_num, B.city ";
		String fromClause = "FROM Vehicles V, Rentals R, Branch B ";
		String whereClause = "WHERE V.status = 'RENTED' AND V.vlicense = R.v_license AND B.branch_num = V.branch_num AND "
				+ locClause + " AND " + typeClause + " AND( " + fromDateClause + " OR " + toDateClause + ")";
		try {
			String query1 = selectClause + fromClause + whereClause;
			String query2 = selectClause + "FROM Vehicles V, Branch B WHERE V.status = 'AVAILABLE' AND V.branch_num = B.branch_num AND "
					+ typeClause + " AND " + locClause;
			String query = query1 + " UNION " + query2;
			PreparedStatement stmt = connection.prepareStatement(query);
			ResultSet rs = stmt.executeQuery();

			while(rs.next()){
				// create the vehicle
				Vehicle vehicle = new Vehicle(rs.getString("vlicense"),
						rs.getString("make"),
						rs.getString("model"),
						rs.getString("color"),
						rs.getString("year"),
						rs.getInt("odometer"),
						rs.getString("vtName"),
						rs.getString("status"),
						rs.getString("city"));
				// add the vehicle
				result.add(vehicle);

			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		}

		return result;
	}


	// Helper to put single quotes for parameter queries
	public String singleQuote(String str) {
		return (str != null ? "'" + str + "'" : null);
	}

	// Helper to put single quotes for parameter queries
	public String toDate(String str) {
		return "TO_DATE(" +  singleQuote(str) + ",'DD-MON-YYYY')";
	}

	// return the list of existing customer (licenses)
	public ArrayList<String> queryCustomers(){
		ArrayList<String> result = new ArrayList<String>();

		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT LICENSE_NO FROM CUSTOMER");

			while(rs.next()) {
				result.add(rs.getString("license_no"));
			}

			rs.close();
			stmt.close();
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		}

		return result;
	}

	// return a map of branch num and city
	public HashMap<String,Integer> branchInfo(){
		HashMap<String,Integer> result = new HashMap<>();

		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Branch");

			while(rs.next()) {
				result.put(rs.getString("city"),rs.getInt("branch_num"));
			}

			rs.close();
			stmt.close();
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		}

		return result;
	}

	// Utility function to get Customer Info of a particular customer
	public List<Customer> getCustomerInfo(String license) {

		ArrayList result = new ArrayList();

		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Customer WHERE LICENSE_NO = " + singleQuote(license));

			while(rs.next()) {
				result.add(new Customer(rs.getString("license_no"),rs.getString("first_name"),
						rs.getString("last_name"), rs.getString("phone_num"), rs.getString("card_num")));
			}

			rs.close();
			stmt.close();

		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		}

		// should only be of size one
		return result;
	}


	// Insert a reservation into the Reservations table
	public boolean insertReservation(Reservation r) {
		try {
			String insert = "INSERT INTO RESERVATIONS VALUES(" + r.getConf_num() + "," + r.getBranch_num() + "," + singleQuote(r.getVtname()) + "," + singleQuote(r.getCust_license()) +
					"," + toDate(r.getFrom_date()) + "," + toDate(r.getTo_date()) + ")";
			PreparedStatement ps = connection.prepareStatement(insert);

			ps.executeUpdate();
			connection.commit();

			ps.close();
			return true;
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
			return false;
		}
	}

	// Function needed to create new reservation confirmation numbers
	public int maxResNum() {
		ArrayList<Integer> result = new ArrayList();

		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT MAX(CONF_NUM) AS num FROM RESERVATIONS");

			while(rs.next()) {
				result.add(rs.getInt("num"));
			}

			rs.close();
			stmt.close();

		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		}

		// should only be of size one
		return result.get(0);
	}

	// Function needed to create new reservation confirmation numbers
	public int maxRentID() {
		ArrayList<Integer> result = new ArrayList();

		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT MAX(RENT_ID) AS num FROM RENTALS");

			while(rs.next()) {
				result.add(rs.getInt("num"));
			}

			rs.close();
			stmt.close();

		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		}

		// should only be of size one
		return result.get(0);
	}


	// Query to insert customer into the database
	public boolean insertCustomer(Customer c) {
		try {
			String insert = "INSERT INTO CUSTOMER VALUES(" + singleQuote(c.getDlicense()) + "," + singleQuote(c.getfName()) + "," + singleQuote(c.getlName()) + "," + singleQuote(c.getPhone_num()) +
					"," + singleQuote(c.getCard_num()) + ")";
			PreparedStatement ps = connection.prepareStatement(insert);

			ps.executeUpdate();
			connection.commit();

			ps.close();
			return true;
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
			return false;
		}
	}

	// Utility function to get Customer Info of a particular customer
	public ArrayList<Reservation> findReservation(Integer confNum) {

		ArrayList result = new ArrayList();

		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM RESERVATIONS WHERE CONF_NUM =" + confNum);

			while(rs.next()) {
				result.add(new Reservation(rs.getInt("conf_num"),rs.getInt("branch_num"),
						rs.getString("vtname"), rs.getString("cust_license_no"), rs.getString("from_date"),
						rs.getString("to_date")));
			}

			rs.close();
			stmt.close();

		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		}

		// should only be of size one
		return result;
	}

	// Query to insert customer into the database
	public Rental insertRental(Vehicle v, Reservation r) {
		try {
			String insert = "INSERT INTO RENTALS VALUES(" + maxRentID() + 1 + "," + r.getBranch_num() + "," + singleQuote(v.getVlicense()) + "," + toDate(r.getFrom_date()) + "," + toDate(r.getTo_date()) + ")";
			PreparedStatement ps = connection.prepareStatement(insert);

			ps.executeUpdate();
			connection.commit();

			ps.close();
			return new Rental(maxRentID() + 1,r.getBranch_num(),v.getVlicense(),r.getFrom_date(),r.getTo_date());
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
			// Should not get here
			return new Rental(maxRentID() + 1,r.getBranch_num(),v.getVlicense(),r.getFrom_date(),r.getTo_date());
		}
	}
	 // Update the status of a vehicle after a rental
	public boolean updateVehicle(String v, String status) {
		try {
			String query = "UPDATE VEHICLES SET STATUS = " + singleQuote(status) + " WHERE VLICENSE = " + singleQuote(v);
		  	PreparedStatement ps = connection.prepareStatement(query);

		  int rowCount = ps.executeUpdate();
		  connection.commit();
		  if (rowCount == 0) {
		      System.out.println(WARNING_TAG + " Vehicle " + v + " does not exist!");
		      return false;
		  }
		  ps.close();
		  return true;

		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
			return false;
		}
	}

	// Utility function to get Customer Info of a particular customer
	public List<Rental> getRentals() {

		ArrayList result = new ArrayList();

		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM RENTALS" );
			while(rs.next()) {
				result.add(new Rental(rs.getInt("rent_id"),rs.getInt("conf_num"),
						rs.getString("v_license"),rs.getString("from_date"),
						rs.getString("to_date")));
			}

			rs.close();
			stmt.close();

		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		}

		// should only be of size one
		return result;
	}

	// Utility function to get all Returns
	public List<Return> getReturns() {
		ArrayList result = new ArrayList();

		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM RETURNS" );
			while(rs.next()) {
				result.add(new Return(rs.getInt("rent_id"),rs.getInt("odometer"),
						rs.getInt("full_tank"),rs.getString("return_date"),
						rs.getInt("value")));
			}

			rs.close();
			stmt.close();

		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		}

		// should only be of size one
		return result;
	}

	// return the vehicle with specified license plate
	public Vehicle queryLicensePlate(String license){
		Vehicle vehicle = null;

		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Vehicles WHERE vlicense = " + singleQuote(license));
			rs.next();
			vehicle = new Vehicle(rs.getString("vlicense"), rs.getString("make"), rs.getString("model"),
					rs.getString("color"), rs.getString("year"), rs.getInt("odometer"), rs.getString("vtname"),
					rs.getString("status"), rs.getString("branch_num"));

			rs.close();
			stmt.close();
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		}

		return vehicle;
	}

	// Update the status of the vehicle's odometer
	public boolean updateOdometer(String v, String odometer) {
		try {
			String query = "UPDATE VEHICLES SET ODOMETER = " + odometer + " WHERE VLICENSE = " + singleQuote(v);
			PreparedStatement ps = connection.prepareStatement(query);

			int rowCount = ps.executeUpdate();
			connection.commit();
			if (rowCount == 0) {
				System.out.println(WARNING_TAG + " Vehicle " + v + " does not exist!");
				return false;
			}
			ps.close();
			return true;

		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
			return false;
		}
	}

	// return the rates for Vehicle Type
	public VehicleType queryRates(String type){
		VehicleType vrates = null;
		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM VehicleType WHERE vtname = " + singleQuote(type));
			rs.next();
			vrates = new VehicleType(rs.getString("vtname"), rs.getInt("wrate"), rs.getInt("drate"),
					rs.getInt("hrate"), rs.getInt("krate"));

			rs.close();
			stmt.close();
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		}

		return vrates;
	}

	// Query to insert Return into the database
	public boolean insertReturn(Return r) {
		try {
			String insert = "INSERT INTO RETURNS VALUES(" + r.getRent_id() + "," + singleQuote(r.getTo_date()) + "," +
					"" + r.getOdometer() + "," + r.getFull_tank() +
					"," + r.getPrice() + ")";
			PreparedStatement ps = connection.prepareStatement(insert);

			ps.executeUpdate();
			connection.commit();

			ps.close();
			return true;
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
			return false;
		}
	}

	// Query to View Vehicles
	public List<List<ReportEntry>> queryGenerateReport(boolean isReturn, String city, String date) {
		List<List<ReportEntry>> result = new ArrayList<>();
		ArrayList<ReportEntry> result1 = new ArrayList<>();
		ArrayList<ReportEntry> result2 = new ArrayList<>();
		int totalCount = 0;
		double totalRevenue = 0;
		HashMap<String, Integer> countaggregate = new HashMap<String, Integer>();

		HashMap<String, Double> revenueaggregate = new HashMap<String, Double>();
		// Dynamically create SQL query; if null, simple leave as 1=1
		String locClause = (!city.equals("All")) ? "B.city =" + singleQuote(city): "1 = 1";
		String table = (isReturn) ? ", Returns R" : ", Rentals R";
		String fromDateClause = (date != null) ? " R.from_Date = " + toDate(date): " 1 = 1 ";
		String toDateClause = (date != null) ? "R.return_date = " + toDate(date): "1 = 1";


		String selectClause = "SELECT V.vtname, B.city, COUNT(rent_id) as count ";


		String fromClause = "FROM Rentals R LEFT JOIN Vehicles V ON (R.v_license = V.vlicense) LEFT JOIN Reservations R1 ON (R.conf_num = R1.conf_num) LEFT JOIN Branch B ON (R1.branch_num = B.branch_num) ";
		String whereClause = "WHERE "+ fromDateClause + " AND " + locClause+ " ";
		String groupClause = "GROUP BY V.vtname, B.city ORDER BY B.city";

		String rentalQuery = selectClause + fromClause + whereClause + groupClause;



		String selectClause2 = "SELECT R1.vtname, B.city, COUNT(*) as count, SUM(R.value) as sum ";


		String fromClause2 = "FROM Returns R LEFT JOIN Rentals R2 ON (R.rent_id = R2.rent_id) LEFT JOIN Reservations R1 ON (R2.conf_num = R1.conf_num) LEFT JOIN Branch B ON (R1.branch_num = B.branch_num) ";
		String whereClause2 = "WHERE "+ toDateClause + " AND " + locClause+ " ";
		String groupClause2 = "GROUP BY R1.vtname, B.city ORDER BY B.city";

		String returnQuery = selectClause2 + fromClause2 + whereClause2 + groupClause2;

		try {
			String query = isReturn ? returnQuery : rentalQuery;

			PreparedStatement stmt = connection.prepareStatement(query);
			ResultSet rs = stmt.executeQuery();


			while(rs.next()){
				// create the vehicle
				String entryCity = rs.getString("city");
				ReportEntry entry= new ReportEntry(entryCity,
						rs.getString("vtName"),
						rs.getInt("count"),
						(double) 0);

				if (isReturn) {
					entry.setRevenue(rs.getDouble("sum"));
					if (revenueaggregate.containsKey(entryCity)) {
						revenueaggregate.put(entryCity, revenueaggregate.get(entryCity) + rs.getDouble("sum"));
					} else {
						revenueaggregate.put(entryCity, rs.getDouble("sum"));
					};
					totalRevenue+=rs.getDouble("sum");
				}
				result1.add(entry);
				if (countaggregate.containsKey(entryCity)) {
					countaggregate.put(entryCity, countaggregate.get(entryCity) + rs.getInt("count"));
				} else {
					countaggregate.put(entryCity, rs.getInt("count"));
				};
				totalCount+=rs.getInt("count");
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		}


		for (HashMap.Entry<String,Integer> entry : countaggregate.entrySet()) {
			Double revenue = isReturn ? revenueaggregate.get(entry.getKey()): (double) 0;
			result2.add(new ReportEntry(entry.getKey(), "", entry.getValue(), revenue));
		}

		if (("All").equals(city)) {
			result2.add(new ReportEntry("All", "", totalCount, totalRevenue));
		}

		result.add(result1);
		result.add(result2);
		return result;
	}

//	public void deleteBranch(int branchId) {
//		try {
//			PreparedStatement ps = connection.prepareStatement("DELETE FROM branch WHERE branch_id = ?");
//			ps.setInt(1, branchId);
//
//			int rowCount = ps.executeUpdate();
//			if (rowCount == 0) {
//				System.out.println(WARNING_TAG + " Branch " + branchId + " does not exist!");
//			}
//
//			connection.commit();
//
//			ps.close();
//		} catch (SQLException e) {
//			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
//			rollbackConnection();
//		}
//	}
	
//	public void insertBranch(Branch model) {
//		try {
//			PreparedStatement ps = connection.prepareStatement("INSERT INTO branch VALUES (?,?,?,?,?)");
//			ps.setInt(1, model.getId());
//			ps.setString(2, model.getName());
//			ps.setString(3, model.getAddress());
//			ps.setString(4, model.getCity());
//			if (model.getPhoneNumber() == 0) {
//				ps.setNull(5, java.sql.Types.INTEGER);
//			} else {
//				ps.setInt(5, model.getPhoneNumber());
//			}
//
//			ps.executeUpdate();
//			connection.commit();
//
//			ps.close();
//		} catch (SQLException e) {
//			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
//			rollbackConnection();
//		}
//	}
//
//	public Branch[] getBranchInfo() {
//		ArrayList<Branch> result = new ArrayList<Branch>();
//
//		try {
//			Statement stmt = connection.createStatement();
//			ResultSet rs = stmt.executeQuery("SELECT * FROM branch");
//
////    		// get info on ResultSet
////    		ResultSetMetaData rsmd = rs.getMetaData();
////
////    		System.out.println(" ");
////
////    		// display column names;
////    		for (int i = 0; i < rsmd.getColumnCount(); i++) {
////    			// get column name and print it
////    			System.out.printf("%-15s", rsmd.getColumnName(i + 1));
////    		}
//
//			while(rs.next()) {
//				Branch model = new Branch(rs.getString("branch_addr"),
//													rs.getString("branch_city"),
//													rs.getInt("branch_id"),
//													rs.getString("branch_name"),
//													rs.getInt("branch_phone"));
//				result.add(model);
//			}
//
//			rs.close();
//			stmt.close();
//		} catch (SQLException e) {
//			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
//		}
//
//		return result.toArray(new Branch[result.size()]);
//	}
//
//	public void updateBranch(int id, String name) {
//		try {
//		  PreparedStatement ps = connection.prepareStatement("UPDATE branch SET branch_name = ? WHERE branch_id = ?");
//		  ps.setString(1, name);
//		  ps.setInt(2, id);
//
//		  int rowCount = ps.executeUpdate();
//		  if (rowCount == 0) {
//		      System.out.println(WARNING_TAG + " Branch " + id + " does not exist!");
//		  }
//
//		  connection.commit();
//
//		  ps.close();
//		} catch (SQLException e) {
//			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
//			rollbackConnection();
//		}
//	}



}
