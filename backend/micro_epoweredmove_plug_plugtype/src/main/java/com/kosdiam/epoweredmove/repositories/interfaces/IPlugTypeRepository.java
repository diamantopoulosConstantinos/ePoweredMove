package com.kosdiam.epoweredmove.repositories.interfaces;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import com.kosdiam.epoweredmove.models.dtos.PlugTypeDto;

@Repository
public interface IPlugTypeRepository {
    PlugTypeDto create(PlugTypeDto plugType);
    PlugTypeDto get(String id);
    List<PlugTypeDto> getAll();
    PlugTypeDto update(PlugTypeDto plug);
    Boolean delete(String id);
    String addPlugTypeImage(MultipartFile file);
    Boolean deletePlugTypeImage(String id);

}
