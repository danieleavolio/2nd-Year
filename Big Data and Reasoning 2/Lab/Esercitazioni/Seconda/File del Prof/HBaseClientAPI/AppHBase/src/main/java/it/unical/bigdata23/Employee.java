package it.unical.bigdata23;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import java.util.Map.Entry;

public class Employee {

	public static int AUTO_INCREMENT=0;
	String name;
	String surname;
	String city;
	String street;
	Map<String, Integer> skills;
	
	public Employee() {
		// TODO Auto-generated constructor stub
		generateRandom();
	}

	private void generateRandom() {
		// TODO Auto-generated method stub
		Random r = new Random();
		name=Constants.PERSON_NAMES[r.nextInt(Constants.PERSON_NAMES.length)];
		surname=Constants.PERSON_SURNAMES[r.nextInt(Constants.PERSON_SURNAMES.length)];
		city=Constants.CITIES[r.nextInt(Constants.CITIES.length)];
		street="street"+r.nextInt(256);
		int num_skills=r.nextInt(Constants.LANGUAGES.length)+1;
		skills=new HashMap<String, Integer>();
		for(int i=0;i<num_skills;i++) {
			int lang = num_skills>=Constants.LANGUAGES.length ? i%Constants.LANGUAGES.length : r.nextInt(Constants.LANGUAGES.length);
			while(skills.containsKey(Constants.LANGUAGES[lang])) {
				lang = r.nextInt(Constants.LANGUAGES.length);
			}
			skills.put(Constants.LANGUAGES[lang],r.nextInt(10)+1);
		}
		AUTO_INCREMENT++;
	}

	public Employee(String name, String surname, String city, String street, Map<String, Integer> skills) {
		super();
		this.name = name;
		this.surname = surname;
		this.city = city;
		this.street = street;
		this.skills = skills;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public Map<String, Integer> getSkills() {
		return skills;
	}

	public void setSkills(Map<String, Integer> skills) {
		this.skills = skills;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String result = "Employee: "+name+" "+surname+", "+city+" "+street+"\nSkills: ";
		for(Entry<String, Integer> skill:skills.entrySet()) {
			result+=skill.getKey()+": "+skill.getValue()+"	";
		}
		return result;
	}
}
