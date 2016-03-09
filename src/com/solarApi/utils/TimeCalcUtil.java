package com.solarApi.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

public class TimeCalcUtil {
	
	private ZoneId zoneId = ZoneId.of("America/Chicago");
	
	public Date getMidnightDate() {
		LocalTime midnight = LocalTime.MIDNIGHT;
		LocalDate today = LocalDate.now(zoneId);
		LocalDateTime todayMidnight = LocalDateTime.of(today, midnight);
		LocalDateTime tomorrowMidnight = todayMidnight.plusDays(1);
		Instant instant = tomorrowMidnight.atZone(zoneId).toInstant();
		
		return Date.from(instant);
	}
	
	public Date minutesFromNow(long minutes) {
		LocalDateTime minutesFromNow = LocalDateTime.now(zoneId).plusMinutes(minutes);
		Instant instant = minutesFromNow.atZone(zoneId).toInstant();
		
		return Date.from(instant);
	}
}