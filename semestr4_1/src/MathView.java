import java.util.Scanner;
public class MathView {
    private MathController controller;

    public MathView() {
        this.controller = new MathController();
    }

    // Метод для запуска программы
    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите математическое выражение:");

        while (true) {
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("exit")) {
                break;
            }

            String result = controller.processExpression(input);
            System.out.println(result);
        }

        scanner.close();
    }

    public static void main(String[] args) {
        MathView view = new MathView();
        view.run();
    }
}