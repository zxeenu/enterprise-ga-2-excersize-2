How To Use
##########

* MySQL must be installed on your computer.
    ** get from https://dev.mysql.com/downloads/
* Go the DBConnection.java file and follwing variables as needed:
    ** location : the location of your MySQL server instance. It should be in the form of "jdbc:mysql://localhost:3306/",
        complete with port number
    ** defaultLocation : your localhost location - prefixed with "jdbc:mysql:". It should be "jdbc:mysql://localhost/".
        Honestly, this should not be different for you, but if it is, kindly change it.
    ** dbName : database name. Can change to anything you wish.
    ** userName : if your MySQL server instance needs a username. For local development instances, its "root"
    ** passPhrase : if your MySQL server instance needs a password.
* Run the app. The database creation and table creation is done at run-time. If a table has been deleted from the db,
  the program  should intelligently make a new table. If the database is missing, it should ask if you want to create a
  new database.
* If the app does not run because the mysql-connector is missing, in the lib folder of this project it exists. Kindly
  use your IDE to import it into the project.# enterprise-ga-2-excersize-2
