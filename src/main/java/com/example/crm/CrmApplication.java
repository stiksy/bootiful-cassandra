package com.example.crm;

import com.datastax.oss.driver.api.core.DriverException;
import com.datastax.oss.driver.api.core.cql.Row;
import com.example.crm.data.CustomerRepository;
import com.example.crm.entity.CustomerOrders;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.data.cassandra.ReactiveSessionFactory;
import org.springframework.data.cassandra.core.cql.ReactiveCqlTemplate;
import org.springframework.data.cassandra.core.cql.RowMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.Flow;

@SpringBootApplication
public class CrmApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrmApplication.class, args);
    }

    private final String fernando = "Fernando", dhayane = "Dhayane", alice = "Alice", sofia = "Sofia";

    @Bean
    ReactiveCqlTemplate reactiveCqlTemplate (ReactiveSessionFactory sessionFactory) {
        return new ReactiveCqlTemplate(sessionFactory);
    }
    @Bean
    ApplicationListener<ApplicationReadyEvent> ready(ReactiveCqlTemplate template,
                                                     CustomerRepository customerRepository) {
        return event -> {
            Mono<Void> delete = customerRepository.deleteAll();
            Flux<CustomerOrders> writes = Flux
                    .just(fernando, dhayane, alice, sofia)
                    .flatMap(name -> addOrdersFor(customerRepository, name));
            Flux<CustomerOrders> all = customerRepository.findAll();

            Flux<CustomerOrders> byId = writes
                    .take(1)
                    .map(CustomerOrders::getCustomerId)
                    .flatMap(customerRepository::findByCustomerId);

            Flux<CustomerOrders> cql = template.query(
                    "select * from orders_by_customer",
                    (row, i) -> new CustomerOrders(
                            row.getUuid("customer_id"),
                            row.getUuid("order_id"),
                            row.getString("customer_name")));

            delete
                    .thenMany(writes)
                    .thenMany(all.doOnNext(co -> System.out.println("all: " + co.toString())))
                    .thenMany(byId.doOnNext(co -> System.out.println("byId: " + co.toString())))
                    .thenMany(cql.doOnNext(co -> System.out.println("CQL: " + co.toString())))
                    .subscribe();

            System.out.println("Hello, Astra");
        };
    }

    private Flux<CustomerOrders> addOrdersFor(CustomerRepository customerRepository, String name) {
        var customerId = UUID.randomUUID();
        var list = new ArrayList<CustomerOrders>();
        for (var i = 0; i < (Math.random() * 100); i++) {
            list.add(new CustomerOrders(customerId, UUID.randomUUID(), name));
        }

        return customerRepository.saveAll(list);
    }
}
