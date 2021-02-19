package com.company.UserInterface;

import com.company.Database.BankDB;
import com.company.Database.DBConnection;
import com.company.Utilities.IO;

public class MainMenu {

    private static void show() {
        System.out.println("\n\n");
        System.out.println("++++++++++++++Welcome to LandL Bank++++++++++++++");
        System.out.println("=================================================");
        System.out.println("[1] Display customers of LandL Bank");
        System.out.println("[2] Add customers to LandL Bank");
        System.out.println("[3] Display transactions of LandL Bank");
        System.out.println("[4] Make deposit transactions for LandL Bank customers");
        System.out.println("[5] Make withdraw transactions for LandL Bank customers");
        System.out.println("[6] Display transactions of LandL Bank - by customer selection");
        System.out.println("[0] Exit");
        System.out.println("=================================================");

        switch (IO.getInteger("^[0-6]")) {
            case 1:
                BankDB.printCustomers();
                break;
            case 2:
                UserCLI.addBankCustomer();
                break;
            case 3:
                BankDB.printTransactions(null);
                break;
            case 4:
                UserCLI.despositBankCustomer();
                break;
            case 5:
                UserCLI.withdrawBankCustomer();
                break;
            case 6:
                UserCLI.displayTransactionListByCustomer();
                break;
            case 0:
                IO.exitApplication();
                break;
        }
    }

    public static void run() {

        DBConnection.createDatabase();
        DBConnection.createTablesIfNotFound();

        while (true) {
            show();
        }
    }
}
