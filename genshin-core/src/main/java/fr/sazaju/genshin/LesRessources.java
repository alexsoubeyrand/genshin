package fr.sazaju.genshin;

import java.util.List;

public class LesRessources {
	public static void main(String[] args) {
		
		System.out.println("Slime_Cs : " + locate("Slime_Cs"));
		
		System.out.println("D_Masks : " + locate("D_Masks"));
		
		System.out.println("F_Arrowheads : " + locate("F_Arrowheads"));
		
		System.out.println("D_Scrolls : " + locate("D_Scrolls"));
		
		System.out.println("TH_Insignia : " + locate("TH_Insignia"));
		
		System.out.println("R_Insignia : " + locate("R_Insignia"));
		
		System.out.println("W_Nectar : " + locate("W_Nectar"));
		
		
	}

	private static List<String> locate(String resource) {
		// TODO Auto-generated method stub
		List<String> locations;
		switch (resource) {
		
			case "Slime_Cs" :
				locations = List.of("Slimes");
				break;

			case "D_Masks" :
				locations = List.of("Hillchurls");
				break;
				
			case "F_Arrowheads" :
				locations = List.of("Hillchurl_Shooters");
				break;
				
			case "D_Scrolls" :
				locations = List.of("Samachurls");
				break;
				
			case "TH_Insignia" :
				locations = List.of("Treasure_Hoarders");
				break;
				
			case "R_Insignia" :
				locations = List.of("Fatuis");
				break;
				
			case "W_Nectar" :
				locations = List.of("Whopperflowers");
				break;
				
			default :
				locations = List.of();
				break;
		}
		return locations;
	}

}
