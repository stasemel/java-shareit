package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = ?1 " +
            "AND b.status IN (?4) " +
            "AND ((b.startDate BETWEEN ?2 AND ?3) OR (b.endDate BETWEEN ?2 AND ?3))")
    List<Booking> findBookingByItemIdBetweenDateAndStatusIn(
            Long itemId,
            LocalDateTime start,
            LocalDateTime end,
            Set<BookingStatus> statuses
    );

    List<Booking> findByBookerIdOrderByStartDateDesc(Long userId);

    List<Booking> findByItemOwnerOrderByStartDateDesc(User owner);

    @Query("SELECT b FROM Booking b WHERE b.booker = ?1 AND b.endDate < CURRENT_TIMESTAMP ORDER BY b.startDate DESC")
    List<Booking> findByBookerIdPast(User user);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker = ?1 AND b.startDate <= CURRENT_TIMESTAMP AND b.endDate >= CURRENT_TIMESTAMP " +
            "ORDER BY b.startDate DESC")
    List<Booking> findByBookerIdCurrent(User user);

    @Query("SELECT b FROM Booking b WHERE b.booker = ?1 AND b.startDate > CURRENT_TIMESTAMP " +
            "ORDER BY b.startDate DESC")
    List<Booking> findByBookerIdFuture(User user);

    List<Booking> findByBookerAndStatusOrderByStartDateDesc(User user, BookingStatus bookingStatus);

    List<Booking> findByItemOwnerAndStatusOrderByStartDateDesc(User owner, BookingStatus bookingStatus);

    @Query("SELECT b FROM Booking b WHERE b.item.owner = ?1 AND b.endDate < CURRENT_TIMESTAMP ORDER BY b.startDate DESC")
    List<Booking> findByOwnerPast(User owner);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner = ?1 AND b.startDate <= CURRENT_TIMESTAMP AND b.endDate >= CURRENT_TIMESTAMP " +
            "ORDER BY b.startDate DESC")
    List<Booking> findByOwnerCurrent(User owner);

    @Query("SELECT b FROM Booking b WHERE b.item.owner = ?1 AND b.startDate > CURRENT_TIMESTAMP " +
            "ORDER BY b.startDate DESC")
    List<Booking> findByOwnerFuture(User owner);

    Optional<Booking> findFirstByItemAndBookerAndEndDateBefore(Item item, User user, LocalDateTime now);

    Optional<Booking> findFirstByItemAndEndDateBeforeAndStatusOrderByEndDateDesc(Item item, LocalDateTime now, BookingStatus bookingStatus);

    Optional<Booking> findFirstByItemAndStartDateAfterAndStatusOrderByStartDateDesc(Item item, LocalDateTime now, BookingStatus bookingStatus);
}
