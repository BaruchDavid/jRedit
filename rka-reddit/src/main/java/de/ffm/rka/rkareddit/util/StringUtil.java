package de.ffm.rka.rkareddit.util;

import java.security.SecureRandom;
import java.util.stream.Collectors;

public class StringUtil {

    public static String generateRandomString(int lettersAmount){
        SecureRandom secureRandom = new SecureRandom();
        final int A = 97;
        final int Z = 122;
        final String randomString = secureRandom.ints(A, Z).limit(lettersAmount)
                                                .collect(StringBuilder::new,
                                                        StringBuilder::appendCodePoint,
                                                        StringBuilder::append)
                                                .toString();
        return randomString;
    }
}
