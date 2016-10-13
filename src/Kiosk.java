
public class Kiosk {
	private double latitude;
	private double longitude;
	private String kioskID;
	private String referenceName;

	public Kiosk(double lat, double lon, String id, String refName) {
		latitude = lat;
		longitude = lon;
		kioskID = id;
		referenceName = refName;
	}
	
	public double getLat(){
		return latitude;
	}
	public double getLon(){
		return longitude;
	}
	public String getReferenceName() {
		return referenceName;
	}
	public String getkioskID() {
		return kioskID;
	}
	
	
}