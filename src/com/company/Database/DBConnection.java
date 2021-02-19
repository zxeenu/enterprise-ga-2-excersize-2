package com.company.Database;

import com.company.Utilities.IO;
import java.sql.*;

public class DBConnection {
    private static final String location = "jdbc:mysql://localhost:3306/"; // might be different for you, change the port number
    private static final String defaultLocation = "jdbc:mysql://localhost/"; // if different for you, please change
    private static final String dbName = "landl_bank"; // db name, can be changed.

    private static final String url = location.concat(dbName); // dont change, okay

    private static final String userName = System.getenv("USER"); // my user name is stored as an environment variable, change as needed
    private static final String passPhrase = System.getenv("PASS"); // my passowrd is stored as an environment variable, change as needed

    private static Connection con; // dont touch this, kay

    public static Connection getConnection() {
        try {
            con = DriverManager.getConnection(url, userName, passPhrase);
            return con;

        } catch (SQLException e) {
            System.err.print(e + "\n");
            System.err.println("Cant Create a connection........" + "\n");
            return null;
        }
    }

    public static void endConnection() {
        try {
            con.close();
        } catch (SQLException e) {
            System.err.print(e + "\n");
            System.err.println("Can't close the connection........" + "\n");
        }
    }

    private static Boolean checkForDatabase() {
        // to do

        Boolean foundDB = false;

        try {
            // Connection connection = <your java.sql.Connection>
            Connection connection = DriverManager.getConnection(url, userName, passPhrase);
            ResultSet resultSet = connection.getMetaData().getCatalogs();

            //iterate each catalog in the ResultSet
            while (resultSet.next()) {
                // Get the database name, which is at position 1
                String databaseName = resultSet.getString(1);
                if (databaseName.contentEquals(dbName)) {
                    System.out.println("Found " + dbName);
                    System.out.println("No need to create DB!");
                    foundDB = true;
                }
            }
            resultSet.close();
        } catch (Exception e) {
            System.err.print(e + "\n");
//            System.err.print("Database does not exist!\n");
        }

        return foundDB;
    }

    public static void createDatabase() {
        Boolean databaseExists = checkForDatabase();
        String userConfirmation = "";

        if (!databaseExists) {
            System.out.println("User confirmation needed to create DB. Current data will be overwritten if you continue!");
            System.out.println("Use keyword 'create' to continue!");
            userConfirmation = IO.getText();

            if (!userConfirmation.equalsIgnoreCase("create")) {
                System.out.println("System shutting down. Can't run app without the database!");
                System.exit(0);
            }

        }



        // creating the database at runtime if it does not exist
        if (!databaseExists && userConfirmation.equalsIgnoreCase("create")) {

            try {
                Statement stmt = null;

                //STEP 2: Register JDBC driver
//                Class.forName("com.mysql.cj.jdbc.Driver");

                //STEP 3: Open a connection
                con = DriverManager.getConnection(defaultLocation, userName, passPhrase);

                //STEP 4: Execute a query
                System.out.println("Creating the database!");
                stmt = con.createStatement();
                String createQuery = "CREATE DATABASE " + dbName; // the space at the end is impt
                stmt.executeUpdate(createQuery);
                System.out.println("Database created successfully!");

                createTablesIfNotFound();

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

    }

    /**
     * Checks if a given table name exists in a db
     * @param tableName
     * @return
     */
    private static Boolean checkTableExists(String tableName) {
        try {
            Boolean foundTable = false;
            con = getConnection();
            DatabaseMetaData meta = con.getMetaData();
            ResultSet res = meta.getTables(null, null, tableName,
                    new String[] {"TABLE"});
            while (res.next()) {
//                System.out.println(
//                        "   "+res.getString("TABLE_CAT")
//                                + ", "+res.getString("TABLE_SCHEM")
//                                + ", "+res.getString("TABLE_NAME")
//                                + ", "+res.getString("TABLE_TYPE")
//                                + ", "+res.getString("REMARKS"));
                if (tableName.contentEquals(res.getString("TABLE_NAME"))) {
                    foundTable = true;
                    return foundTable;
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return false;
    }

    /**
     * Can be run by itself, to check if tables exist in the db and to make them
     * and when the db is initialized, to make the tables!
     */
    public static void createTablesIfNotFound() {
        try {
            Boolean customersTable = checkTableExists("customers");

            if (!customersTable) {
                con = getConnection();

                System.out.println("WARNING: TABLES ARE BEING CREATED FOR THE DATABASE BECAUSE THEY WERE NOT IN THE DATABASE!");

                Statement stmtCT = con.createStatement();
                String customerTableSQL =
                        "CREATE TABLE customers " +
                                "(id INT NOT NULL AUTO_INCREMENT, " +
                                " name VARCHAR(255) NOT NULL, " +
                                " balance DECIMAL(13, 2), " +
                                " PRIMARY KEY (id))";

                stmtCT.executeUpdate(customerTableSQL);
                System.out.println("'customer' table created!");
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        try {
            Boolean transactionsTable = checkTableExists("transactions");

            if (!transactionsTable) {
                Statement stmtCT = con.createStatement();
                String transactionsTableSQL =
                        "CREATE TABLE transactions " +
                                "(id INT NOT NULL AUTO_INCREMENT, " +
                                " date DATETIME NOT NULL, " +
                                " amount DECIMAL(13, 2) NOT NULL, " +
                                " type VARCHAR(255) NOT NULL, " +
                                " current_balance DECIMAL(13, 2) NOT NULL, " +
                                " customer_id INT NOT NULL, " +
                                " PRIMARY KEY (id), " +
                                " FOREIGN KEY (customer_id) REFERENCES customers(id))";

                stmtCT.executeUpdate(transactionsTableSQL);
                System.out.println("'transactions' table created!");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}
