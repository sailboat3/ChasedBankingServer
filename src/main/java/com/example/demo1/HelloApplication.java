package com.example.demo1;

import com.example.demo1.DatabaseHelper;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;
import javafx.util.converter.DoubleStringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class HelloApplication extends Application {
    private static final Logger log = LoggerFactory.getLogger(HelloApplication.class);
    private double balance = 0;
    private String loggedInUser = null;

    @Override
    public void start(Stage stage) throws IOException {

        //Main Banking interface scene

        //Label
        Label label = new Label("Welcome to the Chased Bank Banking server.");
        HBox labelBox = new HBox(label);
        labelBox.setAlignment(Pos.CENTER);

        //TextFormatter to only accept doubles as input
        TextFormatter<Double> doubleTextFormatter = new TextFormatter<>(
                new DoubleStringConverter(),
                0.0,
                change -> {
                    String newText = change.getControlNewText();
                    if (newText.isEmpty() || newText.matches("-?\\d*(\\.\\d*)?")) {
                        return change;
                    }
                    return null;
                });

        //Text Input
        Label dollarSign = new Label("$");
        TextField amount = new TextField();
        amount.setTextFormatter(doubleTextFormatter);
        amount.setMaxWidth(256);
        amount.setPromptText("Amount");
        HBox moneyInput = new HBox();
        moneyInput.getChildren().addAll(dollarSign, amount);
        moneyInput.setAlignment(Pos.CENTER);

        //Deposit and Withdrawal buttons
        Button deposit = new Button("Deposit");
        Button withdrawal = new Button("Withdrawal");
        HBox moneyActionButtons = new HBox();
        moneyActionButtons.getChildren().addAll(deposit, withdrawal);
        moneyActionButtons.setAlignment(Pos.CENTER);

        //Balance Display
        Label balanceLabel = new Label("Welcome, " + loggedInUser);
        Text balanceAmount = new Text("$" + balance);
        balanceAmount.setFont(Font.font("Minecraft", 64));
        balanceAmount.setFill(Color.GREEN);
        VBox right = new VBox(balanceLabel, balanceAmount);
        right.setAlignment(Pos.CENTER);

        //Put all the left side things in a VBox
        VBox left = new VBox(moneyInput, moneyActionButtons);

        //Final Pane
        SplitPane bankSplitPane = new SplitPane();
        final StackPane sp1 = new StackPane();
        sp1.getChildren().add(left);
        final StackPane sp2 = new StackPane();
        sp2.getChildren().add(right);
        bankSplitPane.getItems().addAll(sp1, sp2);
        bankSplitPane.setDividerPositions(0.4f, 0.6f);
        bankSplitPane.setScaleShape(true);
        VBox mainBank = new VBox(labelBox, bankSplitPane);

        //Do stuff when you click on the buttons
        deposit.setOnAction(e -> {
            try {
                balance += Double.parseDouble(amount.getText());
                DatabaseHelper.updateBalance(loggedInUser, balance);
                balanceAmount.setText("$" + balance);
            } catch (NumberFormatException ex) {
                System.out.println("Invalid input: " + amount.getText());
            }
        });
        withdrawal.setOnAction(e -> {
            try {
                balance -= Double.parseDouble(amount.getText());
                DatabaseHelper.updateBalance(loggedInUser, balance);
                balanceAmount.setText("$" + balance);
            } catch (NumberFormatException ex) {
                System.out.println("Invalid input: " + amount.getText());
            }
        });

        Scene mainMenuScene = new Scene(mainBank, 480, 320);

        //Login Interface scene

        //Title
        Label titleText = new Label("Chased Inc. Banking Server");
        titleText.setFont(Font.font(32));
        HBox title = new HBox();
        title.setAlignment(Pos.CENTER);
        title.getChildren().add(titleText);

        //Login fields and button
        TextField username = new TextField();
        username.setMaxWidth(256);
        username.setPromptText("Username");
        PasswordField password = new PasswordField();
        password.setMaxWidth(256);
        password.setPromptText("Password");
        Button loginButton = new Button("Login");
        loginButton.setOnAction(e -> {
            User retrievedUser = DatabaseHelper.getUser(username.getText());
            if (retrievedUser != null) {
                if (Objects.equals(retrievedUser.getPassword(), password.getText())) {
                    balance = retrievedUser.getBalance();
                    balanceAmount.setText("$" + balance);
                    loggedInUser = retrievedUser.getUsername();
                    balanceLabel.setText("Welcome, " + loggedInUser);
                    stage.setScene(mainMenuScene);
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Incorrect Details");
                    alert.setContentText("The username or password you entered is not correct");
                    alert.showAndWait();
                    username.setText("");
                    password.setText("");
                }

            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Incorrect Details");
                alert.setContentText("The username or password you entered is not correct");
                alert.showAndWait();
                username.setText("");
                password.setText("");
            }
        });
        VBox loginMenu = new VBox();
        loginMenu.setAlignment(Pos.CENTER);
        loginMenu.getChildren().addAll(username, password, loginButton);

        //Add User Button
        Button addUser = new Button("Add User");
        addUser.setOnAction(e -> {
            // Create the custom dialog.
            Dialog<Pair<String, String>> dialog = new Dialog<>();
            dialog.setTitle("Add User");
            dialog.setHeaderText("Enter User Details");

            // Set the button types.
            ButtonType loginButtonType = new ButtonType("Create User", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

            // Create the username and password labels and fields.
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            TextField newUsername = new TextField();
            newUsername.setPromptText("Username");
            PasswordField newPassword = new PasswordField();
            newPassword.setPromptText("Password");

            grid.add(new Label("Username:"), 0, 0);
            grid.add(newUsername, 1, 0);
            grid.add(new Label("Password:"), 0, 1);
            grid.add(newPassword, 1, 1);

            // Enable/Disable login button depending on whether a username was entered.
            Node confirmAddUserButton = dialog.getDialogPane().lookupButton(loginButtonType);
            confirmAddUserButton.setDisable(true);

            newUsername.textProperty().addListener((observable, oldValue, newValue) -> {
                confirmAddUserButton.setDisable(newValue.trim().isEmpty());
            });

            dialog.getDialogPane().setContent(grid);

            // Request focus on the username field by default.
            Platform.runLater(newUsername::requestFocus);

            // Convert the result to a username-password-pair when the login button is clicked.
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == loginButtonType) {
                    return new Pair<>(newUsername.getText(), newPassword.getText());
                }
                return null;
            });

            Optional<Pair<String, String>> result = dialog.showAndWait();

            result.ifPresent(usernamePassword -> {
                User user = new User(usernamePassword.getKey(), usernamePassword.getValue(), (double) 0);
                DatabaseHelper.addUser(user);
                System.out.println("New User " + usernamePassword.getKey() + " Created.");
            });
        });

        //All together now
        BorderPane loginPane = new BorderPane();
        loginPane.setTop(title);
        loginPane.setCenter(loginMenu);
        loginPane.setBottom(addUser);
        Scene loginScene = new Scene(loginPane, 480, 320);

        //Finish up and show window
        stage.setScene(loginScene);
        stage.setTitle("Chased Banking Server");
        stage.show();
    }

    public static void main(String[] args) {
        DatabaseHelper.createUsersTable();
        launch();
    }
}