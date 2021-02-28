package de.ffm.rka.rkareddit.util;

import java.security.SecureRandom;

public class StringUtil {

    private StringUtil(){}

    public static String generateRandomString(int lettersAmount){
        SecureRandom secureRandom = new SecureRandom();
        final int A = 97;
        final int Z = 122;
        return secureRandom.ints(A, Z).limit(lettersAmount)
                                                .collect(StringBuilder::new,
                                                        StringBuilder::appendCodePoint,
                                                        StringBuilder::append)
                                                .toString();

    }
}
