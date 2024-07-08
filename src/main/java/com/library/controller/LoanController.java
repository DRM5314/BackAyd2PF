package com.library.controller;

import com.library.dto.loan.LoanCreateRequestDTO;
import com.library.dto.loan.LoanResponseDTO;
import com.library.enums.LoanEnum;
import com.library.exceptions.ServiceException;
import com.library.repository.LoanRepository;
import com.library.service.loan.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/loan")
@PreAuthorize("hasAuthority('ADMIN')")
public class LoanController {
    private LoanService loanService;

    @Autowired
    public LoanController(LoanService loanService){
        this.loanService = loanService;
    }

    @PostMapping
    public ResponseEntity<LoanResponseDTO> create(@RequestBody LoanCreateRequestDTO createRequestDTO) throws ServiceException{
        LoanResponseDTO response = loanService.save(createRequestDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','STUDENT')")
    public ResponseEntity<LoanResponseDTO> findById(@PathVariable Long id) throws ServiceException{
        return ResponseEntity.ok(loanService.findByCodeDto(id));
    }

    @PutMapping("/{id}/{state}")
    public ResponseEntity<LoanResponseDTO> update(@PathVariable Long id, @PathVariable LoanEnum state) throws ServiceException{
        return ResponseEntity.ok(loanService.update(id,state));
    }

}
