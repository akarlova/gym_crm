package com.epam.gym_crm.util.impl;

import com.epam.gym_crm.domain.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SimpleUsernameGeneratorTest {
    private final SimpleUsernameGenerator gen = new SimpleUsernameGenerator();

    private static User user(String first, String last) {
        User u = new User();
        u.setFirstName(first);
        u.setLastName(last);
        return u;
    }

    @Test
    void generate_concatenatesFirstAndLastWithDot_andTrims() {
        // given
        User u = user("  Arya ", " Stark  ");
        // when
        String username = gen.generate(u);
        // then
        assertEquals("Arya.Stark", username);
    }

    @Test
    void generate_handlesNullsAndBlanks() {
        // null + null -> "."
        assertEquals(".", gen.generate(user(null, null)));

        // value + null -> "value."
        assertEquals("Tyrion.", gen.generate(user("Tyrion", null)));

        // null + value -> ".value"
        assertEquals(".Lannister", gen.generate(user(null, "Lannister")));

        // blanks are trimmed to empty
        assertEquals(".", gen.generate(user("   ", "  ")));
    }

    @Test
    void normalizeName_returnsEmptyForNullOrBlank() {
        assertEquals("", SimpleUsernameGenerator.normalizeName(null));
        assertEquals("", SimpleUsernameGenerator.normalizeName("   "));
    }

    @Test
    void normalizeName_trimsAndCapitalizesFirstLetter_only() {
        assertEquals("Jon", SimpleUsernameGenerator.normalizeName("joN"));
        assertEquals("Arya", SimpleUsernameGenerator.normalizeName("   aRYA  "));
        assertEquals("S", SimpleUsernameGenerator.normalizeName("s")); // single letter
    }

    @Test
    void addSuffix_keepsOriginal_whenSuffixIsZeroOrNegative() {
        assertEquals("sansa.stark", SimpleUsernameGenerator.addSuffix("sansa.stark", 0));
        assertEquals("sansa.stark", SimpleUsernameGenerator.addSuffix("sansa.stark", -3));
    }

    @Test
    void addSuffix_appendsNumber_whenPositive() {
        assertEquals("sansa.stark2", SimpleUsernameGenerator.addSuffix("sansa.stark", 2));
        assertEquals("arya1", SimpleUsernameGenerator.addSuffix("arya", 1));
    }
}
