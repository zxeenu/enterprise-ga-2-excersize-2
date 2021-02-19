package com.company.UserInterface;

import com.company.Database.BankDB;
import com.company.Utilities.IO;

public class UserCLI {
    public static void addBankCustomer() {
        System.out.println("Please input the name!");
        System.out.println("Please use type '!back' to go back");
        String name = IO.getText();
        Double balance = 00.00;

        if (!name.equalsIgnoreCase("!back")) {
            BankDB.createCustomer(name, balance);
        } else {
            System.err.print("Cancelled!" + "\n");
        }
    }

    public static void despositBankCustomer() {
        BankDB.printCustomers();
        System.out.println("=================================================");
        System.out.println("Please input the ID of the customer to deposit to!");
        System.out.println("Please deposit 0 to cancel!");
        Integer choice = IO.getInteger(null);
        System.out.println("Please input the amount to deposit!");
        Double amount = IO.getPositiveDouble();

        if (amount > 0) {
            BankDB.createNewDespositTransaction(choice, amount);
        } else {
            System.err.print("Going back! No deposits were made!" + "\n");
        }
    }

    public static void withdrawBankCustomer() {
        BankDB.printCustomers();
        System.out.println("=================================================");
        System.out.println("Please input the ID of the customer to withdraw from!");
        System.out.println("Please deposit 0 to cancel!");
        Integer choice = IO.getInteger(null);
        System.out.println("Please input the amount to withdraw!");
        Double amount = IO.getPositiveDouble();

        if (amount > 0) {
            BankDB.createNewWithdrawTransaction(choice, amount);
        } else {
            System.err.print("Going back! No withdrawals were made!" + "\n");
        }

    }

    public static Boolean displayTransactionListByCustomer() {
        BankDB.printCustomers();
        System.out.println("=================================================");
        System.out.println("Please input the ID of the customer to check the transaction list of!");
        System.out.println("Please deposit 0 to cancel!");
        Integer choice = IO.getInteger(null);

        if (choice == 0) {
            System.err.print("Going back" + "\n");
            return false;
        } else {
            BankDB.printTransactions(choice);
            return true;
        }

    }


}
