package test;

public class Exam{
	
	Student student;
	String course;
	int exam_year;
	String result;
	
	
	public Exam(Student student, String course, int year, boolean result) {
		super();
		this.student = student;
		this.course = course;
		this.exam_year = year;
		this.result = result ? "passed" : "failed";
	}

	public Exam() {
		// TODO Auto-generated constructor stub
	}
	
	public Student getStudent() {
		return student;
	}
	public void setStudent(Student student) {
		this.student = student;
	}
	
	public String getCourse() {
		return course;
	}

	public void setCourse(String course) {
		this.course = course;
	}

	public int getExam_year() {
		return exam_year;
	}

	public void setExam_year(int exam_year) {
		this.exam_year = exam_year;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	
	
	
}
