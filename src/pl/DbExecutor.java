import java.sql.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DbExecutor {
    public static void executeQuery(String query) {
        try {
            Connection connection = DbConnector.connect();
            Statement statement = connection.createStatement();
            statement.executeQuery(query);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static ResultSet executeSelect(String selectQuery) {
        try {
            Connection connection = DbConnector.connect();
            Statement statement = connection.createStatement();
            return statement.executeQuery(selectQuery);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public int addUserToDatabase(String name, String surname, String email, String password, UserRole role,
                                 double balance) throws SQLException {
        Connection connection = DbConnector.connect();
        int userId = -1;
        int accountId = -1;
        try {
            connection.setAutoCommit(false);
            String addUser = "INSERT INTO users(name, surname, email, password, role) VALUES (?,?,?,?,?)";
            PreparedStatement addUserStmt = connection.prepareStatement(addUser, Statement.RETURN_GENERATED_KEYS);
            addUserStmt.setString(1, name);
            addUserStmt.setString(2, surname);
            addUserStmt.setString(3, email);
            addUserStmt.setString(4, password);
            addUserStmt.setString(5, role.name());
            addUserStmt.executeUpdate();
            ResultSet addedUserResults = addUserStmt.getGeneratedKeys();
            if (addedUserResults.next()) {
                userId = addedUserResults.getInt(1);
            }

            String addAccount = "INSERT INTO accounts(balance, account_number) VALUES (?,?)";
            PreparedStatement addAccountStmt = connection.prepareStatement(addAccount, Statement.RETURN_GENERATED_KEYS);
            addAccountStmt.setDouble(1, balance);
            addAccountStmt.setString(2, Account.setRandomAccountNumber());
            addAccountStmt.executeUpdate();
            ResultSet addedAccountResults = addAccountStmt.getGeneratedKeys();
            if (addedAccountResults.next()) {
                accountId = addedAccountResults.getInt(1);
            }

            if (userId > 0 && accountId > 0) {
                String mapping = "INSERT INTO mappings(id_user, id_account) VALUES (?,?)";
                PreparedStatement addMapping = connection.prepareStatement(mapping, Statement.RETURN_GENERATED_KEYS);
                addMapping.setInt(1, userId);
                addMapping.setInt(2, accountId);
                addMapping.executeUpdate();
                connection.commit();
            } else connection.rollback();
            connection.close();
        } catch (SQLException e) {
            System.err.println("Wystąpił błąd.");
        }
        return userId;
    }

    public User getUserFromDatabase(int accountId) throws SQLException {
        String query = "SELECT email, password, role, balance, account_number "
                + "FROM users u JOIN mappings m on u.id = m.id_user "
                + "JOIN accounts a on a.id_account = m.id_account "
                + "WHERE a.id_account = '" + accountId + "'";
        User user = null;
        try {
                ResultSet findUserResults = executeSelect(query);
                if (findUserResults.next()) {
                    String email = findUserResults.getString("email");
                    String password = findUserResults.getString("password");
                    String userRole = findUserResults.getString("role");
                    double balance = findUserResults.getDouble("balance");
                    String accountNumber = findUserResults.getString("account_number");
                    Account account = new Account(accountId, balance, accountNumber);
                    if (userRole.equals(UserRole.User.name())) {
                        user = new User(email, password, account);
                    } else if (userRole.equals(UserRole.Admin.name())) {
                        user = new Admin(email, password, account);
                    }
                }
        } catch (SQLException ex) {
            System.err.println("Wystąpił błąd." + ex.getMessage());
        }
        return user;
    }

    public int getAccountByAccountNumber(String accountNumber) {
        String query = "SELECT a.id_account "
                + "FROM users u JOIN mappings m on u.id = m.id_user "
                + "JOIN accounts a on a.id_account = m.id_account "
                + "WHERE a.account_number = '" + accountNumber + "'";
        int accountId = -1;
        try {
                ResultSet findUserResults = executeSelect(query);
                if (findUserResults.next()) {
                    accountId = findUserResults.getInt("id_account");
                }
        } catch (SQLException ex) {
            System.err.println("Wystąpił błąd." + ex.getMessage());
        }
        return accountId;
    }

    public Map<Integer, User> getAllUsersFromDatabase() {
        String query = "SELECT name, surname, email, password, role, balance, a.id_account, account_number "
                + "FROM users u JOIN mappings m on u.id = m.id_user "
                + "JOIN accounts a on a.id_account = m.id_account";
        Map<Integer, User> usersMap = new HashMap<>();
        try {
            ResultSet findUsersResults = executeSelect(query);
            while (findUsersResults.next()) {
                String name = findUsersResults.getString("name");
                String surname = findUsersResults.getString("surname");
                String email = findUsersResults.getString("email");
                String password = findUsersResults.getString("password");
                String userRole = findUsersResults.getString("role");
                double balance = findUsersResults.getDouble("balance");
                int accountId = findUsersResults.getInt("id_account");
                String accountNumber = findUsersResults.getString("account_number");
                Account account = new Account(accountId, balance, accountNumber);
                if (userRole.equals(UserRole.User.name())) {
                    usersMap.put(accountId, new User(name, surname, email, password, account));
                } else if (userRole.equals(UserRole.Admin.name())) {
                    usersMap.put(accountId, new Admin(name, surname, email, password, account));
                }
            }
        } catch (SQLException ex) {
            System.err.println("Wystąpił błąd." + ex.getMessage());
        }
        return usersMap;
    }

    public int getAccountIdOfLoggedUser(int userId) {
        String query = "SELECT id_account FROM mappings WHERE id_user = '" + userId + "'";
        int accountId = -1;
        try {
            ResultSet findAccountIdResults = executeSelect(query);
            if (findAccountIdResults.next()) {
                accountId = findAccountIdResults.getInt("id_account");
            } else System.out.println("Nie udało się znaleźć powiązanego konta");
        } catch (SQLException ex) {
            System.err.println("Wystąpił błąd" + ex.getMessage());
        }
        return accountId;
    }

    public void updateBalance(int accountId, double newBalance) {
        String query = "UPDATE accounts SET balance = '" + newBalance + "' WHERE id_account = '" + accountId + "'";
        executeQuery(query);
    }

    public void updateUserInfo(int userId, String infoCategory, String updatedInfo) {
        String query = "UPDATE users SET " + infoCategory + "='" + updatedInfo + "' WHERE id = " + userId;
        executeQuery(query);
    }

    public int validateUserAndReturnIdIfExists(String inputEmail, String inputPassword) {
        String query = "SELECT id, email, password FROM users";
        try {
            ResultSet emailsAndPasswordsSet = executeSelect(query);
            int userId = -1;
            while (emailsAndPasswordsSet.next()) {
                int id = emailsAndPasswordsSet.getInt("id");
                String email = emailsAndPasswordsSet.getString("email");
                String password = emailsAndPasswordsSet.getString("password");
                if (email.equals(inputEmail) && password.equals(inputPassword)) {
                    userId = id;
                    System.out.println("Zalogowano pomyślnie.");
                    break;
                }
            }
            return userId;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
