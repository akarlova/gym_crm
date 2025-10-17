//package com.epam.gym_crm.config;
//
//import com.epam.gym_crm.domain.Trainee;
//import com.epam.gym_crm.domain.Trainer;
//import com.epam.gym_crm.domain.Training;
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.core.io.Resource;
//import org.springframework.core.io.ResourceLoader;
//import org.springframework.stereotype.Component;
//
//import java.io.InputStream;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ConcurrentMap;
//import javax.annotation.PostConstruct;
//
//@Component
//public class DataInitializer {
//    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
//
//    private final ConcurrentMap<Long, Trainee> traineeStorage;
//    private final ConcurrentMap<Long, Trainer> trainerStorage;
//    private final ConcurrentMap<Long, Training> trainingStorage;
//    private final ObjectMapper mapper;
//    private final ResourceLoader loader;
//
//    @Value("${trainee.data}")
//    private String traineeFile;
//    @Value("${trainer.data}")
//    private String trainerFile;
//    @Value("${training.data}")
//    private String trainingFile;
//
//    public DataInitializer(@Qualifier("traineeStorage") ConcurrentMap<Long, Trainee> traineeStorage,
//                           @Qualifier("trainerStorage") ConcurrentMap<Long, Trainer> trainerStorage,
//                           @Qualifier("trainingStorage") ConcurrentMap<Long, Training> trainingStorage,
//                           ObjectMapper mapper, ResourceLoader loader) {
//        this.traineeStorage = traineeStorage;
//        this.trainerStorage = trainerStorage;
//        this.trainingStorage = trainingStorage;
//        this.mapper = mapper;
//        this.loader = loader;
//    }
//
//    @PostConstruct
//    public void init() {
//        load(traineeFile, new TypeReference<List<Trainee>>() {
//        }, traineeStorage, "trainees");
//        load(trainerFile, new TypeReference<List<Trainer>>() {
//        }, trainerStorage, "trainers");
//        load(trainingFile, new TypeReference<List<Training>>() {
//        }, trainingStorage, "trainings");
//
//    }
//
//    private <T> void load(String location, TypeReference<List<T>> type, Map<Long, T> target,
//                          String label) {
//        try {
//            Resource res = loader.getResource(location);
//            if (!res.exists()) {
//                log.warn("Resource not found: {}", location);
//                return;
//            }
//            try (InputStream in = res.getInputStream()) {
//                List<T> list = mapper.readValue(in, type);
//                for (T t : list) {
//                    Long id = (Long) t.getClass().getMethod("getId").invoke(t);
//                    if (id != null) target.put(id, t);
//                }
//                log.info("Loaded {} {} from {}", target.size(), label, location);
//            }
//        } catch (Exception e) {
//            log.warn("Skip loading {} from {}: {}", label, location, e.toString());
//        }
//    }
//}
