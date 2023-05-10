package ru.practicum.shareit.request.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequesterIdOrderByCreatedDesc(Long userId);

    @Query("select r from ItemRequest r where r.requester.id <> ?1 order by r.created desc")
    Page<ItemRequest> findAllByOtherUsers(Long userId, Pageable pageable);

    @Query("SELECT e FROM ItemRequest e WHERE e.id IN (:ids)")
    List<ItemRequest> getAllById(List<Long> ids);
}
