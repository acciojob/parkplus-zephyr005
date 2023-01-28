package com.driver.services.impl;

import com.driver.model.Payment;
import com.driver.model.PaymentMode;
import com.driver.model.Reservation;
import com.driver.repository.PaymentRepository;
import com.driver.repository.ReservationRepository;
import com.driver.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    ReservationRepository reservationRepository2;
    @Autowired
    PaymentRepository paymentRepository2;

    @Override
    public Payment pay(Integer reservationId, int amountSent, String mode) throws Exception {
        Reservation reservation = reservationRepository2.findById(reservationId).get();
        Payment payment;
        int billPrice = reservation.getNumberOfHours()*reservation.getSpot().getPricePerHour();
        if(amountSent < billPrice){
            throw new Exception("Insufficient Amount");
        }
        else{
            if(!mode.equals(reservation.getPayment().getPaymentMode())){
                throw new Exception("Payment mode not detected");
            }
            else{
                payment = new Payment();
                payment.setPaymentCompleted(true);
                reservation.getSpot().setOccupied(true);
                reservationRepository2.save(reservation);
                paymentRepository2.save(payment);
            }
        }
        return payment;
    }
}
