package com.epam.gym_crm.security;

import com.epam.gym_crm.domain.Trainee;
import com.epam.gym_crm.domain.Trainer;
import com.epam.gym_crm.domain.User;
import com.epam.gym_crm.repository.ITraineeRepository;
import com.epam.gym_crm.repository.ITrainerRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DbUserDetailsService implements UserDetailsService {
    private final ITraineeRepository traineeRepository;
    private final ITrainerRepository trainerRepository;

    public DbUserDetailsService(ITraineeRepository traineeRepository, ITrainerRepository trainerRepository) {
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Trainee trainee = traineeRepository.findByUsername(username).orElse(null);
        if (trainee != null && trainee.getUser() != null) {
            return toDetails(trainee.getUser(), "ROLE_TRAINEE");
        }
        Trainer trainer = trainerRepository.findByUsername(username).orElse(null);
        if (trainer != null && trainer.getUser() != null) {
            return toDetails(trainer.getUser(), "ROLE_TRAINER");
        }
        throw new UsernameNotFoundException("User not found" + username);
    }

    private UserDetails toDetails(User user, String role) {
        List<GrantedAuthority> auth = List.of(new SimpleGrantedAuthority(role));
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                true, true, true, true,
                auth
        );
    }
}
