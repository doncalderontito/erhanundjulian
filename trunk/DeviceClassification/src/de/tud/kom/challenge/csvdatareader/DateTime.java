package de.tud.kom.challenge.csvdatareader;

/**
 * @author Andreas Schaller
 * @author Frank Englert
 * 
 */
public class DateTime implements Comparable<DateTime> {
	
	private final int year, month, day, hour, minute, second;
	
	public DateTime(final int year, final int month, final int day, final int hour, final int minute, final int second) {
		this.year = year;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.minute = minute;
		this.second = second;
	}
	
	public DateTime(final DateTime date) {
		this.year = date.year;
		this.month = date.month;
		this.day = date.day;
		this.hour = date.hour;
		this.minute = date.minute;
		this.second = date.second;
	}
	
	public int getYear() {
		return this.year;
	}
	
	public int getMonth() {
		return this.month;
	}
	
	public int getDay() {
		return this.day;
	}
	
	public int getHour() {
		return this.hour;
	}
	
	public int getMinute() {
		return this.minute;
	}
	
	public int getSecond() {
		return this.second;
	}
	
	public DateTime getDate() {
		return new DateTime(this.year, this.month, this.day, 0, 0, 0);
	}
	
	public static DateTime fromLong(long value) {
		final int second = (int) (value % 60);
		value /= 60;
		final int minute = (int) (value % 60);
		value /= 60;
		final int hour = (int) (value % 24);
		value /= 24;
		final int day = (int) (value % 30);
		value /= 30;
		final int month = (int) (value % 12);
		value /= 12;
		final int year = (int) (value);
		
		return new DateTime(year, month, day, hour, minute, second);
	}
	
	public long toLong() {
		final long tmpMonth = this.year * 12;
		final long tmpDay = (tmpMonth + this.month) * 30;
		final long tmpHour = (tmpDay + this.day) * 24;
		final long tmpMinute = (tmpHour + this.hour) * 60;
		final long tmpSec = (tmpMinute + this.minute) * 60;
		
		return tmpSec + this.second;
	}
	
	public int getDiffInSeconds(final DateTime o) {
		return (int) Math.abs((this.toLong() - o.toLong()));
	}
	
	public int compareTo(final DateTime o) {
		return ((Long) this.toLong()).compareTo(o.toLong());
	}
	
	public DateTime addSeconds(final int second) {
		return DateTime.fromLong((this.toLong() + second));
	}
	
	public boolean isBefore(final DateTime date) {
		return this.toLong() < date.toLong();
	}
	
	public boolean isAfter(final DateTime date) {
		return !this.isBefore(date);
	}
	
	public String toTimeString() {
		return this.format(this.hour) + ":" + this.format(this.minute) + ":" + this.format(this.second);
	}
	
	private String format(final int num) {
		return (num < 10 ? "0" : "") + num;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if(!(obj instanceof DateTime)) {
			return false;
		}
		return this.equals((DateTime) obj);
	}
	
	public boolean equals(final DateTime other) {
		return other.toLong() == this.toLong();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + this.day;
		result = (prime * result) + this.hour;
		result = (prime * result) + this.minute;
		result = (prime * result) + this.month;
		result = (prime * result) + this.second;
		result = (prime * result) + this.year;
		return result;
	}
	
	@Override
	public String toString() {
		return this.year + "." + this.format(this.month) + "." + this.format(this.day) + " " + this.toTimeString();
	}
	
}
