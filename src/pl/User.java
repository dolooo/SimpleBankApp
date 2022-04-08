import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class User {
    private String name;
    private String surname;
    private String email;
    private String password;
    private UserRole role;
    private final Account account;

    private boolean isRestricted = false;

    public User(String name, String surname, String email, String password, Account account) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.account = account;
        this.role = UserRole.User;
    }

    public User(String email, String password, Account account) {
        this.email = email;
        this.password = password;
        this.account = account;
        this.role = UserRole.User;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isRestricted() {
        return isRestricted;
    }

    public void setRestricted(boolean restricted) {
        isRestricted = restricted;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public Account getAccount() {
        return account;
    }

    @Override
    public String toString() {
        return "Użytkownik{" +
                "imie=" + name + '\'' +
                "nazwisko=" + surname + '\'' +
                "email='" + email + '\'' +
                ", hasło='" + password + '\'' +
                ", uprawnienia=" + role +
                ", " + account +
                "}\n";
    }

    public void getHistory() throws IOException {
        String fileName = "transferHistory/account-" + Bank.loggedUserAccountID + ".txt";
        BufferedReader load = new BufferedReader(new FileReader(fileName));
        String line;
        while ((line = load.readLine()) != null) {
            System.out.println(line);
        }
    }

    public void getAllUsers(Bank bank){
        System.out.println("Nie masz uprawnień do wykonania tej akcji");
    }

}
