package org.openmrs.module.relationshipmapping.model;

import org.openmrs.BaseOpenmrsData;

import java.util.Date;

public class MasterCardRecordMappingExecutionCycle extends BaseOpenmrsData {
    private Integer id;
    private Date queryEndTime;
    private Date latestRecordCreationDate;
    private Integer relationshipsCreated;
    private Integer personsCreated;
    private Integer personsForObsCreated;
    private Integer obsCreated;
    private Integer indexCasesInvolved;

    public MasterCardRecordMappingExecutionCycle(){
        relationshipsCreated = 0;
        personsCreated = 0;
        personsForObsCreated = 0;
        obsCreated = 0;
        indexCasesInvolved = 0;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getQueryEndTime() {
        return queryEndTime;
    }

    public void setQueryEndTime(Date queryEndTime) {
        this.queryEndTime = queryEndTime;
    }

    public Integer getRelationshipsCreated() {
        return relationshipsCreated;
    }

    public void setRelationshipsCreated(Integer relationshipsCreated) {
        this.relationshipsCreated = relationshipsCreated;
    }

    public Integer getPersonsCreated() {
        return personsCreated;
    }

    public void setPersonsCreated(Integer personsCreated) {
        this.personsCreated = personsCreated;
    }

    public Integer getPersonsForObsCreated() {
        return personsForObsCreated;
    }

    public void setPersonsForObsCreated(Integer relationshipsCreated) {
        this.personsForObsCreated = relationshipsCreated;
    }

    public Integer getObsCreated() {
        return obsCreated;
    }

    public void setObsCreated(Integer obsCreated) {
        this.obsCreated = obsCreated;
    }

    public Integer getIndexCasesInvolved() {
        return indexCasesInvolved;
    }

    public void setIndexCasesInvolved(Integer indexCasesInvolved) {
        this.indexCasesInvolved = indexCasesInvolved;
    }

    public Date getLatestRecordCreationDate() {
        return latestRecordCreationDate;
    }

    public void setLatestRecordCreationDate(Date latestRecordCreationDate) {
        this.latestRecordCreationDate = latestRecordCreationDate;
    }

    @Override
    public String toString() {
        return "MasterCardRecordMappingExecutionCycle{" +
                "id=" + id +
                ", queryEndTime=" + queryEndTime +
                ", latestRecordCreationDate=" + latestRecordCreationDate +
                ", relationshipsCreated=" + relationshipsCreated +
                ", personsCreated=" + personsCreated +
                ", personsForObsCreated=" + personsForObsCreated +
                ", obsCreated=" + obsCreated +
                ", indexCasesInvolved=" + indexCasesInvolved +
                '}';
    }
}
