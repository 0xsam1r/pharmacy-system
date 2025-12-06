package model.people;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class DataHasher {

     
    public String hashData(String input, String algorithm) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
        byte[] hashedBytes = messageDigest.digest(input.getBytes());
        return Base64.getEncoder().encodeToString(hashedBytes);
    }

     
    public boolean verifyData(String plainText, String storedHash, String algorithm) throws NoSuchAlgorithmException {
        String hashedPlainText = hashData(plainText, algorithm);
        return hashedPlainText.equals(storedHash);
    }

    public static void main(String[] args) {
        DataHasher hasher = new DataHasher();
        String dataToHash = "mySecretPassword123";
        String algorithm = "SHA-256";

        try {
             
            String hashedPassword = hasher.hashData(dataToHash, algorithm);
            System.out.println("Original Data: " + dataToHash);
            System.out.println("Hashed Data (Base64): " + hashedPassword);

             

             
            String inputToVerify = "mySecretPassword123";
            boolean isMatch = hasher.verifyData(inputToVerify, hashedPassword, algorithm);
            System.out.println("Verification successful: " + isMatch);

            String incorrectInput = "wrongPassword";
            boolean isIncorrectMatch = hasher.verifyData(incorrectInput, hashedPassword, algorithm);
            System.out.println("Verification with incorrect input: " + isIncorrectMatch);

        } catch (NoSuchAlgorithmException e) {
            System.err.println("Hashing algorithm not found: " + e.getMessage());
        }
    }
}
