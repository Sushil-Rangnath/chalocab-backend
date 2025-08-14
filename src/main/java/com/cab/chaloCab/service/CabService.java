package com.cab.chaloCab.service;

import com.cab.chaloCab.dto.CabDTO;

import java.util.List;
import java.util.Map;

public interface CabService {
    CabDTO addCab(CabDTO cabDTO);
    CabDTO updateCab(Long id, CabDTO cabDTO);
    void deleteCab(Long id);
     Map<String, Object> getCabsPaginated(int page, int size);
}
