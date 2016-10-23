import java.io.*;
import java.util.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.net.*;
import java.lang.Math;

public class RebalanceHoustonBCycle {
	
	public static void main(String[] args) {
		
		ArrayList<Trip> tripsArray = loadTrips();
		
		ArrayList<Kiosk> kiosks = loadKiosks();
		
		kiosks = loadSystemStatus(kiosks);
		
		HashMap kioskRelativeDistances = setKioskRelativeDistances(kiosks);
		
		runTestMethods(tripsArray, kiosks, kioskRelativeDistances);
	}
	
	public static ArrayList<Trip> loadTrips(){
		String csvFile = "/Users/latanebullock/Desktop/Google Drive/Rice/Engi 120 B-cycle/git-hub/rebalance-houston-bcycle/lib/trip-data-20-oct.csv";
		String line = "";
		String csvSplitBy = ",";
		ArrayList<String[]> tripStrings = new ArrayList<String[]>();
		ArrayList<Trip> trips = new ArrayList<Trip>();
		
		try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
			
			while ((line = br.readLine()) != null) {

				String[] trip = line.split(csvSplitBy);
                tripStrings.add(trip);

            }
			
		} catch (IOException e) {
            e.printStackTrace();
        }
		
		for (int i = 0; i < tripStrings.size(); i++) {
			trips.add(new Trip(tripStrings.get(i)[6],tripStrings.get(i)[9],tripStrings.get(i)[7],
					tripStrings.get(i)[10],tripStrings.get(i)[5],tripStrings.get(i)[8]));
		}
		
		return trips;
	}
	
	public static ArrayList<Kiosk> loadKiosks(){
		ArrayList<Kiosk> kiosks = new ArrayList<Kiosk>();
		JSONParser parser = new JSONParser();
		
		try {
			
			String jsonFromURL = readURL("https://gbfs.bcycle.com/bcycle_houston/station_information.json");
			Object obj = parser.parse(jsonFromURL);

			JSONObject jsonObject = (JSONObject) obj;
			JSONObject stations = (JSONObject) jsonObject.get("data");
			JSONArray stationArray = (JSONArray) stations.get("stations");
			for (int index = 0; index < stationArray.size(); index++) {
				JSONObject station = (JSONObject) stationArray.get(index);
				String stationID = (String) station.get("station_id");
				String name = (String) station.get("name");
				String address = (String) station.get("address");
				double longitude = (double) station.get("lon");
				double latitude = (double) station.get("lat");
				kiosks.add(new Kiosk(stationID, name, address, latitude, longitude));				 
			}
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		
		return kiosks;
	}

	public static ArrayList<Kiosk> loadSystemStatus(ArrayList<Kiosk> kiosks){
		String statusURL = "https://gbfs.bcycle.com/bcycle_houston/station_status.json";
		String systemStatusString = readURL(statusURL);
		
		JSONParser parser = new JSONParser();
		
		try {
			
			Object obj = parser.parse(systemStatusString);

			JSONObject jsonObject = (JSONObject) obj;
			JSONObject stations = (JSONObject) jsonObject.get("data");
			JSONArray stationArray = (JSONArray) stations.get("stations");
			for (int index = 0; index < stationArray.size(); index++) {
				JSONObject jsonStation = (JSONObject) stationArray.get(index);
				String stationID = (String) jsonStation.get("station_id");
				
				long num_bikes_available_d = (long) jsonStation.get("num_bikes_available");
				long num_docks_available_d = (long) jsonStation.get("num_docks_available");
				int num_bikes_available = (int) num_bikes_available_d;
				int num_docks_available = (int) num_docks_available_d;
				
				Kiosk tempKiosk = findKiosk(stationID, kiosks);
				tempKiosk.setBikes(num_bikes_available);
				tempKiosk.setDocks(num_docks_available);
			}
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return kiosks;
	}
	
	public static Kiosk findKiosk(String tripToFind, ArrayList<Kiosk> kiosks){
		Kiosk kioskFound = null;
		for (Kiosk tempKiosk : kiosks){
			if (tripToFind.equals(tempKiosk.getName())  || tripToFind.equals(tempKiosk.getID())){
				kioskFound = tempKiosk;
			}
		}
		return kioskFound;
	}
	
	public static String readURL(String urlString) {
		String urlContents = "";
		try{
			URL url = new URL(urlString);
			
	        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
	        String line;
	        while ((line = reader.readLine()) != null)
	        {
	          urlContents += line;
	        }
	        reader.close();
		} catch (Exception e){
			e.printStackTrace();
		}
		return urlContents;
        
  }
        
	public static HashMap setKioskRelativeDistances (ArrayList<Kiosk> kiosks){
		HashMap kioskDistances = new HashMap();
		for (Kiosk kiosk1 : kiosks){
			for (Kiosk kiosk2 : kiosks){
				HashSet<Kiosk> kioskPair = new HashSet<Kiosk> ();
				kioskPair.add(kiosk1);
				kioskPair.add(kiosk2);
				if (kioskPair.size() == 2){
					double distance = Math.sqrt( Math.pow(kiosk1.getLat() - kiosk2.getLat() , 2) + Math.pow(kiosk1.getLon() - kiosk2.getLon() ,  2)) ;
					kioskDistances.put(kioskPair, distance);
				}
			}
		}
		return kioskDistances;
	}

	public static void runTestMethods(ArrayList<Trip> tripsArray, ArrayList<Kiosk> kiosks, HashMap kioskRelativeDistances) {
		// Print out specific trip and its attributes from the trips array
		System.out.println(tripsArray.get(110).toString());
		
		// Print out a kiosk from the kiosks array using the findKiosk method
		System.out.println(findKiosk("Lamar & Milam", kiosks).toString());
		
		// Use two kiosks to create a set/pair, and then print out the distance between them
		HashSet<Kiosk> testPair = new HashSet<Kiosk>();
		Kiosk kiosk2271 = findKiosk("Lamar & Milam", kiosks);
		Kiosk kiosk2272 = findKiosk("Spotts Park", kiosks);
		testPair.add(kiosk2271);
		testPair.add(kiosk2272);
		System.out.println("Distance between station \"" + kiosk2271.getName() + "\" and \"" + kiosk2272.getName() + "\" :: " + kioskRelativeDistances.get(testPair));
	}
}
