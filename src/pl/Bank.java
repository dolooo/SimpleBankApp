import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class Bank {
    DbExecutor database = new DbExecutor();
    Scanner scanner = new Scanner(System.in);
    public Map<Integer, User> mapOfUsers = new HashMap<>();
    public User loggedUser;
    static int loggedUserID;
    static int loggedUserAccountID;

    private static Bank instance = null;

    public Bank() {
        try {
            this.mapOfUsers = database.getAllUsersFromDatabase();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bank getInstance(){
        try {
            if (instance == null) {
                instance = new Bank();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return instance;
    }


    public void addAccount() throws SQLException {
        System.out.println("Imie: ");
        String name = scanner.nextLine();
        System.out.println("Nazwisko: ");
        String surname = scanner.nextLine();
        System.out.println("Email: ");
        String email = scanner.nextLine();
        System.out.println("Hasło: ");
        String password = scanner.nextLine();
        repeatPassword(password);
        int userId = database.addUserToDatabase(name, surname, email, password, UserRole.User, 0);
//        mapOfUsers.put(userId, newUser);
        System.out.println("Rejestracja przebiegła pomyślnie!");
    }

    private void repeatPassword(String password) {
        boolean validPassword = false;
        while (!validPassword) {
            System.out.println("Powtórz hasło: ");
            String repeatedPassword = scanner.nextLine();
            if (!password.equals(repeatedPassword)) {
                System.out.println("Niepoprawne hasło! Wpisz hasło poprawnie: ");
            } else validPassword = true;
        }
    }

    public boolean login() throws SQLException {
        System.out.println("Email: ");
        String inputEmail = scanner.nextLine();
        System.out.println("Hasło: ");
        String inputPassword = scanner.nextLine();

        int userId = database.validateUserAndReturnIdIfExists(inputEmail, inputPassword);
        if (userId < 0) {
            System.out.println("Wpisano nieprawidłowe dane!");
            return false;
        }
        loggedUserID = userId;
        loggedUserAccountID = database.getAccountIdOfLoggedUser(userId);
        loggedUser = database.getUserFromDatabase(loggedUserAccountID);
        return true;
    }

    public boolean logout() {
        System.out.println("Wylogowano");
        loggedUserID = -1;
        return false;
    }

    public void deposit() throws SQLException, IOException {
        loggedUser = getUpdatedUser();
        System.out.println("Ile chcesz wpłacić: ");
        double amountOfDeposit = scanner.nextDouble();
        double previousBalance = loggedUser.getAccount().getBalance();
        double newBalance = previousBalance + amountOfDeposit;
        database.updateBalance(loggedUserAccountID, newBalance);
        String registry = "Wpłacono " + amountOfDeposit + "zł w dniu " + LocalDateTime.now();
        System.out.println(registry);
        loggedUser.getAccount().addRegistryToHistory(registry);
    }

    public void transferToAnotherAccount() throws SQLException, IOException {
        loggedUser = getUpdatedUser();
        System.out.println("Ile chcesz przelać: ");
        double amountOfDeposit = scanner.nextDouble();
        scanner.nextLine();
        System.out.println("Podaj numer konta: ");
        String accountNumber = scanner.nextLine();
        System.out.println("Tytułem: ");
        String title = scanner.nextLine();

        double previousBalance = loggedUser.getAccount().getBalance();
        double newBalance = previousBalance - amountOfDeposit;
        if (newBalance >= 0) {
            database.updateBalance(loggedUserAccountID, newBalance);

            int recipientAccountId = database.getAccountByAccountNumber(accountNumber);
            User recipient = database.getUserFromDatabase(recipientAccountId);
            double recipientPreviousBalance = recipient.getAccount().getBalance();
            double recipientNewBalance = recipientPreviousBalance + amountOfDeposit;
            database.updateBalance(recipientAccountId, recipientNewBalance);
            String registry = "Wpłacono " + amountOfDeposit + "zł w dniu " + LocalDateTime.now()
                    + " na konto nr " + accountNumber + " tytułem: " + title;
            String recipientRegistry = "Otrzymano " + amountOfDeposit + "zł w dniu " + LocalDateTime.now()
                    + " z konta nr " + loggedUser.getAccount().getAccountNumber() + " tytułem: " + title;
            System.out.println(registry);
            loggedUser.getAccount().addRegistryToHistory(registry);
            recipient.getAccount().addRegistryToHistory(recipientRegistry);
        } else System.out.println("Nie masz wystarczających środków na koncie!");

    }

    public void withdraw() throws SQLException, IOException {
        loggedUser = getUpdatedUser();
        System.out.println("Ile chcesz wypłacić: ");
        double amountOfWithdraw = scanner.nextDouble();
        double previousBalance = loggedUser.getAccount().getBalance();
        double newBalance = previousBalance - amountOfWithdraw;
        if (newBalance >= 0) {
            database.updateBalance(loggedUserAccountID, newBalance);
            String registry = "Wypłacono " + amountOfWithdraw + "zł w dniu " + LocalDateTime.now();
            System.out.println(registry);
            loggedUser.getAccount().addRegistryToHistory(registry);
        } else System.out.println("Nie masz wystarczających środków na koncie!");

    }

    public void checkBalance() throws SQLException {
        loggedUser = getUpdatedUser();
        double balance = loggedUser.getAccount().getBalance();
        System.out.println("Stan konta: " + balance + "zł");
    }

    public User getUpdatedUser() throws SQLException {
        return database.getUserFromDatabase(loggedUserAccountID);
    }

    public void editInfo() {
        System.out.println("Wpisz którą informację chcesz zmienić: ");
        System.out.println("( name, surname , email, password )");
        String infoCategory = scanner.nextLine();
        System.out.println("Wpisz nową nazwę: ");
        String updatedInfo = scanner.nextLine();
        database.updateUserInfo(loggedUserID, infoCategory,updatedInfo);
    }
}