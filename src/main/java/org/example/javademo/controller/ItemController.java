package org.example.javademo.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.example.javademo.dto.ItemDto;
import org.example.javademo.service.ItemService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/items")
@Validated
public class ItemController {

    private final ItemService service;
    public ItemController(ItemService service) { this.service = service; }

    // CREATE
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ItemDto> create(@Valid @RequestBody ItemDto req, Authentication auth) {
        String email = (String) auth.getPrincipal();
        ItemDto created = service.create(email, req);
        return ResponseEntity.created(URI.create("/api/items/" + created.id)).body(created);
    }

    // LIST（支援 category / brand / 分頁 / 排序）
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<ItemDto> list(
            Authentication auth,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        String email = (String) auth.getPrincipal();
        return service.list(email, category, brand, pageable);
    }

    // READ
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ItemDto get(@PathVariable("id") @Min(1) long id, Authentication auth) {
        String email = (String) auth.getPrincipal();
        return service.get(email, id);
    }

    // UPDATE
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ItemDto update(@PathVariable("id") @Min(1) long id,
                          @RequestBody ItemDto req,
                          Authentication auth) {
        String email = (String) auth.getPrincipal();
        return service.update(email, id, req);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable("id") @Min(1) long id, Authentication auth) {
        String email = (String) auth.getPrincipal();
        return service.delete(email, id);
    }
}
