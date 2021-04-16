package kg.ogogo.academy.web.util;

import java.security.SecureRandom;
import java.util.Locale;

public final class RandomUtil {


    public static final String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static final String lower = upper.toLowerCase(Locale.ROOT);

    public static final String digits = "0123456789";

    public static final String alphanum = upper + lower + digits;

    private static final int DEF_COUNT = 20;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public static String generateRandomAlpha() {
        return generateRandom(lower, DEF_COUNT);
    }

    public static String generateRandomAlphaNum() {
        return generateRandom(alphanum, DEF_COUNT);
    }

    public static String generateRandom(String data, int length){
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            // 0-62 (exclusive), random returns 0-61
            int rndCharAt = random.nextInt(data.length());
            char rndChar = data.charAt(rndCharAt);

            sb.append(rndChar);
        }
        return sb.toString();
    }

    public static String generatePassword(){
        return generateRandom(alphanum, 8);
    }



}
