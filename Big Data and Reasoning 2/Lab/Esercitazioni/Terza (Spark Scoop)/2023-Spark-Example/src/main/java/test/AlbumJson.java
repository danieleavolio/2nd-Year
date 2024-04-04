package test;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class AlbumJson {
	
	Map<String, Integer> songs;
	
	public AlbumJson() {
		// TODO Auto-generated constructor stub
		songs=new HashMap<String, Integer>();
	}

	public Map<String, Integer> getSongs() {
		return songs;
	}

	public void setSongs(Map<String, Integer> songs) {
		this.songs = songs;
	}

	public boolean containsMorePopular(AlbumJson v2) {
		// TODO Auto-generated method stub
		int v1Best = getHighestPopularity();
		int v2Best = getHighestPopularity();
		
		return v1Best >= v2Best;
	}

	public int getHighestPopularity() {
		// TODO Auto-generated method stub
		int max = -1;
		for (Entry<String, Integer> pair: songs.entrySet()) {
			if(pair.getValue() > max) {
				max=pair.getValue();
			}
		}
		return max;
	}

	public Song getMostPopular() {
		// TODO Auto-generated method stub
		Song highest = new Song();
		highest.setPopularity(-1);
		for (Entry<String, Integer> pair: songs.entrySet()) {
			if(pair.getValue() > highest.getPopularity()) {
				highest.setPopularity(pair.getValue());
				highest.setTitle(pair.getKey());
			}
		}
		return highest;
	}
	
	

}
