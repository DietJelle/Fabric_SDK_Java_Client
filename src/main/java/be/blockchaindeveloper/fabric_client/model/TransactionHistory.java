/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.blockchaindeveloper.fabric_client.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.time.Instant;

/**
 *
 * @author jellediet
 */
public class TransactionHistory implements Serializable {

    private static final long serialVersionUID = 1L;
    private Object asset;
    private Instant timeStamp;
    private String transactionId;
    private String creatorId;
    private String creatorMspId;

    public Object getAsset() {
        return asset;
    }

    public void setAsset(Object asset) {
        this.asset = asset;
    }

    public Instant getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Instant timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    @JsonProperty
    public String getCreatorId() {
        return creatorId;
    }

    @JsonIgnore
    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    @JsonProperty
    public String getCreatorMspId() {
        return creatorMspId;
    }

    @JsonIgnore
    public void setCreatorMspId(String creatorMspId) {
        this.creatorMspId = creatorMspId;
    }

}
