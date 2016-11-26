
public class Kiosk {
	private String stationID;
	private String name;
	private double latitude;
	private double longitude;
	private int bikesAvailable;
	private int docksAvailable;
	private int capacity;
	private String address;
	private int[] periodDeltaN;
	private double[] periodDeltaNAvg;
	private int[] NR;

	public Kiosk(String stationID, String name, String address, double latitude, double longitude) {
		this.stationID = stationID;
		this.name = name;
		this.address = address;
		this.longitude = longitude;
		this.latitude = latitude;
		this.periodDeltaN = new int[4];
		this.periodDeltaNAvg = new double[4];
		this.NR = new int[4]; 
			
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
	public int[] getPeriodDeltaN(){
		return periodDeltaN;
	}
	public double[] getPeriodDeltaNAvg(){
		return periodDeltaNAvg;
	}
	public int getCap(){
		return capacity;
	}
	public void setCap(int cap){
		this.capacity = cap;
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
	public void setPeriodDeltaN(int[] periodDeltaN){
		this.periodDeltaN = periodDeltaN;
	}
	public void setPeriodDeltaNAvg(double[] periodDeltaNAvg){
		this.periodDeltaNAvg = periodDeltaNAvg;
	}
	public void setNR(int[] NR){
		this.NR = NR;
	}

	public int[] getNR(){
		return this.NR;
	}
	
	public String toString(){
		return "Station ID: " + stationID + ";  Name: " + name + ";  Bikes Avail: " + bikesAvailable + ";  Docks Avail: "+ docksAvailable + ";  ";
	}
}