package org.openmrs.module.relationshipmapping.model;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.Relationship;

public class MasterCardRecordMappingLog extends BaseOpenmrsData {
    private Integer id;
    private Integer hivCareObsId;
    private Integer hivTestObsId;
    private Integer contactNidObsId;
    private Integer childAtRiskTreatmentObsId;
    private Integer contactNameObsId;
    private Integer contactAgeObsId;
    private Integer relationshipObsId;
    private Person contactPerson;
    private Patient indexPatient;
    private boolean contactPersonWasCreated;
    private boolean personObsWasCreated;
    private Relationship relationship;
    private MasterCardRecordMappingExecutionCycle executionCycle;
    private String migrationResult;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getHivCareObsId() {
        return hivCareObsId;
    }

    public void setHivCareObsId(Integer hivCareObsId) {
        this.hivCareObsId = hivCareObsId;
    }

    public Integer getHivTestObsId() {
        return hivTestObsId;
    }

    public void setHivTestObsId(Integer hivTestObsId) {
        this.hivTestObsId = hivTestObsId;
    }

    public Integer getChildAtRiskTreatmentObsId() {
        return childAtRiskTreatmentObsId;
    }

    public void setChildAtRiskTreatmentObsId(Integer childAtRiskTreatmentObsId) {
        this.childAtRiskTreatmentObsId = childAtRiskTreatmentObsId;
    }

    public Integer getContactNidObsId() {
        return contactNidObsId;
    }

    public void setContactNidObsId(Integer contactNidObsId) {
        this.contactNidObsId = contactNidObsId;
    }

    public Integer getContactNameObsId() {
        return contactNameObsId;
    }

    public void setContactNameObsId(Integer contactNameObsId) {
        this.contactNameObsId = contactNameObsId;
    }

    public Integer getContactAgeObsId() {
        return contactAgeObsId;
    }

    public void setContactAgeObsId(Integer contactAgeObsId) {
        this.contactAgeObsId = contactAgeObsId;
    }

    public Integer getRelationshipObsId() {
        return relationshipObsId;
    }

    public void setRelationshipObsId(Integer relationshipObsId) {
        this.relationshipObsId = relationshipObsId;
    }

    public Person getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(Person contactPerson) {
        this.contactPerson = contactPerson;
    }

    public Patient getIndexPatient() {
        return indexPatient;
    }

    public void setIndexPatient(Patient indexPatient) {
        this.indexPatient = indexPatient;
    }

    public Relationship getRelationship() {
        return relationship;
    }

    public void setRelationship(Relationship relationship) {
        this.relationship = relationship;
    }

    public boolean getContactPersonWasCreated() {
        return contactPersonWasCreated;
    }

    public void setContactPersonWasCreated(boolean contactPersonWasCreated) {
        this.contactPersonWasCreated = contactPersonWasCreated;
    }

    public boolean getPersonObsWasCreated() {
        return personObsWasCreated;
    }

    public void setPersonObsWasCreated(boolean personObsWasCreated) {
        this.personObsWasCreated = personObsWasCreated;
    }

    public MasterCardRecordMappingExecutionCycle getExecutionCycle() {
        return executionCycle;
    }

    public void setExecutionCycle(MasterCardRecordMappingExecutionCycle executionCycle) {
        this.executionCycle = executionCycle;
    }

	public String getMigrationResult() {
		return migrationResult;
	}

	public void setMigrationResult(String migrationResult) {
		this.migrationResult = migrationResult;
	}
}
