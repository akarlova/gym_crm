package com.epam.gym_crm.util.impl;

import com.epam.gym_crm.domain.User;
import com.epam.gym_crm.util.IUsernameGenerator;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class SimpleUsernameGenerator implements IUsernameGenerator {
    @Override
    public String generate(User user) {

        String first = user.getFirstName() == null ? "" : user.getFirstName().trim();
        String last = user.getLastName() == null ? "" : user.getLastName().trim();
        return first + "." + last;
    }

    public static String normalizeName(String name) {
        if (name == null) {
            return "";
        }
        name = name.trim().toLowerCase(Locale.ROOT);
        if (name.isEmpty()) {
            return name;
        }
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

    public static String addSuffix(String username, int suffix) {
        return suffix <= 0 ? username : username + suffix;
    }
}
