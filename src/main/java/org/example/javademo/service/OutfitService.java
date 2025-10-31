package org.example.javademo.service;

import org.example.javademo.dto.OutfitDto;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class OutfitService {
    private final AtomicLong seq = new AtomicLong(1);
    private final Map<Long, OutfitDto> store = new ConcurrentHashMap<>();

    public OutfitDto create(OutfitDto req) {
        OutfitDto o = new OutfitDto();
        long id = seq.getAndIncrement();
        o.id = id;
        o.topId = req.topId;
        o.bottomId = req.bottomId;
        o.shoesId = req.shoesId;
        o.accessoryIds = req.accessoryIds;
        o.notes = req.notes;
        o.createdAt = System.currentTimeMillis();
        store.put(id, o);
        return o;
    }

    public Collection<OutfitDto> list() { return store.values(); }

    public OutfitDto get(long id) {
        OutfitDto o = store.get(id);
        if (o == null) throw new NoSuchElementException("Outfit not found: " + id);
        return o;
    }
}
