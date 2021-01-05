package arbyte.helper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.security.MessageDigest;
import java.util.Scanner;

public class Hasher {

	public static void storeCredentials(String filepath, String email, String password){
		BufferedWriter bw ;
		ResourceLoader rl = new ResourceLoader();

		try {
			File file = rl.getFileFromResource(filepath);
			FileWriter fw = new FileWriter(file, false);
			bw = new BufferedWriter(fw);

			String emailpasswordHash = getDigestFrom(email + password);


			// checks if the file exist
			// create file if not exist?
			if(file.exists()) {
				bw.write(emailpasswordHash);
				bw.newLine();
				bw.close();

			}
			else{
				System.out.println("Error : File does not exist");
			}
		}
		catch(Exception e){
			System.out.println("Error inputting User Info into File");
			e.printStackTrace();
		}
	}

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

	public static boolean getEmailPasswordHash(String filepath, String inputEmail, String inputPassword){
		try {
			ResourceLoader loader = new ResourceLoader();
			File textFile = loader.getFileFromResource(filepath);
			Scanner scanner = new Scanner(textFile);

			// loop through each line
			while (scanner.hasNextLine() ){
				String line = scanner.nextLine();

				Scanner subscanner = new Scanner(line);
				String inputHash = Hasher.getDigestFrom(inputEmail + inputPassword);

				// returns line if email matches
				if(inputHash.equals(subscanner.next())){
					return true;
				}
			}
		}
		catch(Exception e){
			System.out.println("getLineOfEmail method failed");
			e.printStackTrace();

		}
		// otherwise return empty string
		return false;
	}
}
