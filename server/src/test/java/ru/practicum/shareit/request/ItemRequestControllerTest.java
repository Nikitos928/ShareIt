package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;


    @Test
    @SneakyThrows
    void getRequestById() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder().description("description").build();
        when(itemRequestService.getRequestById(Mockito.any(), Mockito.any())).thenReturn(itemRequestDto);
        String result = mockMvc.perform(get("/requests/{requestId}", 1L)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertEquals(objectMapper.writeValueAsString(itemRequestDto), result);
    }

    @Test
    @SneakyThrows
    void getNotUserRequests() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder().description("description").build();
        List<ItemRequestDto> itemRequestDtoList = Arrays.asList(itemRequestDto, itemRequestDto);
        when(itemRequestService.getNotUserRequests(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(itemRequestDtoList);
        String result = mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", "1")
                        .param("from", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertEquals(objectMapper.writeValueAsString(itemRequestDtoList), result);
    }


    @Test
    @SneakyThrows
    void getUserRequest() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder().description("description").build();
        List<ItemRequestDto> itemRequestDtoList = Arrays.asList(itemRequestDto, itemRequestDto);
        when(itemRequestService.getUserRequests(Mockito.any())).thenReturn(itemRequestDtoList);
        String result = mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertEquals(objectMapper.writeValueAsString(itemRequestDtoList), result);
    }

    @Test
    @SneakyThrows
    void addRequest() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder().description("description").build();
        when(itemRequestService.addRequest(Mockito.any(), Mockito.any())).thenReturn(itemRequestDto);
        String result = mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertEquals(objectMapper.writeValueAsString(itemRequestDto), result);
    }

    @Test
    @SneakyThrows
    void addRequest_whenRequestIsNotValid_thenReturnedBadRequest() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder().build();

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isBadRequest());

        verify(itemRequestService, never()).addRequest(itemRequestDto, 1L);

    }


}
