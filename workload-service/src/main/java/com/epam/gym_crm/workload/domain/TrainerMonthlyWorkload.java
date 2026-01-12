package com.epam.gym_crm.workload.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "trainer_monthly_workload",
        uniqueConstraints = @UniqueConstraint(columnNames = {"trainer_username", "year", "month"}))
public class TrainerMonthlyWorkload {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trainer_username", nullable = false)
    private String trainerUsername;

    @Column(name = "trainer_first_name", nullable = false)
    private String trainerFirstName;

    @Column(name = "trainer_last_name", nullable = false)
    private String trainerLastName;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "year_value", nullable = false)
    private int year;

    @Column(name = "month_value", nullable = false)
    private int month;

    @Column(name = "total_minutes", nullable = false)
    private int totalMinutes;

    public TrainerMonthlyWorkload() {}

    public Long getId() { return id; }

    public String getTrainerUsername() { return trainerUsername; }
    public void setTrainerUsername(String trainerUsername) { this.trainerUsername = trainerUsername; }

    public String getTrainerFirstName() { return trainerFirstName; }
    public void setTrainerFirstName(String trainerFirstName) { this.trainerFirstName = trainerFirstName; }

    public String getTrainerLastName() { return trainerLastName; }
    public void setTrainerLastName(String trainerLastName) { this.trainerLastName = trainerLastName; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }

    public int getTotalMinutes() { return totalMinutes; }
    public void setTotalMinutes(int totalMinutes) { this.totalMinutes = totalMinutes; }
}
