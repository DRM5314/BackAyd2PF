package com.library.service.editorial;


import com.library.dto.editorial.EditorialResponseDTO;
import com.library.dto.editorial.EditorialUpdateRequestDTO;
import com.library.exceptions.DuplicatedEntityException;
import com.library.exceptions.NotFoundException;
import com.library.exceptions.ServiceException;
import com.library.model.Editorial;
import com.library.repository.EditorialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EditorialServiceImpl implements EditorialService{
    private final EditorialRepository editorialRepository;

    @Autowired
    public EditorialServiceImpl(EditorialRepository editorialRepository){
        this.editorialRepository = editorialRepository;
    }

    @Override
    public EditorialResponseDTO save(String editorialNew) throws ServiceException {
        if(existDuplicatedName(editorialNew)){
            throw new DuplicatedEntityException(String.format("Editorial with name: %s, already exists!",editorialNew));
        }
        Editorial editorial = new Editorial();
        editorial.setName(editorialNew);

        editorial = editorialRepository.save(editorial);
        return new EditorialResponseDTO(editorial);
    }

    @Override
    public EditorialResponseDTO update(Long id, EditorialUpdateRequestDTO name) throws ServiceException {
        if(existDuplicatedNameUpdate(id,name.getName())){
            throw new DuplicatedEntityException(String.format("Sorry this editorial with name: %s already exist",name.getName()));
        }
        Editorial editorialUpdate = findByCodeNotDto(id);
        editorialUpdate.setName(name.getName());
        editorialUpdate = editorialRepository.save(editorialUpdate);
        return new EditorialResponseDTO(editorialUpdate);
    }

    @Override
    public EditorialResponseDTO findByCode(Long code) throws ServiceException {
        Editorial editorial = editorialRepository.findById(code).orElseThrow(()->
                new NotFoundException(String.format("Editorial with code: %s, not exist",code))
        );
        return new EditorialResponseDTO(editorial);
    }

    @Override
    public List<EditorialResponseDTO> findAll() {
        return editorialRepository.findAll().stream().map(EditorialResponseDTO::new).collect(Collectors.toList());
    }

    @Override
    public Boolean existDuplicatedName(String name) {
        return editorialRepository.existsByName(name);
    }

    @Override
    public Boolean existDuplicatedNameUpdate(Long id, String name){
        return editorialRepository.existsByNameAndIdIsNot(name,id);
    }

    @Override
    public Editorial findByCodeNotDto(Long code) throws ServiceException {
        Editorial editorial = editorialRepository.findById(code).orElseThrow(()->
                new NotFoundException(String.format("Editorial with code: %s, not exist",code))
        );
        return editorial;
    }
}
