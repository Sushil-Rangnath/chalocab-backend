package com.cab.chaloCab.service;

import com.cab.chaloCab.dto.CabDTO;

import java.util.List;

public interface CabService {
    CabDTO addCab(CabDTO cabDTO);
    List<CabDTO> getAllCabs();
    CabDTO updateCab(Long id, CabDTO cabDTO);
    void deleteCab(Long id);
}
