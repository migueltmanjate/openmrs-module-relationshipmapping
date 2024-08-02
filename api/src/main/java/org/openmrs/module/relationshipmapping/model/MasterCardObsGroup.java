package org.openmrs.module.relationshipmapping.model;

import java.util.Date;

public class MasterCardObsGroup implements Comparable{
    private int obsId;
    private Date obsDateTime;
    private int indexCaseId;
    private Date creationDate;

    private Date encounterDateTime;

    public int getObsId() {
        return obsId;
    }

    public void setObsId(int obsId) {
        this.obsId = obsId;
    }

    public Date getObsDateTime() {
        return obsDateTime;
    }

    public void setObsDateTime(Date obsDateTime) {
        this.obsDateTime = obsDateTime;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public int getIndexCaseId() {
        return indexCaseId;
    }

    public void setIndexCaseId(int indexCaseId) {
        this.indexCaseId = indexCaseId;
    }

    public Date getEncounterDateTime() {
        return encounterDateTime;
    }

    public void setEncounterDateTime(Date encounterDateTime) {
        this.encounterDateTime = encounterDateTime;
    }
    @Override
    public int compareTo(Object anotherGroup) {
        return getCreationDate().compareTo(((MasterCardObsGroup) anotherGroup).getCreationDate());
    }
}
