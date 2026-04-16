package security;

import java.security.SecureRandom;

public class PasswordGenerator {
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String PUNCTUATION = "!@#$%&*()_+-=[]|,./?><";

    public static String generatePassword(int length, boolean useSymbols, boolean useNumbers) {
        StringBuilder allowed = new StringBuilder(LOWER + UPPER);
        if (useNumbers) {
            allowed.append(DIGITS);
        }
        if (useSymbols) {
            allowed.append(PUNCTUATION);
        }

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(length);
        
        // Ensure at least one of each requested type, then fill the rest randomly
        if (useNumbers) password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        if (useSymbols) password.append(PUNCTUATION.charAt(random.nextInt(PUNCTUATION.length())));
        password.append(LOWER.charAt(random.nextInt(LOWER.length())));
        password.append(UPPER.charAt(random.nextInt(UPPER.length())));

        for (int i = password.length(); i < length; i++) {
            password.append(allowed.charAt(random.nextInt(allowed.length())));
        }

        // Shuffle
        char[] pwdChars = password.toString().toCharArray();
        for (int i = 0; i < pwdChars.length; i++) {
            int randomPosition = random.nextInt(pwdChars.length);
            char temp = pwdChars[i];
            pwdChars[i] = pwdChars[randomPosition];
            pwdChars[randomPosition] = temp;
        }

        return new String(pwdChars);
    }
}
