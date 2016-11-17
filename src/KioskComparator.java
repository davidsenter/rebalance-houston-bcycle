import java.util.Date;
import java.util.Comparator;

public class KioskComparator implements Comparator<Kiosk> {
	
	private int currentPeriod = 0;

		
	public KioskComparator() {

		Date now = new Date();
		
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
	}
	@Override
	public int compare(Kiosk kiosk1, Kiosk kiosk2) {
		Integer val1 = kiosk1.getNR()[this.currentPeriod];
		Integer val2 = kiosk2.getNR()[this.currentPeriod];
		return val2.compareTo(val1);
	}

}
