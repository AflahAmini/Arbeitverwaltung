package arbyte.helper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.security.MessageDigest;
import java.util.Scanner;

public class Hasher {

	private static final String credentialsPath = "userInfo/userInfo.txt";

	// Stores the credentials as a hash into the file on credentialsPath
	public static void storeCredentials(String email, String password){
		BufferedWriter bw ;
		ResourceLoader rl = new ResourceLoader();

		try {
			File file = rl.getFileOrCreate(credentialsPath);
			FileWriter fw = new FileWriter(file, false);
			bw = new BufferedWriter(fw);

			String emailpasswordHash = getDigestFrom(email + password);

			bw.write(emailpasswordHash);
			bw.newLine();
			bw.close();
		}
		catch(Exception e){
			System.out.println("Error inputting User Info into File");
			e.printStackTrace();
		}
	}

	// Hashes the credentials and compares that with the last known credentials
	public static boolean compareLastCredentials(String inputEmail, String inputPassword){
		try {
			ResourceLoader loader = new ResourceLoader();
			File textFile = loader.getFile(credentialsPath);

			// File must exist after this line
			if (!textFile.exists())
				return false;

			Scanner scanner = new Scanner(textFile);
			Scanner lineScanner = new Scanner(scanner.nextLine());
			String inputHash = Hasher.getDigestFrom(inputEmail + inputPassword);

			return inputHash.equals(lineScanner.next());
		}
		catch(Exception e){
			System.out.println("getLineOfEmail method failed");
			e.printStackTrace();
		}

		return false;
	}

	// Produces a hash digest from the given string (SHA-256)
	private static String getDigestFrom(String input) throws Exception {
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
