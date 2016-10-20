import java.text.SimpleDateFormat;
import java.util.Date;

public class Trip {
	private String startingTime;
	private String endingTime;
	// time format: "11:00 AM" "1:00 PM"
	private String startingKiosk;
	private String endingKiosk;
	private Date startingDate;
	private Date endingDate;
	private int startingPeriod;
	private int endingPeriod;
	private SimpleDateFormat displayFormat;
	private SimpleDateFormat parseFormat;
	// period is either 1 (6:00 to 10:00), 2 (10:01 to 15:00), 3 (15:01 to 19:00), or 4 (19:01 to 23:59)
	
	public Trip(String startingTime, String endingTime, String startingKiosk, String endingKiosk, String startingDate, String endingDate) {
		this.startingTime = startingTime;
		this.endingTime = endingTime;
		this.startingKiosk = startingKiosk;
		this.endingKiosk = endingKiosk;
		
	    parseFormat = new SimpleDateFormat("hh:mm a MM/dd/yyyy");
	    String startingDateString = startingTime + " " + startingDate;
	    String endingDateString = endingTime + " " + endingDate;
	    
		try { 
			this.startingDate = parseFormat.parse(startingDateString);
		}
		catch(Exception e){
		}
		//find time period for trip:
		
	}
	
	public String toString(){
		return "Start Time: " + startingTime + "; End Time: " + endingTime + "; Start Kiosk: " + startingKiosk + 
				"; End Kiosk: " + endingKiosk + "; Starting Date: " + startingDate.toString();
	}
	
}