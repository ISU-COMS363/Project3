package jdbc;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import db_objects.Student;

public class P3 {
	private static Connection connection;
	private static final String databaseUrl = "jdbc:mysql://csdb.cs.iastate.edu:3306/db363mcgovern";
	private static final String userName = "dbu363mcgovern";
	private static final String password = "bDN3/FfF";
	
	public static void main(String[] args) throws Exception {
		loadAndRegisterDriver();
		try {
			makeConnection();
			
			// Do updates on Student
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("select s.StudentID, s.Classification, s.GPA, s.CreditHours from Student s");
			while (result.next()) {
				Student s = Student.makeFromSQLResult(result);
				s.useConnection(connection);
				System.out.println(":: Examining Student ::");
				System.out.println("Before updating student:");
				s.print();
				System.out.println("After updating student:");
				s.update();
				s.print();
			}
			statement.close();
			result.close();
			connection.commit();
			
			// Get and print students with top 5 GPA
			System.out.println("Getting students with top 5 GPAs");
			statement = connection.createStatement();
			result = statement.executeQuery("select s.GPA, p.Name, p2.Name as mentorName from Student s, Person p, Person p2 where s.classification = \"Senior\" and s.studentID = p.ID and p2.ID = s.MentorId order by s.GPA desc;");
			int numUniqueGPAs = 0;
			double lastGPA = -1;
			ArrayList<Student> students = new ArrayList<Student>();
			while (result.next()) {
				double GPA = result.getDouble("GPA");
				String studentName = result.getString("Name");
				String mentorName = result.getString("mentorName");
				if (lastGPA == -1) {
					numUniqueGPAs ++;
				}
				else if(lastGPA != GPA) {
					numUniqueGPAs ++;
				}
				Student s = new Student(studentName, mentorName, GPA);
				students.add(s);
				if (numUniqueGPAs == 5) {
					break;
				}
				System.out.println("Student Name: " + studentName + ", Mentor Name: " + mentorName + ", GPA: " + GPA);
				lastGPA = GPA;
			}
			writeStudentsToFile(students);
			closeConnection();
		}
		catch(SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		}
	}
	
	private static void loadAndRegisterDriver() {
		try{
			Class.forName("com.mysql.jdbc.Driver");
		}
		catch(Exception e) {
			System.err.println("Unable to load driver");
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private static void makeConnection() throws SQLException {
		connection = DriverManager.getConnection(databaseUrl, userName, password);
		connection.setAutoCommit(false);
		System.out.println("*** Connected to the database ***");
	}
	
	private static void closeConnection() throws SQLException {
		connection.close();
	}
	
	private static void writeStudentsToFile(ArrayList<Student> students) throws IOException {
		BufferedWriter out = null;
		try  
		{
		    FileWriter fstream = new FileWriter("P3Output.txt"); //true tells to append data.
		    out = new BufferedWriter(fstream);
		    out.write("Student Name :: Mentor Name :: GPA\n\n");
		    for (Student s : students) {
		    	out.write(s.name + " :: " + s.mentorName + " :: " + s.GPA + "\n");
		    }
		}
		catch (IOException e)
		{
		    System.err.println("Error: " + e.getMessage());
		}
		finally
		{
		    if(out != null) {
		        out.close();
		    }
		}
	}
}
