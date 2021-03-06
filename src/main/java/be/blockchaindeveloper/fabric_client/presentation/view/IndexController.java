package be.blockchaindeveloper.fabric_client.presentation.view;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author jellediet
 */
@Controller
public class IndexController {

    @Autowired
    FishService fishService;

    @RequestMapping("/")
    public String welcome(Model model) {
        return "index";
    }

    @RequestMapping("/products/add")
    public String createFish(Model model) {
        model.addAttribute("fish", new Fish());
        return "edit";
    }

    @RequestMapping("/products/edit")
    public String editFish(@RequestParam UUID id, Model model) {
        model.addAttribute("fish", fishService.getById(id));
        return "edit";
    }

    @RequestMapping("/products/search")
    public String searchFish(Model model) {
        model.addAttribute("fish", new Fish());
        return "search";
    }

    @RequestMapping("/products/history")
    public String getHistory(@RequestParam UUID id, Model model) {
        List<TransactionHistory> history = fishService.getHistory(id);
        model.addAttribute("history", history);
        return "history";
    }

    @RequestMapping("/products/query")
    public String queryFish(@RequestParam(required = false) String type, @RequestParam(required = false) BigDecimal price, @RequestParam(required = false) Double weight, Model model) {
        RichQuery query = new RichQuery();
        Map<String, Object> selector = new HashMap<>();
        if (type != null && !type.isEmpty()) {
            selector.put("type", type);
        }

        if (price != null && !price.equals(BigDecimal.ZERO)) {

            selector.put("price", price);
        }

        if (weight != null && weight != 0.0) {
            selector.put("weight", weight);
        }

        query.setSelector(selector);

        model.addAttribute("fishes", fishService.query(query));
        return "products";
    }

    @RequestMapping("/products")
    public String getAllFish(Model model) {
        model.addAttribute("fishes", fishService.getAll());
        return "products";
    }

    @RequestMapping("/products/delete")
    public String deleteFish(@RequestParam UUID id) {
        fishService.delete(id);
        return "redirect:/products";
    }

    @RequestMapping("/products/save")
    public String saveFish(@RequestParam UUID id, @RequestParam String type, @RequestParam BigDecimal price, @RequestParam Double weight,
            @RequestParam(required = false, value = "fishPrivateData.owner") String owner, @RequestParam(required = false, value = "fishPrivateData.mercuryContent") Double mercuryContent) {
        Fish fish;
        if (id == null) {
            fish = new Fish();
        } else {
            fish = fishService.getById(id);
        }

        fish.setType(type);

        fish.setPrice(price);

        fish.setWeight(weight);

        FishPrivateData privateData = new FishPrivateData();
        privateData.setMercuryContent(mercuryContent);
        privateData.setOwner(owner);
        fish.setFishPrivateData(privateData);

        fishService.save(fish);

        return "redirect:/products";
    }

}
