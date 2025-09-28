package com.epam.gym_crm.service.impl;

import com.epam.gym_crm.dao.TraineeDao;
import com.epam.gym_crm.domain.Trainee;
import com.epam.gym_crm.service.TraineeService;
import com.epam.gym_crm.util.PasswordGenerator;
import com.epam.gym_crm.util.UsernameGenerator;
import com.epam.gym_crm.util.impl.SimpleUsernameGenerator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TraineeServiceImpl implements TraineeService {
    private final TraineeDao traineeDao;
    private final UsernameGenerator usernameGenerator;
    private final PasswordGenerator passwordGenerator;

    public TraineeServiceImpl(TraineeDao traineeDao,
                              UsernameGenerator usernameGenerator,
                              PasswordGenerator passwordGenerator) {
        this.traineeDao = traineeDao;
        this.usernameGenerator = usernameGenerator;
        this.passwordGenerator = passwordGenerator;
    }

    @Override
    public Trainee create(Trainee trainee) {
        trainee.setFirstName(SimpleUsernameGenerator.normalizeName(trainee.getFirstName()));
        trainee.setLastName(SimpleUsernameGenerator.normalizeName(trainee.getLastName()));

        if (trainee.getUsername() == null || trainee.getUsername().isBlank()) {
            trainee.setUsername(generateUniqueUsername(trainee));
        }
        if (trainee.getPassword() == null || trainee.getPassword().isBlank()) {
            trainee.setPassword(passwordGenerator.generate());
        }
        return traineeDao.create(trainee);
    }

    @Override
    public Trainee update(Trainee trainee) {
        return traineeDao.update(trainee);
    }

    @Override
    public Optional<Trainee> findById(Long id) {
        return traineeDao.findById(id);
    }

    @Override
    public List<Trainee> findAll() {
        return traineeDao.findAll();
    }

    @Override
    public boolean deleteById(Long id) {
        return traineeDao.deleteById(id);
    }

    private String generateUniqueUsername(Trainee trainee) {
        String baseUsername = usernameGenerator.generate(trainee);
        String finalUsername = baseUsername;
        int count = 0;
        while (usernameExists(finalUsername)) {
            count++;
            finalUsername = SimpleUsernameGenerator.addSuffix(baseUsername, count);
        }
        return finalUsername;
    }

    private boolean usernameExists(String username) {
        return traineeDao.findAll().stream()
                .anyMatch(t -> username.equals(t.getUsername()));
    }
}
