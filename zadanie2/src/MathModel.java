import java.util.Stack;

public class MathModel {
    public MathModel() {
    }

    public double calculate(String expression) {
        try {
            if (!checkParentheses(expression)) {
                throw new IllegalArgumentException("Несбалансированные скобки в выражении");
            }
            String postfix = this.infixToPostfix(preprocessExpression(expression.replaceAll("\\s+", "")));
            return this.evaluatePostfix(postfix);
        } catch (Exception var3) {
            System.out.println("Ошибка: " + var3.getMessage());
            return Double.NaN;
        }
    }

    private String preprocessExpression(String expression) {
        // Обработка отрицательных чисел и функций
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (c == '-' && (i == 0 || expression.charAt(i - 1) == '(' || isOperator(expression.charAt(i - 1)))) {
                sb.append("(0-");
                i++;
                while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    sb.append(expression.charAt(i));
                    i++;
                }
                sb.append(")");
                i--;
            } else if (c == 'e' && i + 2 < expression.length() && expression.substring(i, i+3).equals("exp")) {
                sb.append("exp");
                i += 2;
            } else if (c == 'l' && i + 2 < expression.length() && expression.substring(i, i+3).equals("log")) {
                sb.append("log");
                i += 2;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private boolean checkParentheses(String expression) {
        Stack<Character> stack = new Stack<>();
        for (char c : expression.toCharArray()) {
            if (c == '(') {
                stack.push(c);
            } else if (c == ')') {
                if (stack.isEmpty()) return false;
                stack.pop();
            }
        }
        return stack.isEmpty();
    }

    private String infixToPostfix(String expression) {
        StringBuilder output = new StringBuilder();
        Stack<String> operators = new Stack<>();
        int i = 0;

        while (i < expression.length()) {
            char c = expression.charAt(i);

            if (Character.isDigit(c) || c == '.') {
                output.append(c);
                i++;
            } else if (c == 'e' && i + 2 < expression.length() && expression.substring(i, i+3).equals("exp")) {
                operators.push("exp");
                i += 3;
            } else if (c == 'l' && i + 2 < expression.length() && expression.substring(i, i+3).equals("log")) {
                operators.push("log");
                i += 3;
            } else if (this.isOperator(c)) {
                output.append(' ');
                if (c == '*' && i + 1 < expression.length() && expression.charAt(i + 1) == '*') {
                    while (!operators.isEmpty() && precedence(operators.peek()) >= precedence("**")) {
                        output.append(operators.pop()).append(' ');
                    }
                    operators.push("**");
                    i += 2;
                } else {
                    while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(String.valueOf(c))) {
                        output.append(operators.pop()).append(' ');
                    }
                    operators.push(String.valueOf(c));
                    i++;
                }
            } else if (c == '(') {
                operators.push(String.valueOf(c));
                i++;
            } else if (c == ')') {
                while (!operators.isEmpty() && !operators.peek().equals("(")) {
                    output.append(' ').append(operators.pop());
                }
                operators.pop();
                i++;
            } else if (c == '!') {
                output.append(' ').append(c);
                i++;
            } else {
                i++;
            }
        }

        while (!operators.isEmpty()) {
            output.append(' ').append(operators.pop());
        }

        return output.toString();
    }

    private double evaluatePostfix(String postfix) {
        Stack<Double> numbers = new Stack<>();
        String[] tokens = postfix.split("\\s+");

        for (String token : tokens) {
            if (!token.isEmpty()) {
                if (Character.isDigit(token.charAt(0)) || (token.length() > 1 && token.charAt(0) == '-')) {
                    numbers.push(Double.parseDouble(token));
                } else if (this.isOperator(token)) {
                    double b = numbers.pop();
                    double a = numbers.pop();
                    numbers.push(this.applyOperation(token, a, b));
                } else if (token.equals("exp")) {
                    numbers.push(Math.exp(numbers.pop()));
                } else if (token.equals("log")) {
                    numbers.push(Math.log(numbers.pop()) / Math.log(2));
                } else if (token.equals("!")) {
                    numbers.push(factorial(numbers.pop()));
                }
            }
        }

        return numbers.pop();
    }

    private double factorial(double n) {
        if (n < 0) throw new IllegalArgumentException("Факториал отрицательного числа не определен");
        if (n == 0 || n == 1) return 1;
        double result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    private boolean isOperator(String token) {
        return token.equals("+") || token.equals("-") || token.equals("*") ||
                token.equals("/") || token.equals("^") || token.equals("**") ||
                token.equals("//");
    }

    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '^';
    }

    private double applyOperation(String operator, double a, double b) {
        switch (operator) {
            case "+":
                return a + b;
            case "-":
                return a - b;
            case "*":
                return a * b;
            case "/":
                return a / b;
            case "**":
            case "^":
                return Math.pow(a, b);
            case "//":
                return (double)((int)(a / b));
            default:
                throw new IllegalArgumentException("Неизвестный оператор: " + operator);
        }
    }

    private int precedence(String operator) {
        switch (operator) {
            case "+":
            case "-":
                return 1;
            case "*":
            case "/":
            case "//":
                return 2;
            case "**":
            case "^":
                return 3;
            case "exp":
            case "log":
                return 4;
            default:
                return 0;
        }
    }
}