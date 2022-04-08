import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Admin extends User{
    public Admin(String email, String password, Account account) {
        super(email, password, account);
        setRole(UserRole.Admin);
    }

    public Admin(String name, String surname, String email, String password, Account account) {
        super(name, surname, email, password, account);
        setRole(UserRole.Admin);
    }

    @Override
    public void getHistory() throws IOException {
            System.out.println("Wpisz id konta użytkownika, którego historię chcesz sprawdzić: ");
            Scanner scanner = new Scanner(System.in);
            int accountId = scanner.nextInt();
            String fileName = "transferHistory/account-" + accountId + ".txt";
            BufferedReader load = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = load.readLine()) != null) {
                System.out.println(line);
            }
    }

    @Override
    public void getAllUsers(Bank bank) {
        System.out.println(bank.mapOfUsers);
    }
}
