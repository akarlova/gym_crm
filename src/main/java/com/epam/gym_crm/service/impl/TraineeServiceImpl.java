package com.epam.gym_crm.service.impl;

import com.epam.gym_crm.dao.ITraineeDao;
import com.epam.gym_crm.domain.Trainee;
import com.epam.gym_crm.service.ITraineeService;
import com.epam.gym_crm.util.IPasswordGenerator;
import com.epam.gym_crm.util.IUsernameGenerator;
import com.epam.gym_crm.util.impl.SimpleUsernameGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TraineeServiceImpl implements ITraineeService {
    private static final Logger log = LoggerFactory.getLogger(TraineeServiceImpl.class);
    private final ITraineeDao traineeDao;
    private final IUsernameGenerator usernameGenerator;
    private final IPasswordGenerator passwordGenerator;

    public TraineeServiceImpl(ITraineeDao traineeDao,
                              IUsernameGenerator usernameGenerator,
                              IPasswordGenerator passwordGenerator) {
        this.traineeDao = traineeDao;
        this.usernameGenerator = usernameGenerator;
        this.passwordGenerator = passwordGenerator;
    }

    @Override
    public Trainee create(Trainee trainee) {
        log.debug("create(): firstName={}, lastName={}", trainee.getFirstName(), trainee.getLastName());
        trainee.setFirstName(SimpleUsernameGenerator.normalizeName(trainee.getFirstName()));
        trainee.setLastName(SimpleUsernameGenerator.normalizeName(trainee.getLastName()));

        if (trainee.getUsername() == null || trainee.getUsername().isBlank()) {
            String uniqueUsername = generateUniqueUsername(trainee);
            log.debug("create(): generated username={}", uniqueUsername);
            trainee.setUsername(uniqueUsername);
        }
        if (trainee.getPassword() == null || trainee.getPassword().isBlank()) {
            trainee.setPassword(passwordGenerator.generate());
            log.debug("create(): generated password (hidden)");
        }
        var saved = traineeDao.create(trainee);
        log.info("create(): trainee created id={}", saved.getId());
        return saved;
    }

    @Override
    public Trainee update(Trainee trainee) {
        log.debug("update(): id={}", trainee.getId());
        var updated = traineeDao.update(trainee);
        log.info("update(): trainee updated id={}", updated.getId());
        return updated;
    }

    @Override
    public Optional<Trainee> findById(Long id) {
        log.debug("findById(): id={}", id);
        Optional<Trainee> result = traineeDao.findById(id);
        if (result.isEmpty()) {
            log.warn("findById(): trainee id={} not found", id);
        }
        return result;
    }

    @Override
    public List<Trainee> findAll() {
        List<Trainee> all = traineeDao.findAll();
        log.debug("findAll: size ={}", all.size());
        return all;
    }

    @Override
    public boolean deleteById(Long id) {
        boolean ok = traineeDao.deleteById(id);
        if (ok) {
            log.info("deleteById: trainee deleted id={}", id);
        } else {
            log.warn("deleteById: trainee id={} not found", id);
        }
        return ok;
    }

    private String generateUniqueUsername(Trainee trainee) {
        String baseUsername = usernameGenerator.generate(trainee);
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
        return traineeDao.findAll().stream()
                .anyMatch(t -> username.equals(t.getUsername()));
    }
}
