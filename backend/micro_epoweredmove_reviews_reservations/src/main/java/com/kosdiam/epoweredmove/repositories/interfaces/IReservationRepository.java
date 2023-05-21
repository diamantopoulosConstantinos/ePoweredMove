package com.kosdiam.epoweredmove.repositories.interfaces;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kosdiam.epoweredmove.models.dtos.ReservationDto;

@Repository
public interface IReservationRepository {
    ReservationDto create(ReservationDto reservation);
    ReservationDto get(String id);
    List<ReservationDto> getAll();
    ReservationDto update(ReservationDto reservation);
    Boolean delete(String id);
    List<ReservationDto> getAllByPlugId(String plugId);
    List<ReservationDto> getAllByOwner(String userId);
}
