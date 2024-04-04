package data;

public class TaxiRide {

	float amount;
	String period;
	int dayOfTheWeek;
	double distance;
	int passenger;
	
	public TaxiRide() {
		// TODO Auto-generated constructor stub
	}

	public TaxiRide(float amount, String period, int dayOfTheWeek, double distance, int passenger) {
		super();
		this.amount = amount;
		this.period = period;
		this.dayOfTheWeek = dayOfTheWeek;
		this.distance = distance;
		this.passenger = passenger;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public int getDayOfTheWeek() {
		return dayOfTheWeek;
	}

	public void setDayOfTheWeek(int dayOfTheWeek) {
		this.dayOfTheWeek = dayOfTheWeek;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public int getPassenger() {
		return passenger;
	}

	public void setPassenger(int passenger) {
		this.passenger = passenger;
	}
	
}
