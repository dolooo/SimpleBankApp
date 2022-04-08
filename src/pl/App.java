import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

public class App {
    Bank bank = Bank.getInstance();
    private boolean isLoggedIn = false;
    private final Scanner scanner = new Scanner(System.in);

    public void userValidation() throws SQLException, IOException {
        while (!isLoggedIn) {
            printUserValidationPanel();
            loginOrRegister(getInput(3));
        }
    }

    public static void printUserValidationPanel() {
        System.out.println("1) Zaloguj się");
        System.out.println("2) Zarejestruj się");
        System.out.println("3) Zakończ");
    }

    public int getInput(int optionsNumber) {
        int input = 0;
        try {
            input = Integer.parseInt(scanner.nextLine());
            if (input < 0 || input > optionsNumber) {
                System.out.println("Wybrano nieprawidłową opcję");
            }
        } catch (NumberFormatException e) {
            System.out.println("Nieprawidłowy format. Należy użyć samych cyfr.");
        }
        return input;
    }

    public void loginOrRegister(int option) throws SQLException, IOException {
        switch (option) {
            case 1:
                isLoggedIn = bank.login();
                mainMenu();
                break;
            case 2:
                bank.addAccount();
                break;
            case 3:
                System.out.println("Zakończono");
                System.exit(0);
                break;
        }
    }

    public void mainMenu() throws SQLException, IOException {
        while (isLoggedIn) {
            printMenu();
            doAction(getInput(8));
        }
    }

    public void printMenu() {
        System.out.println("1) Wpłata");
        System.out.println("2) Wypłata");
        System.out.println("3) Stan konta");
        System.out.println("4) Przelew na inne konto");
        System.out.println("5) Historia przelewów");
        System.out.println("6) Użytkownicy banku (Admin)");
        System.out.println("7) Ustawienia");
        System.out.println("8) Wyloguj się");

    }

    public void doAction(int option) throws SQLException, IOException {
        switch (option) {
            case 1:
                bank.deposit();
                break;
            case 2:
                bank.withdraw();
                break;
            case 3:
                bank.checkBalance();
                break;
            case 4:
                bank.transferToAnotherAccount();
                break;
            case 5:
                bank.loggedUser.getHistory();
                break;
            case 6:
                bank.loggedUser.getAllUsers(bank);
                break;
            case 7:
                settingsMenu();
                settings(getInput(3));
                break;
            case 8:
                isLoggedIn = bank.logout();
                break;
            default:
                System.out.println("Wybrano nieprawidłową opcję");
                break;
        }
    }

    public void settingsMenu() {
        System.out.println("1) Edytuj informacje o użytkowniku");
    }
    public void settings(int option) {
        if (option == 1) {
            bank.editInfo();
        } else {
            System.out.println("Wybrano nieprawidłową opcję");
        }
    }
}
