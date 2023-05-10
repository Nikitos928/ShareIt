package ru.practicum.shareit.request;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.pageapleCreator.PageableCreater;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;
import static ru.practicum.shareit.request.mapper.ItemRequestMapper.toItemRequestDto;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PageableCreater pageableCreater;
    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    ItemRequestService itemRequestService;


    ItemRequestDto itemRequestDto = ItemRequestDto.builder()
            .id(1L)
            .created(LocalDateTime.now())
            .description("description")
            .items(Arrays.asList(ItemDto.builder().build(), ItemDto.builder().build()))
            .build();

    ItemRequest itemRequest = new ItemRequest(1L, "des", new User(), null, new ArrayList<>());

    List<ItemRequest> itemRequestList = Arrays.asList(itemRequest, itemRequest);


    @Test
    @SneakyThrows
    void getRequestById() {
        when(userRepository.existsById(Mockito.any())).thenReturn(true);
        when(itemRequestRepository.existsById(Mockito.any())).thenReturn(true);
        when(itemRequestRepository.getById(Mockito.any())).thenReturn(itemRequest);
        when(itemRequestRepository.getAllById(Mockito.any()))
                .thenReturn(itemRequestList);
        when(itemRepository.getAllById(Mockito.any())).thenReturn(new ArrayList<>());

        Assertions.assertEquals(itemRequestService.getRequestById(1L, 1L), toItemRequestDto(itemRequest));
    }

    @Test
    @SneakyThrows
    void getRequestById_whenRequestNofFound_thenNotFoundException() {
        when(userRepository.existsById(Mockito.any())).thenReturn(true);
        when(itemRequestRepository.existsById(Mockito.any())).thenReturn(false);
        Assertions.assertThrows(NotFoundException.class, () -> itemRequestService.getRequestById(1L, 1L));
    }


    @Test
    @SneakyThrows
    void getRequestById_whenUserNofFound_thenNotFoundException() {
        when(userRepository.existsById(Mockito.any())).thenReturn(false);

        Assertions.assertThrows(NotFoundException.class, () -> itemRequestService.getRequestById(1L, 1L));
    }

    @Test
    @SneakyThrows
    void getNotUserRequests() {
        when(userRepository.existsById(Mockito.any())).thenReturn(true);
        when(itemRequestRepository.findAllByOtherUsers(Mockito.any(), Mockito.any())).thenReturn(Page.empty());
        Assertions.assertEquals(itemRequestService.getNotUserRequests(1L, 1, 1), new ArrayList<>());
    }

    @Test
    @SneakyThrows
    void getNotUserRequests_whenUserNofFound_thenNotFoundException() {
        when(userRepository.existsById(Mockito.any())).thenReturn(false);

        Assertions.assertThrows(NotFoundException.class, () -> itemRequestService.getNotUserRequests(1L, 1, 1));

    }

    @Test
    @SneakyThrows
    void getUserRequests() {
        when(userRepository.existsById(Mockito.any())).thenReturn(true);
        when(itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(Mockito.any())).thenReturn(itemRequestList);
        when(itemRequestRepository.getAllById(Mockito.any()))
                .thenReturn(itemRequestList);
        when(itemRepository.getAllById(Mockito.any())).thenReturn(new ArrayList<>());

        Assertions.assertEquals(itemRequestService.getUserRequests(1L), itemRequestList.stream().map(ItemRequestMapper::toItemRequestDto).collect(Collectors.toList()));

    }

    @Test
    @SneakyThrows
    void getUserRequests_whenUserNofFound_thenNotFoundException() {
        when(userRepository.existsById(Mockito.any())).thenReturn(false);

        Assertions.assertThrows(NotFoundException.class, () -> itemRequestService.getUserRequests(1L));

        verify(itemRequestRepository, never()).save(Mockito.any());
    }


    @Test
    @SneakyThrows
    void addRequest() {
        when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(new User()));
        when(itemRequestRepository.save(Mockito.any())).thenReturn(itemRequest);

        Assertions.assertEquals(itemRequestService.addRequest(itemRequestDto, 1L), toItemRequestDto(itemRequest));

        verify(itemRequestRepository).save(Mockito.any());
    }

    @Test
    @SneakyThrows
    void addRequest_whenUserNofFound_thenNotFoundException() {

        Assertions.assertThrows(NotFoundException.class, () -> itemRequestService.addRequest(itemRequestDto, 1L));

        verify(itemRequestRepository, never()).save(Mockito.any());
    }
}
