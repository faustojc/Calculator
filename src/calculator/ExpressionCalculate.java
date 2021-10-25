package calculator;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.text.DecimalFormat;

/**
 *
 * @author Fausto JC Boko
 */
public class ExpressionCalculate {
    private int iter = 0;
    private String answer = "";

    public String getResult(String expression) {
        calculateExpression(Postfix(expression));
        return this.answer;
    }
    
    /**
    * Converting the arithmetic expression(Infix) into Postfix expression
    * @param expression The expression inputted 
    * @return The postfix expression
    */
    private List<String> Postfix(String expression) {
        ArrayDeque<String> op = new ArrayDeque<>();
        List<String> result = new ArrayList<>();
        int i = 0;
        
        while (i < expression.length()) {
            StringBuilder s = new StringBuilder(Character.toString(expression.charAt(i)));

            // check for the unary operator
            if (s.toString().equals("s") || s.toString().equals("c") || s.toString().equals("t")) {
                op.add(specialOp(expression, i));
                i = iter;
            }
            // check if it is operator
            else if (checkOperatorAndPrec(s.toString()) > 0) {
                while (!op.isEmpty() && checkOperatorAndPrec(op.getLast()) >= checkOperatorAndPrec(s.toString())) {
                    result.add(op.removeLast());
                }
                op.addLast(s.toString());
                i++;
            }
            // check for negative or just an open parenthesis
            else if (s.toString().equals("(")) {
                if (expression.charAt(i+1) == '-') {
                    result.add(pushNegative(expression, i));
                    i = iter;
                }
                else {
                    op.addLast(s.toString());
                    i++;
                }
            }
            else if (s.toString().equals(")")) {
                String x = op.removeLast();
                while (!x.equals("(")) {
                    result.add(x);
                    x = op.removeLast();
                }
                i++;
            }
            // append the values 
            else {
                result.add(pushValues(s, expression, i));
                i = iter;
            }
        }
        // Add the remaining operator(s) into the result
        while (!op.isEmpty()) {
            result.add(op.removeLast());
        }
        return result;
    }
     
    /**
     * Check for the highest operator precedence if and only if it's an operator. Returns -1 if the {@code op} is not an operator
     * @param op The operator to be checked
     * @return The value of the {@code op}
     */
    private int checkOperatorAndPrec(String op) {
        switch (op) {
            case "+", "-" -> { return 1; }
            case "*", "/" -> { return 2; }
            case "^" -> { return 3; }
            default -> {  break; }
        }
        return -1;
    }
    
    /**
     * Pushing values to the result of List String
     * @param s The current StringBuilder in getting the index of expression
     * @param expression Getting the lenght of the expression
     * @param i Index for iterate the expression
     * @return The whole value or decimal value of expression
     */
    private String pushValues(StringBuilder s, String input, int i) {
        StringBuilder sb = new StringBuilder();
        StringBuilder sbToGet = s;
        iter = i;

        while (checkOperatorAndPrec(sbToGet.toString()) < 0 && !sbToGet.toString().equals(")") && !sbToGet.toString().equals("(")) {
            sb.append(sbToGet);
            iter++;

            if (iter >= input.length()) break;
            else sbToGet = new StringBuilder(Character.toString(input.charAt(iter))); 
        }
        return sb.toString();
    }

    /**
     * Returns the negative value in the expression
     * @param input The expression
     * @param i Index of getting the value
     * @return The negative value in string from the expression
     */
    private String pushNegative(String input, int i) {
        StringBuilder strB = new StringBuilder();
        iter = i;

        String tempS = Character.toString(input.charAt(++iter));
        // Append the negative value first
        strB.append(tempS).append(Character.toString(input.charAt(++iter))); 
        iter++;

        if (checkOperatorAndPrec(Character.toString(input.charAt(iter))) < 0) {
            tempS = Character.toString(input.charAt(iter));
        }

        while (checkOperatorAndPrec(tempS) < 0 && !tempS.equals(")")) {
            strB.append(tempS);
            tempS = Character.toString(input.charAt(++iter));
        }
        iter++;
        return strB.toString();
    }

    /**
     * Finds and return for special operator like sin, cos, tan for trigonometry and the square root
     * @param input The expression
     * @param i Index of the {@code expression}
     * @return The special operator
     */
    private String specialOp(String input, int i) {
        StringBuilder sb = new StringBuilder();
        iter = i;
        String t = Character.toString(input.charAt(iter));
            
        while (checkOperatorAndPrec(t) < 0 && !t.equals("(")) {
            sb.append(t);
            iter++;

            if (iter >= input.length()) break;
            else t = Character.toString(input.charAt(iter));
        }
        return sb.toString();
    }
    
    /**
     * Returns {@code true} if it's an operator
     * @param op The operator
     * @return true if and only if it's an operator
     */
    private static boolean isOperator(String op) {
        return (op.equals("+") || op.equals("-") || op.equals("*") || op.equals("/") || op.equals("^"));
    }
    
    /**
    * Returns the calculated trigonometry value
    * @param trigo Getting the sin, cos, and tan
    * @param num The value from the trigo
    * @return The calculated trigo decimal value
    */
    private double calcTrigo(String trigo, double num) {
        double value = 0;
        switch (trigo) {
            case "sin" -> value = Math.sin(num);
            case "cos" -> value = Math.cos(num);
            case "tan" -> value = Math.tan(num);
            default -> {break;}
        }
        return value;
    }
    
    /**
    * Calculating the postfix expression
    * @param postfix The postfix expression
    * @return The calculated value of the postfix expression
    */
    private void calculateExpression(List<String> postfix) {
        ArrayDeque<Double> ad = new ArrayDeque<>();
        
        for (int i = 0; i < postfix.size(); i++) {
            if (isOperator(postfix.get(i))) {
                double val1 = ad.removeLast();
                double val2 = ad.removeLast();

                switch (postfix.get(i)) {
                    case "+" -> ad.addLast(val2 + val1);
                    case "-" -> ad.addLast(val2 - val1);
                    case "*" -> ad.addLast(val2 * val1);
                    case "/" -> ad.addLast(val2 / val1);
                    case "^" -> ad.addLast(java.lang.Math.pow(val2, val1));
                    default -> {break;}
                }
            }
            // Check if it's a trigo
            else if (postfix.get(i).contains("sin") || postfix.get(i).contains("cos") || postfix.get(i).contains("tan")) {
                ad.addLast(calcTrigo(postfix.get(i), ad.removeLast()));
            }
            // Check if it's a square root
            else if (postfix.get(i).contains("sqrt")) {
                ad.addLast(Math.sqrt(ad.removeLast()));
            }
            else {
                ad.addLast(Double.parseDouble(postfix.get(i))); // append the values to be calculated
            }
        }
        this.answer = new DecimalFormat("0.#####").format(ad.getLast());
    }
}
