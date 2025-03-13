/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.lang.Math;
import java.time.Instant;
import java.io.IOException;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class PizzaStore {

   // reference to physical database connection.
   private Connection _connection = null;

   public String login = null;
   public String password = null;
   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of PizzaStore
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public PizzaStore(String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end PizzaStore

   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
		 if(outputHeader){
			for(int i = 1; i <= numCol; i++){
			System.out.print(rsmd.getColumnName(i) + "\t");
			}
			System.out.println();
			outputHeader = false;
		 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>();
      while (rs.next()){
        List<String> record = new ArrayList<String>();
		for (int i=1; i<=numCol; ++i)
			record.add(rs.getString (i));
        result.add(record);
      }//end while
      stmt.close ();
      return result;
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       while (rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
	Statement stmt = this._connection.createStatement ();

	ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
	if (rs.next())
		return rs.getInt(1);
	return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            PizzaStore.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if

      Greeting();
      PizzaStore esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the PizzaStore object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new PizzaStore (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
              boolean usermenu = true;
              while(usermenu) {
                System.out.println("MAIN MENU");
                System.out.println("---------");
                System.out.println("1. View Profile");
                System.out.println("2. Update Profile");
                System.out.println("3. View Menu");
                System.out.println("4. Place Order"); //make sure user specifies which store
                System.out.println("5. View Full Order ID History");
                System.out.println("6. View Past 5 Order IDs");
                System.out.println("7. View Order Information"); //user should specify orderID and then be able to see detailed information about the order
                System.out.println("8. View Stores"); 

                //**the following functionalities should only be able to be used by drivers & managers**
                if(!authorisedUser.trim().equals("customer")) System.out.println("9. Update Order Status");

                //**the following functionalities should ony be able to be used by managers**
                if(authorisedUser.trim().equals("manager")) System.out.println("10. Update Menu");
                if(authorisedUser.trim().equals("manager")) System.out.println("11. Update User");

                System.out.println(".........................");
                System.out.println("20. Log out");
                switch (readChoice()){
                   case 1: viewProfile(esql); break;
                   case 2: updateProfile(esql); break;
                   case 3: viewMenu(esql); break;
                   case 4: placeOrder(esql); break;
                   case 5: viewAllOrders(esql, authorisedUser); break;
                   case 6: viewRecentOrders(esql, authorisedUser); break;
                   case 7: viewOrderInfo(esql, authorisedUser); break;
                   case 8: viewStores(esql); break;
                   case 9: if(authorisedUser.trim().equals("customer")) break; updateOrderStatus(esql); break;
                   case 10: if(!authorisedUser.trim().equals("manager")) break; updateMenu(esql); break;
                   case 11: if(!authorisedUser.trim().equals("manager")) break; updateUser(esql); break;



                   case 20: usermenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
              }
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main

   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   public static String readString() {
      String input; 

      do {
         System.out.print("Please make your choice: ");
         try {
            input = in.readLine();
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }
      } while (true);
      return input;
   }

   /*
    * Creates a new user
    **/
   public static void CreateUser(PizzaStore esql){
      // we need to aggregate all of the required information for the user
      try {
         System.out.print("Enter username: ");
         String username = in.readLine();
         System.out.print("Enter password: ");
         String password = in.readLine();
         System.out.print("Enter phone number: ");
         String phone = in.readLine();

         String query = "INSERT INTO Users (login, password, phoneNum, role, favoriteItems) VALUES ('" 
                + username + "', '" 
                + password + "', '" 
                + phone + "', 'customer', '');";


         esql.executeUpdate(query);
         System.out.println("Successfully created user\n Query: " + query); // Debugging output
      } catch(Exception e) {
         System.err.println(e.getMessage());
      }
   }//end CreateUser

   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(PizzaStore esql) {
      try {
         System.out.print("Enter username: ");
         String username = in.readLine();
         esql.login = username;

         System.out.print("Enter password: ");
         String password = in.readLine();
         esql.password = password;

         String query = "SELECT role FROM Users WHERE login = '" + username + "' AND password = '" + password + "';";

         List<List<String>> result = esql.executeQueryAndReturnResult(query); 

         if (result.size() > 0) {
               System.out.println("Login successful! Welcome, " + username);

               return result.get(0).get(0);
         } else {
               System.out.println("Invalid credentials. Please try again.");
               return null;
         }
      } catch (Exception e) {
         System.out.println("Error: " + e.getMessage());
         return null;
      }
   } //end

// Rest of the functions definition go in here

   public static void viewProfile(PizzaStore esql) {
      try {
         
         String query = "SELECT favoriteItems, phoneNum FROM Users WHERE login = '" + esql.login + "';";
         List<List<String>> result = esql.executeQueryAndReturnResult(query);

         if (!result.isEmpty()) {
               System.out.println("\n--- User Profile ---");
               System.out.println("Favorite Items: " + result.get(0).get(0));
               System.out.println("Phone Number: " + result.get(0).get(1));
         } else {
               System.out.println("User not found.");
         }
      } catch (Exception e) {
         System.out.println("Error: " + e.getMessage());
      }
   }

   public static void updateProfile(PizzaStore esql) {
      try {
         System.out.println("\n--- Update Profile ---");
         System.out.println("1. Change Password");
         System.out.println("2. Change Phone Number");
         System.out.print("Select an option: ");
         int choice = Integer.parseInt(in.readLine());

         String query = "";
         if (choice == 1) {
               System.out.print("Enter new password: ");
               String newPassword = in.readLine();
               query = "UPDATE Users SET password = '" + newPassword + "' WHERE login = '" + esql.login + "';";
         } else if (choice == 2) {
               System.out.print("Enter new phone number: ");
               String newPhone = in.readLine();
               query = "UPDATE Users SET phoneNum = '" + newPhone + "' WHERE login = '" + esql.login + "';";
         } else {
               System.out.println("Invalid option.");
               return;
         }

         esql.executeUpdate(query);
         System.out.println("Profile updated successfully!");

      } catch (Exception e) {
         System.out.println("Error: " + e.getMessage());
      }
   }

   public static void viewMenu(PizzaStore esql) {
      try {
         while (true) {
               System.out.println("\n--- Menu Options ---");
               System.out.println("1. View all items");
               System.out.println("2. Search for items");
               System.out.println("3. Exit");
               System.out.print("Select an option: ");  

               int choice = readChoice();
               switch (choice) {
                  case 1:
                     displayAllItems(esql);
                     displayLessThan(esql, "all", "none");
                     break;
                  case 2:
                     searchItems(esql);
                     break;
                  case 3:
                     return;
                  default:
                     System.out.println("Invalid option. Please try again.");
               }
         }
      } catch (Exception e) {
         System.out.println("Error: " + e.getMessage());
      }
   }

   private static void displayAllItems(PizzaStore esql) throws IOException, SQLException {
      String query = "SELECT * FROM Items;";
      esql.executeQueryAndPrintResult(query);
      
      while (true) {
         System.out.print("\nFilter by ('price asc', 'price desc' or 'exit'): ");
         String modifier = in.readLine().trim().toLowerCase();
         
         if (modifier.equals("exit")) {
            return;
         }
         
         switch (modifier) {
               case "price asc":
                  query = "SELECT * FROM Items ORDER BY price ASC;";
                  esql.executeQueryAndPrintResult(query);
                  break;
               case "price desc":
                  query = "SELECT * FROM Items ORDER BY price DESC;";
                  esql.executeQueryAndPrintResult(query);
                  break;
               default:
                  break;
         }
      }
   }

   public static void displayLessThan(PizzaStore esql, String category, String query) throws IOException, SQLException {
      System.out.print("\nFilter by less than ('price' or 'exit'): ");
      String modifier = in.readLine();

      if (modifier.equals("exit")) {
         return;
      }

      switch (category) {
         case "all":
            query = "SELECT * FROM Items WHERE price < ";

            query += modifier + ";";
            esql.executeQueryAndPrintResult(query);
         case "search":
            query = query.substring(0, query.length() - 1);
            query += " AND price < " + modifier + ";";
            esql.executeQueryAndPrintResult(query);
      }
   }

   private static void searchItems(PizzaStore esql) throws IOException, SQLException {
      System.out.print("\nEnter Search parameter (ex: drinks, sides, etc,...): ");
      String searchTerm = in.readLine();
      
      String query = "SELECT * FROM Items WHERE TRIM(typeOfItem) = '" + searchTerm + "';";
      esql.executeQueryAndPrintResult(query);
      displayLessThan(esql, "search", query);
   }

   // Add logic to limit to user
   public static void placeOrder(PizzaStore esql) {
      try {
         System.out.print("Choose Store: ");
         String store = in.readLine();

         // insert order into ORDERS now and get order ID
         List<List<String>> result = esql.executeQueryAndReturnResult("SELECT MAX(orderID) FROM FoodOrder;");
         int orderID = 1;
         if(!result.isEmpty()){
            orderID = Integer.parseInt(result.get(0).get(0)) + 1;
         }
         String query = "INSERT INTO FoodOrder (orderID, login, storeID, totalPrice, orderTimestamp, orderStatus) VALUES ('" 
            + orderID + "', '" 
            + esql.login + "', '" 
            + store + "', 0, '" + Instant.now() + "' , 'incomplete');";
         esql.executeUpdate(query);


         System.out.print("Choose item(Enter DONE when finished):  ");
         String item = in.readLine();
         int totalPrice = 0;
         while(!item.equals("DONE")){
            System.out.print("Choose quantity: ");
            int quantity = readChoice();

            double price = Double.parseDouble(esql.executeQueryAndReturnResult("SELECT price FROM Items WHERE itemName = '" + item + "';").get(0).get(0));
            totalPrice += price * quantity;
            // insert order into itemonorder
            query = "INSERT INTO ItemsInOrder (orderID, itemName, quantity) VALUES ('" 
               + orderID + "', '" 
               + item + "', '" 
               + quantity + "');";
            esql.executeUpdate(query);
            System.out.print("Choose item(Enter DONE when finished):  ");
            item = in.readLine();
         }
         query = "UPDATE FoodOrder SET totalPrice = '" + totalPrice + "' WHERE orderID = '" + orderID + "';";
         esql.executeUpdate(query);
      }
      catch (Exception e){
         System.out.println("Error: " + e.getMessage());
      }

   }
   // Done
   public static void viewAllOrders(PizzaStore esql, String authorisedUser) {
      try {
         if(authorisedUser.trim().equals("customer")){
            esql.executeQueryAndPrintResult("SELECT orderID FROM FoodOrder WHERE login = '" + esql.login + "' ORDER BY orderTimeStamp DESC");
            return;
         } else{
            esql.executeQueryAndPrintResult("SELECT orderID FROM FoodOrder ORDER BY orderTimeStamp DESC");
         }
      } catch (Exception e){
         System.out.println("Error: " + e.getMessage());
      }
   }
   // DONE
   public static void viewRecentOrders(PizzaStore esql, String authorisedUser) {
      try{
         if(authorisedUser.trim().equals("customer")){
            esql.executeQueryAndPrintResult("SELECT orderID FROM FoodOrder WHERE login = '" + esql.login + "' ORDER BY orderTimeStamp DESC LIMIT(5)");
            return;
         } else{
            esql.executeQueryAndPrintResult("SELECT orderID FROM FoodOrder ORDER BY orderTimeStamp DESC LIMIT(5)");
         }
      } catch (Exception e) {
         System.out.println("Error: " + e.getMessage());
      }
   }
   // Add logic to limit to user
   public static void viewOrderInfo(PizzaStore esql, String authorisedUser) {
      try{
         if(authorisedUser.trim().equals("customer")){
            System.out.print("Choose Order: ");
            String order = in.readLine();
            esql.executeQueryAndPrintResult("SELECT * FROM FoodOrder WHERE orderID = '" + order + "' AND login ='" + esql.login + "';");
            esql.executeQueryAndPrintResult("SELECT itemName, quantity FROM ItemsInOrder WHERE orderID = '" + order + "' AND login ='" + esql.login + "';");
         } else{
            System.out.print("Choose Order: ");
            String order = in.readLine();
            esql.executeQueryAndPrintResult("SELECT * FROM FoodOrder WHERE orderID = '" + order + "';");
            esql.executeQueryAndPrintResult("SELECT itemName, quantity FROM ItemsInOrder WHERE orderID = '" + order + "';");
         }
         
      } catch (Exception e){
         System.out.println("Error: " + e.getMessage());
      }
   }
   // Done
   public static void viewStores(PizzaStore esql) {
      try{
         esql.executeQueryAndPrintResult("SELECT * FROM Store");
      } catch (Exception e){
         System.out.println("Error: " + e.getMessage());
      }
   }
   // Done
   public static void updateOrderStatus(PizzaStore esql) {
      try {
         System.out.print("Choose Order: ");
         String order = in.readLine();
         System.out.print("Choose Status: ");
         String status = in.readLine();
         String query = "UPDATE FoodOrder SET orderStatus = '" + status + "' WHERE orderID = '" + order + "';";
         esql.executeUpdate(query);
      } catch (Exception e){
         System.out.println("Error: " + e.getMessage());
      }
   
   }
   // Done
   public static void updateMenu(PizzaStore esql) {
      try {
         System.out.print("Update existing item, or add new item: \n 1. Update existing \n 2. Add New \n 3. Exit");
         int choice = readChoice();
         String item, price, ingredients, type, description;
         switch(choice){
            case 1:
               System.out.print("Choose Item: ");
               item = in.readLine();
               System.out.print("New Price(0 for no change): ");
               price = in.readLine();
               System.out.print("New Ingredients(0 for no change): ");
               ingredients = in.readLine();
               System.out.print("New Type(0 for no change): ");
               type = in.readLine();
               System.out.print("New Description(0 for no change): ");
               description = in.readLine();            
               if(!price.equals("0")){
                  esql.executeUpdate("UPDATE Items SET price = '" + price + "' WHERE itemName = '" + item + "';");
               }
               if(!ingredients.equals("0")){
                  esql.executeUpdate("UPDATE Items SET ingredients = '" + ingredients + "' WHERE itemName = '" + item + "';");
               }
               if(!type.equals("0")){
                  esql.executeUpdate("UPDATE Items SET typeOfItem = '" + type + "' WHERE itemName = '" + item + "';");
               }
               if(!description.equals("0")){
                  esql.executeUpdate("UPDATE Items SET description = '" + description + "' WHERE itemName = '" + item + "';");
               }
               break;
            case 2:
               System.out.print("Item Name: ");
               item = in.readLine();
               System.out.print("Price: ");
               price = in.readLine();
               System.out.print("Ingredients: ");
               ingredients = in.readLine();
               System.out.print("Type: ");
               type = in.readLine();
               System.out.print("Description: ");
               description = in.readLine();  
               String query = "INSERT INTO Items (itemName, ingredients, typeOfItem, Price, description) VALUES ('" 
                  + item + "', '" 
                  + ingredients + "', '"
                  + type + "', '"
                  + price + "', '"
                  + description + "');";
               esql.executeUpdate(query);
               break;
         }
      } catch (Exception e){
         System.out.println("Error: " + e.getMessage());
      }
   }
   // Done
   public static void updateUser(PizzaStore esql) {
      try{
         System.out.print("Choose Login: ");
         String user = in.readLine();
         System.out.print("New Login(0 for no change): ");
         String newUser = in.readLine();
         System.out.print("New Password(0 for no change): ");
         String password = in.readLine();
         System.out.print("New Role(0 for no change): ");
         String role = in.readLine();
         System.out.print("New Favorite Items(0 for no change): ");
         String favItem = in.readLine();
         System.out.print("New Phone Number Items(0 for no change): ");
         String phone = in.readLine();
         if(!password.equals("0")){
            esql.executeUpdate("UPDATE Users SET password = '" + password + "' WHERE login = '" + user + "';");
         }
         if(!role.equals("0")){
            esql.executeUpdate("UPDATE Users SET role = '" + role + "' WHERE login = '" + user + "';");
         }
         if(!favItem.equals("0")){
            esql.executeUpdate("UPDATE Users SET favoriteItems = '" + favItem + "' WHERE login = '" + user + "';");
         }
         if(!phone.equals("0")){
            esql.executeUpdate("UPDATE Users SET phoneNum = '" + phone + "' WHERE login = '" + user + "';");
         }
         if(!newUser.equals("0")){
            esql.executeUpdate("UPDATE FoodOrder SET login = '" + newUser + "' WHERE login = '" + user + "';");
            esql.executeUpdate("UPDATE Users SET login = '" + newUser + "' WHERE login = '" + user + "';");
         }
      }
      catch(Exception e){
         System.out.println("Error: " + e.getMessage());
      }
   }


}//end PizzaStore

