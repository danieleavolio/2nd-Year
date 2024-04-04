package it.unical.bigdata23;

import org.apache.hadoop.hbase.TableName;

public class Constants {

	public static final String EMPLOYEE="employee";
	public static final String USERINFO="userinfo";
	public static final String LIVES="lives";
	public static final String SKILLS="skills";
	
	public static final String NAME="name";
	public static final String SURNAME="surname";
	public static final String CITY="city";
	public static final String STREET="street";
	
	public static final String STATS = "stats";
	public static final String EMPSTATS = "empstats";
	
	public static final byte[] EMPLOYEE_BYTE="employee".getBytes();
	public static final byte[] USERINFO_BYTE="userinfo".getBytes();
	public static final byte[] LIVES_BYTE="lives".getBytes();
	public static final byte[] SKILLS_BYTE="skills".getBytes();
	
	public static final byte[] NAME_BYTE="name".getBytes();
	public static final byte[] SURNAME_BYTE="surname".getBytes();
	public static final byte[] CITY_BYTE="city".getBytes();
	public static final byte[] STREET_BYTE="street".getBytes();
	
	public static final byte[] EMPSTATS_BYTE = EMPSTATS.getBytes();
	public static final byte[] STATS_BYTE = STATS.getBytes();
	
	public static final TableName EMPLOYEE_TABLE_NAME=TableName.valueOf(EMPLOYEE_BYTE);
	
	public static final String[] PERSON_NAMES= {"pippo","pluto","mario","franco"};
	public static final String[] PERSON_SURNAMES= {"rossi","verdi"};
	public static final String[] CITIES= {"c1","c2","c3"};
	public static final String[] LANGUAGES= {"Java","Python","C++","SQL"};
}
