package com.zarszz.userservice.service;

import com.zarszz.userservice.domain.Order;
import com.zarszz.userservice.domain.OrderItem;
import com.zarszz.userservice.domain.enumData.OrderStatus;
import com.zarszz.userservice.repository.OrderItemRepository;
import com.zarszz.userservice.repository.OrderRepository;
import com.zarszz.userservice.requests.v1.order.OrderDto;
import com.zarszz.userservice.requests.v1.order.OrderItemDto;
import com.zarszz.userservice.security.entity.AuthenticatedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    ProductServiceImpl productService;

    @Autowired
    UserAddressImpl userAddressService;

    @Autowired
    AuthenticatedUser authenticatedUser;

    @Override
    @Transactional(
            propagation = Propagation.REQUIRED,
            isolation = Isolation.SERIALIZABLE
    )
    public Order create(OrderDto createOrderDto) throws NoSuchElementException {
        var orderObj = new Order();
        orderObj.setUser(authenticatedUser.getUser());
        orderObj.setComments(createOrderDto.getComments());
        var createdOrder = orderRepository.save(orderObj);

        var orderItems = new ArrayList<OrderItem>();
        long amount = 0L;
        int qty = 0;


        for (OrderItemDto item : createOrderDto.getProductsItems()) {
            var product = productService.get((long) item.getProductId());
            qty = qty + item.getQty();
            amount = amount + (product.getPrice() * item.getQty());

            var orderItem = new OrderItem();
            orderItem.setOrder(createdOrder);
            orderItem.setProduct(product);
            orderItem.setQty(item.getQty());
            var createdOrderItem = orderItemRepository.save(orderItem);

            product.setStock(product.getStock() - item.getQty());
            productService.save(product);

            orderItems.add(createdOrderItem);
        }

        createdOrder.setSubTotal(amount);
        createdOrder.setQty(qty);

        createdOrder.setOrderItems(orderItems);

        var userAddress = userAddressService.getById(createOrderDto.getOrderId());
        var isAdmin = authenticatedUser.getUser().getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_SUPER_ADMIN"));
        if ((!isAdmin) || userAddress.getUserId() != authenticatedUser.getUserId()){
            throw new NoSuchElementException("User Address not found");
        }
        createdOrder.setUserAddress(userAddress);
        createdOrder.setStatus(OrderStatus.PENDING);

        return orderRepository.save(createdOrder);
    }

    @Override
    public List<Order> get() {
        var isAdmin = authenticatedUser.getUser().getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_SUPER_ADMIN"));
        if (isAdmin)
            return orderRepository.findAll();
        return orderRepository.findByUserId(authenticatedUser.getUserId());
    }

    @Override
    public Order getById(Long id) throws NoSuchElementException {
        return orderRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Order not found"));
    }

    @Override
    @Transactional(
            propagation = Propagation.REQUIRED,
            isolation = Isolation.SERIALIZABLE
    )
    public void update(OrderDto updateOrderDto, Long id) throws NoSuchElementException {
        var orderObj = orderRepository.findById(id).orElseThrow(() -> new NoSuchElementException(("Order not found")));
        orderObj.setUser(authenticatedUser.getUser());
        orderObj.setComments(updateOrderDto.getComments());

        var orderItems = new ArrayList<OrderItem>();
        var amount = 0L;
        int qty = 0;

        orderItemRepository.deleteByOrderId(orderObj.getId());

        var orders = orderItemRepository.findByOrderId(id);

        for (var orderItem: orders) {
            var product = orderItem.getProduct();
            product.setStock(product.getStock() + orderItem.getQty());
            productService.save(product);
        }

        for (var item : updateOrderDto.getProductsItems()) {
            var product = productService.get((long) item.getProductId());
            qty = qty + item.getQty();
            amount = amount + (product.getPrice() * item.getQty());

            var orderItem = new OrderItem();
            orderItem.setOrder(orderObj);
            orderItem.setProduct(product);
            orderItem.setQty(item.getQty());
            var createdOrderItem = orderItemRepository.save(orderItem);

            orderItems.add(createdOrderItem);
        }

        orderObj.setSubTotal(amount);
        orderObj.setQty(qty);

        var userAddress = userAddressService.getById(updateOrderDto.getOrderId());
        var isAdmin = authenticatedUser.getUser().getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_SUPER_ADMIN"));
        if ((!isAdmin) || userAddress.getUserId() != authenticatedUser.getUserId())
            throw new NoSuchElementException("User Address not found");

        orderObj.setOrderItems(orderItems);

        orderRepository.save(orderObj);
    }

    @Override
    @Transactional(
            propagation = Propagation.REQUIRED,
            isolation = Isolation.SERIALIZABLE
    )
    public void delete(Long id) throws NoSuchElementException {
        var isExist = orderRepository.existsById(id);
        if (!isExist) throw new NoSuchElementException("Order not found");
        orderItemRepository.deleteByOrderId(id);
        orderRepository.deleteById(id);
    }
}
