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

        if(!userRepository3.findById(userId).isPresent() || !parkingLotRepository3.findById(parkingLotId).isPresent()){
            throw new Exception("Reservation cannot be made");
        }

        User user = userRepository3.findById(userId).get();
        ParkingLot parkingLot = parkingLotRepository3.findById(parkingLotId).get();

        List<Spot> spotList = parkingLot.getSpotList();

        SpotType spotType;
        if(numberOfWheels > 4){
            spotType = SpotType.OTHERS;
        }
        else if (numberOfWheels > 2){
            spotType = SpotType.FOUR_WHEELER;
        }
        else {
            spotType = SpotType.TWO_WHEELER;
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



        if(minPriceSpot == null){
            throw new Exception("There is no slot available");
        }

        Reservation reservation = new Reservation();
        minPriceSpot.setOccupied(true);
        reservation.setNumberOfHours(timeInHours);
        reservation.setSpot(minPriceSpot);
        reservation.setUser(user);

        //Bidirectional
        minPriceSpot.getReservationList().add(reservation);
        user.getReservationList().add(reservation);

        userRepository3.save(user);
        spotRepository3.save(minPriceSpot);

        return reservation;
    }
}
