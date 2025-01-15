package lmati.java.carservice.service;

import lmati.java.carservice.repository.ClientServiceFeignClient;
import lmati.java.carservice.entity.Car;
import lmati.java.carservice.entity.Client;
import lmati.java.carservice.model.CarResponse;
import lmati.java.carservice.repository.CarRepository;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarService {
    @Autowired
    private CarRepository carRepository;

    @Autowired
    private ClientServiceFeignClient feignClient;


    private final String CLIENT_SERVICE_URL = "http://localhost:8888/api/client/";

    // FeignClient implementation
    public List<CarResponse> findAllWithFeignClient() {
        List<Car> cars = carRepository.findAll();
        return cars.stream()
                .map(car -> {
                    Client client = fetchClientWithFeignClient(car.getClientId());
                    return buildCarResponse(car, client);
                })
                .collect(Collectors.toList());
    }

    private Client fetchClientWithFeignClient(Long clientId) {
        if (clientId == null) return null;
        try {
            return feignClient.getClientById(clientId);
        } catch (FeignException.NotFound e) {
            System.out.println("Client not found with ID: {}"+clientId);
            return null;
        } catch (Exception e) {
            System.out.println("Error fetching client with FeignClient for ID {}: {}"+ clientId + e.getMessage());
            return null;
        }
    }

    private CarResponse buildCarResponse(Car car, Client client) {
        return CarResponse.builder()
                .id(car.getId())
                .brand(car.getBrand())
                .model(car.getModel())
                .matricule(car.getMatricule())
                .client(client)
                .build();
    }

    public Car save(Car car) {
        return carRepository.save(car);
    }

    public void deleteById(Long id) {
        carRepository.deleteById(id);
    }

    // FeignClient version
    public CarResponse findByIdWithFeignClient(Long id) {
        Car car = carRepository.findById(id).orElseThrow(() -> new RuntimeException("Car not found"));
        Client client = fetchClientWithFeignClient(car.getClientId());
        return buildCarResponse(car, client);
    }
}