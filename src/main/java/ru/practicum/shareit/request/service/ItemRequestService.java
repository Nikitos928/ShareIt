package ru.practicum.shareit.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.pageapleCreator.PageableCreater;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.request.mapper.ItemRequestMapper.toItemRequest;
import static ru.practicum.shareit.request.mapper.ItemRequestMapper.toItemRequestDto;

@Service
@Transactional(readOnly = true)
public class ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final PageableCreater pageableCreater;
    private final ItemRepository itemRepository;


    @Autowired
    public ItemRequestService(ItemRequestRepository itemRequestRepository,
                              UserRepository userRepository,
                              PageableCreater pageableCreater,
                              ItemRepository itemRepository) {
        this.itemRequestRepository = itemRequestRepository;
        this.userRepository = userRepository;
        this.pageableCreater = pageableCreater;
        this.itemRepository = itemRepository;
    }


    @Transactional
    public ItemRequestDto addRequest(ItemRequestDto itemRequestDto, Long userId) throws NotFoundException {
        ItemRequest itemRequest = toItemRequest(itemRequestDto);
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        itemRequest.setRequester(user);
        itemRequest.setCreated(LocalDateTime.now());
        return toItemRequestDto(itemRequestRepository.save(itemRequest));
    }


    public List<ItemRequestDto> getUserRequests(Long userId) throws NotFoundException {
        checkUserById(userId);
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId);

        Map<Long, List<Item>> mapItemsForRequests =
                mapItemsForRequests(itemRequests.stream().map(ItemRequest::getId).collect(Collectors.toList()));
        for (ItemRequest itemRequest : itemRequests) {
            itemRequest.setItemList(mapItemsForRequests.get(itemRequest.getId()));
        }
        return itemRequests.stream().map(ItemRequestMapper::toItemRequestDto).collect(Collectors.toList());
    }


    public List<ItemRequestDto> getNotUserRequests(Long userId, Integer from, Integer size) throws NotFoundException, BadRequestException {
        checkUserById(userId);
        List<ItemRequest> itemRequests = new ArrayList<>();
        Pageable pageable = pageableCreater.doPageable(from, size);
        Page<ItemRequest> itemRequestPage = itemRequestRepository.findAllByOtherUsers(userId, pageable);

        for (ItemRequest itemRequest : itemRequestPage.getContent()) {
            itemRequests.add(itemRequest);
        }

        Map<Long, List<Item>> mapItemsForRequests =
                mapItemsForRequests(itemRequests.stream().map(ItemRequest::getId).collect(Collectors.toList()));

        for (ItemRequest itemRequest : itemRequests) {
            itemRequest.setItemList(mapItemsForRequests.get(itemRequest.getId()));
        }
        return itemRequests.stream().map(ItemRequestMapper::toItemRequestDto).collect(Collectors.toList());
    }


    public ItemRequestDto getRequestById(Long requestId, Long userId) throws NotFoundException {
        checkUserById(userId);
        checkRequestById(requestId);
        ItemRequest itemRequest = itemRequestRepository.getById(requestId);


        Map<Long, List<Item>> mapItemsForRequests = mapItemsForRequests(Arrays.asList(itemRequest.getId()));
        itemRequest.setItemList(mapItemsForRequests.get(requestId));
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

    private Map<Long, List<Item>> mapItemsForRequests(List<Long> requestIds) {
        Map<Long, List<Item>> requestItemMap = new HashMap<>();
        List<Item> itemList = itemRepository.getAllById(requestIds);
        List<ItemRequest> itemRequestList = itemRequestRepository.getAllById(requestIds);
        for (ItemRequest itemRequest : itemRequestList) {
            List<Item> itemsToAdd = new ArrayList<>();
            for (Item item : itemList) {
                if (Objects.equals(item.getRequest().getId(), itemRequest.getId()))
                    itemsToAdd.add(item);
            }
            requestItemMap.put(itemRequest.getId(), itemsToAdd);
        }
        return requestItemMap;
    }
}
