package ru.practicum.shareit.booking.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Booking.Status;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerAndStatus(User booker, Status status);

    List<Booking> findAllByBookerAndStartBeforeAndEndAfter(User booker, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByBookerAndEndBefore(User booker, LocalDateTime end);

    List<Booking> findAllByBookerAndStartAfter(User booker, LocalDateTime start);

    List<Booking> findAllByBookerOrderByStartDesc(User booker);

    List<Booking> findAllByItemIdOrderByStartAsc(Long itemId);

    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.item.id = :itemId " +
            "AND b.end < :currentTime")
    boolean existsByBookerIdAndItemIdAndEndBefore(@Param("bookerId") Long bookerId, @Param("itemId") Long itemId,
                                                  @Param("currentTime") LocalDateTime currentTime);
}