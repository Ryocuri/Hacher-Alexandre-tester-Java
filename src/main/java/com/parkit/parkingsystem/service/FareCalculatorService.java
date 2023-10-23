package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

import java.time.Duration;

public class FareCalculatorService {

	public static final double DISCOUNT = 0.95;
	public static final int MS_TO_HOURS = 36000;

	public void calculateFare(Ticket ticket){
		calculateFare(ticket, false);
	}
	public void calculateFare(Ticket ticket, boolean discount){
		if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
			throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
		}

		double inHour = ticket.getInTime().getTime();
		double outHour = ticket.getOutTime().getTime();

		// Problème avec le duration.toHours() -> renvoie un long -> perte des décimales
		// Duration duration = Duration.between(ticket.getInTime().toInstant(), ticket.getOutTime().toInstant());
		// duration.toMinutes() <= 30

		double duration = outHour - inHour;

		duration = duration / (60*60*1000);

		if(duration <= 0.5){
			ticket.setPrice(0.0);
			return;
		}

		switch (ticket.getParkingSpot().getParkingType()){
			case CAR: {
				ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR * (discount ? DISCOUNT : 1));
				break;
			}
			case BIKE: {
				ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR * (discount ? DISCOUNT : 1));
				break;
			}
			default: throw new IllegalArgumentException("Unkown Parking Type");
		}
	}
}