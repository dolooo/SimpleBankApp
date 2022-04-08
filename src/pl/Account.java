import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Account {
    private final int accountId;
    private double balance;
    private final String accountNumber;

    public Account(int accountId, double balance, String accountNumber) {
        this.accountId = accountId;
        this.balance = balance;
        this.accountNumber = accountNumber;
    }

    public static String setRandomAccountNumber() {
        Random random = new Random();
        long randomNumber = random.nextInt(899999999) + 1000000000;
        return randomNumber + "";
    }

    public void addRegistryToHistory(String registry) throws IOException {
        String fileName = "transferHistory/account-" + this.accountId + ".txt";
        BufferedWriter save = new BufferedWriter(new FileWriter(fileName, true));
        save.append(registry);
        save.append("\n");
        save.flush();
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    @Override
    public String toString() {
        return "Konto {" +
                "stan=" + balance +
                ", numer konta='" + accountNumber + '\'' +
                '}';
    }
}
