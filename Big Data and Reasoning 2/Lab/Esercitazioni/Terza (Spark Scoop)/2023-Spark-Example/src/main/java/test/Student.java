package test;

public class Student {

	String student_id;
	int year;
	String degree;
	
	public Student(String student_id, int year, String degree) {
		super();
		this.student_id = student_id;
		this.year = year;
		this.degree = degree;
	}

	public Student() {
		super();
	}

	

	public String getStudent_id() {
		return student_id;
	}

	public void setStudent_id(String student_id) {
		this.student_id = student_id;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getDegree() {
		return degree;
	}

	public void setDegree(String degree) {
		this.degree = degree;
	}
	
	
}
