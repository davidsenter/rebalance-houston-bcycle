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
		
		// BEGIN PHASE 1
		
		// load trips
		ArrayList<Trip> trips = loadTrips();
		
		int days = 19;
		
		// load kiosks
		ArrayList<Kiosk> kiosks = loadKiosks();
		
		kiosks = loadSystemStatus(kiosks);
		
		HashMap kioskRelativeDistances = setKioskRelativeDistances(kiosks);
		
		HashMap kioskTrips = new HashMap<Integer,ArrayList<Trip>>();
		
		// find deltaN for each kiosk for each station
		
		for (int i = 0; i < trips.size(); i++) {
			for (int j = 0; j < kiosks.size(); j++) {
				String kiosk = kiosks.get(j).getName();
				String tripStartingKiosk = trips.get(i).getStartingKiosk();
				String tripEndingKiosk = trips.get(i).getEndingKiosk();
				int tripStartingPeriod = trips.get(i).getStartingPeriod();
				int tripEndingPeriod = trips.get(i).getEndingPeriod();
				int deltaN = 0;
				
				if (tripStartingKiosk.equals(kiosk)) {
					int[] temp = kiosks.get(j).getPeriodDeltaN();
					temp[tripStartingPeriod]--;
					kiosks.get(j).setPeriodDeltaN(temp);
				}
				
				if (tripEndingKiosk.equals(kiosk)) {
					int[] temp = kiosks.get(j).getPeriodDeltaN();
					temp[tripEndingPeriod]++;
					kiosks.get(j).setPeriodDeltaN(temp);
				}
			}
		}
		
		// find average deltaN for each kiosk for each period
		
		for (int i = 0; i < kiosks.size(); i++){
			int[] temp = kiosks.get(i).getPeriodDeltaN();
			double[] temp2 = new double[4];
			for (int j = 0; j < temp.length; j++){
				temp2[j] = (double) temp[j] / (double) days;
			}
			kiosks.get(i).setPeriodDeltaNAvg(temp2);
		}
		
		// print phase 1 test
		
//		for (int j = 0; j < kiosks.size(); j++){
//			System.out.println(j + kiosks.get(j).toString());
//			for (int i = 0; i < 4; i++){
//				System.out.println(kiosks.get(j).getPeriodDeltaNAvg()[i]);
//			}
//			System.out.println();
//		}
		
		// BEGIN PHASE 2:
		
		// calculate NR for each kiosk for each period
		for (int i = 0; i < kiosks.size(); i++){
			int[] temp = new int[4];
			for (int j = 0; j < 4; j++){
				temp[j] = (int)Math.round((kiosks.get(i).getCap() / 2) + 1 - kiosks.get(i).getN() - kiosks.get(i).getPeriodDeltaNAvg()[j]);
			}
			kiosks.get(i).setNR(temp);
		}
		
		// print phase 2 test
		
		for (int j = 0; j < kiosks.size(); j++){
			System.out.println(j + " " + kiosks.get(j).toString());
			for (int i = 0; i < 4; i++){
				System.out.println(kiosks.get(j).getNR()[i]);
			}
			System.out.println();
		}
		
		// BEGIN PHASE 3:
		
		
		
		runTestMethods(trips, kiosks, kioskRelativeDistances);
		
	}
	
	public static ArrayList<Trip> loadTrips(){
		String csvFile = "/Users/davidsenter/GitHub/rebalance-houston-bcycle/lib/trip-data-20-oct.csv";
		String line = "";
		String csvSplitBy = ";";
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
		int days = 0;
		for (int i = 0; i < tripStrings.size(); i++) {
			if (i >= 1) {
				String nextDate = tripStrings.get(i - 1)[5];
				String tempDate = tripStrings.get(i)[5];
				
				if (!(nextDate.equals(tempDate))) {
					days += 1;
//					System.out.println(tempDate + " " +  days);
				}
			}
			trips.add(new Trip(tripStrings.get(i)[6],tripStrings.get(i)[9],tripStrings.get(i)[7],
					tripStrings.get(i)[10],tripStrings.get(i)[5],tripStrings.get(i)[8]));
		}
		System.out.println(days);
		
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
				name = name.trim();
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
//				int stationID = Integer.valueOf(stationIDTemp.substring(16));
				
				long num_bikes_available_d = (long) jsonStation.get("num_bikes_available");
				long num_docks_available_d = (long) jsonStation.get("num_docks_available");
				int num_bikes_available = (int) num_bikes_available_d;
				int num_docks_available = (int) num_docks_available_d;
				
				Kiosk tempKiosk = findKiosk(stationID, kiosks);
				tempKiosk.setBikes(num_bikes_available);
				tempKiosk.setDocks(num_docks_available);
				tempKiosk.setCap(num_bikes_available + num_docks_available);
			}
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return kiosks;
	}
	
	public int periodDeltaN(ArrayList<Trip> tripsArray, int period){
		int count = 0;
		for (int i = 0; i < tripsArray.size(); i++) {
			if (tripsArray.get(i).getStartingPeriod() == period) {
				count++;
			}
			if (tripsArray.get(i).getEndingPeriod() == period) {
				count--;
			}
		}
		return count;
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
		System.out.println(kiosks.get(10).getPeriodDeltaN());
		
		testPair.add(kiosk2271);
		testPair.add(kiosk2272);
		System.out.println("Distance between station \"" + kiosk2271.getName() + "\" and \"" + kiosk2272.getName() + "\" :: " + kioskRelativeDistances.get(testPair));
		
		
	}
}
