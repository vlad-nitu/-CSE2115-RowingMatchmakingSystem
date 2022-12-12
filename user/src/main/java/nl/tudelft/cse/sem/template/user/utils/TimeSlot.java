package nl.tudelft.cse.sem.template.user.utils;

import java.time.LocalDateTime;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Embeddable
@NoArgsConstructor
@EqualsAndHashCode
public class TimeSlot {
    @Column(name = "starttime")
    private LocalDateTime start;
    @Column(name = "endtime")
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

    public boolean overlaps(TimeSlot o) {
        return (this.end.isAfter(o.getStart()) && this.end.isBefore(o.getEnd())
                || (this.start.isAfter(o.getStart()) && this.start.isBefore(o.getEnd())));
    }
}
