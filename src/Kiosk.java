
public class Kiosk {
	private String stationID;
	private String name;
	private double latitude;
	private double longitude;
	private int bikesAvailable;
	private int docksAvailable;
	private int capacity;
	private String address;
	private int periodDeltaN;
	private int periodDeltaNAvg;
	private int NR;

	public Kiosk(String stationID, String name, String address, double latitude, double longitude) {
		this.stationID = stationID;
		this.name = name;
		this.address = address;
		this.longitude = longitude;
		this.latitude = latitude;
			
	}
	public String getID(){
		return stationID;
	}
	public String getName(){
		return name;
	}
	public String getAddress(){
		return address;
	}
	public double getLat(){
		return latitude;
	}
	public double getLon(){
		return longitude;
	}
	public int getN(){
		return bikesAvailable;
	}
	public int getDocks(){
		return docksAvailable;
	}
	public int getCap(){
		return capacity;
	}
	public void setName(String name){
		this.name =  name;
	}
	public void setDocks(int docksAvailable){
		this.docksAvailable = docksAvailable;
	}
	public void setBikes(int bikesAvailable){
		this.bikesAvailable = bikesAvailable;
	}
	public void setPeriodDeltaN(int periodTotalReturn){
		this.periodDeltaN = periodTotalReturn;
	}
	public void setPeriodDeltaNAvg(int periodDeltaNAvg){
		this.periodDeltaNAvg = periodDeltaNAvg;
	}
	public void setNR(int NR){
		this.NR = NR;
	}
	
	public String toString(){
		return "Station ID: " + stationID + ";  Name: " + name + ";  Bikes Avail: " + bikesAvailable + ";  Docks Avail: "+ docksAvailable;
	}
}