package org.example.javademo.service;

import org.example.javademo.dto.ItemDto;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ItemService {
    private final AtomicLong seq = new AtomicLong(1);
    private final Map<Long, ItemDto> store = new ConcurrentHashMap<>();

    public ItemDto create(ItemDto req) {
        long id = seq.getAndIncrement();
        ItemDto item = new ItemDto();
        item.id = id;
        item.name = req.name;
        item.category = req.category;
        item.color = req.color;
        item.size = req.size;
        item.brand = req.brand;
        item.createdAt = System.currentTimeMillis();
        store.put(id, item);
        return item;
    }

    public Collection<ItemDto> list() { return store.values(); }

    public ItemDto get(long id) {
        ItemDto it = store.get(id);
        if (it == null) throw new NoSuchElementException("Item not found: " + id);
        return it;
    }

    public ItemDto update(long id, ItemDto req) {
        ItemDto cur = get(id);
        if (req.name != null) cur.name = req.name;
        if (req.category != null) cur.category = req.category;
        if (req.color != null) cur.color = req.color;
        if (req.size != null) cur.size = req.size;
        if (req.brand != null) cur.brand = req.brand;
        return cur;
    }

    public Map<String,Object> delete(long id) {
        ItemDto removed = store.remove(id);
        return Map.of("deleted", removed != null, "id", id);
    }
}
