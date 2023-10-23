package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

public class FareCalculatorServiceTest {

	public static final int HOUR = 3600000;
	public static final int DAY = 24 * HOUR;
	public static final int THREE_QUARTERS = 45 * 60 * 1000;
	public static final int HALF_HOUR = 30 * 60 * 1000;
	public static final double RECURRENT_DISCOUNT = 0.95;
	private static FareCalculatorService fareCalculatorService;
	private Ticket ticket;

	@BeforeAll
	private static void setUp() {
		fareCalculatorService = new FareCalculatorService();
	}

	@BeforeEach
	private void setUpPerTest() {
		ticket = new Ticket();
	}

	@Test
	public void calculateFareCar(){
		Date inTime = new Date();
		inTime.setTime( System.currentTimeMillis() - (  HOUR) );
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals(ticket.getPrice(), Fare.CAR_RATE_PER_HOUR);
	}

	@Test
	public void calculateFareBike(){
		Date inTime = new Date();
		inTime.setTime( System.currentTimeMillis() - (  HOUR) );
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals(ticket.getPrice(), Fare.BIKE_RATE_PER_HOUR);
	}

	@Test
	public void calculateFareUnkownType(){
		Date inTime = new Date();
		inTime.setTime( System.currentTimeMillis() - (  HOUR) );
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, null,false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
	}

	@Test
	public void calculateFareBikeWithFutureInTime(){
		Date inTime = new Date();
		inTime.setTime( System.currentTimeMillis() + (  HOUR) );
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
	}

	@Test
	public void calculateFareBikeWithLessThanOneHourParkingTime(){
		Date inTime = new Date();
		inTime.setTime( System.currentTimeMillis() - THREE_QUARTERS);//45 minutes parking time should give 3/4th parking fare
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals((0.75 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice() );
	}

	@Test
	public void calculateFareCarWithLessThanOneHourParkingTime(){
		Date inTime = new Date();
		inTime.setTime( System.currentTimeMillis() - THREE_QUARTERS );//45 minutes parking time should give 3/4th parking fare
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals( (0.75 * Fare.CAR_RATE_PER_HOUR) , ticket.getPrice());
	}

	@Test
	public void calculateFareCarWithMoreThanADayParkingTime(){
		Date inTime = new Date();
		inTime.setTime( System.currentTimeMillis() - DAY );//24 hours parking time should give 24 * parking fare per hour
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals( (24 * Fare.CAR_RATE_PER_HOUR) , ticket.getPrice());
	}

	@Test
	public void calculateFareCarWithLessThan30minutesParkingTime(){
		Date inTime = new Date();
		inTime.setTime( System.currentTimeMillis() - HALF_HOUR);//30 minutes parking time should give 0 parking fare
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);

		assertEquals( 0 , ticket.getPrice());
	}

	@Test
	public void calculateFareBikeWithLessThan30minutesParkingTime(){
		Date inTime = new Date();
		inTime.setTime( System.currentTimeMillis() - HALF_HOUR );//30 minutes parking time should give 0 parking fare
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);

		assertEquals( 0 , ticket.getPrice());
	}

	@Test
	public void calculateFareCarWithDiscount(){
		Date inTime = new Date();
		inTime.setTime( System.currentTimeMillis() - HOUR);
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,true);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket, true);

		assertEquals( Fare.CAR_RATE_PER_HOUR * RECURRENT_DISCOUNT , ticket.getPrice());
	}

	@Test
	public void calculateFareBikeWithDiscount(){
		Date inTime = new Date();
		inTime.setTime( System.currentTimeMillis() - HOUR );
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,true);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket, true);

		assertEquals( Fare.BIKE_RATE_PER_HOUR * RECURRENT_DISCOUNT, ticket.getPrice());
	}
	// TODO : run tests, add functionality
}
