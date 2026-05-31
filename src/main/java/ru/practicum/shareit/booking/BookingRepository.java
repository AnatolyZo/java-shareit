package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdOrderByCreatedDesc(long bookerId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId AND b.start <= :now AND b.end >= :now ORDER BY b.created DESC")
    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfter(@Param("bookerId") long bookerId, @Param("now") LocalDateTime time);

    List<Booking> findByBookerIdAndEndIsBeforeOrderByCreatedDesc(long bookerId, LocalDateTime time);

    List<Booking> findByBookerIdAndStartIsAfterOrderByCreatedDesc(long bookerId, LocalDateTime time);

    List<Booking> findByBookerIdAndStatusOrderByCreatedDesc(long bookerId, BookingStatus state);

    List<Booking> findByItem_OwnerIdOrderByCreatedDesc(long ownerId);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.start <= :now AND b.end >= :now ORDER BY b.created DESC")
    List<Booking> findByItem_OwnerIdAndStartIsBeforeAndEndIsAfter(@Param("ownerId") long ownerId, @Param("now") LocalDateTime time);

    List<Booking> findByItem_OwnerIdAndEndIsBeforeOrderByCreatedDesc(long bookerId, LocalDateTime time);

    List<Booking> findByItem_OwnerIdAndStartIsAfterOrderByCreatedDesc(long bookerId, LocalDateTime time);

    List<Booking> findByItem_OwnerIdAndStatusOrderByCreatedDesc(long bookerId, BookingStatus state);

    @Query("SELECT MAX(b.end) FROM Booking b WHERE b.item.id = :itemId AND b.status = 'APPROVED' AND b.end < :now")
    LocalDateTime findLastBookingEnd(@Param("itemId") long itemId, @Param("now") LocalDateTime now);

    @Query("SELECT MIN(b.start) FROM Booking b WHERE b.item.id = :itemId AND b.status = 'APPROVED' AND b.start > :now")
    LocalDateTime findNextBookingStart(@Param("itemId") long itemId, @Param("now") LocalDateTime now);
}
