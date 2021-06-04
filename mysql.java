import java.sql.*;
import java.util.*;
import java.sql.SQLException;
import java.text.ParseException;

//	Name: Steven Phung
//	Lab: #4
//	Due: December 5th, 2018
//	Course: cs-4350-02-f18

//	Description:
//		A program that uses mysql and JDBC to implement a "Pomona transit system".


public class mysql {
	public static void main(String[] args) throws ParseException {
		Connection connection;
		Statement statement;
		Scanner scanner = new Scanner(System.in);
		String userInput = "";
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://localhost/cs4350? user=root&password=_________");
			statement = connection.createStatement();
			printMenu();
			while(true) {
				System.out.print("Enter command: ");
				userInput = scanner.nextLine();
				if(userInput.trim().equals("1"))
					displaySchedule(statement);
				else if(userInput.trim().equals("2"))
					deleteTripOffering(statement);
				else if(userInput.trim().equals("3"))
					addTripOffering(statement);
				else if(userInput.trim().equals("4"))
					changeDriver(statement);
				else if(userInput.trim().equals("5"))
					changeBus(statement);
				else if(userInput.trim().equals("6"))
					displayTripStops(statement);
				else if(userInput.trim().equals("7"))
					displayWeekly(statement);
				else if(userInput.trim().equals("8"))
					addDriver(statement);
				else if(userInput.trim().equals("9"))
					addBus(statement);
				else if(userInput.trim().equals("10"))
					deleteBus(statement);
				else if(userInput.trim().equals("11"))
					insertTripData(statement);
				else if(userInput.trim().charAt(0) == 'q')
					System.exit(0);
				else
					printMenu();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		scanner.close();
	}
	
	private static void printMenu() {
		System.out.println("1:\tDisplay a schedule");
		System.out.println("2:\tDelete a trip offering");
		System.out.println("3:\tAdd a trip offering");
		System.out.println("4:\tChange the driver for a trip");
		System.out.println("5:\tChange the bus for a trip");
		System.out.println("6:\tDisplay stops for a trip");
		System.out.println("7:\tDisplay weekly schedule for a driver");
		System.out.println("8:\tAdd a driver");
		System.out.println("9:\tAdd a bus");
		System.out.println("10:\tDelete a bus");
		System.out.println("11:\tInsert actual trip info");
		System.out.println("h:\tPrint menu");
		System.out.println("q:\tQuit");
		}
	
	public static void displaySchedule(Statement s) throws SQLException {
		Scanner scan = new Scanner(System.in);
		System.out.print("Start Location Name: ");
		String startLocation = scan.nextLine().trim();
		System.out.print("Destination Name: ");
		String destinationLocation = scan.nextLine().trim();
		System.out.print("Date: ");
		String date = scan.nextLine().trim();
		
		try{
			ResultSet rs = s.executeQuery("SELECT T0.ScheduledStartTime, T0.ScheduledArrivalTime, T0.DriverName, T0.BusID "
					+ "FROM TripOffering T0, Trip T1 "
					+ "WHERE T1.StartLocationName LIKE '" + startLocation + "' AND "
					+ "T1.DestinationName LIKE '" + destinationLocation + "' AND "
					+ "T0.Date = '" + date + "' AND "
					+ "T1.TripNumber = T0.TripNumber "
					+ "Order by ScheduledStartTime ");
			ResultSetMetaData rsmd = rs.getMetaData();
			int column = rsmd.getColumnCount();
			for(int i = 1; i <= column; i++){
				System.out.print(rsmd.getColumnName(i) + "\t");
			}
			System.out.println();
			while(rs.next()) {
				for(int i = 1; i <= column; i++)
					System.out.print(rs.getString(i) + "\t\t");
				System.out.println();
			}
			rs.close();
			System.out.println();
		} catch (SQLException e) {
			System.out.println("No schedule from " + startLocation + " to " + destinationLocation + " on " + date);
		}
	}
	
	public static void deleteTripOffering(Statement s) throws SQLException {
		@SuppressWarnings("resource")
		Scanner scan = new Scanner(System.in);
		System.out.print("Start Trip Number: ");
		String tripNumber = scan.nextLine().trim();
		System.out.print("Date: ");
		String date = scan.nextLine().trim();
		System.out.print("Scheduled Start Time: ");
		String startTime = scan.nextLine().trim();
		
		try {
			if(s.executeUpdate("DELETE FROM TripOffering "
								+ "WHERE TripNumber = '" + tripNumber + "' AND "
								+ "Date = '" + date + "' AND "
								+ "ScheduledStartTime = '" + startTime + "'") == 0) {
				System.out.println("No Trip Offering with Trip Number: " + tripNumber + " on " + date + " starting at " + startTime);
			} else
				System.out.println("Successfully deleted Trip Offering");
		} catch (SQLException e) {
			System.out.println("No Trip Offering with Trip Number: " + tripNumber + " on " + date + " starting at " + startTime);
		}
	}
	
	public static void addTripOffering(Statement s) throws SQLException {
		@SuppressWarnings("resource")
		Scanner scan = new Scanner(System.in);
		while(true) {
			System.out.print("Enter Trip Number: ");
			String tripNumber = scan.nextLine().trim();
			System.out.print("Date: ");
			String date = scan.nextLine().trim();
			System.out.print("Scheduled Start Time: ");
			String startTime = scan.nextLine().trim();
			System.out.print("Scheduled Arrival Time: ");
			String arrivalTime = scan.nextLine().trim();
			System.out.print("Driver Name: ");
			String driver = scan.nextLine().trim();
			System.out.print("Bus ID: ");
			String bus = scan.nextLine().trim();

			try {
				s.execute("INSERT INTO TripOffering VALUES ('" + tripNumber + "', '" + date + "', '" + startTime + "', '" + arrivalTime + "', '" + driver + "', '" + bus + "')");
				System.out.println("Successfully added a new Trip Offering");
			} catch (SQLException e) {
				System.out.println("Check input formatting");
			}
			System.out.println("Add another Trip Offering? (y/n): ");
			String input = scan.nextLine();
			if(input.trim().charAt(0) == 'y') {
			} else
				break;
		}
	}
	
	public static void changeDriver(Statement s) throws SQLException {
		@SuppressWarnings("resource")
		Scanner scan = new Scanner(System.in);
		System.out.print("New Driver Name: ");
		String driver = scan.nextLine().trim();
		System.out.print("Start Trip Number: ");
		String tripNumber = scan.nextLine().trim();
		System.out.print("Date: ");
		String date = scan.nextLine().trim();
		System.out.print("Scheduled Start Time: ");
		String startTime = scan.nextLine().trim();
		
		try {
			if(s.executeUpdate("UPDATE TripOffering "
								+ "SET DriverName = '" + driver + "' "
								+ "WHERE TripNumber = '" + tripNumber + "' AND "
								+ "Date = '" + date + "' AND "
								+ "ScheduledStartTime = '" + startTime + "'") == 0) {
				System.out.println("No Trip Offering with Trip Number: " + tripNumber + " on "+ date + " starting at "+ startTime);
			} else
				System.out.println("Successfully updated Driver");
		} catch (SQLException e) {
			System.out.println("No such Trip Offering or Driver in database");
		}
	}
	
	public static void changeBus(Statement s) throws SQLException {
		@SuppressWarnings("resource")
		Scanner scan = new Scanner(System.in);
		System.out.print("New Bus Number: ");
		String bus = scan.nextLine().trim();
		System.out.print("Start Trip Number: ");
		String tripNumber = scan.nextLine().trim();
		System.out.print("Date: ");
		String date = scan.nextLine().trim();
		System.out.print("Scheduled Start Time: ");
		String startTime = scan.nextLine().trim();
		
		try {
			if(s.executeUpdate("UPDATE TripOffering "
								+ "SET BusID = '" + bus + "' "
								+ "WHERE TripNumber = '" + tripNumber + "' AND "
								+ "Date = '" + date + "' AND "
								+ "ScheduledStartTime = '" + startTime + "'") == 0) {
				System.out.println("No Trip Offering with Trip Number: " + tripNumber + " on "+ date + " starting at "+ startTime);
			} else
				System.out.println("Successfully updated Bus");
		} catch (SQLException e) {
			System.out.println("No such Trip Offering or Bus Number in database");
		}
	}
	
	public static void displayTripStops(Statement s) throws SQLException {
		Scanner scan = new Scanner(System.in);
		System.out.print("Trip Number: ");
		String tripNumber = scan.nextLine().trim();
		
		try {
			ResultSet rs = s.executeQuery("SELECT * "
										+ "FROM TripStopInfo "
										+ "WHERE TripNumber = '" + tripNumber + "' "
										+ "Order By SequenceNumber ");
			ResultSetMetaData rsmd = rs.getMetaData();
			int column = rsmd.getColumnCount();
			for(int i = 1; i <= column; i++) {
				System.out.print(rsmd.getColumnName(i) + "\t");
			}
			System.out.println();
			while(rs.next()) {
				for(int i = 1; i <= column; i++)
					System.out.print(rs.getString(i) + "\t\t");
				System.out.println();
			}
			rs.close();
		} catch (SQLException e) {
			System.out.println("Trip Number: '" + tripNumber + "' does not exist");
		}
	}
	
	public static void displayWeekly(Statement s) throws ParseException, SQLException {
		@SuppressWarnings("resource")
		Scanner scan = new Scanner(System.in);
		System.out.print("Driver name: ");
		String driver = scan.nextLine().trim();
		System.out.print("Date: ");
		String dateString = scan.nextLine().trim();
		
		for(int i = 0; i < 7; i++) {
			try {
				ResultSet rs = s.executeQuery("SELECT TripNumber, Date, ScheduledStartTime, ScheduledArrivalTime, BusID "
											+ "FROM TripOffering "
											+ "WHERE DriverName LIKE '" + driver + "' "
											+ "AND Date = '" + dateString + "' "
											+ "Order By ScheduledStartTime ");
				ResultSetMetaData rsmd = rs.getMetaData();
				int column = rsmd.getColumnCount();
				if(i == 0) {
					System.out.println("Day 1");
					for(int j = 1; j <= column; j++) {
						if(j == 1 || j == 3)
							System.out.print(rsmd.getColumnName(j) + "\t");
						else
							System.out.print(rsmd.getColumnName(j) + "\t\t");
					}
					System.out.println();
				}
				while(rs.next()) {
					for(int j = 1; j <= column; j++)
						System.out.print(rs.getString(j) + "\t\t");
					System.out.println();
				}
				rs.close();
			} catch(SQLException e) {
				System.out.println("Check input formatting");
			}
			if(i < 6)
				System.out.println("Day " + (i+2));
		}
	}
	
	public static void addDriver(Statement s) throws SQLException {
		@SuppressWarnings("resource")
		Scanner scan = new Scanner(System.in);
		System.out.print("Driver name: ");
		String driver = scan.nextLine().trim();
		System.out.print("Phone number: ");
		String phone = scan.nextLine().trim();
		
		try {
			s.execute("INSERT INTO Driver VALUES ('" + driver + "', '" + phone + "')");
			System.out.println("Successfully added a new Driver");
		} catch (SQLException e) {
			System.out.println("Check input formatting");
		}
	}
	
	public static void addBus(Statement s) throws SQLException {
		@SuppressWarnings("resource")
		Scanner scan = new Scanner(System.in);
		System.out.print("Bus ID: ");
		String bus = scan.nextLine().trim();
		System.out.print("Bus model: ");
		String model = scan.nextLine().trim();
		System.out.print("Bus year: ");
		String year = scan.nextLine().trim();
		
		try {
			s.execute("INSERT INTO Bus VALUES ('" + bus + "', '" + model + "', '" + year + "')");
			System.out.println("Successfully added a new Bus");
		} catch (SQLException e) {
			System.out.println("Check input formatting");
		}
	}
	
	public static void deleteBus(Statement s) throws SQLException {
		@SuppressWarnings("resource")
		Scanner scan = new Scanner(System.in);
		System.out.print("Bus ID: ");
		String bus = scan.nextLine().trim();
		
		try {
			if(s.executeUpdate("DELETE FROM Bus WHERE BusID = '" + bus + "'") == 0) {
				System.out.println("No Bus ID = " + bus);
			} else {
				System.out.println("Successfully deleted");
			}
		} catch(SQLException e) {
			System.out.println("No Bus ID = " + bus);
		}
	}
	
	public static void insertTripData(Statement s) throws SQLException {
		@SuppressWarnings("resource")
		Scanner scan = new Scanner(System.in);
		System.out.print("Trip Number: ");
		String tripNumber = scan.nextLine().trim();
		System.out.print("Date: ");
		String date = scan.nextLine().trim();
		System.out.print("Scheduled Start Time: ");
		String startTime = scan.nextLine().trim();
		System.out.print("Stop Number: ");
		String stopTime = scan.nextLine().trim();
		System.out.print("Scheduled Arrival Time: ");
		String arrivalTime = scan.nextLine().trim();
		System.out.print("Actual Start Time: ");
		String actualStart = scan.nextLine().trim();
		System.out.print("Actual Arrival Time: ");
		String actualArrival = scan.nextLine().trim();
		System.out.print("Passengers in: ");
		String passengersIn = scan.nextLine().trim();
		System.out.print("Passengers out: ");
		String passengersOut = scan.nextLine().trim();
		
		try {
			s.execute("INSERT INTO ActualTripStopInfo VALUES ('" + tripNumber + "', '" + date + "', '" + startTime + "', '" + stopTime + "', '" + arrivalTime
					 + "', '" + actualStart + "', '" + actualArrival + "', '" + passengersIn + "', '" + passengersOut + "')");
			System.out.println("Successfully recorded data");
		} catch(SQLException e) {
			System.out.println("Check input formatting");
		}
	}
}