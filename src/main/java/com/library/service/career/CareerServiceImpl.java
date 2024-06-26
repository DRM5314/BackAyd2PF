package com.library.service.career;


import com.library.dto.career.CareerResponseDTO;
import com.library.dto.career.CareerUpdateRequestDTO;
import com.library.exceptions.DuplicatedEntityException;
import com.library.exceptions.NotFoundException;
import com.library.exceptions.ServiceException;
import com.library.model.Career;
import com.library.repository.CareerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CareerServiceImpl implements CareerService{
    private CareerRepository careerRepository;

    @Autowired
    public CareerServiceImpl(CareerRepository careerRepository){
        this.careerRepository = careerRepository;
    }

    @Override
    public CareerResponseDTO save(String career) throws ServiceException {
        if(existByName(career)){
            throw new DuplicatedEntityException(String.format("Career with name: %s, already exists",career));
        }
        Career careerSave = new Career();
        careerSave.setName(career);
        careerSave = careerRepository.save(careerSave);
        return new CareerResponseDTO(careerSave);
    }

    @Override
    public CareerResponseDTO findByIdDto(Long id) throws ServiceException {
        Career career = careerRepository.findById(id).orElseThrow(()->
                    new NotFoundException(String.format("Career with id: %s, not exist",id))
                );
        return new CareerResponseDTO(career);
    }

    @Override
    public CareerResponseDTO update(Long id,CareerUpdateRequestDTO update) throws ServiceException {
        Career careerUdate = findByIdNoDto(id);
        if(existByNameUpdate(update.getName(),id)){
            throw new DuplicatedEntityException(String.format("Sorry, this name: %s already exist!",update.getName()));
        }
        careerUdate.setName(update.getName());
        careerUdate = careerRepository.save(careerUdate);
        return new CareerResponseDTO(careerUdate);
    }

    @Override
    public List<CareerResponseDTO> findAll() {
        return careerRepository.findAll().stream().map(CareerResponseDTO::new).collect(Collectors.toList());
    }

    @Override
    public Career findByIdNoDto(Long id) throws ServiceException {
        Career career = careerRepository.findById(id).orElseThrow(()->
                new NotFoundException(String.format("Career with id: %s, not exist",id))
        );
        return career;
    }

    @Override
    public Boolean existByName(String name) {
        return careerRepository.existsByName(name);
    }

    @Override
    public Boolean existByNameUpdate(String name, Long id) {
        return careerRepository.existsByNameAndIdIsNot(name,id);
    }
}
