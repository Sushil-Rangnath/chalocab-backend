package com.cab.chaloCab.service.impl;

import com.cab.chaloCab.dto.CabDTO;
import com.cab.chaloCab.entity.Cab;
import com.cab.chaloCab.repository.CabRepository;
import com.cab.chaloCab.service.CabService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CabServiceImpl implements CabService {

    @Autowired
    private CabRepository cabRepo;

    @Override
    public CabDTO addCab(CabDTO dto) {
        Cab cab = Cab.builder()
                .model(dto.getModel())
                .registrationNumber(dto.getRegistrationNumber())
                .color(dto.getColor())
                .type(dto.getType())
                .available(dto.isAvailable())
                .build();

        return convertToDTO(cabRepo.save(cab));
    }

    public Map<String, Object> getCabsPaginated(int page, int size) {
        Pageable paging = PageRequest.of(page, size);
        Page<Cab> cabPage = cabRepo.findAll(paging);

        List<CabDTO> cabs = cabPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("cabs", cabs);
        response.put("currentPage", cabPage.getNumber());
        response.put("totalItems", cabPage.getTotalElements());
        response.put("totalPages", cabPage.getTotalPages());

        return response;
    }
    @Override
    public CabDTO updateCab(Long id, CabDTO dto) {
        Cab cab = cabRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cab not found"));

        cab.setModel(dto.getModel());
        cab.setRegistrationNumber(dto.getRegistrationNumber());
        cab.setColor(dto.getColor());
        cab.setType(dto.getType());
        cab.setAvailable(dto.isAvailable());

        return convertToDTO(cabRepo.save(cab));
    }

    @Override
    public void deleteCab(Long id) {
        if (!cabRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cab not found");
        }
        cabRepo.deleteById(id);
    }

    private CabDTO convertToDTO(Cab cab) {
        return CabDTO.builder()
                .id(cab.getId())
                .model(cab.getModel())
                .registrationNumber(cab.getRegistrationNumber())
                .color(cab.getColor())
                .type(cab.getType())
                .available(cab.isAvailable())
                .build();
    }
}
