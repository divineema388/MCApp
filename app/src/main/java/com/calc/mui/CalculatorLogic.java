package com.calc.mui;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class CalculatorLogic {

    private String currentNumber = "0";
    private String operand = ""; // +, -, *, /, %
    private BigDecimal firstOperand = BigDecimal.ZERO;
    private boolean newNumberStarted = true; // True if a new number is being entered or result is shown
    private boolean decimalAdded = false;

    private static final int MAX_DISPLAY_DIGITS = 12; // Adjust as needed
    private static final MathContext MC = new MathContext(15, RoundingMode.HALF_UP); // For precision

    public String getCurrentDisplay() {
        return currentNumber;
    }

    public void appendDigit(String digit) {
        if (newNumberStarted) {
            currentNumber = digit;
            newNumberStarted = false;
            decimalAdded = (digit.equals(".")); // If first char is dot, decimal is true
        } else {
            if (currentNumber.length() >= MAX_DISPLAY_DIGITS && !decimalAdded) {
                // Prevent adding more digits if maxed out and no decimal yet
                return;
            }
            if (digit.equals(".") && decimalAdded) {
                return; // Only one decimal point allowed
            }
            currentNumber += digit;
            if (digit.equals(".")) {
                decimalAdded = true;
            }
        }
        // Remove leading zero if present and not a decimal
        if (currentNumber.length() > 1 && currentNumber.startsWith("0") && !currentNumber.startsWith("0.")) {
            currentNumber = currentNumber.substring(1);
        }
    }

    public void setOperand(String newOperand) {
        if (!newNumberStarted && !operand.isEmpty()) {
            // If an operand was already set and a number was entered, calculate immediately
            calculateResult();
        }

        if (!currentNumber.isEmpty() && !currentNumber.equals(".")) {
            firstOperand = new BigDecimal(currentNumber, MC);
        } else {
            // If currentNumber is empty or just a dot, default to 0
            firstOperand = BigDecimal.ZERO;
        }

        this.operand = newOperand;
        newNumberStarted = true;
        decimalAdded = false;
    }

    public void calculateResult() {
        if (operand.isEmpty() || newNumberStarted) {
            // No operation to perform or no second number entered yet
            return;
        }

        BigDecimal secondOperand;
        if (!currentNumber.isEmpty() && !currentNumber.equals(".")) {
            secondOperand = new BigDecimal(currentNumber, MC);
        } else {
            secondOperand = BigDecimal.ZERO; // Default to 0 if currentNumber is empty or just a dot
        }

        BigDecimal result = BigDecimal.ZERO;
        boolean error = false;

        try {
            switch (operand) {
                case "+":
                    result = firstOperand.add(secondOperand, MC);
                    break;
                case "-":
                    result = firstOperand.subtract(secondOperand, MC);
                    break;
                case "×": // Multiplication symbol
                    result = firstOperand.multiply(secondOperand, MC);
                    break;
                case "÷": // Division symbol
                    if (secondOperand.compareTo(BigDecimal.ZERO) == 0) {
                        error = true; // Division by zero
                    } else {
                        result = firstOperand.divide(secondOperand, MC);
                    }
                    break;
                case "%": // Percentage operation, maybe (firstOperand / 100) * secondOperand or firstOperand * secondOperand / 100
                    // For a standard calculator, '%' usually means (number / 100).
                    // Or, if used after an operator, it can mean (firstOperand * (secondOperand/100))
                    // Let's implement it as (currentNumber / 100) or (firstOperand * secondOperand / 100)
                    // For simplicity, let's treat it as a unary operator applied to currentNumber
                    result = secondOperand.divide(new BigDecimal("100"), MC);
                    break;
            }
        } catch (ArithmeticException e) {
            error = true;
        }

        if (error) {
            currentNumber = "Error";
            firstOperand = BigDecimal.ZERO;
            operand = "";
            newNumberStarted = true;
            decimalAdded = false;
        } else {
            // Format result to remove trailing .0 if integer
            String formattedResult = result.stripTrailingZeros().toPlainString();
            if (formattedResult.length() > MAX_DISPLAY_DIGITS && formattedResult.contains(".")) {
                // Try to shorten decimal part if too long
                if (formattedResult.indexOf(".") < MAX_DISPLAY_DIGITS) {
                    formattedResult = result.round(new MathContext(MAX_DISPLAY_DIGITS, RoundingMode.HALF_UP)).stripTrailingZeros().toPlainString();
                } else {
                    // If integer part is too long, show scientific notation
                    formattedResult = result.toEngineeringString(); // Or try to truncate
                }
            } else if (formattedResult.length() > MAX_DISPLAY_DIGITS) {
                 formattedResult = result.toEngineeringString();
            }

            currentNumber = formattedResult;
            firstOperand = result; // Set result as first operand for chained calculations
            operand = ""; // Reset operand
            newNumberStarted = true; // Ready for new input or next operation
            decimalAdded = currentNumber.contains(".");
        }
    }

    public void clear() {
        currentNumber = "0";
        operand = "";
        firstOperand = BigDecimal.ZERO;
        newNumberStarted = true;
        decimalAdded = false;
    }

    public void toggleSign() {
        if (!currentNumber.equals("0") && !currentNumber.equals("Error")) {
            if (currentNumber.startsWith("-")) {
                currentNumber = currentNumber.substring(1);
            } else {
                currentNumber = "-" + currentNumber;
            }
        }
    }

    // Handles the percentage button
    // If it's the first number, it makes it (number / 100)
    // If it's after an operator, it computes (firstOperand * (currentNumber / 100))
    public void calculatePercentage() {
        if (currentNumber.isEmpty() || currentNumber.equals(".")) {
            return;
        }
        BigDecimal value = new BigDecimal(currentNumber, MC);

        if (operand.isEmpty()) { // No pending operation, just current number / 100
            value = value.divide(new BigDecimal("100"), MC);
        } else { // Operation pending, e.g., 50 + 10% (means 50 + (50 * 0.10))
            BigDecimal percentageValue = firstOperand.multiply(value.divide(new BigDecimal("100"), MC), MC);
            switch (operand) {
                case "+":
                    value = firstOperand.add(percentageValue, MC);
                    break;
                case "-":
                    value = firstOperand.subtract(percentageValue, MC);
                    break;
                case "×":
                    value = firstOperand.multiply(value.divide(new BigDecimal("100"), MC), MC); // 50 * 10% = 50 * 0.1
                    break;
                case "÷":
                    if (value.compareTo(BigDecimal.ZERO) == 0) {
                        currentNumber = "Error"; // Avoid division by zero with percentage
                        firstOperand = BigDecimal.ZERO;
                        operand = "";
                        newNumberStarted = true;
                        decimalAdded = false;
                        return;
                    }
                    value = firstOperand.divide(value.divide(new BigDecimal("100"), MC), MC); // 50 / 10% = 50 / 0.1
                    break;
            }
        }
        currentNumber = value.stripTrailingZeros().toPlainString();
        // Reset for next operation after percentage calculation
        firstOperand = value;
        operand = "";
        newNumberStarted = true;
        decimalAdded = currentNumber.contains(".");
    }
}