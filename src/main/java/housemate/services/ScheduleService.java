package housemate.services;

import housemate.constants.Cycle;
import housemate.entities.Schedule;
import housemate.entities.Service;
import housemate.entities.ServiceType;
import housemate.entities.UserUsage;
import housemate.mappers.ScheduleMapper;
import housemate.models.DeliveryScheduleDTO;
import housemate.models.HourlyScheduleDTO;
import housemate.models.ReturnScheduleDTO;
import housemate.repositories.ScheduleRepository;
import housemate.repositories.ServiceRepository;
import housemate.repositories.ServiceTypeRepository;
import housemate.repositories.UserUsageRepository;
import housemate.responses.EventRes;
import housemate.responses.PurchasedServiceRes;
import housemate.utils.AuthorizationUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author hdang09
 */
@org.springframework.stereotype.Service
public class ScheduleService {

    private final int FIND_STAFF_HOURS = 3;

    private final ServiceRepository serviceRepository;
    private final ScheduleRepository scheduleRepository;
    private final ScheduleMapper scheduleMapper;
    private final AuthorizationUtil authorizationUtil;
    private final ServiceTypeRepository serviceTypeRepository;
    private final UserUsageRepository userUsageRepository;

    @Autowired
    public ScheduleService(
            ServiceRepository serviceRepository,
            ScheduleRepository scheduleRepository,
            ScheduleMapper scheduleMapper,
            AuthorizationUtil authorizationUtil,
            ServiceTypeRepository serviceTypeRepository,
            UserUsageRepository userUsageRepository
    ) {
        this.serviceRepository = serviceRepository;
        this.scheduleRepository = scheduleRepository;
        this.scheduleMapper = scheduleMapper;
        this.authorizationUtil = authorizationUtil;
        this.serviceTypeRepository = serviceTypeRepository;
        this.userUsageRepository = userUsageRepository;
    }

    public ResponseEntity<List<EventRes>> getScheduleForUser(HttpServletRequest request) {
        List<EventRes> events = new ArrayList<>();
        int userId = authorizationUtil.getUserIdFromAuthorizationHeader(request);

        for (Schedule schedule : scheduleRepository.getByCustomerId(userId)) {
            // TODO: Check expire date?
            Service service = serviceRepository.getServiceByServiceId(schedule.getServiceId());
            EventRes event = scheduleMapper.mapToEventRes(schedule, service);
            events.add(event);
        }
        return ResponseEntity.status(HttpStatus.OK).body(events);
    }

    public ResponseEntity<Set<PurchasedServiceRes>> getAllPurchased(HttpServletRequest request) {
        Set<PurchasedServiceRes> purchases = new HashSet<>();
        int userId = authorizationUtil.getUserIdFromAuthorizationHeader(request);
        List<UserUsage> usageList = userUsageRepository.getByUserId(userId);

        // Get all serviceID based on order ID
        for (UserUsage userUsage : usageList) {
            // Check expiration and run out of remaining
            if (userUsage.getRemaining() == 0 && userUsage.getEndDate().isAfter(LocalDateTime.now())) continue;

            Service service = serviceRepository.getServiceByServiceId(userUsage.getServiceId());
            List<ServiceType> typeList = serviceTypeRepository.findAllByServiceId(service.getServiceId()).orElse(null);

            PurchasedServiceRes purchase = new PurchasedServiceRes();
            purchase.setServiceId(service.getServiceId());
            purchase.setTitleName(service.getTitleName());
            purchase.setTypeList(typeList);
            purchase.setGroupType(service.getGroupType());

            purchases.add(purchase);
        }

        return ResponseEntity.status(HttpStatus.OK).body(purchases);
    }

    public ResponseEntity<String> createHourlySchedule(HttpServletRequest request, HourlyScheduleDTO scheduleDTO) {
        // TODO: Validate quantity in DTO is smaller than quantity in order

        // Validate service ID
        ResponseEntity<String> serviceIdValidation = validateServiceId(scheduleDTO.getServiceId(), request);
        if (serviceIdValidation != null) return serviceIdValidation;

        // Validate start date
        LocalDateTime startDate = scheduleDTO.getDate().atTime(scheduleDTO.getTimeRanges().get(0));
        ResponseEntity<String> startDateValidation = validateDate(LocalDateTime.now(), startDate, FIND_STAFF_HOURS, "start time");
        if (startDateValidation != null) return startDateValidation;

        // Validate end date
        LocalDateTime endDate = scheduleDTO.getDate().atTime(scheduleDTO.getTimeRanges().get(1));
        ResponseEntity<String> endDateValidation = validateDate(startDate, endDate, 1, "end time");
        if (endDateValidation != null) return endDateValidation;

        // Store to database
        int userId = authorizationUtil.getUserIdFromAuthorizationHeader(request);
        Schedule schedule = scheduleMapper.mapToEntity(scheduleDTO);
        schedule.setCustomerId(userId);
        storeToDatabase(schedule);

        // TODO: Send notification to staff

        return ResponseEntity.status(HttpStatus.OK).body("Set schedule successfully! Please wait for our staff apply this job!");
    }

    public ResponseEntity<String> createReturnSchedule(HttpServletRequest request, ReturnScheduleDTO scheduleDTO) {
        //  TODO: Validate quantity in DTO is smaller than quantity in order

        // Validate service ID
        ResponseEntity<String> serviceIdValidation = validateServiceId(scheduleDTO.getServiceId(), request);
        if (serviceIdValidation != null) return serviceIdValidation;

        // Validate pickupDate > current + 3hr
        LocalDateTime pickupDate = scheduleDTO.getPickupDate().atTime(scheduleDTO.getTime());
        ResponseEntity<String> pickupDateValidation = validateDate(LocalDateTime.now(), pickupDate, FIND_STAFF_HOURS, "pickup date");
        if (pickupDateValidation != null) return pickupDateValidation;

        // Validate receivedDate > pickupDate + 4
        LocalDateTime receivedDate = scheduleDTO.getReceivedDate().atTime(scheduleDTO.getReceivedTime());
        int MINIMUM_RETURN_HOURS = 4;
        ResponseEntity<String> receivedDateValidation = validateDate(pickupDate, receivedDate, MINIMUM_RETURN_HOURS, "received date");
        if (receivedDateValidation != null) return receivedDateValidation;

        // Store to database
        storeToDatabase(scheduleDTO, request);

        // TODO: Send notification to staff

        return ResponseEntity.status(HttpStatus.OK).body("Set schedule successfully! Please wait for our staff apply this job!");
    }

    public ResponseEntity<String> createDeliverySchedule(HttpServletRequest request, DeliveryScheduleDTO scheduleDTO) {
//        TODO: Validate quantity in DTO is smaller than quantity in order
//        int userId = authorizationUtil.getUserIdFromAuthorizationHeader(request);
//        UserUsage userUsage = userUsageRepository.getByServiceIdAndOrderIdAndUserId(scheduleDTO.getServiceId(), orderId, userId);
//        int remainingQuantity = userUsage.getRemaining();
//        int totalQuantity = userUsage.getTotal();
//
//        if (remainingQuantity + scheduleDTO.getQuantity() > totalQuantity) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
//                    "You are out of quantity. Please purchase with quantity lower or equal than " + (totalQuantity - remainingQuantity)
//            );
//        }

        // Validate service ID
        ResponseEntity<String> serviceIdValidation = validateServiceId(scheduleDTO.getServiceId(), request);
        if (serviceIdValidation != null) return serviceIdValidation;

        // Validate date > current + 3
        LocalDateTime date = scheduleDTO.getDate().atTime(scheduleDTO.getTime());
        ResponseEntity<String> receivedDateValidation = validateDate(LocalDateTime.now(), date, FIND_STAFF_HOURS, "date");
        if (receivedDateValidation != null) return receivedDateValidation;

        // Store to database
        int userId = authorizationUtil.getUserIdFromAuthorizationHeader(request);
        Schedule schedule = scheduleMapper.mapToEntity(scheduleDTO);
        schedule.setCustomerId(userId);
        storeToDatabase(schedule);

        // TODO: Send notification to staff

        return ResponseEntity.status(HttpStatus.OK).body("Set schedule successfully! Please wait for our staff apply this job!");
    }

    private ResponseEntity<String> validateServiceId(int serviceId, HttpServletRequest request) {
        // Validate service ID
        Service service = serviceRepository.getServiceByServiceId(serviceId);
        if (service == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Can't find this service ID");
        }

        // Validate serviceId is in order
        if (!isContainsServiceId(getAllPurchased(request).getBody(), serviceId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("You haven't buy this service");
        }

        // TODO: Check serviceID is correct type (Waiting feature/services ...)

        return null;
    }

    private boolean isContainsServiceId(Set<PurchasedServiceRes> purchases, int serviceId) {
        if (purchases == null) return false;

        for (PurchasedServiceRes purchase : purchases) {
            if (purchase.getServiceId() == serviceId) {
                return true; // ServiceId found in the array
            }
        }
        return false; // ServiceId not found in the array
    }

    private ResponseEntity<String> validateDate(LocalDateTime startDate, LocalDateTime endDate, int hours, String varName) {
        // Check endTime in office hours
        int endHour = endDate.getHour();
        if (endHour <= 6 || endHour >= 17) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please set your " + varName + " in range from 7:00 to 18:00");
        }

        // Check is valid working date
        LocalDateTime startWorkingDate = startDate.plusHours(hours);
        if (endDate.isAfter(startWorkingDate)) return null;

        // Check workingDate in office hours
        if (startWorkingDate.getHour() <= 6 || startWorkingDate.getHour() >= 17) {
            String formattedDate = startWorkingDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You must set your " + varName + " after 7:00:00 " + formattedDate);
        }

        // Validate endDate > startDate + n hours
        String formattedDate = startWorkingDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You must set your " + varName + " after " + formattedDate);
    }

    private void storeToDatabase(ReturnScheduleDTO returnScheduleDTO, HttpServletRequest request) {
        int customerId = authorizationUtil.getUserIdFromAuthorizationHeader(request);
        List<Schedule> schedules = scheduleMapper.mapToEntity(returnScheduleDTO);
        int serviceId = schedules.get(0).getServiceId();
        Cycle cycle = schedules.get(0).getCycle();

        // Get user usage
        UserUsage userUsage = userUsageRepository.getSoonerSchedule(serviceId, customerId);

        // Get max quantity
        int maxQuantity = getMaxQuantity(userUsage.getEndDate(), cycle, userUsage.getRemaining(), 1);

        // Minus quantity to user usage
        int remaining = userUsage.getRemaining() - maxQuantity;
        userUsage.setRemaining(Math.max(remaining, 0));
        userUsageRepository.save(userUsage);

        // Store to database (ONLY_ONE_TIME)
        if (cycle == Cycle.ONLY_ONE_TIME) {
            for (Schedule schedule : schedules) {
                schedule.setCustomerId(customerId);
                scheduleRepository.save(schedule);
            }
            return;
        }

        // Store to database (EVERY_WEEK)
        if (cycle == Cycle.EVERY_WEEK) {
            for (int i = 0; i < maxQuantity; i++) {
                for (Schedule schedule : schedules) {
                    // Create new instance for schedule
                    Schedule newSchedule = schedule.clone();

                    newSchedule.setCustomerId(customerId);
                    newSchedule.setStartDate(schedule.getStartDate().plusWeeks(i));
                    newSchedule.setEndDate(schedule.getEndDate().plusWeeks(i));

                    scheduleRepository.save(newSchedule);
                }
            }
            return;
        }

        // Store to database (EVERY_MONTH)
        for (int i = 0; i < maxQuantity; i++) {
            for (Schedule schedule : schedules) {
                // Create new instance for schedule
                Schedule newSchedule = schedule.clone();

                newSchedule.setCustomerId(customerId);
                newSchedule.setStartDate(schedule.getStartDate().plusMonths(i));
                newSchedule.setEndDate(schedule.getEndDate().plusMonths(i));

                scheduleRepository.save(newSchedule);
            }
        }
    }

    private void storeToDatabase(Schedule schedule) {
        int customerId = schedule.getCustomerId();

        // Usage of user
        UserUsage userUsage = userUsageRepository.getSoonerSchedule(schedule.getServiceId(), customerId);

        // Get max quantity
        int maxQuantity = getMaxQuantity(userUsage.getEndDate(), schedule.getCycle(), userUsage.getRemaining(), schedule.getQuantityRetrieve());

        // Minus quantity to user usage
        int remaining = userUsage.getRemaining() - maxQuantity * schedule.getQuantityRetrieve();
        userUsage.setRemaining(Math.max(remaining, 0));
        userUsageRepository.save(userUsage);

        // Store to database (ONLY_ONE_TIME)
        if (schedule.getCycle() == Cycle.ONLY_ONE_TIME) {
            schedule.setCustomerId(customerId);
            scheduleRepository.save(schedule);
            return;
        }

        // Store to database (EVERY_WEEK)
        if (schedule.getCycle() == Cycle.EVERY_WEEK) {
            for (int i = 0; i < maxQuantity; i++) {
                schedule.setCustomerId(customerId);

                schedule.setStartDate(schedule.getStartDate().plusWeeks(i));
                schedule.setEndDate(schedule.getEndDate().plusWeeks(i));

                scheduleRepository.save(schedule);
            }
            return;
        }

        // Store to database (EVERY_MONTH)
        for (int i = 0; i < maxQuantity; i++) {
            schedule.setCustomerId(customerId);

            schedule.setStartDate(schedule.getStartDate().plusMonths(i));
            schedule.setEndDate(schedule.getEndDate().plusMonths(i));

            scheduleRepository.save(schedule);
        }
    }

    private int getMaxQuantity(LocalDateTime date, Cycle cycle, int remaining, int quantity) {
        int maxForCycle = 1; // Default for ONLY_ONE_TIME
        LocalDateTime now = LocalDateTime.now();

        if (cycle == Cycle.EVERY_WEEK) {
            maxForCycle = (int) ChronoUnit.WEEKS.between(now, date);
        }

        if (cycle == Cycle.EVERY_MONTH) {
            maxForCycle = (int) ChronoUnit.MONTHS.between(now, date);
        }

        return Math.min(maxForCycle, Math.floorDiv(remaining, quantity));
    }
}
