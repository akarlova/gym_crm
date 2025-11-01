package com.epam.gym_crm.util.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

public class BasePasswordGeneratorTest {
    private static final String ALPHABET =
            "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789!@#$%^&_+";

    private final BasePasswordGenerator gen = new BasePasswordGenerator();

    @Test
    void generate_returnsLength10_andOnlyAllowedChars() {
        for (int i = 0; i < 200; i++) {
            String pwd = gen.generate();
            assertEquals(10, pwd.length(), "Length should be 10");

            for (char c : pwd.toCharArray()) {
                assertTrue(ALPHABET.indexOf(c) >= 0,
                        () -> "Invalid character: '" + c + "' in the password: " + pwd);
            }
        }
    }

    @Test
    void generate_producesMostlyUniqueValues() {
        int n = 500;
        Set<String> seen = new HashSet<>(n);
        for (int i = 0; i < n; i++) {
            seen.add(gen.generate());
        }
        assertTrue(seen.size() >= n - 25,
                () -> "Слишком много совпадений: уникально " + seen.size() + " из " + n);
    }

    @Test
    void generate_usesDifferentClassesOfChars_overSample() {
        boolean hasLetter = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (int i = 0; i < 200 && !(hasLetter && hasDigit && hasSpecial); i++) {
            for (char c : gen.generate().toCharArray()) {
                if (Character.isLetter(c)) hasLetter = true;
                else if (Character.isDigit(c)) hasDigit = true;
                else hasSpecial = true;
            }
        }

        assertTrue(hasLetter);
        assertTrue(hasDigit);
        assertTrue(hasSpecial);
    }
}
