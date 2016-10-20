import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class RebalanceHoustonBCycle {

	public static void main(String[] args) {
		String csvFile = "/Users/davidsenter/Documents/TripData.csv";
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
		for (int i = 0; i < tripStrings.size(); i++) {
			trips.add(new Trip(tripStrings.get(i)[6],tripStrings.get(i)[9],tripStrings.get(i)[7],
					tripStrings.get(i)[10],tripStrings.get(i)[5],tripStrings.get(i)[8]));
		}
		System.out.println(trips.get(115).toString());
	}

}
