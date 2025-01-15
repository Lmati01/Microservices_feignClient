package lmati.java.carservice.controller;

import lmati.java.carservice.entity.Car;
import lmati.java.carservice.model.CarResponse;
import lmati.java.carservice.service.CarService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/car")
public class CarController {
    @Autowired
    private CarService carService;

    private static final Logger log = LoggerFactory.getLogger(CarController.class);

    @GetMapping("/feign-client/all")
    public ResponseEntity<List<CarResponse>> getAllCarsFeignClient() {
        return ResponseEntity.ok(carService.findAllWithFeignClient());
    }

    @GetMapping("/feign-client/{id}")
    public ResponseEntity<CarResponse> findByIdFeignClient(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(carService.findByIdWithFeignClient(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Standard CRUD operations (unchanged)
    @PostMapping
    public ResponseEntity<Car> save(@RequestBody Car car) {
        try {
            return ResponseEntity.ok(carService.save(car));
        } catch (Exception e) {
            log.error("Error saving car: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Car> update(@PathVariable Long id, @RequestBody Car car) {
        try {
            car.setId(id);
            return ResponseEntity.ok(carService.save(car));
        } catch (Exception e) {
            log.error("Error updating car: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            carService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting car: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/client/feign-client/{clientId}")
    public CarResponse findByClientIdFeignClient(@PathVariable Long clientId) {
        return carService.findByIdWithFeignClient(clientId);
    }
}
