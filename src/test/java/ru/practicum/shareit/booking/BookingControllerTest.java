package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;


    @SneakyThrows
    @Test
    void getAllBookingsByOwner() {
        
        List<BookingDto> bookingDto = Arrays.asList(BookingDto.builder().build(), BookingDto.builder().build());
        
        when(bookingService.getAllBookingsByOwner(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(bookingDto);

        String result = mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertEquals(objectMapper.writeValueAsString(bookingDto), result);

        verify(bookingService).getAllBookingsByOwner("ALL", 1L, 0, 2);

    }


    @SneakyThrows
    @Test
    void getAllBookingsByUser() {

        List<BookingDto> bookingDto = Arrays.asList(BookingDto.builder().build(), BookingDto.builder().build());
        when(bookingService.getAllBookingsByUser(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(bookingDto);

        String result = mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertEquals(objectMapper.writeValueAsString(bookingDto), result);

        verify(bookingService).getAllBookingsByUser("ALL", 1L, 0, 2);
    }

    @SneakyThrows
    @Test
    void findById() {

        BookingDto bookingDto = BookingDto.builder().build();
        when(bookingService.getById(Mockito.any(), Mockito.any())).thenReturn(bookingDto);
        String result = mockMvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertEquals(objectMapper.writeValueAsString(bookingDto), result);

        verify(bookingService).getById(1L, 1L);
    }


    @SneakyThrows
    @Test
    void approved() {

        BookingDto bookingDto = BookingDto.builder().build();

        when(bookingService.bookingApproving(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(bookingDto);

        String result = mockMvc.perform(patch("/bookings/{bookingId}", 1)
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertEquals(objectMapper.writeValueAsString(bookingDto), result);

        verify(bookingService).bookingApproving(1L, true, 1L);

    }

    @SneakyThrows
    @Test
    void create() {

        BookingDto bookingDto = BookingDto.builder().build();

        when(bookingService.addBooking(Mockito.any(), Mockito.any())).thenReturn(bookingDto);

        String result = mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertEquals(objectMapper.writeValueAsString(bookingDto), result);

        verify(bookingService).addBooking(bookingDto, 1L);
    }
}
