package application;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;

public class Main extends Application {
    private TextField textField = new TextField();
    private boolean start = true;

    public void start(Stage primaryStage) throws Exception {
        textField.setPrefHeight(50);
        textField.setFont(Font.font(20));
        textField.setEditable(false);
        textField.setAlignment(Pos.CENTER_RIGHT);

        StackPane stackPane = new StackPane();
        stackPane.setPadding(new Insets(10));
        stackPane.getChildren().add(textField);

        FlowPane pane = new FlowPane();
        pane.setHgap(10);
        pane.setVgap(5);
        pane.setAlignment(Pos.TOP_CENTER);

        // Create calculator buttons
        pane.getChildren().add(createButton("C", this::processClear));
        pane.getChildren().add(createButton("B", this::processBackspace));
        pane.getChildren().add(createButton(".", this::processNumber));
        pane.getChildren().add(createButton("=", this::processOperator));

        pane.getChildren().add(createButton("7", this::processNumber));
        pane.getChildren().add(createButton("8", this::processNumber));
        pane.getChildren().add(createButton("9", this::processNumber));
        pane.getChildren().add(createButton("/", this::processOperator));

        pane.getChildren().add(createButton("4", this::processNumber));
        pane.getChildren().add(createButton("5", this::processNumber));
        pane.getChildren().add(createButton("6", this::processNumber));
        pane.getChildren().add(createButton("*", this::processOperator));

        pane.getChildren().add(createButton("1", this::processNumber));
        pane.getChildren().add(createButton("2", this::processNumber));
        pane.getChildren().add(createButton("3", this::processNumber));
        pane.getChildren().add(createButton("-", this::processOperator));

        pane.getChildren().add(createButton("0", this::processNumber));
        pane.getChildren().add(createButton("+", this::processOperator));
        pane.getChildren().add(createButton("%", this::processOperator)); // Modulus button
        pane.getChildren().add(createButton("!", this::processFactorial)); // Factorial button
        
        pane.getChildren().add(createButton("√", this::processOperator));
        pane.getChildren().add(createButton("(", this::processOperator)); // Opening parenthesis
        pane.getChildren().add(createButton(")", this::processOperator)); // Closing parenthesis
     // Add this line in the pane.getChildren() section to create the exponent button
        pane.getChildren().add(createButton("^", this::processOperator)); // Exponent button


        

        BorderPane root = new BorderPane();
        root.setTop(stackPane);
        root.setCenter(pane);
        Scene scene = new Scene(root, 270, 320);
        primaryStage.setScene(scene);
        primaryStage.setTitle("My Calculator");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private Button createButton(String label, javafx.event.EventHandler<ActionEvent> handler) {
        Button button = new Button(label);
        button.setPrefSize(50, 40);
        button.setFont(Font.font(18));
        button.setOnAction(handler);
        return button;
    }

    private void processNumber(ActionEvent e) {
        if (start) {
            textField.setText("");
            start = false;
        }
        Button button = (Button) e.getSource();
        String value = button.getText();

        boolean decimalExists = false;
        if (value.equals(".")) {
            // Check if point already exists
            String exp = textField.getText();
            for (int i = exp.length() - 1; i >= 0; i--) {
                char ch = exp.charAt(i);
                if (ch == '.') {
                    decimalExists = true;
                } else if (Character.isDigit(ch)) {
                    continue;
                } else {
                    break;
                }
            }
        }
        if (decimalExists)
            return;
        else
            textField.setText(textField.getText() + value);
    }

    private void processOperator(ActionEvent e) {
        Button button = (Button) e.getSource();
        String value = button.getText();
        String text = textField.getText();

        // Handle starting with parentheses
        if (start) {
            textField.setText("");
            start = false;
        }

        // Ensure we can only add operators after digits or parentheses
        if (!text.isEmpty() || value.equals("√") || value.equals("(")) {
            char lastChar = text.isEmpty() ? ' ' : text.charAt(text.length() - 1); // Handle empty text case

            // Check for the case of adding a new operator or parenthesis
            if (isOperator(lastChar) && lastChar != '!') {
                // If the last character is an operator (but not factorial)
                if (value.equals("(")) {
                    // Allow adding the opening parenthesis without replacing the last operator
                    textField.setText(text + value);
                }
                // If the new value is not a parenthesis, just ignore it (no action taken)
                return; 
            }

            // Add the operator or parentheses
            if (!value.equals("=")) {
                textField.setText(textField.getText() + value);
            } else {
                // Calculate the expression
                String exp = textField.getText();
                String result = MathExpressions.evaluate(exp); // Evaluate the entire expression
                textField.setText(result);
                start = true;
            }
        }
    }



    private void processFactorial(ActionEvent e) {
        String text = textField.getText();
        if (!text.isEmpty()) {
            // Append '!' symbol, don't compute immediately
            textField.setText(text + "!");
        }
    }


    private long factorial(int n) {
        if (n == 0) return 1;
        return n * factorial(n - 1);
    }

    private void processClear(ActionEvent e) {
        textField.setText("");
        start = true;
    }

    private boolean isOperator(char ch) {
        return ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '^' || ch == '%' || ch == '!'; // Include '!' here
    }

    private void processBackspace(ActionEvent e) {
        String text = textField.getText();
        if (!text.isEmpty()) {
            text = text.substring(0, text.length() - 1);
            textField.setText(text);
        }
    }

    private String filterExpression(String exp) {
        // Remove trailing operator, e.g., "2+" becomes "2"
        if (!exp.isEmpty() && isOperator(exp.charAt(exp.length() - 1))) {
            exp = exp.substring(0, exp.length() - 1);
        }

        // Ensure a decimal point is preceded by a digit, e.g., "2+." becomes "2+0."
        for (int i = 1; i < exp.length(); i++) {
            if (exp.charAt(i) == '.' && isOperator(exp.charAt(i - 1))) {
                String str1 = exp.substring(0, i);
                String str2 = exp.substring(i);
                exp = str1 + "0" + str2;
            }
        }

        return exp;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
