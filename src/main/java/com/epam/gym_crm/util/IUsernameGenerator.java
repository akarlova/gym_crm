package com.epam.gym_crm.util;

import com.epam.gym_crm.domain.User;

public interface IUsernameGenerator {
    String generate(User user);
}
