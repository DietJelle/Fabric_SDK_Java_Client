/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.mentoringsystems.blockchain.service.impl;

import be.mentoringsystems.blockchain.model.Fish;
import be.mentoringsystems.blockchain.model.query.RichQuery;
import be.mentoringsystems.blockchain.persistence.FishDAO;
import be.mentoringsystems.blockchain.service.FishService;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author jelle
 */
@Service
public class FishServiceImpl implements FishService {

    @Autowired
    FishDAO fishDAO;

    @Override
    public Fish getById(UUID id) {
        return fishDAO.getById(id);
    }

    @Override
    public void save(Fish fish) {
        fishDAO.save(fish);
    }

    @Override
    public List<Fish> query(RichQuery query) {
        return fishDAO.query(query);
    }

    @Override
    public void delete(UUID id) {
        fishDAO.delete(id);
    }

    @Override
    public List<Fish> getAll() {
        return fishDAO.getAll();
    }

}
