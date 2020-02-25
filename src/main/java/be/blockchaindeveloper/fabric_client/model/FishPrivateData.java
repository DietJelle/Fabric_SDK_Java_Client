package be.blockchaindeveloper.fabric_client.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jellediet
 */
@JsonInclude(Include.NON_NULL)
public class FishPrivateData implements Serializable {

    private String owner;
    private Double mercuryContent;

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Double getMercuryContent() {
        return mercuryContent;
    }

    public void setMercuryContent(Double mercuryContent) {
        this.mercuryContent = mercuryContent;
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

    public static FishPrivateData fromJSONString(String json) {
        ObjectMapper mapper = new ObjectMapper();
        FishPrivateData fish = null;
        try {
            fish = mapper.readValue(json, FishPrivateData.class);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(Fish.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FishPrivateData.class.getName()).log(Level.SEVERE, null, ex);
        }
        return fish;
    }

}
