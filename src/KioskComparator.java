import java.util.Comparator;

public class KioskComparator {

	public int compare(Kiosk kiosk1, Kiosk kiosk2, int currentPeriod) {
		Integer val1 = kiosk1.getNR()[currentPeriod];
		Integer val2 = kiosk2.getNR()[currentPeriod];
		return val1.compareTo(val2);
	}

}
