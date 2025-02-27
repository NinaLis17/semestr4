public class MathController {
    private MathModel model;

    public MathController() {
        this.model = new MathModel();
    }

    // Метод для обработки выражения
    public String processExpression(String expression) {
        double result = model.calculate(expression);
        if (Double.isNaN(result)) {
            return "Ошибка в вычислении.";
        }
        return "Результат: " + result;
    }
}