package org.example.javademo.controller;

import org.example.javademo.dto.ItemDto;
import org.example.javademo.service.ItemService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping("/api/items")
@Validated
public class ItemController {

    private final ItemService service;

    public ItemController(ItemService service) { this.service = service; }

    @PostMapping
    public ItemDto create(@Valid @RequestBody ItemDto req) {
        return service.create(req);
    }

    @GetMapping
    public Collection<ItemDto> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public ItemDto get(@PathVariable("id") @Min(1) long id) {
        return service.get(id);
    }

    @PutMapping("/{id}")
    public ItemDto update(@PathVariable("id") @Min(1) long id, @RequestBody ItemDto req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    public Map<String,Object> delete(@PathVariable("id") @Min(1) long id) {
        return service.delete(id);
    }
}
