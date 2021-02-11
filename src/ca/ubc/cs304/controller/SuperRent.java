package ca.ubc.cs304.controller;

import ca.ubc.cs304.database.DatabaseConnectionHandler;
import ca.ubc.cs304.delegates.LoginWindowDelegate;
import ca.ubc.cs304.ui.LoginWindow;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;


/**
 * This is the main controller class that will orchestrate everything.
 */
public class SuperRent extends Application implements LoginWindowDelegate {
	private DatabaseConnectionHandler dbHandler;
	private LoginWindow loginWindow = null;
	private Controller controller;

	public SuperRent() {
		dbHandler = new DatabaseConnectionHandler();
		controller = new Controller();
	}


	// JAVAFX start method
	@Override
	public void start(Stage primaryStage) throws Exception {
		URL url = new File("src/ca/ubc/cs304/controller/GUI.fxml").toURI().toURL();
		Parent root = FXMLLoader.load(url);
		primaryStage.setTitle("Rental Database");
		primaryStage.setScene(new Scene(root,300,300));
		primaryStage.show();
	}


	// Login start procedure
	private void start() {
		loginWindow = new LoginWindow();
		loginWindow.showFrame(this);
	}

	/**
	 * LoginWindowDelegate Implementation
	 *
     * connects to Oracle database with supplied username and password
     */
	public void login(String username, String password) throws IOException {
		boolean didConnect = dbHandler.login(username, password);

		if (didConnect) {
			// Once connected, remove login window and start text transaction flow
			loginWindow.dispose();
			controller.setHandler(dbHandler);
			launch();


			//TerminalTransactions transaction = new TerminalTransactions();
			//transaction.showMainMenu(this);
		} else {
			loginWindow.handleLoginFailed();

			if (loginWindow.hasReachedMaxLoginAttempts()) {
				loginWindow.dispose();
				System.out.println("You have exceeded your number of allowed attempts");
				System.exit(-1);
			}
		}
	}

    
	/**
	 * Main method called at launch time
	 */
	public static void main(String args[]) {
		SuperRent superRent = new SuperRent();
		superRent.start();
	}







	//
//	/**
//	 * TermainalTransactionsDelegate Implementation
//	 *
//	 * Insert a branch with the given info
//	 */
//    public void insertBranch(Branch model) {
//    	dbHandler.insertBranch(model);
//    }
//
//    /**
//	 * TermainalTransactionsDelegate Implementation
//	 *
//	 * Delete branch with given branch ID.
//	 */
//    public void deleteBranch(int branchId) {
//    	dbHandler.deleteBranch(branchId);
//    }
//
//    /**
//	 * TermainalTransactionsDelegate Implementation
//	 *
//	 * Update the branch name for a specific ID
//	 */
//
//    public void updateBranch(int branchId, String name) {
//    	dbHandler.updateBranch(branchId, name);
//    }
//
//    /**
//	 * TermainalTransactionsDelegate Implementation
//	 *
//	 * Displays information about varies bank branches.
//	 */
//    public void showBranch() {
//    	Branch[] models = dbHandler.getBranchInfo();
//
//    	for (int i = 0; i < models.length; i++) {
//    		Branch model = models[i];
//
//    		// simplified output formatting; truncation may occur
//    		System.out.printf("%-10.10s", model.getId());
//    		System.out.printf("%-20.20s", model.getName());
//    		if (model.getAddress() == null) {
//    			System.out.printf("%-20.20s", " ");
//    		} else {
//    			System.out.printf("%-20.20s", model.getAddress());
//    		}
//    		System.out.printf("%-15.15s", model.getCity());
//    		if (model.getPhoneNumber() == 0) {
//    			System.out.printf("%-15.15s", " ");
//    		} else {
//    			System.out.printf("%-15.15s", model.getPhoneNumber());
//    		}
//
//    		System.out.println();
//    	}
//    }
//
//    /**
//	 * TerminalTransactionsDelegate Implementation
//	 *
//     * The TerminalTransaction instance tells us that it is done with what it's
//     * doing so we are cleaning up the connection since it's no longer needed.
//     */
//    public void terminalTransactionsFinished() {
//    	dbHandler.close();
//    	dbHandler = null;
//
//    	System.exit(0);
//    }
}
