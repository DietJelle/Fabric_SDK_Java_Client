package be.blockchaindeveloper.fabric_client.presentation.rest;

import be.blockchaindeveloper.fabric_client.model.Fish;
import be.blockchaindeveloper.fabric_client.model.FishPrivateData;
import be.blockchaindeveloper.fabric_client.model.TransactionHistory;
import be.blockchaindeveloper.fabric_client.model.query.RichQuery;
import be.blockchaindeveloper.fabric_client.service.FishService;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author jelle
 */
@RestController
@RequestMapping("/fish")
public class FishController {

    @Autowired
    FishService fishService;

    //Gets a fish using id as key
    @RequestMapping("/get")
    Fish getFish(@RequestParam UUID id) {
        return fishService.getById(id);
    }

    //Get all fish
    @RequestMapping("/getAll")
    List<Fish> getAllFish() {
        return fishService.getAll();
    }

    //Saves a fish using id as key
    @RequestMapping("/save")
    Fish saveFish(@RequestParam(required = false) UUID id, @RequestParam(required = false) String type, @RequestParam(required = false) Double weight, @RequestParam(required = false) BigDecimal price, @RequestParam(required = false, value = "fishPrivateData.owner") String owner, @RequestParam(required = false, value = "fishPrivateData.mercuryContent") Double mercuryContent) {
        Logger.getLogger(FishController.class.getName()).log(Level.INFO, "Owner {0}", owner);

        Fish fish = new Fish();
        if (id != null) {
            fish = fishService.getById(id);
        }
        fish.setPrice(price);
        if (weight != null) {
            fish.setWeight(weight);
        }
        fish.setType(type);
        FishPrivateData privateData = new FishPrivateData();
        privateData.setMercuryContent(mercuryContent);
        privateData.setOwner(owner);
        fish.setFishPrivateData(privateData);
        fishService.save(fish);
        return fish;
    }

    //Deletes a fish by its key
    @RequestMapping("/delete")
    String deleteFish(@RequestParam UUID id) {
        fishService.delete(id);

        return id.toString();
    }

    //Search all documents in the blockchain with docType fish and the input type
    @RequestMapping("/query")
    List<Fish> queryFish(@RequestParam(required = false) String type) {
        RichQuery query = new RichQuery();
        Map<String, Object> selector = new HashMap<>();
        if (type != null && !type.isEmpty()) {
            selector.put("type", type);
        }
        selector.put("docType", "fish");
        query.setSelector(selector);

        return fishService.query(query);
    }

    @RequestMapping("/getHistory")
    List<TransactionHistory> getHistory(@RequestParam UUID id) {
        return fishService.getHistory(id);
    }

}
