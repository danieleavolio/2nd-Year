package test;

public class Song {

	String title;
	int popularity;
	
	public Song() {
		// TODO Auto-generated constructor stub
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getPopularity() {
		return popularity;
	}
	public void setPopularity(int popularity) {
		this.popularity = popularity;
	}

	public boolean morePopularThan(Song v2) {
		// TODO Auto-generated method stub
		return popularity >= v2.getPopularity();
	}
	
	
}
