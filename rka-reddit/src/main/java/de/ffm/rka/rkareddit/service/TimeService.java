package de.ffm.rka.rkareddit.service;

import java.time.LocalDateTime;
import java.time.temporal.TemporalUnit;

public class TimeService {

    public static boolean isBehindDeadline(long maxMinDifference, LocalDateTime deadLine){
        return LocalDateTime.now().minusMinutes(maxMinDifference).isAfter(deadLine);
    }
}
