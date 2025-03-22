package ru.practicum.shareit.request.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequestorOrderByCreatedDesc(User requestor);

    @Query("SELECT ir FROM ItemRequest ir WHERE ir.requestor.id <> :userId ORDER BY ir.created DESC")
    List<ItemRequest> findAllByRequestorNotOrderByCreatedDesc(@Param("userId") Long userId);
}