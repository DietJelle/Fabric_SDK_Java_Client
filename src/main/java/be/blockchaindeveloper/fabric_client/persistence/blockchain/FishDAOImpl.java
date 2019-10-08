/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.blockchaindeveloper.fabric_client.persistence.blockchain;

import be.blockchaindeveloper.fabric_client.model.Fish;
import be.blockchaindeveloper.fabric_client.model.TransactionHistory;
import be.blockchaindeveloper.fabric_client.model.query.RichQuery;
import be.blockchaindeveloper.fabric_client.persistence.FishDAO;
import be.blockchaindeveloper.fabric_client.util.ChaincodeExecuter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hyperledger.fabric.sdk.BlockInfo;
import org.hyperledger.fabric.sdk.BlockInfo.EnvelopeInfo;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 *
 * @author jelle
 */
@Repository
public class FishDAOImpl implements FishDAO {

    @Autowired
    ChaincodeExecuter chaincodeExecuter;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    Channel channel;

    @Override
    public Fish getById(UUID id) {
        String key = String.valueOf(id);
        String json = chaincodeExecuter.getObjectByKey(key);
        Fish fish = null;
        if (json != null && !json.isEmpty()) {
            try {
                fish = objectMapper.readValue(json, Fish.class);
            } catch (IOException ex) {
                Logger.getLogger(FishDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return fish;
    }

    @Override
    public void save(Fish fish) {
        if (fish.getId() == null) {
            fish.setId(UUID.randomUUID());
        }
        String json = "";
        try {
            json = objectMapper.writeValueAsString(fish);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(FishDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        chaincodeExecuter.saveObject(String.valueOf(fish.getId()), json);
    }

    @Override
    public List<Fish> query(RichQuery query) {
        List<Fish> fish = new ArrayList<>();
        TypeReference<List<Fish>> listType = new TypeReference<List<Fish>>() {
        };

        String json = chaincodeExecuter.query(query);

        try {
            fish = objectMapper.readValue(json, listType);
        } catch (IOException ex) {
            Logger.getLogger(FishDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return fish;
    }

    @Override
    public void delete(UUID id) {
        chaincodeExecuter.deleteObject(String.valueOf(id));
    }

    @Override
    public List<Fish> getAll() {
        List<Fish> fish = new ArrayList<>();
        TypeReference<List<Fish>> listType = new TypeReference<List<Fish>>() {
        };

        RichQuery query = new RichQuery();
        Map<String, Object> selector = new HashMap<>();
        selector.put("docType", "fish");
        query.setSelector(selector);

        String json = chaincodeExecuter.query(query);

        try {
            fish = objectMapper.readValue(json, listType);
        } catch (IOException ex) {
            Logger.getLogger(FishDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return fish;
    }

    @Override
    public List<TransactionHistory> getHistory(UUID id) {
        String key = String.valueOf(id);
        List<TransactionHistory> list = chaincodeExecuter.getHistory(key);
        list.forEach((history) -> {
            try {
                String fishString = (String) history.getAsset();
                Fish fish = objectMapper.readValue(fishString, Fish.class);
                history.setAsset(fish);
                BlockInfo info = channel.queryBlockByTransactionID(history.getTransactionId());
                for (EnvelopeInfo envelopeInfo : info.getEnvelopeInfos()) {
                    if (envelopeInfo.getTransactionID().equals(history.getTransactionId())) {
                        String creator = envelopeInfo.getCreator().getId();
                        String mspId = envelopeInfo.getCreator().getMspid();
                        history.setCreatorId(creator);
                        history.setCreatorMspId(mspId);
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(FishDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidArgumentException ex) {
                Logger.getLogger(FishDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ProposalException ex) {
                Logger.getLogger(FishDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        return list;
    }

}
