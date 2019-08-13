
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.mentoringsystems.blockchain.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

/**
 *
 * @author jellediet
 */
public class Fish implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID id;
    private String type;
    private double weight;
    private BigDecimal price;
    private final String docType = "fish"; // Used to seperate assets in couchdb blockchain database

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

}
