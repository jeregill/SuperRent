package ca.ubc.cs304.controller;

import ca.ubc.cs304.Exceptions.DateException;
import ca.ubc.cs304.database.DatabaseConnectionHandler;
import ca.ubc.cs304.model.*;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Controller {

    private static DatabaseConnectionHandler dbHandler;
    private queryViewer qv = new queryViewer();

    public Controller(){
        // default constructor
    }

    public void setHandler(DatabaseConnectionHandler db){
        this.dbHandler = db;
    }

    public void viewVehicles(ActionEvent event) {
        // new window for querying
        Stage newStage = new Stage();
        VBox comp = new VBox();
        Label carTypeLabel = new Label("Car Type: ");
        Label locationLabel = new Label("Location: ");
        Label fromLabel = new Label("From: ");
        Label toLabel = new Label("To: ");
        // VTypes
        String vtypes[] = { "Economy", "Compact", "Mid-Size", "Standard", "Full-size", "SUV", "Truck" };
        // branchLocs
        String branchLocs[] = { "Vancouver", "Surrey", "Richmond"};
        // Create a combo box
        ComboBox vtypesBox =new ComboBox(FXCollections.observableArrayList(vtypes));
        // Create a combo box
        ComboBox locsBox = new ComboBox(FXCollections.observableArrayList(branchLocs));
        comp.getChildren().addAll(carTypeLabel,vtypesBox);
        comp.getChildren().addAll(locationLabel,locsBox);
        DatePicker from = new DatePicker();
        DatePicker to = new DatePicker();
        comp.getChildren().add(fromLabel);
        comp.getChildren().add(from);
        comp.getChildren().add(toLabel);
        comp.getChildren().add(to);


        //Defining the Count button
        Button count = new Button("View Count");
        comp.getChildren().add(count);

        //Setting an action for the count button
        count.setOnAction(new EventHandler<ActionEvent>() {
            String locText = null;
            String carTypeText = null;
            String fromText = null;
            String toText = null;
            // flag for error handling
            boolean valid = true;
            boolean old = false;
            List<Vehicle> result;

            @Override
            public void handle(ActionEvent e) {
                // get the values

                if (locsBox.getEditor().getText() != null) {
                    locText = (String) locsBox.getValue();

                }
                if (vtypesBox.getEditor().getText() != null) {
                    carTypeText = (String) vtypesBox.getValue();

                }
                if (from.getValue() != null && to.getValue() !=null) {
                    fromText = dateFormatter(from.getValue());
                    toText = dateFormatter(to.getValue());

                    // check that to date is greater than from date
                    if (to.getValue().compareTo(from.getValue()) < 0) {
                        // show alert
                        Alert alert = new Alert(Alert.AlertType.WARNING, "To Date must be greater than from date", ButtonType.OK);
                        alert.showAndWait();
                        valid = false;
                    } else {
                        valid = true;
                    }
                    if(to.getValue().compareTo(LocalDate.of(2019,10,01)) < 0 ||
                            from.getValue().compareTo(LocalDate.of(2019,10,01)) < 0)
                        old = true;
                }


                if(valid) {
                    // query the database
                    // if dates are too old

                    try {
                        result = dbHandler.queryViewVehicles(carTypeText, locText, fromText, toText);
                        // if dates are too old, return an empty list
                        if(old)
                            result = new ArrayList<Vehicle>();
                        newStage.close();
                        Stage secondaryStage = new Stage();
                        VBox comp1 = new VBox();
                        Label labelVehicles = new Label("Number of Available Vehicles is " + result.size() + "\n Click view to see the results");
                        comp1.getChildren().add(labelVehicles);
                        //Defining the Submit button
                        Button submit = new Button("View");
                        comp1.getChildren().add(submit);

                        //Setting an action for the Submit button
                        submit.setOnAction(new EventHandler<ActionEvent>() {

                            @Override
                            public void handle(ActionEvent e) {
                                    try {
                                        secondaryStage.close();
                                        qv.showResults(COLUMNS,result);

                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }

                            }


                        });
                        comp1.setAlignment(Pos.CENTER);
                        Scene stageScene = new Scene(comp1, 300, 300);
                        secondaryStage.setScene(stageScene);
                        secondaryStage.show();



                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }




            }

            public final List<Column<?>> COLUMNS = new ArrayList<>(Arrays.asList(
                    new Column<String>(String.class, "vlicense", "License Plate"),
                    new Column<Double>(Double.class, "make", "Make"),
                    new Column<String>(String.class, "model", "Model"),
                    new Column<String>(String.class, "color", "Color"),
                    new Column<String>(String.class, "year", "Year"),
                    new Column<String>(String.class, "odometer", "Odometer"),
                    new Column<String>(String.class, "vtName", "Vehicle Type"),
                    new Column<String>(String.class, "status", "Status"),
                    new Column<String>(String.class, "city", "Location")
            ));

        });

        comp.setAlignment(Pos.CENTER);
        Scene stageScene = new Scene(comp, 300, 300);
        newStage.setScene(stageScene);
        newStage.show();
    }

    // Helper to format dates
    public String dateFormatter(LocalDate date){
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern("dd-MMM-uuuu");
        String text = date.format(formatters);
        return text;
    }


    public void makeReservation(ActionEvent event){
        // new window for querying
        Stage newStage = new Stage();
        VBox info = new VBox();
        Label custLicense = new Label("License #: ");
        Label carTypeLabel = new Label("Car Type:");
        Label locationLabel = new Label("Location: ");
        Label fromLabel = new Label("From: ");
        Label toLabel = new Label("To: ");
        TextField license = new TextField("");
        // VTypes
        String vtypes[] = { "Economy", "Compact", "Mid-Size", "Standard", "Full-size", "SUV", "Truck" };
        // branchLocs
        String branchLocs[] = { "Vancouver", "Surrey", "Richmond"};
        // Create a combo box
        ComboBox vtypesBox =new ComboBox(FXCollections.observableArrayList(vtypes));
        // Create a combo box
        ComboBox locsBox = new ComboBox(FXCollections.observableArrayList(branchLocs));
        info.getChildren().addAll(custLicense,license);
        info.getChildren().addAll(carTypeLabel,vtypesBox);
        info.getChildren().addAll(locationLabel,locsBox);
        DatePicker from = new DatePicker();
        DatePicker to = new DatePicker();
        info.getChildren().add(fromLabel);
        info.getChildren().add(from);
        info.getChildren().add(toLabel);
        info.getChildren().add(to);


        //Defining the Count button
        Button reserve = new Button("Reserve");
        info.getChildren().add(reserve);

        //Setting an action for the count button
        reserve.setOnAction(new EventHandler<ActionEvent>() {
            String locText = null;
            String carTypeText = null;
            String fromText = null;
            String toText = null;
            String licenseText = null;
            // flag for error handling
            boolean valid = true;

            @Override
            public void handle(ActionEvent e) {
                // get the values

                if (locsBox.getEditor().getText() == null ||
                        vtypesBox.getEditor().getText() == null ||
                        from.getValue() == null || to.getValue() ==null ||
                license.getText().isEmpty()) {
                    // all fields need to be entered
                    valid = false;

                }
                // Assuming the values are present
                locText = (String) locsBox.getValue();
                carTypeText = (String) vtypesBox.getValue();
                fromText = dateFormatter(from.getValue());
                toText = dateFormatter(to.getValue());
                licenseText = license.getText();

                // Ensure dates are compatible
                if (from.getValue() != null && to.getValue() !=null) {
                    // check that to date is greater than from date
                    if (to.getValue().compareTo(from.getValue()) < 0) {
                        // show alert
                        Alert alert = new Alert(Alert.AlertType.WARNING, "To Date must be greater than from date", ButtonType.OK);
                        alert.showAndWait();
                        valid = false;
                    }
                }
                // Ensure customer is registered
                if(valid){
                    try {
                        ArrayList<String> licenses = dbHandler.queryCustomers();
                        if(!licenses.contains(licenseText)){
                            // show alert
                            Alert alert = new Alert(Alert.AlertType.WARNING, "Customer does not exist.\nPlease register customer before making a reservation ", ButtonType.OK);
                            alert.showAndWait();
                            valid = false;
                            // close the window
                            newStage.close();
                        }
                    } catch (Exception ex){
                        ex.printStackTrace();
                    }
                }

                // Customer must exist at this point
                if (valid) {
                    // check if such a vehicle exists
                    if(dbHandler.queryViewVehicles(carTypeText,locText,toText,fromText).size() > 0){
                        HashMap<String,Integer> branches = dbHandler.branchInfo();
                        // this should always work because of the previous checks
                        Customer c = dbHandler.getCustomerInfo(licenseText).get(0);
                        // such a reservation can be made
                        Reservation r = new Reservation(dbHandler.maxResNum()+1,branches.get(locText),carTypeText,licenseText,fromText,toText);
                        boolean confirmed = dbHandler.insertReservation(r);
                        newStage.close();
                        Stage secondaryStage = new Stage();
                        VBox receipt = new VBox();
                        Label confNum = new Label("Conf Num: " + r.getConf_num());
                        Label custLicense = new Label("License #: " + licenseText);
                        Label custFName = new Label("First Name: " + c.getfName());
                        Label custLName = new Label("Last Name: " + c.getlName());
                        Label custPhoneNum = new Label("Phone #: " + c.getPhone_num());
                        Label carTypeLabel = new Label("Car Type: " + carTypeText);
                        Label locationLabel = new Label("Location: " + branches.get(locText));
                        Label fromLabel = new Label("From: " + fromText);
                        Label toLabel = new Label("To: " + toText);
                        receipt.getChildren().add(confNum);
                        receipt.getChildren().add(custLicense);
                        receipt.getChildren().add(custFName);
                        receipt.getChildren().add(custLName);
                        receipt.getChildren().add(custPhoneNum);
                        receipt.getChildren().add(carTypeLabel);
                        receipt.getChildren().add(locationLabel);
                        receipt.getChildren().add(fromLabel);
                        receipt.getChildren().add(toLabel);
                        Scene stageScene = new Scene(receipt, 200, 200);
                        secondaryStage.setScene(stageScene);
                        secondaryStage.show();

                    } else {
                        // no such vehicle exists
                        Alert alert = new Alert(Alert.AlertType.WARNING, "A vehicle is not available meeting this criteria.\nPlease re-visit the query window and try again", ButtonType.OK);
                        alert.showAndWait();
                    }

                }






            }

        });

        info.setAlignment(Pos.CENTER);
        Scene stageScene = new Scene(info, 300, 300);
        newStage.setScene(stageScene);
        newStage.show();
    }


    // Add a customer
    public void addCustomer(ActionEvent e){
        Stage newStage = new Stage();
        VBox info = new VBox();
        Label custLicense = new Label("License #: ");
        Label fnLabel = new Label("First Name:");
        Label lnLabel = new Label("Last Name: ");
        Label phoneNumLabel = new Label("Phone Number: ");
        Label cardNumLabel = new Label("Credit Card Number: ");
        TextField license = new TextField("");
        TextField fn = new TextField("");
        TextField ln = new TextField("");
        TextField phoneNum = new TextField("");
        TextField cardNum = new TextField("");
        info.getChildren().addAll(custLicense,license);
        info.getChildren().addAll(fnLabel,fn);
        info.getChildren().addAll(lnLabel, ln);
        info.getChildren().addAll(phoneNumLabel,phoneNum);
        info.getChildren().addAll(cardNumLabel,cardNum);

        //Defining the Count button
        Button add = new Button("Add");
        info.getChildren().add(add);

        //Setting an action for the count button
        add.setOnAction(new EventHandler<ActionEvent>() {
            String licenseText = null;
            String fnText = null;
            String lnText = null;
            String phoneNumText = null;
            String cardNumText = null;
            // flag for error handling
            boolean valid = true;

            @Override
            public void handle(ActionEvent e) {
                // get the values

                if (license.getText().isEmpty() ||
                        fn.getText().isEmpty()||
                        ln.getText().isEmpty()|| phoneNum.getText().isEmpty()||
                        cardNum.getText().isEmpty()) {
                    // all fields need to be entered
                    valid = false;

                }
                // Assuming the values are present
                licenseText = license.getText();
                fnText = fn.getText();
                lnText = ln.getText();
                phoneNumText = phoneNum.getText();
                cardNumText = cardNum.getText();


                // Ensure customer is registered
                if(valid){
                    try {
                        ArrayList<String> licenses = dbHandler.queryCustomers();
                        if(licenses.contains(licenseText)){
                            // show alert
                            Alert alert = new Alert(Alert.AlertType.WARNING, "Customer already exists.\nPlease register a new customer", ButtonType.OK);
                            alert.showAndWait();
                            valid = false;
                            // close the window
                        }
                    } catch (Exception ex){
                        ex.printStackTrace();
                    }
                }

                // Customer must exist at this point
                if (valid) {
                    // Add a new Customer
                    Customer c = new Customer(licenseText,fnText,lnText,phoneNumText,cardNumText);
                    boolean confirmed = dbHandler.insertCustomer(c);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Customer has been added.", ButtonType.OK);
                    alert.showAndWait();
                    newStage.close();

                }

            }

        });

        info.setAlignment(Pos.CENTER);
        Scene stageScene = new Scene(info, 300, 300);
        newStage.setScene(stageScene);
        newStage.show();

    }

    // Make a new rental
    public void newRental(ActionEvent e){
        Stage newStage = new Stage();
        VBox info = new VBox();
        Label confNum = new Label("Enter Confirmation #: ");
        TextField confNumText = new TextField();
        info.getChildren().addAll(confNum,confNumText);


        //Defining the Count button
        Button button = new Button("Submit");
        info.getChildren().add(button);

        //Setting an action for the count button
        button.setOnAction(new EventHandler<ActionEvent>() {
            Integer confNumValue = null;
            // flag for error handling
            boolean valid = true;

            @Override
            public void handle(ActionEvent e) {
                // get the values

                if (confNumText.getText().isEmpty()) {
                    // all fields need to be entered
                    valid = false;
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Please enter a confirmation number", ButtonType.OK);
                    alert.showAndWait();

                } else {
                    // Assuming the values are present
                    confNumValue = Integer.parseInt(confNumText.getText());
                    valid = true;
                }

                ArrayList<Reservation> reservation= dbHandler.findReservation(confNumValue);

                // Ensure customer is registered
                if(valid){
                    // If the reservation does not exist
                    if(reservation.size() == 0){
                        // show alert
                        Alert alert = new Alert(Alert.AlertType.WARNING, "No such reservation with this confirmation number exists.\nPlease make a new reservation and try again", ButtonType.OK);
                        alert.showAndWait();
                        valid = false;
                        // close the window
                        newStage.close();
                    }

                }

                // Reservation must exist at this point
                // If the confirmation number has already been marked in a rental

                List<Rental> existingRentals = dbHandler.getRentals();
                for(Rental r: existingRentals) {
                    if (r.getConf_num() == confNumValue) {
                        // rental already exists
                        valid = false;
                        Alert alert = new Alert(Alert.AlertType.WARNING, "This reservation has already been rented.", ButtonType.OK);
                        alert.showAndWait();
                        // close the window
                        newStage.close();
                        break;
                    }

                }
                if (valid) {
                    // Retrieve the reservation information
                    Reservation r = reservation.get(0);

                    // Fix Date formatting
                    r.setFrom_date(changeDateFormat(r.getFrom_date()));
                    r.setTo_date(changeDateFormat(r.getTo_date()));
                    List<Vehicle> validVehicles = dbHandler.queryViewVehicles(r.getVtname(),cityFromBranchNum(r.getBranch_num()),r.getFrom_date(),r.getTo_date());
                    // Pick a random vehicle to be assigned to the customer
                    Vehicle v = validVehicles.get(0);
                    // add the new rental, using the vehicle info and the reservation info
                    Rental rental = dbHandler.insertRental(v,r);
                    // change the status of that vehicle to rented
                    boolean updated = dbHandler.updateVehicle(v.getVlicense(), "RENTED");

                    Alert alert = new Alert(Alert.AlertType.WARNING, "Rental Confirmed. Press OK to see receipt.", ButtonType.OK);
                    alert.showAndWait();
                    // show receipt

                    Stage secondaryStage = new Stage();
                    VBox receipt = new VBox();
                    Label rentID = new Label("RentID: " + rental.getRent_id());
                    Label confNum = new Label("Conf Num: " + r.getConf_num());
                    Label carTypeLabel = new Label("Car Type: " + v.getVtName());
                    Label carMake = new Label("Make: " + v.getMake());
                    Label carModel = new Label("Model: " + v.getModel());
                    Label carYear = new Label("Year: " + v.getYear());
                    Label locationLabel = new Label("Location: " + cityFromBranchNum(r.getBranch_num()));
                    Label fromLabel = new Label("From: " + r.getFrom_date());
                    Label toLabel = new Label("To: " + r.getTo_date());
                    receipt.getChildren().add(rentID);
                    receipt.getChildren().add(confNum);
                    receipt.getChildren().add(carTypeLabel);
                    receipt.getChildren().add(carMake);
                    receipt.getChildren().add(carModel);
                    receipt.getChildren().add(carYear);
                    receipt.getChildren().add(locationLabel);
                    receipt.getChildren().add(fromLabel);
                    receipt.getChildren().add(toLabel);
                    Scene stageScene = new Scene(receipt, 200, 200);
                    secondaryStage.setScene(stageScene);
                    secondaryStage.show();

                    newStage.close();

                }

            }

        });

        info.setAlignment(Pos.CENTER);
        Scene stageScene = new Scene(info, 300, 300);
        newStage.setScene(stageScene);
        newStage.show();

    }

    public void newReturn(ActionEvent e){
        // new window for querying
        Stage newStage = new Stage();
        VBox info = new VBox();
        Label rentNumber = new Label("Rent #: ");
        Label odometerNumber = new Label("Odometer: ");
        Label gasTankStatus = new Label("Gas Tank Status: ");
        Label toLabel = new Label("Return Date: ");

        TextField returnCar = new TextField("");
        TextField odometer = new TextField("");
        // Gas Tank Status Enum
        String gasTankEnum[] = { "Full", "Not Full" };
        // Create a combo box
        ComboBox gasTankBox =new ComboBox(FXCollections.observableArrayList(gasTankEnum));
        info.getChildren().addAll(rentNumber,returnCar);
        info.getChildren().addAll(odometerNumber,odometer);
        info.getChildren().addAll(gasTankStatus,gasTankBox);
        DatePicker to = new DatePicker();
        info.getChildren().add(toLabel);
        info.getChildren().add(to);


        //Defining the Count button
        Button returnVehicle = new Button("Return");
        info.getChildren().add(returnVehicle);

        //Setting an action for the count button
        returnVehicle.setOnAction(new EventHandler<ActionEvent>() {
            String returnText = null;
            String gasTankStatusText = null;
            String odometerText = null;
            String toText = null;
            String vehicleLicenseText = null;
            String vehicleTypeText = null;
            String fromText = null;
            int reservationText = 0;
            int totalWeeks = 0;
            int totalDays = 0;
            // flag for error handling
            boolean valid = true;

            @Override
            public void handle(ActionEvent e) {
                // get the values

                if (gasTankBox.getEditor().getText() == null ||
                        to.getValue() == null ||
                        returnCar.getText().isEmpty() ||
                        odometer.getText().isEmpty()) {
                    // all fields need to be entered
                    valid = false;

                }
                // Assuming the values are present
                returnText = returnCar.getText();
                gasTankStatusText = (String) gasTankBox.getValue();
                toText = dateFormatter(to.getValue());
                odometerText = odometer.getText();

                // Ensure Rent ID exists
                if(valid){
                    try {
                        List<Rental> existingRentals = dbHandler.getRentals();
                        for(Rental r: existingRentals) {
                            if (r.getRent_id() == Integer.parseInt(returnText)) {
                                // rental exists
                                fromText = changeDateFormat(r.getFrom_date());
                                reservationText = r.getConf_num();
                                vehicleLicenseText = r.getV_license();
                                Vehicle vehicle = dbHandler.queryLicensePlate(vehicleLicenseText);
                                if(vehicle != null) {
                                    vehicleTypeText = vehicle.getVtName();
                                }
                                break;
                            }
                        }
                    } catch (Exception ex){
                        ex.printStackTrace();
                    }
                }

                // Check if Vehicle was found
                if (valid) {
                    if (vehicleLicenseText == null || vehicleTypeText == null) {
                        Alert alert = new Alert(Alert.AlertType.WARNING, "This rent ID doesn't exist.", ButtonType.OK);
                        alert.showAndWait();
                        // close the window
                        valid = false;
                        newStage.close();
                    }
                }

                // Reservation must exist at this point
                // If the confirmation number has already been marked in a rental
                List<Return> existingReturns = dbHandler.getReturns();
                for(Return r: existingReturns) {
                    if (r.getRent_id() ==  Integer.parseInt(returnText)) {
                        // Return already exists
                        valid = false;
                        Alert alert = new Alert(Alert.AlertType.WARNING, "This rental has already been returned.", ButtonType.OK);
                        alert.showAndWait();
                        // close the window
                        newStage.close();
                        break;
                    }

                }

                // Rent ID must exist at this point
                if (valid) {
                    // update Vehicle to be available again
                    dbHandler.updateVehicle(vehicleLicenseText, "AVAILABLE");

                    // update Vehicle odometer
                    dbHandler.updateOdometer(vehicleLicenseText, odometerText);

                    // calculate price with VT rates
                    VehicleType vehicleType = dbHandler.queryRates(vehicleTypeText);
                    SimpleDateFormat myFormat = new SimpleDateFormat("dd-MMM-yyyy");
                    String inputString1 = fromText;
                    String inputString2 = toText;
                    int price = -1;

                    try {
                        Date date1 = myFormat.parse(inputString1);
                        Date date2 = myFormat.parse(inputString2);
                        long diffMs = Math.abs(date2.getTime() - date1.getTime());
                        int diffDays = Math.toIntExact(TimeUnit.DAYS.convert(diffMs, TimeUnit.MILLISECONDS));
                        if (diffDays >= 7) {
                            totalWeeks = diffDays / 7;
                            totalDays = diffDays % 7;
                            price = ((diffDays / 7) * vehicleType.getWrate()) + ((diffDays % 7) * vehicleType.getDrate());
                        } else {
                            totalDays = diffDays;
                            price = diffDays * vehicleType.getDrate();
                        }
                    } catch (ParseException errorParse) {
                        errorParse.printStackTrace();
                    }

                    // a return can be made
                    int full_tank;
                    if (gasTankStatusText == "Full") {
                        full_tank = 1;
                    } else {
                        full_tank = 0;
                    }

                    Return r = new Return(Integer.parseInt(returnText), Integer.parseInt(odometerText), full_tank, toText, price);

                    // insert Return
                    boolean confirmed = dbHandler.insertReturn(r);

                    // close prompt
                    newStage.close();

                    // Receipt
                    Stage secondaryStage = new Stage();
                    VBox receipt = new VBox();
                    Label rentNumber = new Label("Rent #: " + returnText);
                    Label reservationNumber = new Label("Reservation #: " + reservationText);
                    Label licenseLabel = new Label("Car License: " + vehicleLicenseText);
                    Label carTypeLabel = new Label("Car Type: " + vehicleTypeText);
                    Label odometerLabel = new Label("Odometer: " + odometerText);
                    Label statusLabel = new Label("Tank Status: " + gasTankStatusText);
                    Label toLabel = new Label("Return Date: " + toText);
                    Label priceLabel = new Label("Price (" + totalWeeks + " * " + vehicleType.getWrate() + " + " +
                            + totalDays + " * " + vehicleType.getDrate() +"): " + price);
                    receipt.getChildren().add(rentNumber);
                    receipt.getChildren().add(reservationNumber);
                    receipt.getChildren().add(licenseLabel);
                    receipt.getChildren().add(carTypeLabel);
                    receipt.getChildren().add(odometerLabel);
                    receipt.getChildren().add(statusLabel);
                    receipt.getChildren().add(toLabel);
                    receipt.getChildren().add(priceLabel);
                    Scene stageScene = new Scene(receipt, 200, 200);
                    secondaryStage.setScene(stageScene);
                    secondaryStage.show();
                }
            }
        });

        info.setAlignment(Pos.CENTER);
        Scene stageScene = new Scene(info, 300, 300);
        newStage.setScene(stageScene);
        newStage.show();
    }

    // Get city from branch num
    public String cityFromBranchNum(int branchNum) {
        HashMap<String, Integer> branches = dbHandler.branchInfo();
        // Obtain city name from branch number
        String key = "";
        int value = branchNum;
        for (String k : branches.keySet()) {
            if (branches.get(k).equals(value)) {
                key = k;
            }
        }
        return key;
    }

    public String changeDateFormat(String date){
        String OLD_FORMAT = "yyyy-MM-dd hh:mm:ss.s";
        String NEW_FORMAT = "dd-MMM-yyyy";


        SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
        try {
            Date d = sdf.parse(date);

            sdf.applyPattern(NEW_FORMAT);
            String newDateString = sdf.format(d);
            return newDateString;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void newReport(ActionEvent event) {
        // new window for querying
        Stage newStage = new Stage();
        VBox comp = new VBox();
        Label reportTypeLabel = new Label("Report Type: ");
        Label locationLabel = new Label("Location(s): ");

        // rtypes
        String rtypes[] = { "Returns", "Rentals" };
        // branchLocs
        String branchLocs[] = { "All", "Vancouver", "Surrey", "Richmond"};
        // Create a combo box
        ComboBox rtypesBox =new ComboBox(FXCollections.observableArrayList(rtypes));
        // Create a combo box
        ComboBox locsBox = new ComboBox(FXCollections.observableArrayList(branchLocs));
        comp.getChildren().addAll(reportTypeLabel,rtypesBox);
        comp.getChildren().addAll(locationLabel,locsBox);

        Label dayLabel = new Label("Date:");
        DatePicker day = new DatePicker();

        comp.getChildren().add(dayLabel);
        comp.getChildren().add(day);
        //Defining the Count button
        Button genReports = new Button("Generate Report");
        comp.getChildren().add(genReports);

        //Setting an action for the genReports button
        genReports.setOnAction(new EventHandler<ActionEvent>() {
            String locText = null;
            String rTypeText = null;
            String dayText = null;

            // flag for error handling
            boolean valid = true;
            boolean old = false;
            boolean isReturns = false;
            List result;

            @Override
            public void handle(ActionEvent e) {

                // get the values

                if(locsBox.getEditor().getText() == null || rtypesBox.getEditor().getText() == null
                        || day.getValue() == null) {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Please input all values.", ButtonType.OK);
                    alert.showAndWait();
                    valid = false;
                } else {
                    valid = true;
                }
                // get the values
                if(valid) {

                    if (locsBox.getEditor().getText() != null) {
                        locText = (String) locsBox.getValue();
                    }
                    if (rtypesBox.getEditor().getText() != null) {
                        rTypeText = (String) rtypesBox.getValue();
                        if (rTypeText.equals("Returns")) {
                            isReturns = true;
                        }
                    }
                    if (day.getValue() != null) {
                        dayText = dateFormatter(day.getValue());


                        if (day.getValue().compareTo(LocalDate.of(2019, 10, 01)) < 0 ||
                                day.getValue().compareTo(LocalDate.of(2019, 10, 01)) < 0)
                            old = true;
                    }
                }



                if (valid) {
                    // query the database
                    // if dates are too old

                    try {
                        result = dbHandler.queryGenerateReport(isReturns, locText, dayText).get(0);
                        List aggretgateInfo = dbHandler.queryGenerateReport(isReturns, locText, dayText).get(1);
                        // if dates are too old, return an empty list
                        if (old)
                            result = new ArrayList<ReportEntry>();
                        newStage.close();
                        Stage secondaryStage = new Stage();
                        VBox comp1 = new VBox();


                        Label labelVehicles = new Label("Choose level of detail: \n");
                        comp1.getChildren().add(labelVehicles);
                        //Defining the Submit button
                        Button submit = new Button("View Detailed");
                        Button submit2 = new Button("View Aggregated");
                        comp1.getChildren().add(submit);
                        comp1.getChildren().add(submit2);

                        //Setting an action for the Submit button
                        submit.setOnAction(new EventHandler<ActionEvent>() {

                            @Override
                            public void handle(ActionEvent e) {
                                try {

                                    List<Column<?>> columns = isReturns ? COLUMNS_RETURN : COLUMNS_RENTAL;
                                    qv.showResults(columns, result);

                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }

                            }


                        });

                        //Setting an action for the Submit button
                        submit2.setOnAction(new EventHandler<ActionEvent>() {

                            @Override
                            public void handle(ActionEvent e) {
                                try {

                                    List<Column<?>> columns = isReturns ? COLUMNS_RETURN2 : COLUMNS_RENTAL2;
                                    qv.showResults(columns, aggretgateInfo);

                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }

                            }


                        });
                        comp1.setAlignment(Pos.CENTER);
                        Scene stageScene = new Scene(comp1, 300, 300);
                        secondaryStage.setScene(stageScene);
                        secondaryStage.show();


                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }




            }

            public final List<Column<?>> COLUMNS_RETURN = new ArrayList<>(Arrays.asList(
                    new Column<String>(String.class, "branch", "Branch"),
                    new Column<Double>(Double.class, "vtName", "Vehicle Type"),
                    new Column<String>(String.class, "count", "Count"),
                    new Column<String>(String.class, "revenue", "Revenue")
            ));

            public final List<Column<?>> COLUMNS_RENTAL = new ArrayList<>(Arrays.asList(
                    new Column<String>(String.class, "branch", "Branch"),
                    new Column<Double>(Double.class, "vtName", "Vehicle Type"),
                    new Column<String>(String.class, "count", "Count")
            ));
            public final List<Column<?>> COLUMNS_RETURN2 = new ArrayList<>(Arrays.asList(
                    new Column<String>(String.class, "branch", "Branch"),
                    new Column<String>(String.class, "count", "Count"),
                    new Column<String>(String.class, "revenue", "Revenue")
            ));

            public final List<Column<?>> COLUMNS_RENTAL2 = new ArrayList<>(Arrays.asList(
                    new Column<String>(String.class, "branch", "Branch"),
                    new Column<String>(String.class, "count", "Count")
            ));
        });

        comp.setAlignment(Pos.CENTER);
        Scene stageScene = new Scene(comp, 300, 300);
        newStage.setScene(stageScene);
        newStage.show();
    }

}
