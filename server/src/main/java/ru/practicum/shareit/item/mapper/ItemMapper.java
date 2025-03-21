package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.comments.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;

import java.util.List;

@UtilityClass
public class ItemMapper {

    public static ItemDto toDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setRequestId(item.getRequest() != null ? item.getRequest().getId() : null);
        return itemDto;
    }

    public static ItemWithBookingsDto toItemWithBookingsDto(Item item, BookingDto lastBooking, BookingDto nextBooking) {
        return toItemWithBookingsDto(item, lastBooking, nextBooking, null);
    }

    public static ItemWithBookingsDto toItemWithBookingsDto(Item item, BookingDto lastBooking, BookingDto nextBooking, List<CommentDto> comments) {
        ItemWithBookingsDto itemWithBookingsDto = new ItemWithBookingsDto();
        itemWithBookingsDto.setId(item.getId());
        itemWithBookingsDto.setName(item.getName());
        itemWithBookingsDto.setDescription(item.getDescription());
        itemWithBookingsDto.setAvailable(item.getAvailable());

        if (item.getRequest() != null) {
            itemWithBookingsDto.setRequest(ItemRequestMapper.toDto(item.getRequest()));
        } else {
            itemWithBookingsDto.setRequest(null);
        }

        itemWithBookingsDto.setLastBooking(lastBooking);
        itemWithBookingsDto.setNextBooking(nextBooking);
        itemWithBookingsDto.setComments(comments); // Добавляем комментарии

        return itemWithBookingsDto;
    }

    public static Item toEntity(ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        return item;
    }
}