package com.epam.gym_crm.service.impl;

import com.epam.gym_crm.domain.Trainee;
import com.epam.gym_crm.domain.Trainer;
import com.epam.gym_crm.domain.Training;
import com.epam.gym_crm.domain.User;
import com.epam.gym_crm.repository.ITraineeRepository;
import com.epam.gym_crm.repository.ITrainerRepository;
import com.epam.gym_crm.service.ITraineeService;
import com.epam.gym_crm.util.IPasswordGenerator;
import com.epam.gym_crm.util.IUsernameGenerator;
import com.epam.gym_crm.util.impl.SimpleUsernameGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TraineeServiceImpl implements ITraineeService {
    private static final Logger log = LoggerFactory.getLogger(TraineeServiceImpl.class);
    private final ITraineeRepository traineeRepository;
    private final ITrainerRepository trainerRepository;
    private final IUsernameGenerator usernameGenerator;
    private final IPasswordGenerator passwordGenerator;
    private final PasswordEncoder passwordEncoder;

    public TraineeServiceImpl(ITraineeRepository traineeRepository,
                              ITrainerRepository trainerRepository,
                              IUsernameGenerator usernameGenerator,
                              IPasswordGenerator passwordGenerator,
                              PasswordEncoder passwordEncoder) {
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
        this.usernameGenerator = usernameGenerator;
        this.passwordGenerator = passwordGenerator;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Trainee create(Trainee trainee) {
        var user = trainee.getUser();
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
        Trainee saved = traineeRepository.save(trainee);
        log.info("create(): trainee created id={}", saved.getId());
        return saved;
    }

    @Override
    public Trainee updateProfile(String username, String newFirstName,
                                 String newLastName, LocalDate newDateOfBirth,
                                 String newAddress) {
        log.debug("updateProfile(): username={}", username);

        Trainee current = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Trainee not found"));

        if (newFirstName != null) {
            current.getUser().setFirstName(SimpleUsernameGenerator.normalizeName(newFirstName));
        }
        if (newLastName != null) {
            current.getUser().setLastName(SimpleUsernameGenerator.normalizeName(newLastName));
        }
        if (newDateOfBirth != null) {
            current.setDateOfBirth(newDateOfBirth);
        }
        if (newAddress != null) {
            current.setAddress(newAddress.trim());
        }
        Trainee updated = traineeRepository.update(current);
        log.info("updateProfile(): updated id={}, username={}", updated.getId(), username);

        return traineeRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Trainee not found"));
    }

    @Override
    public List<Trainee> findAll() {
        List<Trainee> all = traineeRepository.findAll();
        log.debug("findAll: size ={}", all.size());
        return all;
    }

    @Override
    public List<Training> getTrainings(String username) {
        log.debug("getTrainings(): username={}", username);
        return traineeRepository.findTrainings(username);
    }

    @Override
    public List<Training> findTrainingsByDateRange(String username,
                                                   LocalDateTime from, LocalDateTime to) {
        log.debug("findTrainingsByDateRange(): username={}, {}..{}", username, from, to);
        if (from == null || to == null) {
            throw new IllegalArgumentException("from/to must not be null");
        }
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("from must be <= to");
        }
        return traineeRepository.findTrainingsByDateRange(username, from, to);
    }

    @Override
    public List<Training> findTrainingsByTrainerName(String traineeUsername,
                                                     String trainerName) {

        log.debug("findTrainingsByTrainerName(): traineeUsername={}, " +
                  "trainerName={}", traineeUsername, trainerName);
        String trainer = (trainerName == null) ? "" : trainerName.trim();
        return traineeRepository.findTrainingsByTrainerName(traineeUsername, trainer);
    }

    @Override
    public List<Training> findTrainingsByType(String username,
                                              String trainingTypeName) {
        log.debug("findTrainingsByType(): username={}, training type={}", username, trainingTypeName);
        if (trainingTypeName == null || trainingTypeName.isBlank()) {
            throw new IllegalArgumentException("trainingTypeName must not be blank");
        }
        return traineeRepository.findTrainingsByType(username, trainingTypeName.trim());
    }

    @Override
    public List<Trainer> findNotAssignedTrainers(String username) {
        log.debug("findNotAssignedTrainers(): username={}", username);
        return traineeRepository.findNotAssignedTrainers(username);
    }

    @Override
    public Trainee updateTrainers(String username,
                                  List<String> trainerUsernames) {
        log.debug("updateTrainers(): username={}, trainers={}", username, trainerUsernames);
        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Trainee not found"));

        Set<Trainer> current = new HashSet<>(trainee.getTrainers());
        if (trainerUsernames != null) {
            for (String tu : trainerUsernames) {
                String u = (tu == null) ? "" : tu.trim();
                if (u.isEmpty()) {
                    continue;
                }
                Trainer trn = trainerRepository.findByUsername(u)
                        .orElseThrow(() -> new RuntimeException("Trainer not found: " + u));
                current.add(trn);
            }
        }
        trainee.setTrainers(current);
        Trainee updated = traineeRepository.update(trainee);
        log.info("updateTrainers(): updated id={}, trainers={}", updated.getId(), current.size());
        return updated;
    }

    @Override
    public boolean deleteByUsername(String username) {
        log.debug("deleteByUsername(): username={}", username);
        boolean exists = traineeRepository.findByUsername(username).isPresent();
        if (!exists) {
            log.warn("deleteByUsername(): trainee={} not found", username);
            return false;
        }
        traineeRepository.deleteByUsername(username);
        log.info("deleteByUsername(): trainee {} deleted", username);
        return true;
    }

    @Override
    public Trainee getProfile(String username) {
        log.debug("getProfile(): username={}", username);
        return traineeRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Trainee not found"));
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        log.debug("changePassword(): username={}", username);

        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("Password must not be blank");
        }

        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Trainee not found"));

        String storedHash = trainee.getUser().getPassword();

        if (!passwordEncoder.matches(oldPassword, storedHash)) {
            log.warn("changePassword(): old password mismatch for username={}", username);
            throw new RuntimeException("Old password is incorrect");
        }
        String newHash = passwordEncoder.encode(newPassword);
        trainee.getUser().setPassword(newHash);

        traineeRepository.update(trainee);
        log.info("changePassword(): password updated for username={}", username);
    }


    @Override
    public Trainee setActive(String username, boolean isActive) {
        log.debug("setActive(): username={}", username);

        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Trainee not found"));
        trainee.getUser().setActive(isActive);
        Trainee updated = traineeRepository.update(trainee);
        log.info("setActive(): updated username={}, active={}", username, isActive);
        return updated;
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
