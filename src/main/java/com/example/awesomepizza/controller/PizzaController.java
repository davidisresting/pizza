package com.example.awesomepizza.controller;

import com.example.awesomepizza.domain.Pizza;
import com.example.awesomepizza.dto.PizzaDto;
import com.example.awesomepizza.service.PizzaService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/pizzas")
public class PizzaController {
    private final PizzaService pizzaService;
    private final ModelMapper mapper;

    private PizzaDto convertToDto(Pizza pizza) {
        return mapper.map(pizza, PizzaDto.class);
    }

    private Pizza convertToEntity(@Valid PizzaDto pizzaDto) {
        return mapper.map(pizzaDto, Pizza.class);
    }

    @GetMapping
    public ResponseEntity<List<PizzaDto>> getPizzas() {
        List<Pizza> pizzas = pizzaService.getPizzas();
        List<PizzaDto> pizzaDtos = pizzas.stream().map(this::convertToDto).collect(Collectors.toList());
        return new ResponseEntity<>(pizzaDtos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PizzaDto> getPizza(@PathVariable("id") long id) {
        PizzaDto pizzaDto = convertToDto(pizzaService.getPizza(id));
        return new ResponseEntity<>(pizzaDto, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<PizzaDto> createPizza(@RequestBody @Valid PizzaDto pizzaDto) {
        Pizza pizza = pizzaService.createPizza(convertToEntity(pizzaDto));
        return new ResponseEntity<>(convertToDto(pizza), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity updatePizza(
            @PathVariable("id") Long id,
            @Valid @RequestBody PizzaDto pizzaDto
    ) {
        if (!id.equals(pizzaDto.getId())) throw new
                ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "id does not match."
        );

        pizzaService.updatePizza(id, convertToEntity(pizzaDto));
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deletePizza(@PathVariable("id") Long id) {
        pizzaService.deletePizza(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
