
public class main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Hello Houston");
		System.out.println("Fix bikes");
		System.out.println("Lat was here");
		Kiosk kiosk1 = new Kiosk(84.0001, 82.00, "houston-bcycle-3211","Main and 4th");
		Kiosk kiosk2 = new Kiosk(83.2, -81.52, "houston-bcycle-3213","Main and 6th");
		//Kiosk kiosk2 = new Kiosk(84.2001);
		
		int x = 5;
		int y = 4;
		int z = x +y;
		
		double temp = kiosk1.getLat();
		
		phase1(kiosk1);
		System.out.println("kiosk2 reference name: " + kiosk2.getReferenceName());
		
	}
	
	private static void phase1(Kiosk kiosk){
		// phase 1 code
	}

}
