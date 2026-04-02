package com.example.application.Scheduler;

import com.example.application.Model.Role;
import com.example.application.Model.User;
import com.example.application.Model.UserDetail;
import com.example.application.Repository.UserDetailRepository;
import com.example.application.Repository.UserRepository;
import com.example.application.Service.UserDetailService;
import com.example.application.Service.OrderService;
import com.example.application.Repository.OrderRepository;
import com.example.application.Model.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DeliveryScheduler {

    private final UserDetailRepository userDetailRepository;
    private final UserRepository userRepository;
    private final UserDetailService userDetailService;
    private final OrderRepository orderRepository;
    private final OrderService orderService;

    public DeliveryScheduler(UserDetailRepository userDetailRepository,
                             UserRepository userRepository,
                             UserDetailService userDetailService,
                             OrderRepository orderRepository,
                             OrderService orderService) {
        this.userDetailRepository = userDetailRepository;
        this.userRepository = userRepository;
        this.userDetailService = userDetailService;
        this.orderRepository = orderRepository;
        this.orderService = orderService;
    }

    /**
     * Runs every 30 seconds to check for pending pickups and orders, 
     * assigning them to available delivery boys.
     */
    @Scheduled(fixedDelay = 5000)
    public void assignPendingTasks() {
        System.out.println("--- Delivery Scheduler Running ---");
        List<User> deliveryBoys = userRepository.findByRole(Role.DELIVERY_BOY);
        System.out.println("Found " + deliveryBoys.size() + " delivery boys in the system.");
        
        if (deliveryBoys.isEmpty()) {
            return;
        }

        assignPickups(deliveryBoys);
        assignOrders(deliveryBoys);
        System.out.println("--- Delivery Scheduler Cycle Complete ---");
    }

    private boolean isBoyAvailable(User boy) {
        // A boy is available if they have NO assigned pickups and NO assigned orders currently in progress
        List<UserDetail> assignedPickups = userDetailRepository.findByAssignedDeliveryBoyId(boy.getId());
        boolean hasActivePickup = assignedPickups.stream()
                .anyMatch(d -> "ASSIGNED".equals(d.getStatus()));

        List<Order> assignedOrders = orderRepository.findByAssignedDeliveryBoyId(boy.getId());
        boolean hasActiveOrder = assignedOrders.stream()
                .anyMatch(o -> "ASSIGNED".equals(o.getDeliveryStatus()));

        return !hasActivePickup && !hasActiveOrder;
    }

    private void assignPickups(List<User> deliveryBoys) {
        List<UserDetail> pendingPickups = userDetailRepository.findByStatusOrStatusIsNull("PENDING");
        System.out.println("Found " + pendingPickups.size() + " pending material pickups (including NULL status).");

        if (pendingPickups.isEmpty()) {
            return;
        }

        for (UserDetail pickup : pendingPickups) {
            User availableBoy = null;
            for (User boy : deliveryBoys) {
                if (isBoyAvailable(boy)) {
                    availableBoy = boy;
                    break;
                }
            }

            if (availableBoy != null) {
                try {
                    userDetailService.assignDeliveryBoy(pickup.getId(), availableBoy.getId());
                    System.out.println("Auto-assigned pickup ID " + pickup.getId() + " to Delivery Boy ID " + availableBoy.getId() + " (" + availableBoy.getEmail() + ")");
                } catch (Exception e) {
                    System.err.println("Failed to auto-assign pickup: " + e.getMessage());
                }
            } else {
                System.out.println("No available delivery boys for pickup ID " + pickup.getId());
                break; 
            }
        }
    }

    private void assignOrders(List<User> deliveryBoys) {
        List<Order> pendingOrders = orderRepository.findByDeliveryStatusOrDeliveryStatusIsNull("PENDING");
        System.out.println("Found " + pendingOrders.size() + " pending product orders (including NULL status).");

        if (pendingOrders.isEmpty()) {
            return;
        }

        for (Order order : pendingOrders) {
            User availableBoy = null;
            for (User boy : deliveryBoys) {
                if (isBoyAvailable(boy)) {
                    availableBoy = boy;
                    break;
                }
            }

            if (availableBoy != null) {
                try {
                    orderService.assignDeliveryBoy(order.getId(), availableBoy.getId());
                    System.out.println("Auto-assigned order ID " + order.getId() + " to Delivery Boy ID " + availableBoy.getId() + " (" + availableBoy.getEmail() + ")");
                } catch (Exception e) {
                    System.err.println("Failed to auto-assign order: " + e.getMessage());
                }
            } else {
                System.out.println("No available delivery boys for order ID " + order.getId());
                break;
            }
        }
    }
}
