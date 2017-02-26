package t16.model;

import com.sun.jndi.ldap.pool.PooledConnection;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Charles Gandon on 25/02/2017.
 */
public class Database {

    /*
    - We could use PooledConnection but it seems incompatible with the use of testament
     */
//    PooledConnection connection;
    Connection connection;
    public Database(){


    }

    public void createDB(String name, String login, String password)  {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            this.connection = DriverManager.getConnection("jdbc:h2:~"+name, login, password);
//            this.connection = (PooledConnection) DriverManager.getConnection("jdbc:h2:~"+name, login, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("DB created");

    }

    public void addTables(File impression, File click, File server){
        try {
            /*
                - From connections, create SQL statement to create the table from the files in parameters
             */
            Statement doimpression = this.connection.createStatement();

            doimpression.execute("CREATE TABLE Impression(Date datetime, ID float(53), Gender varchar(20), " +
                    "Age varchar(20), Income varchar(20), Context varchar(20), Impression_cost decimal(10,7)) " +
                    "AS SELECT * FROM CSVREAD('"+impression.getPath()+"')");


            Statement doclick = this.connection.createStatement();

            doclick.execute("CREATE TABLE Click(Date datetime, ID float(53), Click_cost decimal(10,7)) " +
                    "AS SELECT * FROM CSVREAD('"+click.getPath()+"')");


            Statement doserver = this.connection.createStatement();

            doserver.execute("CREATE TABLE Server(Date datetime, ID float(53), Exit_date datetime, Page_viewed int, " +
                    "Conversion varchar(20)) AS SELECT * FROM CSVREAD('"+server.getPath()+"')");

        }catch (SQLException e){

        }
    }

    public void go(){
        /*
        -1 get the csv files from the zip file in the resources folder
        -2 use createDB (the db might have already been created)
        -3 use addTables to create and populate the tables using the files generated in -1

         */
    }

    /*
    Do the access methods (using statement with Select/where for getting the data needed for the GUI and control)
     */


}
