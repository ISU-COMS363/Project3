package db_objects;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Enrollment {
	public String grade;
	public static final int CREDIT_HOURS = 3;
	
	public Enrollment(String grade) {
		this.grade = grade.trim().toLowerCase();
	}
	
	public static Enrollment makeFromSQLResult(ResultSet result) throws SQLException {
		return new Enrollment(result.getString("grade"));
	}
	
	public double getGPA() throws Exception {
		if (grade.equals("a")) {
			return 4.00;
		}
		else if(grade.equals("a-")) {
			return 3.66;
		}
		else if(grade.equals("b+")) {
			return 3.33;
		}
		else if(grade.equals("b")) {
			return 3.00;
		}
		else if(grade.equals("b-")) {
			return 2.66;
		}
		else if(grade.equals("c+")) {
			return 2.33;
		}
		else if(grade.equals("c")) {
			return 2.00;
		}
		else if(grade.equals("c-")) {
			return 1.66;
		}
		else if(grade.equals("d+")) {
			return 1.33;
		}
		else if(grade.equals("d")) {
			return 1.00;
		}
		else if(grade.equals("f")) {
			return 0.00;
		}
		throw new Exception("Invalid grade given");
	}
}
