package com.reppo.backend.entity;

import com.reppo.backend.enums.SplitType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class WorkoutTest{
    @Autowired
    private TestEntityManager em;

    @Test
    void addExerciseLog_setBothSides_persistsForeignKey() {
        //Arrange
        Exercise exercise = new Exercise();
        exercise.setPrimaryMuscle("chest");
        exercise.setName("Bench press");
        exercise.setExternalId("al-sok");
        em.persist(exercise);

        User user = new User();
        user.setEmail("abc@gmail.com");
        user.setDisplayName("User1");
        user.setPasswordHash("a2adas@a");
        user.setTimezone("America/New_York");
        em.persist(user);

        
        Workout workout = new Workout();
        workout.setPerformedAt(Instant.now());
        workout.setSplitType(SplitType.PUSH);
        workout.setUser(user);

        ExerciseLog log1 = new ExerciseLog();
        log1.setExercise(exercise);
        log1.setReps(12);
        log1.setSets(3);
        log1.setWeightKg(20.00);
        log1.setOrderIndex(0);

        ExerciseLog log2 = new ExerciseLog();
        log2.setExercise(exercise);
        log2.setReps(6);
        log2.setSets(2);
        log2.setWeightKg(25.00);
        log2.setOrderIndex(1);


        //Act
        workout.addExerciseLog(log1);
        workout.addExerciseLog(log2);
        em.persist(workout);
        em.flush();
        em.clear();

        //Assert
        Workout reloaded = em.find(Workout.class, workout.getId());

        assertThat(reloaded.getExerciseLogs()).hasSize(2);
        assertThat(reloaded.getExerciseLogs())
                .allSatisfy(log -> assertThat(log.getWorkout().getId()).isEqualTo(reloaded.getId()));

    }
}
