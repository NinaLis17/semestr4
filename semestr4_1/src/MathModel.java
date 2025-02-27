import java.util.Stack;

public class MathModel {
    // Метод для вычисления выражения
    public double calculate(String expression) {
        try {
            // Удаляем пробелы и преобразуем выражение в постфиксную форму (Обратная польская запись)
            String postfix = infixToPostfix(expression.replaceAll("\\s+", ""));
            return evaluatePostfix(postfix);
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
            return Double.NaN; // Возвращаем NaN в случае ошибки
        }
    }

    // Преобразование инфиксной записи в постфиксную
    private String infixToPostfix(String expression) {
        StringBuilder output = new StringBuilder();
        Stack<String> operators = new Stack<>();
        int i = 0;
        while (i < expression.length()) {
            char c = expression.charAt(i);

            // Если символ - число или точка (часть числа)
            if (Character.isDigit(c) || c == '.') {
                output.append(c);
                i++;
            }
            // Если символ - оператор
            else if (isOperator(c)) {
                output.append(' '); // Разделитель для чисел

                // Обработка унарного минуса (отрицательных чисел)
                if (c == '-' && (i == 0 || expression.charAt(i - 1) == '(')) {
                    output.append("0 "); // Добавляем 0 перед унарным минусом
                }

                // Проверяем, является ли оператор "//"
                if (i + 1 < expression.length() && c == '/' && expression.charAt(i + 1) == '/') {
                    while (!operators.isEmpty() && precedence(operators.peek()) >= precedence("//")) {
                        output.append(operators.pop()).append(' ');
                    }
                    operators.push("//");
                    i += 2; // Пропускаем два символа
                } else {
                    while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(String.valueOf(c))) {
                        output.append(operators.pop()).append(' ');
                    }
                    operators.push(String.valueOf(c));
                    i++;
                }
            }
            // Если символ - открывающая скобка
            else if (c == '(') {
                operators.push(String.valueOf(c));
                i++;
            }
            // Если символ - закрывающая скобка
            else if (c == ')') {
                while (!operators.isEmpty() && !operators.peek().equals("(")) {
                    output.append(' ').append(operators.pop());
                }
                operators.pop(); // Удаляем открывающую скобку
                i++;
            } else {
                i++; // Пропускаем неизвестные символы
            }
        }

        // Добавляем оставшиеся операторы
        while (!operators.isEmpty()) {
            output.append(' ').append(operators.pop());
        }

        return output.toString();
    }

    // Вычисление постфиксного выражения
    private double evaluatePostfix(String postfix) {
        Stack<Double> numbers = new Stack<>();
        String[] tokens = postfix.split("\\s+");

        for (String token : tokens) {
            if (token.isEmpty()) continue;

            // Если токен - число
            if (Character.isDigit(token.charAt(0)) || (token.length() > 1 && token.charAt(0) == '-')) {
                numbers.push(Double.parseDouble(token));
            }
            // Если токен - оператор
            else if (isOperator(token)) {
                double b = numbers.pop();
                double a = numbers.pop();
                numbers.push(applyOperation(token, a, b));
            }
        }

        return numbers.pop();
    }

    // Проверка, является ли строка оператором
    private boolean isOperator(String token) {
        return token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/") || token.equals("^") || token.equals("//");
    }

    // Проверка, является ли символ оператором
    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '^';
    }

    // Применение операции
    private double applyOperation(String operator, double a, double b) {
        switch (operator) {
            case "+": return a + b;
            case "-": return a - b;
            case "*": return a * b;
            case "/": return a / b;
            case "^": return Math.pow(a, b);
            case "//": return (int) (a / b); // Деление без остатка
            default: throw new IllegalArgumentException("Неизвестный оператор: " + operator);
        }
    }

    // Определение приоритета оператора
    private int precedence(String operator) {
        switch (operator) {
            case "+":
            case "-":
                return 1;
            case "*":
            case "/":
            case "//":
                return 2;
            case "^":
                return 3;
            default:
                return 0;
        }
    }
}