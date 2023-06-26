package org.openmrs.module.relationshipmapping.model;

public class MasterCardObsGroupMember {
    private Integer obsId;
    private Integer conceptId;
    private Integer valueCoded;
    private Double valueNumeric;
    private String valueText;

    public Integer getObsId() {
        return obsId;
    }

    public void setObsId(Integer obsId) {
        this.obsId = obsId;
    }

    public Integer getConceptId() {
        return conceptId;
    }

    public void setConceptId(Integer conceptId) {
        this.conceptId = conceptId;
    }

    public Integer getValueCoded() {
        return valueCoded;
    }

    public void setValueCoded(Integer valueCoded) {
        this.valueCoded = valueCoded;
    }

    public Double getValueNumeric() {
        return valueNumeric;
    }

    public void setValueNumeric(Double valueNumeric) {
        this.valueNumeric = valueNumeric;
    }

    public String getValueText() {
        return valueText;
    }

    public void setValueText(String valueText) {
        this.valueText = valueText;
    }
}
