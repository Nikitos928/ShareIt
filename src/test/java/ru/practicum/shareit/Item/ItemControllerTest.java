package ru.practicum.shareit.Item;

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
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;


    @Test
    @SneakyThrows
    void createComment() {
        CommentDto commentDto = CommentDto.builder().build();
        when(itemService.createComment(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(commentDto);
        String result = mockMvc.perform(post("/items/{itemId}/comment", 1)
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertEquals(objectMapper.writeValueAsString(commentDto), result);
    }


    @Test
    @SneakyThrows
    void searchItem() {
        List<ItemDto> items = Arrays.asList(ItemDto.builder().build(), ItemDto.builder().build());
        when(itemService.searchItem(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(items);
        String result = mockMvc.perform(get("/items/search"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertEquals(objectMapper.writeValueAsString(items), result);

        verify(itemService).searchItem(Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    @SneakyThrows
    void getItems() {
        List<ItemWithBookingDto> item = Arrays.asList(ItemWithBookingDto.builder().id(1L).build(),
                ItemWithBookingDto.builder().id(2L).build());

        when(itemService.getItems(Mockito.any())).thenReturn(item);

        String result = mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", "1")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertEquals(objectMapper.writeValueAsString(item), result);

        verify(itemService).getItems(1L);
    }

    @Test
    @SneakyThrows
    void updateItem() {
        ItemDto itemDto = ItemDto.builder().name("Name").description("description").available(true).build();

        when(itemService.updateItem(1L, itemDto, 1L)).thenReturn(itemDto);

        String result = mockMvc.perform(patch("/items/{id}", 1L)
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertEquals(objectMapper.writeValueAsString(itemDto), result);
    }

    @Test
    @SneakyThrows
    void getItem() {
        ItemWithBookingDto item = ItemWithBookingDto.builder().build();
        when(itemService.getItem(Mockito.any(), Mockito.any())).thenReturn(item);
        String result = mockMvc.perform(get("/items/{id}", 1L)
                        .header("X-Sharer-User-Id", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertEquals(objectMapper.writeValueAsString(item), result);

        verify(itemService).getItem(1L, 1L);
    }


    @Test
    @SneakyThrows
    void addItem() {
        ItemDto itemDto = ItemDto.builder().name("Name").description("description").available(true).build();

        when(itemService.addItem(itemDto, 1L)).thenReturn(itemDto);

        String result = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertEquals(objectMapper.writeValueAsString(itemDto), result);
    }

    @Test
    @SneakyThrows
    void addItem_whenItemIsNotValidAvailable_thenReturnedBadRequest() {
        ItemDto itemDto = ItemDto.builder().name("Name").description("description").build();

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).addItem(itemDto, 1L);
    }

    @Test
    @SneakyThrows
    void addItem_whenItemIsNotValidDescription_thenReturnedBadRequest() {
        ItemDto itemDto = ItemDto.builder().name("Name").description("").available(true).build();

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).addItem(itemDto, 1L);
    }

    @Test
    @SneakyThrows
    void addItem_whenItemIsNotValidName_thenReturnedBadRequest() {
        ItemDto itemDto = ItemDto.builder().description("description").available(true).build();

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).addItem(itemDto, 1L);
    }
}
