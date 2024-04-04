package test;

public class Attempt {

	int year;
	String degree;
	public Attempt(int year, String degree) {
		super();
		this.year = year;
		this.degree = degree;
	}
	public Attempt() {
		super();
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
