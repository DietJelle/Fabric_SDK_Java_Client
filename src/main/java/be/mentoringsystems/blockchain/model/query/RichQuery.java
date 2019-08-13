/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.mentoringsystems.blockchain.model.query;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jellediet
 */
@JsonInclude(Include.NON_NULL)
public class RichQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    private Map<String, Object> selector;
    private String limit;
    private String skip;
    private List<Map<String, String>> sort;
    private Map<String, String> fields;
    //Use two strings: one for the ddoc and one for the index name
    private List<String> use_index;

    public List<String> getUse_index() {
        return use_index;
    }

    public void setUse_index(List<String> use_index) {
        this.use_index = use_index;
    }

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public String getSkip() {
        return skip;
    }

    public void setSkip(String skip) {
        this.skip = skip;
    }

    public Map<String, Object> getSelector() {
        return selector;
    }

    public List<Map<String, String>> getSort() {
        return sort;
    }

    public void setSort(List<Map<String, String>> sort) {
        this.sort = sort;
    }

    public void setSelector(Map<String, Object> selector) {
        this.selector = selector;
    }

    public Map<String, String> getFields() {
        return fields;
    }

    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }

}
