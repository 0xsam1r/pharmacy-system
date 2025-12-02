import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class DataHasher {

    /**
     * Hashes the input string using the specified algorithm and returns a Base64 encoded string.
     *
     * @param input The string to hash.
     * @param algorithm The hashing algorithm (e.g., "SHA-256", "MD5").
     * @return The Base64 encoded hash string.
     * @throws NoSuchAlgorithmException If the specified algorithm is not available.
     */
    public String hashData(String input, String algorithm) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
        byte[] hashedBytes = messageDigest.digest(input.getBytes());
        return Base64.getEncoder().encodeToString(hashedBytes);
    }

    /**
     * Verifies if the provided plain text matches the stored hashed value.
     *
     * @param plainText The plain text to verify.
     * @param storedHash The stored Base64 encoded hash.
     * @param algorithm The hashing algorithm used to create the stored hash.
     * @return True if the plain text matches the stored hash, false otherwise.
     * @throws NoSuchAlgorithmException If the specified algorithm is not available.
     */
    public boolean verifyData(String plainText, String storedHash, String algorithm) throws NoSuchAlgorithmException {
        String hashedPlainText = hashData(plainText, algorithm);
        return hashedPlainText.equals(storedHash);
    }

    public static void main(String[] args) {
        DataHasher hasher = new DataHasher();
        String dataToHash = "mySecretPassword123";
        String algorithm = "SHA-256";

        try {
            // Hash the data
            String hashedPassword = hasher.hashData(dataToHash, algorithm);
            System.out.println("Original Data: " + dataToHash);
            System.out.println("Hashed Data (Base64): " + hashedPassword);

            // Store 'hashedPassword' in your database.

            // Later, when verifying
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
