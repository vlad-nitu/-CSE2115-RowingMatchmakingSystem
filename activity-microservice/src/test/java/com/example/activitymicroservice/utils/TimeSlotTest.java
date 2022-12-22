package com.example.activitymicroservice.utils;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.example.activitymicroservice.utils.TimeSlot;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class TimeSlotTest {

    private TimeSlot timeSlot;

    /**
     * Executes before each test in order to initialise primary tested object TimeSlot.
     */
    @BeforeEach
    public void setUp() {
        this.timeSlot = new TimeSlot(LocalDateTime.of(2003, 12, 1, 23, 15),
                LocalDateTime.of(2003, 10, 5, 23, 15)
        );
    }

    @Test
    public void constructorTest() {
        timeSlot = new TimeSlot(LocalDateTime.of(2003, 12, 1, 23, 15),
                LocalDateTime.of(2003, 12, 1, 23, 15)
        );
        assertThat(timeSlot).isNotNull();
    }

    @Test
    void getStartTest() {
        assertThat(timeSlot.getStart())
                .isEqualTo(LocalDateTime.of(2003, 12, 1, 23, 15));
    }

    @Test
    void getEndTest() {
        assertThat(timeSlot.getEnd())
                .isEqualTo(LocalDateTime.of(2003, 10, 5, 23, 15));
    }

    @Test
    void setStartTest() {
        timeSlot.setStart(LocalDateTime.of(1990, 10, 5, 23, 15));
        assertThat(timeSlot.getStart())
                .isEqualTo(LocalDateTime.of(1990, 10, 5, 23, 15));
    }

    @Test
    void setEndTest() {
        timeSlot.setEnd(LocalDateTime.of(1990, 10, 5, 23, 15));
        assertThat(timeSlot.getEnd())
                .isEqualTo(LocalDateTime.of(1990, 10, 5, 23, 15));
    }

    @Test
    void overlapTest() {
        TimeSlot other = new TimeSlot(LocalDateTime.of(1990, 10, 5, 23, 15),
                LocalDateTime.of(2090, 10, 5, 23, 15));

        assertThat(timeSlot.overlaps(other)).isTrue();
    }

    @Test
    void doesNotOverlapTest() {
        TimeSlot other = new TimeSlot(LocalDateTime.of(1990, 10, 5, 23, 15),
                LocalDateTime.of(1991, 10, 5, 23, 15));

        assertThat(timeSlot.overlaps(other)).isFalse();
    }

    @Test
    void doesNotEntirelyOverlapEndTest() {

        this.timeSlot = new TimeSlot(LocalDateTime.of(2003, 10, 1, 23, 15),
                LocalDateTime.of(2003, 12, 5, 23, 15)
        );

        TimeSlot other = new TimeSlot(LocalDateTime.of(1990, 10, 5, 23, 15),
                LocalDateTime.of(2003, 11, 5, 23, 15));

        assertThat(timeSlot.overlaps(other)).isTrue();
    }

    @Test
    void doesNotEntirelyOverlapStartTest() {

        this.timeSlot = new TimeSlot(LocalDateTime.of(2003, 10, 1, 23, 15),
                LocalDateTime.of(2003, 12, 5, 23, 15)
        );

        TimeSlot other = new TimeSlot(LocalDateTime.of(2003, 11, 5, 23, 15),
                LocalDateTime.of(2100, 10, 5, 23, 15));

        assertThat(timeSlot.overlaps(other)).isTrue();
    }

    @Test
    void isIncludedTestFalse1() {
        this.timeSlot = new TimeSlot(LocalDateTime.of(2003, 10, 1, 23, 15),
                LocalDateTime.of(2003, 12, 5, 23, 15)
        );

        TimeSlot other = new TimeSlot(LocalDateTime.of(2002, 11, 5, 23, 15),
                LocalDateTime.of(2100, 10, 5, 23, 15));

        assertThat(timeSlot.isIncluded(other, LocalDateTime.now())).isFalse();
    }

    @Test
    void isIncludedTestFalse2() {
        this.timeSlot = new TimeSlot(LocalDateTime.of(2033, 10, 1, 23, 15),
                LocalDateTime.of(20334, 12, 5, 23, 15)
        );

        TimeSlot other = new TimeSlot(LocalDateTime.of(2002, 11, 5, 23, 15),
                LocalDateTime.of(2033, 10, 5, 23, 15));

        assertThat(timeSlot.isIncluded(other, LocalDateTime.now())).isFalse();
    }

    @Test
    void isIncludedTestTrue() {
        this.timeSlot = new TimeSlot(LocalDateTime.of(2033, 10, 1, 23, 15),
                LocalDateTime.of(2034, 12, 5, 23, 15)
        );

        TimeSlot other = new TimeSlot(LocalDateTime.of(2002, 11, 5, 23, 15),
                LocalDateTime.of(2100, 10, 5, 23, 15));

        assertThat(timeSlot.isIncluded(other, LocalDateTime.now())).isTrue();
    }

}
