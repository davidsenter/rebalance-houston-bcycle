
public class Kiosk {
	private int stationID;
	private String name;
	private double latitude;
	private double longitude;
	private int bikesAvailable;
	private int docksAvailable;
	private int capacity;
	private String address;
	private int periodTotalReturn;
	private int periodTotalCheckout;
	private int periodDeltaNAvg;
	private int NR;

	public Kiosk(int stationID, String name, double latitude, double longitude, int bikesAvailable, int docksAvailable, String address) {
		this.stationID = stationID;
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.bikesAvailable = bikesAvailable;
		this.docksAvailable = docksAvailable;
		this.capacity = docksAvailable + bikesAvailable;
		this.address = address;
	}
	public int getID(){
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
	public void setPeriodTotalReturn(int periodTotalReturn){
		this.periodTotalReturn = periodTotalReturn;
	}
	public void setPeriodTotalCheckout(int periodTotalCheckout){
		this.periodTotalCheckout = periodTotalCheckout;
	}
	public void setPeriodDeltaNAvg(int periodDeltaNAvg){
		this.periodDeltaNAvg = periodDeltaNAvg;
	}
	public void setNR(int NR){
		this.NR = NR;
	}
}