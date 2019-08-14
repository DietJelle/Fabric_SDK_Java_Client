package be.blockchaindeveloper.fabric_client.service;

import be.blockchaindeveloper.fabric_client.model.Fish;
import be.blockchaindeveloper.fabric_client.model.query.RichQuery;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author jelle
 */
public interface FishService {

    Fish getById(UUID id);

    void save(Fish fish);

    List<Fish> query(RichQuery query);

    void delete(UUID id);

    List<Fish> getAll();

}
