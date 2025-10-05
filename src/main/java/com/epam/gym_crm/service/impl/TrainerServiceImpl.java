package com.epam.gym_crm.service.impl;

import com.epam.gym_crm.dao.ITrainerDao;
import com.epam.gym_crm.domain.Trainer;
import com.epam.gym_crm.service.ITrainerService;
import com.epam.gym_crm.util.IPasswordGenerator;
import com.epam.gym_crm.util.IUsernameGenerator;
import com.epam.gym_crm.util.impl.SimpleUsernameGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TrainerServiceImpl implements ITrainerService {
    private static final Logger log = LoggerFactory.getLogger(TrainerServiceImpl.class);
    private final ITrainerDao trainerDao;
    private final IUsernameGenerator usernameGenerator;
    private final IPasswordGenerator passwordGenerator;

    public TrainerServiceImpl(ITrainerDao trainerDao,
                              IUsernameGenerator usernameGenerator,
                              IPasswordGenerator passwordGenerator) {
        this.trainerDao = trainerDao;
        this.usernameGenerator = usernameGenerator;
        this.passwordGenerator = passwordGenerator;
    }

    @Override
    public Trainer create(Trainer trainer) {
        log.debug("create(): firstName={}, lastName={}", trainer.getFirstName(), trainer.getLastName());
        trainer.setFirstName(SimpleUsernameGenerator.normalizeName(trainer.getFirstName()));
        trainer.setLastName(SimpleUsernameGenerator.normalizeName(trainer.getLastName()));

        if (trainer.getUsername() == null || trainer.getUsername().isBlank()) {
            String uniqueUsername = generateUniqueUsername(trainer);
            log.debug("create(): generated username={}", uniqueUsername);
            trainer.setUsername(uniqueUsername);
        }
        if (trainer.getPassword() == null || trainer.getPassword().isBlank()) {
            trainer.setPassword(passwordGenerator.generate());
            log.debug("create(): generated password (hidden)");
        }
        var saved = trainerDao.create(trainer);
        log.info("create(): trainer created id={}", saved.getId());
        return saved;
    }

    @Override
    public Trainer update(Trainer trainer) {
        log.debug("update(): id={}", trainer.getId());
        var updated = trainerDao.update(trainer);
        log.info("update(): id={}", updated.getId());
        return updated;
    }

    @Override
    public Optional<Trainer> findById(Long id) {
        log.debug("findById(): id={}", id);
        Optional<Trainer> result = trainerDao.findById(id);
        if (result.isEmpty()) {
            log.warn("findById(): trainer id={} not found", id);
        }
        return result;
    }

    @Override
    public List<Trainer> findAll() {
        List<Trainer> all = trainerDao.findAll();
        log.debug("findAll: size ={}", all.size());
        return all;
    }

    @Override
    public boolean deleteById(Long id) {
        boolean ok = trainerDao.deleteById(id);
        if (ok) {
            log.info("deleteById: trainer deleted id={}", id);
        } else {
            log.warn("deleteById: trainer id={} not found", id);
        }
        return ok;
    }

    private String generateUniqueUsername(Trainer trainer) {
        String baseUsername = usernameGenerator.generate(trainer);
        String finalUsername = baseUsername;
        int count = 0;
        while (usernameExists(finalUsername)) {
            count++;
            finalUsername = SimpleUsernameGenerator.addSuffix(baseUsername, count);
            log.trace("generateUniqueUsername(): {} already exists, trying {}", baseUsername, finalUsername);
        }
        return finalUsername;
    }

    private boolean usernameExists(String username) {
        return trainerDao.findAll().stream()
                .anyMatch(t -> username.equals(t.getUsername()));
    }
}
