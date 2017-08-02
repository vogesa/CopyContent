package p;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MyDate {

	private String year;
	private String month;
	private String day;

	public MyDate(String year, String month, String day) {
		this.year = year;
		this.month = month;
		this.day = day;
	}
	
	public MyDate(Date date) {
		SimpleDateFormat yyyy = new SimpleDateFormat("yyyy");
		SimpleDateFormat mm = new SimpleDateFormat("MM");
		SimpleDateFormat dd = new SimpleDateFormat("dd");
		this.year = yyyy.format(date);
		this.month = mm.format(date);
		this.day = dd.format(date);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder("");
		if (StringUtil.isNull(day) && StringUtil.isNull(month)) {
			sb.append(year);
		}
		else if (StringUtil.isNull(day)) {
			sb.append(year).append("-").append(month);
		}
		else {
			sb.append(year).append("-").append(month).append("-").append(day);
		}
		return sb.toString();
	}

}
