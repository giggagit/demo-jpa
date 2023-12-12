package com.giggagit.demo.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.giggagit.demo.model.order.Address;
import com.giggagit.demo.model.order.Coupon;
import com.giggagit.demo.model.order.Customer;
import com.giggagit.demo.model.order.Order;
import com.giggagit.demo.model.order.OrderItem;
import com.giggagit.demo.model.order.PaymentMethod;
import com.giggagit.demo.repository.OrderRepository;
import com.github.javafaker.CreditCardType;
import com.github.javafaker.Faker;

@RestController
@RequestMapping("/order")
public class OrderController {

	private final OrderRepository orderRepository;

	public OrderController(OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}

	@GetMapping("/mock-up")
	public ResponseEntity<Object> mockupData() {
		try {
			List<Order> orders = generateData();
			orderRepository.saveAll(orders);

			return ResponseEntity.ok(orders.size());
		} catch (Exception e) {
			throw e;
		}
	}

	@GetMapping("")
	public ResponseEntity<Object> searchOrder(@RequestParam int page, @RequestParam int size) {
		try {
			Pageable pageable = PageRequest.of(page - 1, size);
			Page<Order> orders = orderRepository.findAll(pageable);
			return ResponseEntity.ok(orders);
		} catch (Exception e) {
			throw e;
		}
	}

	private List<Order> generateData() {
		Faker faker = new Faker();
		Random r = new Random();
		int oRan = r.nextInt(10) + 1;
		List<Order> orders = new ArrayList<>();

		for (int i = 0; i < oRan; i++) {
			Order order = new Order();
			order.setCustomerName(faker.name().fullName());
			order.setOrderDate(LocalDate.now());
			orderRepository.save(order);
			// Generate Customer (optional)
			if (faker.random().nextBoolean()) {
				Customer customer = new Customer();
				customer.setFirstName(faker.name().firstName());
				customer.setLastName(faker.name().lastName());
				customer.setEmail(faker.internet().emailAddress());
				order.setCustomer(customer);
			}

			// Generate Order Items
			int itemCount = faker.random().nextInt(1, 10);
			List<OrderItem> orderItems = generateOrderItems(itemCount);
			orderItems.forEach(item -> order.addOrderItem(item));

			// Generate Shipping Address (optional)
			if (faker.random().nextBoolean()) {
				Address address = new Address();
				address.setStreet(faker.address().streetName());
				address.setCity(faker.address().city());
				address.setState(faker.address().stateAbbr());
				address.setPostalCode(faker.address().zipCode());
				order.setShippingAddress(address);
			}

			// Generate Payment Method (optional)
			if (faker.random().nextBoolean()) {
				PaymentMethod paymentMethod = new PaymentMethod();
				int creditType = r.nextInt(CreditCardType.values().length);
				paymentMethod.setType(CreditCardType.values()[creditType].name());
				paymentMethod.setDetails(faker.finance().creditCard());
				order.setPaymentMethod(paymentMethod);
			}

			// Generate Coupon (optional)
			if (faker.random().nextBoolean()) {
				Coupon coupon = new Coupon();
				coupon.setDiscount(faker.random().nextDouble());
				coupon.setValidUntil(LocalDate.now().plusDays(faker.random().nextInt(7, 30)));
				order.setCoupon(coupon);
			}
			orders.add(order);
		}
		return orders;
	}

	private List<OrderItem> generateOrderItems(int count) {
		Faker faker = new Faker();
		List<OrderItem> items = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			OrderItem item = new OrderItem();
			item.setName(faker.book().title());
			item.setQuantity(faker.random().nextInt(1, 5));
			item.setPrice(new Double(faker.commerce().price()));
			items.add(item);
		}
		return items;
	}
}
