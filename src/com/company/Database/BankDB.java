package com.company.Database;

import com.company.Utilities.IO;

import java.sql.*;
import java.text.MessageFormat;

public class BankDB {
    private static Connection con;


    /**
     * A fully paramerized insert function for the customer table. Null values are not accepted for
     * any of the columns
     *
     * @param name_ - a String
     * @param balance_ -  a Double
     */
    public static void createCustomer(String name_, Double balance_) {
        if (con == null) {
            con = DBConnection.getConnection();
        }

        String insertQuery = "INSERT INTO customers (name, balance) values(?, ?);";

        try (PreparedStatement insertCustomerPS = con.prepareStatement(insertQuery)) {
            con.setAutoCommit(false);

            // guts begin here
            insertCustomerPS.setString(1, name_);
            insertCustomerPS.setDouble(2, balance_);
            insertCustomerPS.executeUpdate();
            // guts end here

            con.commit();
        } catch (SQLException e) {
            System.err.print(e + "\n");

            if (con != null) {
                try {
                    System.err.print("Transaction is being rolled back" + "\n");
                    con.rollback();
                } catch (SQLException excep) {
                    System.err.print(excep);
                }
            }
        }
    }

    /**
     * Gets the entire customer table data
     * @return ResultSet
     */
    public static ResultSet readCustomers(Integer customerID) {
        if (con == null) {
            con = DBConnection.getConnection();
        }

        try {
            // the TYPE_SCROLL_INSENSITIVE <- refers to the result set not being sensitive to changes made in the database after we get it
            String sqlQuery = "SELECT * FROM customers WHERE id = ?";
            PreparedStatement state = con.prepareStatement(sqlQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            state.setInt(1, customerID);
            ResultSet res = state.executeQuery();
            return res;

        } catch (SQLException e) {
            System.err.print(e + "\n");
            return null;
        }

    }

    public static ResultSet readCustomers() {
        if (con == null) {
            con = DBConnection.getConnection();
        }

        try {
            // normal statement version disabled, in favor of Prepared statement
            // the TYPE_SCROLL_INSENSITIVE <- refers to the result set not being sensitive to changes made in the database after we get it
            Statement state = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet res = state.executeQuery(" SELECT * FROM customers");
            return res;
        } catch (SQLException e) {
            System.err.print(e + "\n");
            return null;
        }

    }

    public static void printCustomers() {
        try {
            ResultSet rs = BankDB.readCustomers();
            while (rs.next()) {
                System.out.println(rs.getInt("id") + " - "+ rs.getString("name") + ", Balnce Balance " + rs.getDouble("balance"));
            }
        } catch (SQLException e) {
            System.err.print(e + "\n");
        }
    }

//    /**
//     * Gets the current balance value of a customer, and adds an amount to it, arbitaritly. Does not edit the value in the
//     * DB
//     * @param customerID_
//     * @param amount_
//     * @return
//     */
//    public static ResultSet getDepositedAmount(Integer customerID_, Double amount_) {
//        if (con == null) {
//            con = DBConnection.getConnection();
//        }
//
//        try {
////            con.setAutoCommit(false);
//            String depositQuery = "SELECT balance+" + amount_.toString() + "FROM customers WHERE id='" + customerID_.toString() + "'";
//            PreparedStatement depositCustomerPS = con.prepareStatement(depositQuery);
//            ResultSet rs =  depositCustomerPS.executeQuery();
//
//            return rs;
//        } catch (SQLException e) {
//            System.err.print(e + "\n");
//            return null;
//        }
//    }
//
//    /**
//     * Adds to the customer balance value in the DB, returns true is successful
//     *
//     * @param customerID_
//     * @param amount_
//     * @return
//     */
//    private static Boolean depositAmountCustomer(Integer customerID_, Double amount_) {
//        try {
//            ResultSet rs = BankDB.getDepositedAmount(customerID_, amount_);
//            rs.next();
//            Double newBalance = rs.getDouble(1);
//
//            PreparedStatement prep = con.prepareStatement(" UPDATE customers set balance=? WHERE id='"+customerID_+"' ");
//            prep.setDouble(1, newBalance);
//            prep.execute();
//            prep.close();
//            return true;
//
//        } catch (SQLException e) {
//            System.err.print(e + "\n");
//
//            if (con != null) {
//                try {
//                    System.err.print("Transaction is being rolled back" + "\n");
//                    con.rollback();
//                } catch (SQLException excep) {
//                    System.err.print(excep);
//                }
//            }
//
//            return false;
//        }
//    }

    public static Double depositAmountCustomer(Integer customerID, Double amount) {

        if (con == null) {
            con = DBConnection.getConnection();
        }

        try  {
            String sqlQuery = "SELECT * FROM customers WHERE id = ?";
            PreparedStatement state = con.prepareStatement(sqlQuery, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            state.setInt(1, customerID);
            ResultSet uprs = state.executeQuery();

            Double newBalance = 0.0;
            while (uprs.next()) {
                Double f = uprs.getDouble("balance");
                newBalance = f + amount;
                uprs.updateDouble("balance", f + amount);
                uprs.updateRow();
            }
            return newBalance;
        } catch (SQLException e) {
            System.err.print(e + "\n");
            return null;
        }
    }

    /**
     * Deposts an amount to a customer, and records the transaction to a transaction table
     *
     * @param customerID_
     * @param amount_
     */
    public static void createNewDespositTransaction(Integer customerID_, Double amount_) {
        if (con == null) {
            con = DBConnection.getConnection();
        }

        String insertQuery = "INSERT INTO transactions (date, amount, type, customer_id, current_balance) values(?, ?, ?, ?, ?);";
        String type_ = "DEPOSIT";
        String date_ = IO.getCurrentDateTime();

        try (PreparedStatement insertTransactionPS = con.prepareStatement(insertQuery)) {
            con.setAutoCommit(false);

            // guts begin here

            // deposit the amount
            Double currentBalanceAfterDeposit_ = depositAmountCustomer(customerID_, amount_);

            if (currentBalanceAfterDeposit_ != null) {
                // record as a transaction
                insertTransactionPS.setString(1, date_);
                insertTransactionPS.setDouble(2, amount_);
                insertTransactionPS.setString(3, type_);
                insertTransactionPS.setInt(4, customerID_);
                insertTransactionPS.setDouble(5, currentBalanceAfterDeposit_);
                insertTransactionPS.executeUpdate();
            }

            // guts end here

            con.commit();
        } catch (SQLException e) {
            System.err.print(e + "\n");

            if (con != null) {
                try {
                    System.err.print("Transaction is being rolled back" + "\n");
                    con.rollback();
                } catch (SQLException excep) {
                    System.err.print(excep);
                }
            }
        }
    }

    public static Double withdrawAmountCustomer(Integer customerID, Double amount) {

        if (con == null) {
            con = DBConnection.getConnection();
        }

        try {
            String sqlQuery = "SELECT * FROM customers WHERE id = ?";
            PreparedStatement state = con.prepareStatement(sqlQuery, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            state.setInt(1, customerID);
            ResultSet uprs = state.executeQuery();

            Double newBalance = 0.0;
            while (uprs.next()) {
                Double f = uprs.getDouble("balance");
                newBalance = f - amount;

                if (newBalance >= 0) {
                    uprs.updateDouble("balance", newBalance);
                    uprs.updateRow();
                    return newBalance;

                } else {
                    System.err.println("You tried to withdraw more than total balance! Only " + f + " is avaiable!");
                }
            }

        } catch (SQLException e) {
            System.err.print(e + "\n");

        }

        return null;
    }

    public static void createNewWithdrawTransaction(Integer customerID_, Double amount_) {
        if (con == null) {
            con = DBConnection.getConnection();
        }

        String insertQuery = "INSERT INTO transactions (date, amount, type, customer_id, current_balance) values(?, ?, ?, ?, ?);";
        String type_ = "WITHDRAW";
        String date_ = IO.getCurrentDateTime();

        try (PreparedStatement insertTransactionPS = con.prepareStatement(insertQuery)) {
            con.setAutoCommit(false);

            // guts begin here

            // withdraw the amount
            Double withdrawAmountCustomer_ = withdrawAmountCustomer(customerID_, amount_);

            if (withdrawAmountCustomer_ != null) {
                // record as a transaction
                insertTransactionPS.setString(1, date_);
                insertTransactionPS.setDouble(2, amount_);
                insertTransactionPS.setString(3, type_);
                insertTransactionPS.setInt(4, customerID_);
                insertTransactionPS.setDouble(5, withdrawAmountCustomer_);
                insertTransactionPS.executeUpdate();
            }

            // guts end here

            con.commit();
        } catch (SQLException e) {
            System.err.print(e + "\n");

            if (con != null) {
                try {
                    System.err.print("Transaction is being rolled back" + "\n");
                    con.rollback();
                } catch (SQLException excep) {
                    System.err.print(excep);
                }
            }
        }
    }

    public static ResultSet readTransactions() {
        if (con == null) {
            con = DBConnection.getConnection();
        }

        try {
            Statement state = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet res = state.executeQuery("SELECT * FROM customers JOIN transactions ON customers.id = transactions.customer_id");
            return res;
        } catch (SQLException e) {
            System.err.print(e + "\n");
        }

        return null;
    }

    public static ResultSet readTransactions(Integer customerID) {
        if (con == null) {
            con = DBConnection.getConnection();
        }

        try {
            String sqlQuery = "SELECT * FROM customers JOIN transactions ON customers.id = transactions.customer_id WHERE customers.id = ?"; //"SELECT * FROM customers WHERE id = ?";
            PreparedStatement state = con.prepareStatement(sqlQuery, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            state.setInt(1, customerID);
            ResultSet uprs = state.executeQuery();
            return uprs;

        } catch (SQLException e) {
            System.err.print(e + "\n");

        }

        return null;
    }

    public static void printTransactions(Integer customerID_) {

        ResultSet rs = null;

        try {

            if (customerID_ == null) {
                rs = readTransactions();
            } else {
                rs = readTransactions(customerID_);
            }

            while (rs.next()) {

                String trandsID = String.valueOf(rs.getInt("id"));
                String date = String.valueOf(rs.getDate("date"));
                String transAmount = String.valueOf(rs.getDouble("amount"));
                String transType = String.valueOf(rs.getString("type"));
                String customerID = String.valueOf(rs.getInt("customer_id"));
                String customerName = rs.getString("customers.name");
                String currentCustomerBalance = String.valueOf(rs.getString("current_balance"));

                System.out.println(
                        MessageFormat.format("transaction id: {0}, date: {1}, transaction amount: {2}, type: {3}, customer id: {4}, customer name: {5}, customer balance after transaction: {6}",
                                trandsID,
                                date,
                                transAmount,
                                transType,
                                customerID,
                                customerName,
                                currentCustomerBalance
                                ).toString()
                );
            }
        } catch (SQLException e) {
            System.err.print(e + "\n");
        }
    }


}
