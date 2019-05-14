package ro.lbi.sqliggybank.client.view.dashboard;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import ro.lbi.sqliggybank.client.backend.Bank;
import ro.lbi.sqliggybank.client.backend.Group;
import ro.lbi.sqliggybank.client.backend.User;
import ro.lbi.sqliggybank.client.backend.database.DatabaseHandler;
import ro.lbi.sqliggybank.client.backend.exceptions.ForbiddenException;
import ro.lbi.sqliggybank.client.util.Alert;
import ro.lbi.sqliggybank.client.view.window_manager.WindowManager;

import java.io.IOException;

public class TransactionsController {

	private WindowManager windowManager;

	private User user;

	private Group group;

	private Bank bank;

	private DatabaseHandler databaseHandler;

	@FXML
	private TextField amountField;

	@FXML
	private TextArea descriptionField;

	@FXML
	private TextField tagsField;

	TransactionsController(WindowManager windowManager, User user, Group group, Bank bank) {
		this.windowManager = windowManager;
		this.user = user;
		this.group = group;
		this.bank = bank;
		databaseHandler = new DatabaseHandler();
	}

	@FXML
	private void okButtonPressed(ActionEvent event) {
		try {
			if (Integer.parseInt(amountField.getText()) <= 0) {
				throw new NumberFormatException("Amount needs to be higher than 0");
			}
			String result = databaseHandler.addTransaction(group.getName(), bank.getName(), user.getJWT(), Integer.parseInt(amountField.getText()),
					user.getUsername(), descriptionField.getText(), tagsField.getText());

			System.out.println(result);

			if (result.equals("200")) {
				Alert.infoAlert("Success", "You successfully added a transaction!");
				((Node)event.getSource()).getScene().getWindow().hide();
			}
		} catch (NumberFormatException e) {
			Alert.errorAlert("Error", "Amount needs to be a number higher than 0!");
		} catch (IOException e) {
			Alert.errorAlert("Error", "There was a connection error.");
		} catch (ForbiddenException e) {
			Alert.errorAlert("Error", "You are not the owner of this group so you cannot make transactions!");
			((Node)event.getSource()).getScene().getWindow().hide();
		}
	}

}
