package com.library.controller;

import com.library.dto.career.CareerResponseDTO;
import com.library.dto.career.CareerUpdateRequestDTO;
import com.library.exceptions.ServiceException;
import com.library.service.career.CareerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/career")
public class CareerController {
    private CareerService careerService;
    @Autowired
    public CareerController(CareerService careerService){
        this.careerService = careerService;
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<CareerResponseDTO> save(@RequestBody String name) throws ServiceException {
        return ResponseEntity.ok(careerService.save(name));
    }
    @PreAuthorize("hasAnyAuthority('ADMIN','STUDENT')")
    @GetMapping("/findAll")
    public ResponseEntity<List<CareerResponseDTO>> findAll(){
        return ResponseEntity.ok(careerService.findAll());
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<CareerResponseDTO> update(@PathVariable Long id, @RequestBody CareerUpdateRequestDTO update) throws ServiceException{
        CareerResponseDTO response = careerService.update(id,update);
        return ResponseEntity.ok(response);
    }


}
