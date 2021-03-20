package com.example.crm.data;

import com.example.crm.entity.CustomerOrders;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface CustomerRepository extends ReactiveCassandraRepository<CustomerOrders, CustomerOrdersPrimaryKey> {
    Flux<CustomerOrders> findByCustomerId(UUID customerId);
}
