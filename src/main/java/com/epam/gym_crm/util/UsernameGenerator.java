package com.epam.gym_crm.util;

import com.epam.gym_crm.domain.User;

public interface UsernameGenerator {
    String generate(User user);
}
