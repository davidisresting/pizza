package com.example.awesomepizza.controller;

import com.example.awesomepizza.dto.OrderDto;
import com.example.awesomepizza.domain.Order;
import com.example.awesomepizza.dto.OrderPendingDto;
import com.example.awesomepizza.service.OrderService;
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
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;
    private final ModelMapper mapper;

    private OrderDto convertToDto(Order order) {
        return mapper.map(order, OrderDto.class);
    }

    private Order convertToEntity(@Valid OrderDto orderDto) {
        return mapper.map(orderDto, Order.class);
    }

    @GetMapping
    public ResponseEntity<List<OrderDto>> getOrders() {
        List<Order> orders = orderService.getOrders();
        List<OrderDto> orderDtos = orders.stream().map(this::convertToDto).collect(Collectors.toList());
        return new ResponseEntity<>(orderDtos, HttpStatus.OK);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<OrderPendingDto>> getOrdersPending() {
        List<Order> orders = orderService.getOrdersPending();
        List<OrderPendingDto> orderPendingDtos = orders.stream().map((order) -> mapper.map(order, OrderPendingDto.class)).collect(Collectors.toList());
        return new ResponseEntity<>(orderPendingDtos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable("id") long id) {
        OrderDto orderDto = convertToDto(orderService.getOrder(id));
        return new ResponseEntity<>(orderDto, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@RequestBody @Valid OrderDto orderDto) {
        Order order = orderService.createOrder(convertToEntity(orderDto));
        return new ResponseEntity<>(convertToDto(order), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateOrder(
            @PathVariable("id") Long id,
            @Valid @RequestBody OrderDto orderDto
    ) {
        if (!id.equals(orderDto.getId())) throw new
                ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                "id does not match."
        );

        orderService.updateOrder(id, convertToEntity(orderDto));
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteOrder(@PathVariable("id") Long id) {
        orderService.deleteOrder(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
