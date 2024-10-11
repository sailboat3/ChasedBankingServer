package com.example.demo1;

import javafx.application.Application;
import javafx.geometry.Pos;
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
import javafx.util.converter.DoubleStringConverter;

import java.io.IOException;

public class HelloApplication extends Application {

    private double balance = 0;

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
        Label balanceLabel = new Label("Balance");
        Text balanceAmount = new Text("$" + Double.toString(balance));
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
                balanceAmount.setText("$" + balance);
            } catch (NumberFormatException ex) {
                System.out.println("Invalid input: " + amount.getText());
            }
        });
        withdrawal.setOnAction(e -> {
            try {
                balance -= Double.parseDouble(amount.getText());
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
        loginButton.setOnAction(e -> stage.setScene(mainMenuScene));
        VBox loginMenu = new VBox();
        loginMenu.setAlignment(Pos.CENTER);
        loginMenu.getChildren().addAll(username, password, loginButton);

        //All together now
        BorderPane loginPane = new BorderPane();
        loginPane.setTop(title);
        loginPane.setCenter(loginMenu);
        Scene loginScene = new Scene(loginPane, 480, 320);

        //Finish up and show window
        stage.setScene(loginScene);
        stage.setTitle("Chased Banking Server");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}