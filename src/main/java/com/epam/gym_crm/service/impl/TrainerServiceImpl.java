package com.epam.gym_crm.service.impl;

import com.epam.gym_crm.dao.TrainerDao;
import com.epam.gym_crm.domain.Trainer;
import com.epam.gym_crm.service.TrainerService;
import com.epam.gym_crm.util.PasswordGenerator;
import com.epam.gym_crm.util.UsernameGenerator;
import com.epam.gym_crm.util.impl.SimpleUsernameGenerator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TrainerServiceImpl implements TrainerService {
    private final TrainerDao trainerDao;
    private final UsernameGenerator usernameGenerator;
    private final PasswordGenerator passwordGenerator;

    public TrainerServiceImpl(TrainerDao trainerDao,
                              UsernameGenerator usernameGenerator,
                              PasswordGenerator passwordGenerator) {
        this.trainerDao = trainerDao;
        this.usernameGenerator = usernameGenerator;
        this.passwordGenerator = passwordGenerator;
    }

    @Override
    public Trainer create(Trainer trainer) {
        trainer.setFirstName(SimpleUsernameGenerator.normalizeName(trainer.getFirstName()));
        trainer.setLastName(SimpleUsernameGenerator.normalizeName(trainer.getLastName()));

        if (trainer.getUsername() == null || trainer.getUsername().isBlank()) {
            trainer.setUsername(generateUniqueUsername(trainer));
        }
        if (trainer.getPassword() == null || trainer.getPassword().isBlank()) {
            trainer.setPassword(passwordGenerator.generate());
        }
        return trainerDao.create(trainer);
    }

    @Override
    public Trainer update(Trainer trainer) {
        return trainerDao.update(trainer);
    }

    @Override
    public Optional<Trainer> findById(Long id) {
        return trainerDao.findById(id);
    }

    @Override
    public List<Trainer> findAll() {
        return trainerDao.findAll();
    }

    @Override
    public boolean deleteById(Long id) {
        return trainerDao.deleteById(id);
    }

    private String generateUniqueUsername(Trainer trainer) {
        String baseUsername = usernameGenerator.generate(trainer);
        String finalUsername = baseUsername;
        int count = 0;
        while (usernameExists(finalUsername)) {
            count++;
            finalUsername = SimpleUsernameGenerator.addSuffix(baseUsername, count);
        }
        return finalUsername;
    }

    private boolean usernameExists(String username) {
        return trainerDao.findAll().stream()
                .anyMatch(t -> username.equals(t.getUsername()));
    }
}
