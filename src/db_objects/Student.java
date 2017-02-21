package db_objects;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Student {
	public String studentId;
	public String name;
	public String mentorName;
	public String classification;
	public int creditHours;
	public double GPA;
	Connection connection;
	ArrayList<Enrollment> enrollments;
	
	public static final String CLASSIFICATION_FRESHMAN = "Freshman";
	public static final String CLASSIFICATION_SOPHOMORE = "Sophomore";
	public static final String CLASSIFICATION_JUNIOR = "Junior";
	public static final String CLASSIFICATION_SENIOR = "Senior";
	
	public Student(String studentId, String classification, int creditHours, double GPA) {
		this.studentId = studentId;
		this.classification = classification;
		this.creditHours = creditHours;
		this.GPA = GPA;
		this.connection = null;
		this.enrollments = new ArrayList<Enrollment>();
	}
	
	public Student(String studentName, String mentorName, double GPA) {
		this.name = studentName;
		this.mentorName = mentorName;
		this.GPA = GPA;
	}
	
	public static Student makeFromSQLResult(ResultSet result) throws SQLException {
		return new Student(result.getString("StudentID"), result.getString("classification"), result.getInt("creditHours"), result.getDouble("GPA"));
	}
	
	public void useConnection(Connection connection) {
		this.connection = connection;
	}
	
	public void print() {
		System.out.println("ID: " + studentId + ", gpa: " + GPA + ", hours: " + creditHours + ", classification: " + classification);
	}
	
	public void update() throws Exception {
		setCourses();
		this.GPA = calculateNewGPA();
		this.creditHours = computeNewCreditHours();
		this.classification = getNewClassification();
		PreparedStatement statement = connection.prepareStatement("update Student set GPA=?, CreditHours=?, Classification=? where StudentID=?");
		statement.setDouble(1, GPA);
		statement.setInt(2, creditHours);
		statement.setString(3, classification);
		statement.setString(4, studentId);
		statement.executeUpdate();
		statement.close();
	}
	
	private double calculateNewGPA() throws Exception {
		double avgNumerator = this.GPA * this.creditHours;
		int avgDenominator = computeNewCreditHours();
		for (Enrollment e : enrollments) {
			avgNumerator += (Enrollment.CREDIT_HOURS * e.getGPA());
		}
		return avgNumerator / avgDenominator;
	}
	
	private int computeNewCreditHours() {
		return creditHours + (enrollments.size() * Enrollment.CREDIT_HOURS);
	}
	
	private String getNewClassification() {
		if (creditHours <= 29) {
			return CLASSIFICATION_FRESHMAN;
		}
		else if(creditHours <= 59) {
			return CLASSIFICATION_SOPHOMORE;
		}
		else if(creditHours <= 89) {
			return CLASSIFICATION_JUNIOR;
		}
		return CLASSIFICATION_SENIOR;
	}
	
	private void setCourses() throws SQLException {
		raiseExceptionIfConnectionNotSet();
		Statement s = connection.createStatement();
		ResultSet result = s.executeQuery("select e.Grade from Enrollment e where e.studentID = " + studentId);
		while(result.next()) {
			Enrollment e = Enrollment.makeFromSQLResult(result);
			enrollments.add(e);
		}
	}
	
	private void raiseExceptionIfConnectionNotSet() throws SQLException {
		if (connection == null) {
			throw new SQLException("Connection not set");
		}
	}
}
