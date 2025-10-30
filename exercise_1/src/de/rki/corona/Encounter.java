package de.rki.corona;

public class Encounter {
	private final String hash;
	private final long firstEncounter;
	private long lastEncounter;
	private boolean longEncounter;
	
	public Encounter(String hash, long firstEncounter) {
		this.hash = hash;
		this.firstEncounter = firstEncounter;
		this.lastEncounter = firstEncounter;
	}
	
	public String getHash() {
		return hash;
	}
	
	public long getFirstEncounter() {
		return firstEncounter;
	}
	
	public long getLastEncounter() {
		return lastEncounter;
	}
	
	public void setLastEncounter(long lastEncounter) {
		this.lastEncounter = lastEncounter;
	}
	
	public void toLongEncounter() {
		longEncounter = true;
	}
	
	public boolean isLongEncounter() {
		return longEncounter;
	}
}
