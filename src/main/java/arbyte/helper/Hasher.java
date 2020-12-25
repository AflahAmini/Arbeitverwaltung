package arbyte.helper;

import java.security.MessageDigest;

public class Hasher {
	public static String getDigestFrom(String input) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(input.getBytes());

		byte[] digest = md.digest();
		StringBuilder hexString = new StringBuilder();

		for (byte b : digest) {
			hexString.append(Integer.toHexString(0xFF & b));
		}
		return hexString.toString();
	}
}
