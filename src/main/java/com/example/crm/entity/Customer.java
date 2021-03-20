package com.example.crm.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Customer {
    private UUID id;
    private String name;
    private final Set<Order> orders = new HashSet<>();
}
