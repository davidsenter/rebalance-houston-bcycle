import java.text.SimpleDateFormat;

public class Trip {
	private String startingTime;
	private String endingTime;
	// time format: "11:00 AM" "1:00 PM"
	private String startingKiosk;
	private String endingKiosk;
	private int startingPeriod;
	private int endingPeriod;
	// period is either 1 (6:00 AM to 10:00 AM), 2 (10:01 AM to 3:00 PM), 3 (3:01 PM to 7:00 PM), or 4 (7:01 PM to 11:59 PM)
	
	public Trip(String startingTime, String endingTime, String startingKiosk, String endingKiosk) {
		this.startingTime = startingTime;
		this.endingTime = endingTime;
		this.startingKiosk = startingKiosk;
		this.endingKiosk = endingKiosk;
		//find time period for trip:
	}
	
}