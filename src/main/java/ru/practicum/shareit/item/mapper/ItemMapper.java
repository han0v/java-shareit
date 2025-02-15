package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;

public class ItemMapper {
    public static Item toEntity(ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());

        // Обрабатываем поле request
        if (itemDto.getRequest() != null) {
            item.setRequest(ItemRequestMapper.toEntity(itemDto.getRequest()));
        } else {
            item.setRequest(null); // Явно устанавливаем null, если requestDto равен null
        }

        return item;
    }

    public static ItemDto toDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());

        // Обрабатываем поле request
        if (item.getRequest() != null) {
            itemDto.setRequest(ItemRequestMapper.toDto(item.getRequest()));
        } else {
            itemDto.setRequest(null); // Явно устанавливаем null, если request равен null
        }

        return itemDto;
    }
}