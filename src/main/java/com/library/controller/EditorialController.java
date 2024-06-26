package com.library.controller;

import com.library.dto.editorial.EditorialResponseDTO;
import com.library.dto.editorial.EditorialUpdateRequestDTO;
import com.library.exceptions.ServiceException;
import com.library.service.editorial.EditorialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/editorial")
public class EditorialController {
    private EditorialService editorialService;

    @Autowired
    public EditorialController(EditorialService editorialService){
        this.editorialService = editorialService;
    }

    @PostMapping
    public ResponseEntity<EditorialResponseDTO> create(@RequestBody String name) throws ServiceException {
        return ResponseEntity.ok(editorialService.save(name));
    }

    @GetMapping("/findAll")
    public ResponseEntity<List<EditorialResponseDTO>> findAll(){
        return ResponseEntity.ok(editorialService.findAll());
    }
    @PutMapping("/{id}")
    public ResponseEntity<EditorialResponseDTO> update(@PathVariable long id, @RequestBody EditorialUpdateRequestDTO update) throws ServiceException{
        return ResponseEntity.ok(editorialService.update(id,update));
    }
}
