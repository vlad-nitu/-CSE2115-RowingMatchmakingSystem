package com.example.micro.utils;

import java.time.LocalDateTime;
import java.util.Objects;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class TimeSlot {
    private LocalDateTime start;
    private LocalDateTime end;

    public TimeSlot(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TimeSlot timeSlot = (TimeSlot) o;
        return start.equals(timeSlot.start) && end.equals(timeSlot.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }

    public boolean overlaps(TimeSlot o) {
        return (this.end.isAfter(o.getStart()) && this.end.isBefore(o.getEnd()))
                || (this.start.isAfter(o.getStart()) && this.start.isBefore(o.getEnd()));
    }
}
