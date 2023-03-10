package com.example.awesomepizza.dto;

import com.example.awesomepizza.enums.OrderStatusEnum;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class OrderDto {
    private long id;

    private int orderNumber;

    private OrderStatusEnum orderStatus;

    private CustomerDto customer;

    private List<PizzaDto> pizzas;

    // private double totalPrice;
}
