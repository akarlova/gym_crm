package com.epam.gym_crm.util.impl;

import com.epam.gym_crm.util.PasswordGenerator;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class BasePasswordGenerator implements PasswordGenerator {
    private static final String ALPHABET =
            "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789!@#$%^&_+";
    private static final int LEN = 10;
    private final SecureRandom rnd = new SecureRandom();

    @Override
    public String generate() {
        return rnd.ints(LEN, 0, ALPHABET.length())
                .mapToObj(ALPHABET::charAt)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }
}
