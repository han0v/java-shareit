package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.comments.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemMapperTest {

    @Test
    void toDto_shouldMapItemToItemDto() {
        // Подготовка
        Item item = new Item();
        item.setId(1L);
        item.setName("Дрель");
        item.setDescription("Простая дрель");
        item.setAvailable(true);

        ItemRequest request = new ItemRequest();
        request.setId(2L);
        item.setRequest(request);

        // Действие
        ItemDto itemDto = ItemMapper.toDto(item);

        // Проверка
        assertNotNull(itemDto);
        assertEquals(1L, itemDto.getId());
        assertEquals("Дрель", itemDto.getName());
        assertEquals("Простая дрель", itemDto.getDescription());
        assertTrue(itemDto.getAvailable());
        assertEquals(2L, itemDto.getRequestId());
    }

    @Test
    void toDto_shouldMapItemToItemDtoWithNullRequest() {
        // Подготовка
        Item item = new Item();
        item.setId(1L);
        item.setName("Дрель");
        item.setDescription("Простая дрель");
        item.setAvailable(true);
        item.setRequest(null); // request = null

        // Действие
        ItemDto itemDto = ItemMapper.toDto(item);

        // Проверка
        assertNotNull(itemDto);
        assertNull(itemDto.getRequestId()); // requestId должен быть null
    }

    @Test
    void toItemWithBookingsDto_shouldMapItemToItemWithBookingsDto() {
        // Подготовка
        Item item = new Item();
        item.setId(1L);
        item.setName("Дрель");
        item.setDescription("Простая дрель");
        item.setAvailable(true);

        BookingDto lastBooking = new BookingDto();
        lastBooking.setId(1L);

        BookingDto nextBooking = new BookingDto();
        nextBooking.setId(2L);

        List<CommentDto> comments = List.of(new CommentDto());

        // Действие
        ItemWithBookingsDto itemWithBookingsDto = ItemMapper.toItemWithBookingsDto(item, lastBooking, nextBooking, comments);

        // Проверка
        assertNotNull(itemWithBookingsDto);
        assertEquals(1L, itemWithBookingsDto.getId());
        assertEquals("Дрель", itemWithBookingsDto.getName());
        assertEquals("Простая дрель", itemWithBookingsDto.getDescription());
        assertTrue(itemWithBookingsDto.getAvailable());
        assertEquals(lastBooking, itemWithBookingsDto.getLastBooking());
        assertEquals(nextBooking, itemWithBookingsDto.getNextBooking());
        assertEquals(comments, itemWithBookingsDto.getComments());
        assertNull(itemWithBookingsDto.getRequest()); // request не установлен
    }

    @Test
    void toItemWithBookingsDto_shouldMapItemToItemWithBookingsDtoWithRequest() {
        // Подготовка
        Item item = new Item();
        item.setId(1L);
        item.setName("Дрель");
        item.setDescription("Простая дрель");
        item.setAvailable(true);

        ItemRequest request = new ItemRequest();
        request.setId(2L);
        item.setRequest(request);

        BookingDto lastBooking = new BookingDto();
        lastBooking.setId(1L);

        BookingDto nextBooking = new BookingDto();
        nextBooking.setId(2L);

        List<CommentDto> comments = List.of(new CommentDto());

        // Действие
        ItemWithBookingsDto itemWithBookingsDto = ItemMapper.toItemWithBookingsDto(item, lastBooking, nextBooking, comments);

        // Проверка
        assertNotNull(itemWithBookingsDto);
        assertEquals(2L, itemWithBookingsDto.getRequest().getId()); // request установлен
    }

    @Test
    void toItemWithBookingsDto_shouldMapItemToItemWithBookingsDtoWithoutComments() {
        // Подготовка
        Item item = new Item();
        item.setId(1L);
        item.setName("Дрель");
        item.setDescription("Простая дрель");
        item.setAvailable(true);

        BookingDto lastBooking = new BookingDto();
        lastBooking.setId(1L);

        BookingDto nextBooking = new BookingDto();
        nextBooking.setId(2L);

        // Действие
        ItemWithBookingsDto itemWithBookingsDto = ItemMapper.toItemWithBookingsDto(item, lastBooking, nextBooking, null);

        // Проверка
        assertNotNull(itemWithBookingsDto);
        assertNull(itemWithBookingsDto.getComments()); // comments = null
    }

    @Test
    void toEntity_shouldMapItemDtoToItem() {
        // Подготовка
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Дрель");
        itemDto.setDescription("Простая дрель");
        itemDto.setAvailable(true);

        // Действие
        Item item = ItemMapper.toEntity(itemDto);

        // Проверка
        assertNotNull(item);
        assertEquals(1L, item.getId());
        assertEquals("Дрель", item.getName());
        assertEquals("Простая дрель", item.getDescription());
        assertTrue(item.getAvailable());
    }
}