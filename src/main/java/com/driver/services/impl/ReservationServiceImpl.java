package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.ReservationRepository;
import com.driver.repository.SpotRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {
    @Autowired
    UserRepository userRepository3;
    @Autowired
    SpotRepository spotRepository3;
    @Autowired
    ReservationRepository reservationRepository3;
    @Autowired
    ParkingLotRepository parkingLotRepository3;
    @Override
    public Reservation reserveSpot(Integer userId, Integer parkingLotId, Integer timeInHours, Integer numberOfWheels) throws Exception {
        User user = userRepository3.findById(userId).get();
        ParkingLot parkingLot = parkingLotRepository3.findById(parkingLotId).get();

        if(user == null || parkingLot == null){
            throw new Exception("Reservation cannot be made");
        }

        List<Spot> spotList = parkingLot.getSpotList();

        SpotType spotType;
        if(numberOfWheels == 2){
            spotType = SpotType.TWO_WHEELER;
        }
        else if (numberOfWheels == 4){
            spotType = SpotType.FOUR_WHEELER;
        }
        else {
            spotType = SpotType.OTHERS;
        }

        Spot minPriceSpot = null;
        int minPrice = Integer.MAX_VALUE;

        for(Spot spot : spotList){
            if(spot.getOccupied() == false && minPrice <= spot.getPricePerHour() && spotType.equals(SpotType.TWO_WHEELER)){
                if(spotType.equals(SpotType.TWO_WHEELER) || (spotType.equals(SpotType.FOUR_WHEELER) || spotType.equals(SpotType.OTHERS))){
                    minPriceSpot = spot;
                }
            }

            else if(spot.getOccupied() == false && minPrice <= spot.getPricePerHour() && spotType.equals(SpotType.FOUR_WHEELER)){
                if((spotType.equals(SpotType.FOUR_WHEELER) || spotType.equals(SpotType.OTHERS))){
                    minPriceSpot = spot;
                }
            }

            else if(spot.getOccupied() == false && minPrice <= spot.getPricePerHour() && spotType.equals(SpotType.OTHERS)){
                minPriceSpot = spot;
            }
        }

        Reservation reservation = new Reservation();

        if(minPriceSpot != null){
            minPriceSpot.setOccupied(true);
            user.getReservationList().add(reservation);
            spotRepository3.save(minPriceSpot);
            userRepository3.save(user);
            reservation.setSpot(minPriceSpot);
            reservation.setUser(user);
            reservation.setNumberOfHours(timeInHours);
            reservationRepository3.save(reservation);
        }
        else{
            throw new Exception("There is no slot available");
        }

        return reservation;
    }
}
