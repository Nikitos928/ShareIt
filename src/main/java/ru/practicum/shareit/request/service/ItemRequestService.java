package ru.practicum.shareit.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.pageapleCreator.PageableCreater;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.storage.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.shareit.request.mapper.ItemRequestMapper.toItemRequest;
import static ru.practicum.shareit.request.mapper.ItemRequestMapper.toItemRequestDto;

@Service
@Slf4j
@RestController
public class ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final PageableCreater pageableCreater;
    private final ItemService itemService;

    public ItemRequestService(ItemRequestRepository itemRequestRepository,
                              UserRepository userRepository,
                              PageableCreater pageableCreater,
                              ItemService itemService) {
        this.itemRequestRepository = itemRequestRepository;
        this.userRepository = userRepository;
        this.pageableCreater = pageableCreater;
        this.itemService = itemService;
    }

    @Transactional
    public ItemRequestDto addRequest(ItemRequestDto itemRequestDto, Long userId) throws NotFoundException {
        ItemRequest itemRequest = toItemRequest(itemRequestDto);
        checkUserById(userId);
        itemRequest.setRequester(userRepository.getById(userId));
        itemRequest.setCreated(LocalDateTime.now());
        return toItemRequestDto(itemRequestRepository.save(itemRequest));
    }


    public List<ItemRequestDto> getUserRequests(Long userId) throws NotFoundException {
        checkUserById(userId);
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId);
        for (ItemRequest itemRequest : itemRequests) {
            itemRequest.setItemList(itemService.findItemsByRequestId(itemRequest.getId()));
        }
        return itemRequests.stream().map(ItemRequestMapper::toItemRequestDto).collect(Collectors.toList());
    }


    public List<ItemRequestDto> getNotUserRequests(Long userId, Integer from, Integer size) throws NotFoundException, BadRequestException {
        checkUserById(userId);
        List<ItemRequest> itemRequests = new ArrayList<>();
        Pageable pageable = pageableCreater.doPageable(from, size);
        Page<ItemRequest> itemRequestPage = itemRequestRepository.findAllByOtherUsers(userId, pageable);
        for (ItemRequest itemRequest : itemRequestPage.getContent()) {
            if (!Objects.equals(itemRequest.getRequester().getId(), userId)) {
                itemRequests.add(itemRequest);
            }
        }

        for (ItemRequest itemRequest : itemRequests) {
            itemRequest.setItemList(itemService.findItemsByRequestId(itemRequest.getId()));
        }
        return itemRequests.stream().map(ItemRequestMapper::toItemRequestDto).collect(Collectors.toList());
    }


    public ItemRequestDto getRequestById(Long requestId, Long userId) throws NotFoundException {
        checkUserById(userId);
        checkRequestById(requestId);
        ItemRequest itemRequest = itemRequestRepository.getById(requestId);
        itemRequest.setItemList(itemService.findItemsByRequestId(requestId));
        return toItemRequestDto(itemRequest);
    }

    private void checkUserById(Long userId) throws NotFoundException {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
    }

    private void checkRequestById(Long requestId) throws NotFoundException {
        if (!itemRequestRepository.existsById(requestId)) {
            throw new NotFoundException("Запрос с id = " + requestId + " не найд");
        }
    }
}
