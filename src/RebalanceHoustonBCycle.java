import java.io.*;
import java.util.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.net.*;
import java.lang.Math;
import java.text.SimpleDateFormat;

public class RebalanceHoustonBCycle {
	
	public static void main(String[] args) {
		
		// BEGIN PHASE 1
		
		// load trips
		ArrayList<Trip> trips = loadTrips();
		
		int dayCount = findNumDaysinTrips(trips);
		
		// load kiosks
		ArrayList<Kiosk> kiosks = loadKiosks();
		
		kiosks = loadSystemStatus(kiosks);
		
		// find deltaN for each kiosk for each station
		
		kiosks = setDeltaNs(kiosks, trips);
		
		// find average deltaN for each kiosk for each period
		
		for (int i = 0; i < kiosks.size(); i++){
			int[] temp = kiosks.get(i).getPeriodDeltaN();
			double[] temp2 = new double[4];
			for (int j = 0; j < temp.length; j++){
				temp2[j] = (double) temp[j] / (double) dayCount;
			}
			kiosks.get(i).setPeriodDeltaNAvg(temp2);
		}
		
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
			System.out.println(j + " " + kiosks.get(j).toString() + "\n  Period DeltaN Avgs:");
			for (int i = 0; i < 4; i++){
				System.out.println("  P" + i + ": " + kiosks.get(j).getPeriodDeltaNAvg()[i]);
			}
			System.out.println();
		}
		
		// BEGIN PHASE 3
		Date now = new Date();
		int currentPeriod = 0;
		
		if (now.getHours() >= 6 && now.getHours() <= 9){
			currentPeriod = 0;
		}
		else if (now.getHours() >= 10 && now.getHours() <= 14){
			currentPeriod = 1;
		}
		else if (now.getHours() >= 15 && now.getHours() <= 18){
			currentPeriod = 2;
		}
		else if (now.getHours() >= 19 && now.getHours() <= 23){
			currentPeriod = 3;
		}
		
		kiosks.sort((Comparator<Kiosk>) new KioskComparator());
		
		ArrayList<Kiosk> sortedPositiveKiosks = new ArrayList<Kiosk>();
		ArrayList<Kiosk> sortedNegativeKiosks = new ArrayList<Kiosk>();
		
		int zeroCount = 0;
		for (int i = kiosks.size() - 1; i >= 0; i--) {
			if (kiosks.get(i).getNR()[currentPeriod] > 0) {
				sortedPositiveKiosks.add(kiosks.get(i));
			}
			else if (kiosks.get(i).getNR()[currentPeriod] < 0) {
				sortedNegativeKiosks.add(kiosks.get(i));
			}
			else if (kiosks.get(i).getNR()[currentPeriod] == 0) {
				zeroCount ++;
			}
		}
		
		Collections.reverse(sortedPositiveKiosks);
		
		for (int i = 0; i < sortedPositiveKiosks.size(); i++) {
			System.out.println(sortedPositiveKiosks.get(i).getName() + ": " + sortedPositiveKiosks.get(i).getNR()[currentPeriod]);
		}
		for (int i = 0; i < sortedNegativeKiosks.size(); i++) {
			System.out.println(sortedNegativeKiosks.get(i).getName() + ": " + sortedNegativeKiosks.get(i).getNR()[currentPeriod]);
		}
		System.out.println("Zero need stations: " + zeroCount);
		
		int smallerList = 0;
		if (sortedPositiveKiosks.size() > sortedNegativeKiosks.size()){
			smallerList = 0;
		} else {
			smallerList = 1;
		}
		
		System.out.println();
		
		HashMap<String,Double> kioskRelativeDistances = setKioskRelativeDistances(kiosks);
		ArrayList<Kiosk> results = new ArrayList<Kiosk>();
		
		if (smallerList == 0) {
			
			int i  = 0;
			results = new ArrayList<Kiosk>();
			while (sortedNegativeKiosks.size() > 0) {
				ArrayList<Double> scores = new ArrayList<Double>();
				for (int j = 0; j < sortedNegativeKiosks.size(); j++){
					String pair1 = sortedPositiveKiosks.get(i).getName() + ":" + sortedNegativeKiosks.get(j).getName();
					String pair2 = sortedNegativeKiosks.get(j).getName() + ":" + sortedPositiveKiosks.get(i).getName();
					double distance = -1.0;
					if (kioskRelativeDistances.containsKey(pair1)) {
						distance = kioskRelativeDistances.get(pair1);
					}
					else if (kioskRelativeDistances.containsKey(pair2)) {
						distance = kioskRelativeDistances.get(pair2);
					}
					// Calculate pairing score
					double score = (Math.abs(sortedPositiveKiosks.get(i).getNR()[currentPeriod] - sortedNegativeKiosks.get(j).getNR()[currentPeriod]) - 1) / distance;
					scores.add(score);
				}
				results.add(sortedNegativeKiosks.remove(scores.indexOf(Collections.max(scores))));
				i++;
			}
			for (int j = 0; j < results.size(); j++) {
				System.out.println("Move " + (int)Math.floor((Math.abs(results.get(j).getNR()[currentPeriod]) + Math.abs(sortedPositiveKiosks.get(j).getNR()[currentPeriod])) / 2.0) + " bikes from " + results.get(j).getName() + " ->  to " + sortedPositiveKiosks.get(j).getName());
			}
		}
		else {
			
			int i  = 0;
			results = new ArrayList<Kiosk>();
			while (sortedPositiveKiosks.size() > 0) {
				ArrayList<Double> scores = new ArrayList<Double>();
				for (int j = 0; j < sortedPositiveKiosks.size(); j++){
					String pair1 = sortedNegativeKiosks.get(i).getName() + ":" + sortedPositiveKiosks.get(j).getName();
					String pair2 = sortedPositiveKiosks.get(j).getName() + ":" + sortedNegativeKiosks.get(i).getName();
					double distance = -1.0;
					if (kioskRelativeDistances.containsKey(pair1)) {
						distance = kioskRelativeDistances.get(pair1);
					}
					else if (kioskRelativeDistances.containsKey(pair2)) {
						distance = kioskRelativeDistances.get(pair2);
					}
					// Calculate pairing score
					double score = (Math.abs(sortedNegativeKiosks.get(i).getNR()[currentPeriod] - sortedPositiveKiosks.get(j).getNR()[currentPeriod]) - 1) / distance;
					scores.add(score);
				}
				results.add(sortedPositiveKiosks.remove(scores.indexOf(Collections.max(scores))));
				i++;
			}
			for (int j = 0; j < results.size(); j++) {
				System.out.println("Move " + (int)Math.floor((Math.abs(results.get(j).getNR()[currentPeriod]) + Math.abs(sortedNegativeKiosks.get(j).getNR()[currentPeriod])) / 2.0) + " bikes from " + sortedNegativeKiosks.get(j).getName() + " ->  to " + results.get(j).getName());
			}
		}
//		System.out.println(results.size());
//		for (int i = 0; i < results.size(); i++) {
//			System.out.println(Math.abs(Math.max(results.get(i).getNR()[currentPeriod], sortedPositiveKiosks.get(i).getNR()[currentPeriod])) + " bikes from " + results.get(i).getName() + " ->  to " + sortedPositiveKiosks.get(i).getName());
//			System.out.println("Move " + (int)Math.floor((Math.abs(results.get(i).getNR()[currentPeriod]) + Math.abs(sortedPositiveKiosks.get(i).getNR()[currentPeriod])) / 2.0) + " bikes from " + results.get(i).getName() + " ->  to " + sortedPositiveKiosks.get(i).getName());
//			System.out.println("Move " + (int)Math.floor((Math.abs(results.get(i).getNR()[currentPeriod]) + Math.abs(sortedNegativeKiosks.get(i).getNR()[currentPeriod])) / 2.0) + " bikes from " + sortedNegativeKiosks.get(i).getName() + " ->  to " + results.get(i).getName());

//		}
		System.out.println();
		runTestMethods(trips, kiosks, kioskRelativeDistances);
		
		updateSystemStatusFiles();
		
//		createSystemStatusCSV();
	
		
	}
	
	/* loads trips from the csv data file
	 * @return ArrayList of all the trips and all of their info */
	public static ArrayList<Trip> loadTrips(){

		String csvFile = "/Users/latanebullock/Desktop/Google Drive/Rice/Engi 120 B-cycle/git-hub/rebalance-houston-bcycle/lib/trip-data-20-nov.csv";
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
		
		int days = 0;
		Date now = new Date();
		boolean isWeekend = false;
		try {
			if (now.getDay() == 0 || now.getDay() == 6 ) {
				isWeekend = true;
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		
		for (int i = 1; i < tripStrings.size(); i++) {
			
			Trip tempTrip = new Trip(tripStrings.get(i)[6],tripStrings.get(i)[9],tripStrings.get(i)[7],
					tripStrings.get(i)[10],tripStrings.get(i)[5],tripStrings.get(i)[8]);
			
			String tempLastName = tripStrings.get(i)[2].toLowerCase();
			
			if (tempLastName.length() >= 4 && !tempLastName.substring(0,  3).equals("tech")) {
				if (tempTrip.isWeekend() && isWeekend) {
					trips.add(tempTrip);
				}
				if (!tempTrip.isWeekend() && !isWeekend) {
					trips.add(tempTrip);
				}
			}
		}
		
		
		System.out.println("Num trips : " + trips.size());
		
		
		return trips;
	}
	
	/* loads kiosks from JSON file of system status information
	 * @return ArrayList of all the kiosks and their information */
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

	/* loads the dynamic data and pushes it to the kiosks in the ArrayList
	 * @return the updated ArrayList of kiosks with the new dynamic data*/
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
	
	
	public static int findNumDaysinTrips (ArrayList<Trip> trips) {
		ArrayList<Integer> daysPresent = new ArrayList<Integer>();
		for (int i = 0; i < trips.size(); i++ ){
			
			int tempDate = trips.get(i).getStartingDateDate().getDate();
			if (!daysPresent.contains(tempDate)) {
				daysPresent.add(tempDate);
			}
			
		}
		return daysPresent.size();
	}
	
	public static ArrayList<Kiosk> setDeltaNs (ArrayList<Kiosk> kiosks, ArrayList<Trip> trips) {
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
		return kiosks;
	}
	
	/* identifies a specific kiosk based on its name or ID
	 * @return the kiosk object with all its info associated with it
	 * @param string of kioskToFind and arraylist of kiosk
	 */
	public static Kiosk findKiosk(String kioskToFind, ArrayList<Kiosk> kiosks){
		Kiosk kioskFound = null;
		for (Kiosk tempKiosk : kiosks){
			if (kioskToFind.equals(tempKiosk.getName())  || kioskToFind.equals(tempKiosk.getID())){
				kioskFound = tempKiosk;
			}
		}
		return kioskFound;
	}
	
	/* goes to a url and reads data in as a string
	 * @return a string of the data in the url
	 * @param string of url
	 */
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
	 
	public static String readFile(String fileName) throws IOException {
	    BufferedReader br = new BufferedReader(new FileReader(fileName));
	    try {
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();

	        while (line != null) {
	            sb.append(line);
	            sb.append("\n");
	            line = br.readLine();
	        }
	        return sb.toString();
	    } finally {
	        br.close();
	    }
	}
    
	
	/* relates distances between kiosks with associated kiosks
	 * @return hashmap of kiosk distances with a kiosk pair  as keys and distance between kiosks as the value
	 * @param array list of kiosks
	 */
	public static HashMap<String,Double> setKioskRelativeDistances (ArrayList<Kiosk> kiosks){
		HashMap<String,Double> kioskDistances = new HashMap<String,Double>();
		for (Kiosk kiosk1 : kiosks){
			for (Kiosk kiosk2 : kiosks){
				String kioskPair = kiosk1.getName() + ":" + kiosk2.getName();
				if (!kiosk1.getName().equals(kiosk2.getName())){
					double distance = Math.sqrt( Math.pow(kiosk1.getLat() - kiosk2.getLat() , 2) + Math.pow(kiosk1.getLon() - kiosk2.getLon() ,  2)) ;
					kioskDistances.put(kioskPair, distance);
				}
			}
		}
		return kioskDistances;
	}

	/* Prints out data to show results of code
	 * print kiosk information
	 * @param array list of kiosks, array list of trips, hashmap of relative distances
	 */
	public static void runTestMethods(ArrayList<Trip> tripsArray, ArrayList<Kiosk> kiosks, HashMap kioskRelativeDistances) {
		// Print out specific trip and its attributes from the trips array
		System.out.println(tripsArray.get(10).toString());
		
		// Print out a kiosk from the kiosks array using the findKiosk method
		System.out.println(findKiosk("Lamar & Milam", kiosks).toString());
		
		// Use two kiosks to create a set/pair, and then print out the distance between them
		HashSet<Kiosk> testPair = new HashSet<Kiosk>();
		Kiosk kiosk2271 = findKiosk("La Branch & Lamar", kiosks);
		Kiosk kiosk2272 = findKiosk("Lamar & Crawford", kiosks);
		System.out.println(kiosks.get(4).getPeriodDeltaN());
		
		String kioskPair = kiosk2271.getName() + ":" + kiosk2272.getName();
		System.out.println("Distance between station \"" + kiosk2271.getName() + "\" and \"" + kiosk2272.getName() + "\" :: " + kioskRelativeDistances.get(kioskPair));
		
		double[] pairingScores;
		
		
	}

	public static void updateSystemStatusFiles(){
		String input = "";
		Scanner in = new Scanner(System.in);
		System.out.println("Are you sure you would like to update system status files? y or n");
		input = in.nextLine();
		if (input.equals("y")){
			try{
				
				FileWriter writer = new FileWriter("/Users/latanebullock/Desktop/Google Drive/Rice/Engi 120 B-cycle/git-hub/rebalance-houston-bcycle/lib/system-status-stream-final-json.txt", true);
				BufferedWriter bufWriter = new BufferedWriter(writer);
				PrintWriter out = new PrintWriter(bufWriter);
				
				String statusURL = "https://gbfs.bcycle.com/bcycle_houston/station_status.json";
				String systemStatusString = readURL(statusURL);
				
				out.println("," + systemStatusString + "\n\n");
				out.close();
				
				
				
				JSONParser parser = new JSONParser();
				
				FileWriter csvWriter = new FileWriter("/Users/latanebullock/Desktop/Google Drive/Rice/Engi 120 B-cycle/git-hub/rebalance-houston-bcycle/lib/system-status-stream-final-csv.csv", true);
				BufferedWriter csvBufWriter = new BufferedWriter(csvWriter);
				PrintWriter csvOut = new PrintWriter(csvBufWriter);
				
				Object obj = parser.parse(systemStatusString);
				JSONObject jsonSystemUpdate = (JSONObject) obj;
					
				long timeStampUnix = (long) jsonSystemUpdate.get("last_updated");
				Date timeStampReadable = new Date((long) timeStampUnix *1000);
				csvOut.write(timeStampReadable.toString() + ",");
				int hours = timeStampReadable.getHours();
				int minutes = timeStampReadable.getMinutes();
				if (minutes >= 30) {
					hours ++;
				}
				
				csvOut.write(hours + ",");
				String timeStampStr = Long.toString(timeStampUnix) + ",";
				csvOut.write(timeStampStr);

				JSONObject stations = (JSONObject) jsonSystemUpdate.get("data");
				JSONArray stationArray = (JSONArray) stations.get("stations");

				for (int j = 0; j < stationArray.size(); j++) {
					
					JSONObject jsonStation = (JSONObject) stationArray.get(j);
					long num_bikes_available = (long) jsonStation.get("num_bikes_available");
					String bikesAvail = Long.toString(num_bikes_available);
					if(j == (stationArray.size() - 1)) {
						csvOut.write(bikesAvail + "\n");
					}
					else{
						csvOut.write(bikesAvail + ",");
					}
					
				}
				
			
				csvOut.close();
				
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		else{
			System.out.println("System status files not updated...");
		}
		
	}

//	public static void createSystemStatusCSV(){
//		String fileName = "/Users/latanebullock/Desktop/Google Drive/Rice/Engi 120 B-cycle/git-hub/rebalance-houston-bcycle/lib/system-status-stream.txt";
//		try {
//			String jsonFileContents = readFile(fileName);
//			JSONParser parser = new JSONParser();
//			
//			FileWriter writer = new FileWriter("/Users/latanebullock/Desktop/Google Drive/Rice/Engi 120 B-cycle/git-hub/rebalance-houston-bcycle/lib/system-status-stream-final-csv.csv", true);
//			BufferedWriter bufWriter = new BufferedWriter(writer);
//			PrintWriter out = new PrintWriter(bufWriter);
//			
//			Object obj = parser.parse(jsonFileContents);
//			JSONObject jsonObject = (JSONObject) obj;
//			JSONArray systemUpdatesArray = (JSONArray) jsonObject.get("system_updates");
//			for (int index = 0; index < systemUpdatesArray.size(); index++) {
//				
//				JSONObject jsonSystemUpdate = (JSONObject) systemUpdatesArray.get(index);
//				long timeStampUnix = (long) jsonSystemUpdate.get("last_updated");
//				Date timeStampReadable = new Date((long) timeStampUnix *1000);
//				out.write(timeStampReadable.toString() + ",");
//				int hours = timeStampReadable.getHours();
//				int minutes = timeStampReadable.getMinutes();
//				if (minutes >= 30) {
//					hours ++;
//				}
//				
//				out.write(hours + ",");
//				String timeStampStr = Long.toString(timeStampUnix) + ",";
//				out.write(timeStampStr);
//				
//				JSONObject stations = (JSONObject) jsonSystemUpdate.get("data");
//				JSONArray stationArray = (JSONArray) stations.get("stations");
//
//				for (int j = 0; j < stationArray.size(); j++) {
//					
//					JSONObject jsonStation = (JSONObject) stationArray.get(j);
//					long num_bikes_available = (long) jsonStation.get("num_bikes_available");
//					String bikesAvail = Long.toString(num_bikes_available);
//					out.write(bikesAvail + ",");
//					
//				}
//				
//				out.println();
//			}
//			
//			out.close();
//			
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		
//		
//	}
//
}
