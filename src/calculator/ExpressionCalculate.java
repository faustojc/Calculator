package calculator;

import java.text.DecimalFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

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
    
    private List<String> Postfix(String expression) {
        ArrayDeque<String> op = new ArrayDeque<>();
        List<String> result = new ArrayList<>();
        int i = 0;
        
        while (i < expression.length()) {
            String s = Character.toString(expression.charAt(i));

            // check for the urinary operator
            if (s.equals("s") || s.equals("c") || s.equals("t")) {
                op.add(pushValuesOrUrinaryOp(expression, i));
                i = iter;
            }
            // check if it is operator
            else if (checkOperatorAndPrec(s) > 0) {
                while (!op.isEmpty() && checkOperatorAndPrec(op.getLast()) >= checkOperatorAndPrec(s)) {
                    result.add(op.removeLast());
                }
                op.addLast(s);
                i++;
            }
            // check for negative or just an open parenthesis
            else if (s.equals("(")) {
                if (expression.charAt(i+1) == '-') {
                    result.add(pushNegative(expression, i));
                    i = iter;
                }
                else {
                    op.addLast(s);
                    i++;
                }
            }
            else if (s.equals(")")) {
                String x = op.removeLast();
                while (!x.equals("(")) {
                    result.add(x);
                    x = op.removeLast();
                }
                i++;
            }
            // append the values 
            else {
                result.add(pushValuesOrUrinaryOp(expression, i));
                i = iter;
            }
        }
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
     * Get the value or the urinary operators to the resulta
     * @param input The expression for iteration
     * @param i Index for iterate the expression
     * @return The whole value, decimal value, or the urinary operator
     */
    private String pushValuesOrUrinaryOp(String input, int i) {
        StringBuilder sb = new StringBuilder();
        iter = i;
        String t = Character.toString(input.charAt(iter));

        while (checkOperatorAndPrec(t) < 0 && !t.equals(")") && !t.equals("(")) {
            sb.append(t);
            iter++;

            if (iter >= input.length()) break;
            else t = Character.toString(input.charAt(iter)); 
        }
        return sb.toString();
    }

    /**
     * Returns the negative value in the inputted expression
     * @param input The inputted expression
     * @param i Index of getting the value
     * @return The negative value in string from the inputted expression
     */
    private String pushNegative(String input, int i) {
        StringBuilder strB = new StringBuilder();
        iter = i;

        String temp = Character.toString(input.charAt(++iter));
        // Append the negative value first
        strB.append(temp).append(Character.toString(input.charAt(++iter))); 
        iter++;

        if (checkOperatorAndPrec(Character.toString(input.charAt(iter))) < 0) {
            temp = Character.toString(input.charAt(iter));
        }

        while (checkOperatorAndPrec(temp) < 0 && !temp.equals(")")) {
            strB.append(temp);
            temp = Character.toString(input.charAt(++iter));
        }
        iter++;
        return strB.toString();
    }
    
    /**
     * Returns {@code true} if it's an operator
     * @param op The operator
     * @return true if it's an operator, false if it's not
     */
    private static boolean isOperator(String op) {
        return (op.equals("+") || op.equals("-") || op.equals("*") || op.equals("/") || op.equals("^"));
    }
    
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
            else if (postfix.get(i).contains("sin") || postfix.get(i).contains("cos") || postfix.get(i).contains("tan")) {
                ad.addLast(calcTrigo(postfix.get(i), ad.removeLast()));
            }
            else if (postfix.get(i).contains("sqrt")) {
                ad.addLast(Math.sqrt(ad.removeLast()));
            }
            else {
                ad.addLast(Double.parseDouble(postfix.get(i)));
            }
        }
        this.answer = new DecimalFormat("0.#####").format(ad.getLast());
    }
}
