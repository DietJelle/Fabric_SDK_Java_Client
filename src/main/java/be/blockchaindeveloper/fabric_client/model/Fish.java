package be.blockchaindeveloper.fabric_client.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jellediet
 */
@JsonInclude(Include.NON_NULL)
public class Fish implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID id;
    private String type;
    private double weight;
    private BigDecimal price;
    private FishPrivateData fishPrivateData;
    private String privateDataHash;
    private final String docType = "fish"; // Used to seperate assets in couchdb fabric database

    public String getDocType() {
        return docType;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public FishPrivateData getFishPrivateData() {
        return fishPrivateData;
    }

    public void setFishPrivateData(FishPrivateData fishPrivateData) {
        this.fishPrivateData = fishPrivateData;
    }

    public String toJSONString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(Fish.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String getPrivateDataHash() {
        return privateDataHash;
    }

    public void setPrivateDataHash(String privateDataHash) {
        this.privateDataHash = privateDataHash;
    }

    public static Fish fromJSONString(String json) {
        ObjectMapper mapper = new ObjectMapper();
        Fish fish = null;
        try {
            fish = mapper.readValue(json, Fish.class);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(Fish.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Fish.class.getName()).log(Level.SEVERE, null, ex);
        }
        return fish;
    }

}
