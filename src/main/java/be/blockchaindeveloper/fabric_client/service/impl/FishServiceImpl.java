package be.blockchaindeveloper.fabric_client.service.impl;

import be.blockchaindeveloper.fabric_client.model.Fish;
import be.blockchaindeveloper.fabric_client.model.query.RichQuery;
import be.blockchaindeveloper.fabric_client.persistence.FishDAO;
import be.blockchaindeveloper.fabric_client.service.FishService;
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
