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
	private SimpleDateFormat parseFormat;
	// period is either 0 (6:00 to 9:59), 1 (10:01 to 14:59), 2 (15:00 to 18:59), or 3 (19:00 to 23:59)

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
			this.endingDate = parseFormat.parse(endingDateString);
			//find time period for trip:
			if (this.startingDate.getHours() >= 6 && this.startingDate.getHours() <= 9) {
				this.startingPeriod = 0;
			}
			else if (this.startingDate.getHours() >= 10 && this.startingDate.getHours() <= 14) {
				this.startingPeriod = 1;
			}
			else if (this.startingDate.getHours() >= 15 && this.startingDate.getHours() <= 18) {
				this.startingPeriod = 2;
			}
			else if (this.startingDate.getHours() >= 19 && this.startingDate.getHours() <= 23) {
				this.startingPeriod = 3;
			}
			else {
				//System.out.println("Outside time period");
			}
			
			if (this.endingDate.getHours() >= 6 && this.endingDate.getHours() <= 9) {
				this.endingPeriod = 0;
			}
			else if (this.endingDate.getHours() >= 10 && this.endingDate.getHours() <= 14) {
				this.endingPeriod = 1;
			}
			else if (this.endingDate.getHours() >= 15 && this.endingDate.getHours() <= 18) {
				this.endingPeriod = 2;
			}
			else if (this.endingDate.getHours() >= 19 && this.endingDate.getHours() <= 23) {
				this.endingPeriod = 3;
			}
			else {
				//System.out.println("Outside time period");
			}
		}
		catch(Exception e){
		}
	}
	
	public String toString(){
		return "Start Time: " + startingTime + "; End Time: " + endingTime + "; Start Kiosk: " + startingKiosk + 
				"; End Kiosk: " + endingKiosk + "; Starting Date: " + startingDate.toString() + "; Ending Date: " 
				+ endingDate.toString() + "; Start Period: " + startingPeriod + "; End Period: " + endingPeriod;
	}
	
	public int getStartingPeriod() {
		return startingPeriod;
	}
	
	public int getEndingPeriod() {
		return endingPeriod;
	}
	public String getStartingKiosk() {
		return startingKiosk;
	}
	
	public String getEndingKiosk() {
		return endingKiosk;
	}
	public String getStartingDate() {
		return startingDate.toString();
	}
	public String getEndingDate() {
		return endingDate.toString();
	}
	public Date getStartingDateDate() {
		return startingDate;
	}
	public Date getEndingDateDate() {
		return endingDate;
	}
	
}