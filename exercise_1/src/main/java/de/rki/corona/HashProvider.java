package de.rki.corona;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class HashProvider {
	private HashProvider() {
	}
	
	public static String generateHash() {
		final StringBuilder sb = new StringBuilder();
		try {
			final MessageDigest md = MessageDigest.getInstance("MD5");
			md.update("corona".getBytes());
			final byte[] bytes = md.digest();
			for (int i = 0; i < bytes.length; i++)
				sb.append(String.format("%02X", bytes[i]));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb.toString();
	}
}
