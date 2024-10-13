package com.example.demo1;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
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
        HBox.setMargin(label, new Insets(10, 0, 10, 0));
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
        GridPane.setConstraints(dollarSign, 0, 0);
        TextField amount = new TextField();
        GridPane.setMargin(amount, new Insets(5));
        GridPane.setConstraints(amount, 1,0);
        GridPane.setColumnSpan(amount, 2);
        amount.setTextFormatter(doubleTextFormatter);
        amount.setMaxWidth(256);
        amount.setPromptText("Amount");

        //Deposit and Withdrawal buttons
        Button deposit = new Button("Deposit");
        GridPane.setMargin(deposit, new Insets(5));
        GridPane.setConstraints(deposit, 1,1);
        Button withdrawal = new Button("Withdrawal");
        GridPane.setMargin(withdrawal, new Insets(5));
        GridPane.setConstraints(withdrawal, 2, 1);


        //Balance Display
        Label balanceLabel = new Label("Current Balance:");
        Text balanceAmount = new Text("$" + balance);
        balanceAmount.setFont(Font.font("Minecraft", 64));
        balanceAmount.setFill(Color.GREEN);
        VBox right = new VBox(balanceLabel, balanceAmount);
        right.setAlignment(Pos.CENTER);

        //Put all the left side things in a Grid Pane
        GridPane left = new GridPane();
        left.getChildren().addAll(dollarSign, amount, deposit, withdrawal);
        left.setAlignment(Pos.CENTER);

        //Final Pane
        SplitPane bankSplitPane = new SplitPane();

        final StackPane sp1 = new StackPane(left);
        final StackPane sp2 = new StackPane(right);
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

        Scene mainMenuScene = new Scene(mainBank, 960, 640);
        mainMenuScene.getRoot().setStyle("-fx-font-family: Minecraft; -fx-font-size: 14;");


        //Login Interface scene

        //Title
        Label titleText = new Label("Chased Inc. Banking Server");
        titleText.setFont(Font.font(32));
        HBox.setMargin(titleText, new Insets(20));
        HBox title = new HBox(titleText);
        title.setAlignment(Pos.CENTER);

        //Login fields and button
        TextField username = new TextField();
        GridPane.setMargin(username, new Insets(5, 5, 5, 5));
        GridPane.setConstraints(username, 1, 0);
        username.setMaxWidth(256);
        username.setPromptText("Username");

        Label usernameLabel = new Label("Username: ");
        GridPane.setHalignment(usernameLabel, HPos.RIGHT);
        GridPane.setConstraints(usernameLabel, 0,0);

        PasswordField password = new PasswordField();
        GridPane.setMargin(password, new Insets(5, 5, 5, 5));
        GridPane.setConstraints(password, 1,1);
        password.setMaxWidth(256);
        password.setPromptText("Password");

        Label passwordLabel = new Label("Password: ");
        GridPane.setHalignment(passwordLabel, HPos.RIGHT);
        GridPane.setConstraints(passwordLabel, 0,1);

        Button loginButton = new Button(">");
        GridPane.setMargin(loginButton, new Insets(5, 5, 5, 5));
        GridPane.setConstraints(loginButton, 2,1);
        loginButton.setOnAction(e -> {
            User retrievedUser = DatabaseHelper.getUser(username.getText());
            if (retrievedUser != null) {
                if (Objects.equals(retrievedUser.getPassword(), password.getText())) {
                    balance = retrievedUser.getBalance();
                    balanceAmount.setText("$" + balance);
                    loggedInUser = retrievedUser.getUsername();
                    label.setText("Welcome to Chased Bank, " + loggedInUser + "!");
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

        ColumnConstraints col1 = new ColumnConstraints();
        ColumnConstraints col2 = new ColumnConstraints();
        ColumnConstraints col3 = new ColumnConstraints();

        col1.setPercentWidth(25);  // 25% width for the first column
        col2.setPercentWidth(50);  // 50% width for the center column with the TextField
        col3.setPercentWidth(25);  // 25% width for the third column

        GridPane loginMenu = new GridPane();
        loginMenu.getChildren().addAll(usernameLabel, passwordLabel, username, password, loginButton);
        loginMenu.getColumnConstraints().addAll(col1, col2, col3);
        loginMenu.setMaxWidth(400);
        loginMenu.setAlignment(Pos.CENTER);

        //Add User Button
        Button addUser = new Button("Add User");
        HBox.setMargin(addUser, new Insets(10, 10, 10, 10));
        HBox addUserHBox = new HBox(addUser);
        addUserHBox.setAlignment(Pos.BOTTOM_RIGHT);
        addUser.setOnAction(e -> {
            // Create the dialog.
            Dialog<Pair<String, String>> dialog = new Dialog<>();
            dialog.setTitle("Add User");
            dialog.setHeaderText("Enter User Details");
            dialog.getDialogPane().setStyle("-fx-font-family: Minecraft; -fx-font-size: 14;");


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
        loginPane.setBottom(addUserHBox);
        Scene loginScene = new Scene(loginPane, 960, 640);
        loginScene.getRoot().setStyle("-fx-font-family: Minecraft; -fx-font-size: 14;");


        //Finish up and show window
        stage.setScene(loginScene);
        stage.setTitle("Chased Banking Server");
        stage.setMinWidth(600);
        stage.setMinHeight(300);
        stage.show();
    }

    public static void main(String[] args) {
        System.setProperty("prism.text", "t2k");
        DatabaseHelper.createUsersTable();
        launch(args);
    }
}