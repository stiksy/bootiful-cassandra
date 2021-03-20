package com.example.crm.data;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.util.UUID;

@PrimaryKeyClass
public class CustomerOrdersPrimaryKey {

    @PrimaryKeyColumn(name = "customer_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private UUID customerId;

    @PrimaryKeyColumn(name = "order_id", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private UUID orderId;
}
