package it.uniroma3.siw.model;


public enum Orario{
	
    ORE_19_00("19:00"),
    ORE_19_15("19:15"),
    ORE_19_30("19:30"),
    ORE_19_45("19:45"),
	ORE_20_00("20:00"),
	ORE_20_15("20:15"),
	ORE_20_30("20:30"),
	ORE_20_45("20:45"),
	ORE_21_00("21:00"),
	ORE_21_15("21:15"),
	ORE_21_30("21:30"),
	ORE_21_45("21:45"),
	ORE_22_00("22:00");
	

    private final String orario;

    Orario(String orario) {
        this.orario = orario;
    }

    public String getOrario() {
        return this.orario;
    }
    
}
