package com.example.activitymicroservice.utils;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;

import javax.persistence.Entity;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class TimeSlot implements Serializable {
    static final long serialVersionUID = -3387516993124229948L;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime start;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime end;

    /**
     * Checks if the current TimeSlot is included in another TimeSlot.
     *
     * @param t1 a TimeSLot object that we check if the current one is included
     * @return a boolean
     */
    public boolean isIncluded(TimeSlot t1) {
        if (t1 == null) {
            return false;
        }
        if (this.getStart().isAfter(t1.getStart()) || this.getStart().isEqual(t1.getStart())) {
            if (this.getEnd().isBefore(t1.getEnd()) || this.getEnd().isEqual(t1.getEnd())) {
                return true;
            }
        }
        return false;
    }

    public boolean overlaps(TimeSlot o) {
        return (this.end.isAfter(o.getStart()) && this.end.isBefore(o.getEnd())
                || (this.start.isAfter(o.getStart()) && this.start.isBefore(o.getEnd())));
    }
}
