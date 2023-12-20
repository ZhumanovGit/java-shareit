package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.StateStatus;
import ru.practicum.shareit.booking.service.BookingService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    ObjectMapper mapper;
    @MockBean
    BookingService service;
    @Autowired
    private MockMvc mvc;

    @Test
    public void createBooking_whenRequestIsCorrect_thenReturnNewBooking() throws Exception {
        long bookerId = 1L;
        BookingCreateDto createDto = BookingCreateDto.builder()
                .itemId(1L)
                .start(LocalDateTime.of(2024, 1, 1, 1, 1))
                .end(LocalDateTime.of(2024, 2, 1, 1, 1))
                .build();
        BookingDto result = BookingDto.builder().id(1L).build();
        when(service.createBooking(any(BookingCreateDto.class), anyLong(), anyBoolean())).thenReturn(result);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", bookerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(result)));
    }

    @Test
    public void createBooking_whenRequestHasWrongBody_thenReturnStatus400() throws Exception {
        long bookerId = 1L;
        BookingCreateDto createDto = BookingCreateDto.builder()
                .start(LocalDateTime.of(2024, 1, 1, 1, 1))
                .end(LocalDateTime.of(2024, 2, 1, 1, 1))
                .build();

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", bookerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createBooking_whenMissHeader_thenReturnStatus500() throws Exception {

        mvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    @Test
    public void patchBooking_whenRequestIsCorrect_thenReturnUpdatedBooking() throws Exception {
        long ownerId = 1L;
        long bookingId = 1L;
        String approved = "true";
        BookingDto result = BookingDto.builder().id(1L).build();
        when(service.approveBooking(anyLong(), anyBoolean(), anyLong())).thenReturn(result);

        mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", ownerId)
                        .param("approved", approved)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(result)));
    }

    @Test
    public void patchBooking_whenMissHeader_thenReturnStatus500() throws Exception {

        mvc.perform(patch("/bookings/{bookingId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    @Test
    public void patchBooking_whenMissParam_thenReturnStatus500() throws Exception {

        mvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    @Test
    public void getBookingById_whenRequestIsCorrect_thenReturnNeedBooking() throws Exception {
        long userId = 1L;
        long bookingId = 1L;
        BookingDto result = BookingDto.builder().id(1L).build();
        when(service.getBookingById(anyLong(), anyLong())).thenReturn(result);

        mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(result)));
    }

    @Test
    public void getBookingById_whenMissHeader_thenReturnStatus500() throws Exception {

        mvc.perform(get("/bookings/{bookingId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    @Test
    public void getUserBookings_whenRequestISCorrectWithAllParams_thenReturnListOfBookings() throws Exception {
        long userId = 1L;
        String from = "0";
        String size = "5";
        String state = "ALL";
        BookingDto first = BookingDto.builder().id(1L).build();
        BookingDto second = BookingDto.builder().id(2L).build();
        BookingDto third = BookingDto.builder().id(3L).build();
        List<BookingDto> result = List.of(first, second, third);
        when(service.getAllBookingsForUser(anyLong(), any(StateStatus.class), anyInt(), anyInt())).thenReturn(result);

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", state)
                        .param("from", from)
                        .param("size", size)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(result)));
    }

    @Test
    public void getUserBookings_whenMissHeader_thenReturnStatus500() throws Exception {

        mvc.perform(get("/bookings", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    @Test
    public void getUserBookings_whenParamsAreWrong_thenReturnStatus400() throws Exception {

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "-1")
                        .param("size", "-3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getUserBookings_whenRequestWithDefaultParam_thenReturnListOfBookings() throws Exception {
        long userId = 1L;
        BookingDto first = BookingDto.builder().id(1L).build();
        BookingDto second = BookingDto.builder().id(2L).build();
        BookingDto third = BookingDto.builder().id(3L).build();
        List<BookingDto> result = List.of(first, second, third);
        when(service.getAllBookingsForUser(anyLong(), any(StateStatus.class), anyInt(), anyInt())).thenReturn(result);

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(result)));
    }

    @Test
    public void getOwnerBookings_whenRequestISCorrectWithAllParams_thenReturnListOfBookings() throws Exception {
        long userId = 1L;
        String from = "0";
        String size = "5";
        String state = "ALL";
        BookingDto first = BookingDto.builder().id(1L).build();
        BookingDto second = BookingDto.builder().id(2L).build();
        BookingDto third = BookingDto.builder().id(3L).build();
        List<BookingDto> result = List.of(first, second, third);
        when(service.getAllBookingsForOwner(anyLong(), any(StateStatus.class), anyInt(), anyInt())).thenReturn(result);

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", state)
                        .param("from", from)
                        .param("size", size)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(result)));
    }

    @Test
    public void getOwnerBookings_whenMissHeader_thenReturnStatus500() throws Exception {

        mvc.perform(get("/bookings/owner", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    @Test
    public void getOwnerBookings_whenParamsAreWrong_thenReturnStatus400() throws Exception {

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "-1")
                        .param("size", "-3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getOwnerBookings_whenRequestWithDefaultParam_thenReturnListOfBookings() throws Exception {
        long userId = 1L;
        BookingDto first = BookingDto.builder().id(1L).build();
        BookingDto second = BookingDto.builder().id(2L).build();
        BookingDto third = BookingDto.builder().id(3L).build();
        List<BookingDto> result = List.of(first, second, third);
        when(service.getAllBookingsForOwner(anyLong(), any(StateStatus.class), anyInt(), anyInt())).thenReturn(result);

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(result)));
    }


}