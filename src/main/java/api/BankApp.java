package api;

import model.Account;
import model.Transaction;
import repository.AccountRepository;
import repository.TransactionRepository;
import service.AccountService;
import service.TransactionService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class BankApp {

    private final AccountService accountService;
    private final TransactionService transactionService;
    private final Scanner scanner = new Scanner(System.in);
    private Account currentAccount;

    public BankApp() {
        AccountRepository accountRepository = new AccountRepository();
        TransactionRepository transactionRepository = new TransactionRepository();
        this.accountService = new AccountService(accountRepository);
        this.transactionService = new TransactionService(accountRepository, transactionRepository);
    }

    public void run() {
        System.out.println("=== Welcome to Bank of CLI ===");
        while (true) {
            if (currentAccount == null) showLoggedOutMenu();
            else showLoggedInMenu();
        }
    }

    private void showLoggedOutMenu() {
        System.out.println("\n1) Register  2) Login  3) Exit");
        System.out.print("> ");
        switch (scanner.nextLine().trim()) {
            case "1" -> handleRegister();
            case "2" -> handleLogin();
            case "3" -> { System.out.println("Goodbye!"); System.exit(0); }
            default -> System.out.println("Invalid option.");
        }
    }

    private void showLoggedInMenu() {
        System.out.println("\nLogged in as Account #" + currentAccount.getAccountID());
        System.out.println("1) Balance  2) Deposit  3) Withdraw  4) Transfer  5) History  6) Logout");
        System.out.print("> ");
        switch (scanner.nextLine().trim()) {
            case "1" -> handleBalance();
            case "2" -> handleDeposit();
            case "3" -> handleWithdraw();
            case "4" -> handleTransfer();
            case "5" -> handleHistory();
            case "6" -> { currentAccount = null; System.out.println("Logged out."); }
            default -> System.out.println("Invalid option.");
        }
    }

    private void handleRegister() {
        try {
            System.out.print("Choose a PIN (4+ digits): ");
            String pin = scanner.nextLine().trim();
            System.out.print("Initial deposit: ");
            BigDecimal initial = new BigDecimal(scanner.nextLine().trim());
            Account account = accountService.register(pin, initial);
            System.out.println("Account created! Your Account ID is " + account.getAccountID());
        } catch (IllegalArgumentException e) {
            System.out.println("Could not register: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Service unavailable. Please try again later.");
        }
    }

    private void handleLogin() {
        try {
            System.out.print("Account ID: ");
            int id = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("PIN: ");
            String pin = scanner.nextLine().trim();
            currentAccount = accountService.login(id, pin);
            System.out.println("Login successful.");
        } catch (NumberFormatException e) {
            System.out.println("Account ID must be a number.");
        } catch (IllegalArgumentException | SecurityException e) {
            System.out.println("Login failed: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Service unavailable. Please try again later.");
        }
    }

    private void handleBalance() {
        try {
            currentAccount = accountService.getAccount(currentAccount.getAccountID());
            System.out.println("Balance: $" + currentAccount.getBalance());
        } catch (SQLException e) {
            System.out.println("Service unavailable. Please try again later.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void handleDeposit() {
        try {
            System.out.print("Amount to deposit: ");
            BigDecimal amount = new BigDecimal(scanner.nextLine().trim());
            currentAccount = transactionService.deposit(currentAccount.getAccountID(), amount);
            System.out.println("New balance: $" + currentAccount.getBalance());
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid amount.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.println("Service unavailable. Please try again later.");
        }
    }

    private void handleWithdraw() {
        try {
            System.out.print("Amount to withdraw: ");
            BigDecimal amount = new BigDecimal(scanner.nextLine().trim());
            currentAccount = transactionService.withdraw(currentAccount.getAccountID(), amount);
            System.out.println("New balance: $" + currentAccount.getBalance());
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid amount.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.println("Service unavailable. Please try again later.");
        }
    }

    private void handleTransfer() {
        try {
            System.out.print("Recipient Account ID: ");
            int toId = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Amount to transfer: ");
            BigDecimal amount = new BigDecimal(scanner.nextLine().trim());
            transactionService.transfer(currentAccount.getAccountID(), toId, amount);
            currentAccount = accountService.getAccount(currentAccount.getAccountID());
            System.out.println("Transfer complete. New balance: $" + currentAccount.getBalance());
        } catch (NumberFormatException e) {
            System.out.println("Please enter valid numbers.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.println("Service unavailable. Please try again later.");
        }
    }

    private void handleHistory() {
        try {
            List<Transaction> history = transactionService.getHistory(currentAccount.getAccountID());
            if (history.isEmpty()) {
                System.out.println("No transactions yet.");
            } else {
                for (Transaction t : history) {
                    System.out.printf("%s | %s | $%s%n", t.getCreatedAt(), t.getTransactionType(), t.getAmount());
                }
            }
        } catch (SQLException e) {
            System.out.println("Service unavailable. Please try again later.");
        }
    }
}