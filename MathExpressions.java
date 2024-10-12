package application;

import java.util.Stack;

public class MathExpressions {

	private static final String DIVIDE_BY_ZERO = "Can't divide by zero";
	private static final String NEGATIVE_SQRT = "Can't take square root of negative number"; // New constant for square root

	public static String evaluate(String exp) {
	    Stack<Double> operands = new Stack<>();
	    Stack<Character> operators = new Stack<>();

	    for (int i = 0; i < exp.length(); i++) {
	        char ch = exp.charAt(i);

	        if (Character.isDigit(ch)) {
	            String value = "";
	            while (i < exp.length() && (Character.isDigit(exp.charAt(i)) || exp.charAt(i) == '.')) {
	                value += exp.charAt(i++);
	            }
	            i--;
	            operands.push(Double.parseDouble(value));
	        } else if (ch == '(') {
	            operators.push(ch);
	        } else if (isOperator(ch)) {
	            // Handle factorial and square root operators
	            if (ch == '!') {
	                if (!operands.isEmpty()) {
	                    double num = operands.pop();
	                    operands.push(factorial(num));
	                }
	                continue; // Skip the rest for factorial
	            } else if (ch == '√') {
	                if (!operands.isEmpty()) {
	                    double num = operands.pop();
	                    if (num < 0) return NEGATIVE_SQRT; // Handle negative square root
	                    operands.push(Math.sqrt(num));
	                }
	                continue; // Skip the rest for square root
	            }

	            // Handle other operators
	            while (!operators.isEmpty() && getPrecedence(operators.peek()) >= getPrecedence(ch)) {
	                char op = operators.pop();
	                double num2 = operands.pop();
	                double num1 = operands.pop();
	                Object value = calculate(num1, num2, op);
	                if (value instanceof String) {
	                    return (String) value; // Return the error message
	                }
	                operands.push((double) value);
	            }
	            operators.push(ch);
	        } else if (ch == ')') {
	            while (!operators.isEmpty() && operators.peek() != '(') {
	                char op = operators.pop();
	                double num2 = operands.pop();
	                double num1 = operands.pop();
	                Object value = calculate(num1, num2, op);
	                if (value instanceof String) {
	                    return (String) value; // Return the error message
	                }
	                operands.push((double) value);
	            }
	            operators.pop(); // Pop the '('
	        }
	    }

	    // Final calculations for remaining operators
	    while (!operators.isEmpty()) {
	        char op = operators.pop();
	        double num2 = operands.pop();
	        double num1 = operands.pop();
	        Object value = calculate(num1, num2, op);
	        if (value instanceof String) {
	            return (String) value; // Return the error message
	        }
	        operands.push((double) value);
	    }

	    return operands.pop().toString();
	}

	// Utility method to check if the character is an operator
	private static boolean isOperator(char ch) {
	    return ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '^' || ch == '%' || ch == '!' || ch == '√';
	}


    private static int getPrecedence(char ch) {
        if (ch == '+' || ch == '-')
            return 1;
        else if (ch == '*' || ch == '/')
            return 2;
        else if (ch == '^')
            return 3;
        else if (ch == '(' || ch == ')')
            return 4;
        else
            return 0;
    }

    private static Object calculate(double num1, double num2, char operator) {
        switch (operator) {
            case '+':
                return num1 + num2;
            case '-':
                return num1 - num2;
            case '*':
                return num1 * num2;
            case '/':
                if (num2 == 0)
                    return DIVIDE_BY_ZERO;
                else
                    return num1 / num2;
            case '^':
                return Math.pow(num1, num2);
            case '%':  // Make sure this case exists
                return num1 % num2; // Correctly returns remainder
            default:
                return 0; // Handle unexpected operators
        }
    }

    // Factorial method
    private static double factorial(double num) {
        if (num < 0) {
            return 0; // Factorial is not defined for negative numbers
        }
        double result = 1;
        for (int i = 2; i <= num; i++) {
            result *= i;
        }
        return result;
    }
}
