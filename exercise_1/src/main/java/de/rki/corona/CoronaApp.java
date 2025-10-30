package de.rki.corona;

import java.util.HashMap;
import java.util.Map;

import de.rki.corona.bluetooth.BluetoothDevice;
import de.rki.corona.bluetooth.BluetoothService;
import de.rki.corona.database.CoronaDatabase;
import de.rki.corona.time.TimeService;

public class CoronaApp implements Runnable {
	enum Severity {
		NONE,
		SAFE_CONTACT,
		INFECTIOUS_CONTACT
	}
	private final String hash = HashProvider.generateHash();
	private final BluetoothService bluetooth;
	private final CoronaDatabase database;
	private final TimeService time;
	
	private final Map<String, Encounter> encounters = new HashMap<String, Encounter>();
	
	private Severity severity;
	
	public CoronaApp(BluetoothService bluetooth, CoronaDatabase database, TimeService time) {
		this.bluetooth = bluetooth;
		this.database = database;
		this.time = time;
		new Thread(this).start();
	}
	
	public CoronaApp(BluetoothService bluetooth, CoronaDatabase database) {
		this(bluetooth, database, new TimeService());
	}
	
	public CoronaApp() {
		this(new BluetoothService(), new CoronaDatabase());
	}

	@Override
	public void run() {
		while (true) {
			for (BluetoothDevice device : bluetooth.queryDevices()) {
				updateEncounter(device.queryHash());
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
	}
	
	public void submitInfection() {
		database.submitInfection(hash);
	}
	
	public Severity getSeverity() {
		return severity;
	}
	
	private void updateEncounter(String hash) {
		final long now = time.currentTimeMillis();
		Encounter encounter = encounters.get(hash);
		if (encounter == null) {
			encounter = new Encounter(hash, now);
		}
		encounters.put(hash, encounter);
		encounter.setLastEncounter(now);
		if (database.isInfected(hash)) {
			if (encounter.getLastEncounter() - encounter.getFirstEncounter() >= 5000) {
				severity = Severity.INFECTIOUS_CONTACT;
			}
			else {
				severity = Severity.SAFE_CONTACT;
			}
		}
		else {
			severity = Severity.NONE;
		}
	}
}
