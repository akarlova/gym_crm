package com.epam.gym_crm.service.impl;

import com.epam.gym_crm.domain.Trainer;
import com.epam.gym_crm.domain.Training;
import com.epam.gym_crm.domain.User;
import com.epam.gym_crm.repository.ITraineeRepository;
import com.epam.gym_crm.repository.ITrainerRepository;
import com.epam.gym_crm.service.ITrainerService;
import com.epam.gym_crm.util.IPasswordGenerator;
import com.epam.gym_crm.util.IUsernameGenerator;
import com.epam.gym_crm.util.impl.SimpleUsernameGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class TrainerServiceImpl implements ITrainerService {
    private static final Logger log = LoggerFactory.getLogger(TrainerServiceImpl.class);
    private final ITrainerRepository trainerRepository;
    private final ITraineeRepository traineeRepository;
    private final IUsernameGenerator usernameGenerator;
    private final IPasswordGenerator passwordGenerator;
    private final PasswordEncoder passwordEncoder;

    public TrainerServiceImpl(ITrainerRepository trainerRepository,
                              ITraineeRepository traineeRepository,
                              IUsernameGenerator usernameGenerator,
                              IPasswordGenerator passwordGenerator,
                              PasswordEncoder passwordEncoder) {
        this.trainerRepository = trainerRepository;
        this.traineeRepository = traineeRepository;
        this.usernameGenerator = usernameGenerator;
        this.passwordGenerator = passwordGenerator;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public Trainer create(Trainer trainer) {
        var user = trainer.getUser();
        if (user == null) {
            throw new IllegalArgumentException("User must not be null");
        }
        log.debug("create(): firstName={}, lastName={}", user.getFirstName(), user.getLastName());
        user.setFirstName(SimpleUsernameGenerator.normalizeName(user.getFirstName()));
        user.setLastName(SimpleUsernameGenerator.normalizeName(user.getLastName()));

        if (user.getUsername() == null || user.getUsername().isBlank()) {
            String uniqueUsername = generateUniqueUsername(user);
            log.debug("create(): generated username={}", uniqueUsername);
            user.setUsername(uniqueUsername);
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            String rawPassword = passwordGenerator.generate();
            user.setRawPassword(rawPassword);
            user.setPassword(passwordEncoder.encode(rawPassword));
            log.debug("create(): generated password (hidden)");
        }
        Trainer saved = trainerRepository.save(trainer);
        log.info("create(): trainer created id={}", saved.getId());
        return saved;
    }

    @Override
    public Trainer updateProfile(String username, String newFirstName,
                                 String newLastName) {
        log.debug("updateProfile(): username={}", username);
        Trainer trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Trainer not found"));
        if (newFirstName != null) {
            trainer.getUser().setFirstName(SimpleUsernameGenerator.normalizeName(newFirstName));
        }
        if (newLastName != null) {
            trainer.getUser().setLastName(SimpleUsernameGenerator.normalizeName(newLastName));
        }
        Trainer updated = trainerRepository.update(trainer);
        log.info("updateProfile(): updated id={}, username={}", updated.getId(), username);
        return trainerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Trainee not found"));
    }

    @Override
    public Trainer getProfile(String username) {
        log.debug("getProfile(): username={}", username);
        return trainerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Trainer not found"));
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        log.debug("changePassword(): username={}", username);

        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("Password must not be blank");
        }
        Trainer trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Trainer not found"));
        String storedHash = trainer.getUser().getPassword(); // BCrypt

        if (!passwordEncoder.matches(oldPassword, storedHash)) {
            log.warn("changePassword(): old password mismatch for {}", username);
            throw new RuntimeException("Old password is incorrect");
        }
        String newHash = passwordEncoder.encode(newPassword);
        trainer.getUser().setPassword(newHash);
        trainerRepository.update(trainer);
        log.info("changePassword(): password updated for {}", username);
    }

    @Override
    public Trainer setActive(String username, boolean isActive) {
        log.debug("setActive(): username={}, isActive={}", username, isActive);

        Trainer trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Trainer not found"));
        trainer.getUser().setActive(isActive);
        Trainer updated = trainerRepository.update(trainer);
        log.info("setActive(): updated username={}, isActive={}", username, isActive);
        return updated;
    }

    @Override
    public List<Training> getTrainings(String username) {
        log.debug("getTrainings(): username={}", username);
        return trainerRepository.findTrainings(username);
    }

    @Override
    public List<Training> findTrainingsByDateRange(String username,
                                                   LocalDateTime from, LocalDateTime to) {
        log.debug("findTrainingsByDateRange(): username={}, {}..{}", username, from, to);

        if (from == null || to == null) throw new IllegalArgumentException("from/to must not be null");
        if (from.isAfter(to)) throw new IllegalArgumentException("from must be <= to");

        return trainerRepository.findTrainingsByDateRange(username, from, to);
    }

    @Override
    public List<Training> findTrainingsByTraineeName(String trainerUsername, String traineeName) {
        log.debug("findTrainingsByTraineeName(): username={}, traineeName='{}'",
                trainerUsername, traineeName);
        String trainee = traineeName == null ? "" : traineeName.trim();
        return trainerRepository.findTrainingsByTraineeName(trainerUsername, trainee);
    }

    @Override
    public List<Training> findTrainingsByType(String username, String trainingTypeName) {
        log.debug("findTrainingsByType(): username={}, type='{}'", username, trainingTypeName);
        if (trainingTypeName == null || trainingTypeName.isBlank()) {
            throw new IllegalArgumentException("trainingTypeName must not be blank");
        }
        return trainerRepository.findTrainingsByType(username, trainingTypeName.trim());
    }

    @Override
    public List<Trainer> findAll() {
        List<Trainer> all = trainerRepository.findAll();
        log.debug("findAll: size ={}", all.size());
        return all;
    }

    private String generateUniqueUsername(User user) {
        String baseUsername = usernameGenerator.generate(user);
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
        return traineeRepository.findByUsername(username).isPresent()
               || trainerRepository.findByUsername(username).isPresent();
    }
}
