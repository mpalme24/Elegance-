import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Calendar {
	private static Calendar self;
	private static java.util.Calendar calender;

	private Calendar() {
		calender = java.util.Calendar.getInstance();
	}

	public static Calendar instanceCalendar() {
		if (self == null) {
			self = new Calendar();
		}
		return self;
	}

	public void incrementDate(int days) {
		calender.add(java.util.Calendar.DATE, days);
	}

	public synchronized void setCalendarDate(Date date) {
		try {
			calender.setTime(date);
			calender.set(java.util.Calendar.HOUR_OF_DAY, 0);
			calender.set(java.util.Calendar.MINUTE, 0);
			calender.set(java.util.Calendar.SECOND, 0);
			calender.set(java.util.Calendar.MILLISECOND, 0);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public synchronized Date date() {
		try {
			calender.set(java.util.Calendar.HOUR_OF_DAY, 0);
			calender.set(java.util.Calendar.MINUTE, 0);
			calender.set(java.util.Calendar.SECOND, 0);
			calender.set(java.util.Calendar.MILLISECOND, 0);
			return calender.getTime();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public synchronized Date loanDueDate(int loanPeriod) {
		Date currentDate = date();
		calender.add(java.util.Calendar.DATE, loanPeriod);
		Date loanDueDate = calender.getTime();
		calender.setTime(currentDate);
		return loanDueDate;
	}

	public synchronized long getDaysDifference(Date targetDate) {
		long differenceMilliseconds = date().getTime() - targetDate.getTime();
		long differenceDays = TimeUnit.DAYS.convert(differenceMilliseconds, TimeUnit.MILLISECONDS);
		return differenceDays;
	}
}
