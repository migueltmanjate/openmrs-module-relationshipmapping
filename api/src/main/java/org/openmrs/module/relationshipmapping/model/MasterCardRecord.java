package org.openmrs.module.relationshipmapping.model;

import org.openmrs.Patient;
import org.openmrs.Person;

import java.util.Date;

public class MasterCardRecord {
    private String name;
    private int age;
    private int relationshipTypeConceptId;
    private String nid;
    private int hivTest;
    private int hivCare;
    private int childAtRiskTreatment;
    private Patient indexPatient;
    private Person contactPerson;
    private Date obsDateTime;
    private Date encounterDateTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getHivTest() {
        return hivTest;
    }

    public void setHivTest(int hivTest) {
        this.hivTest = hivTest;
    }

    public int getHivCare() {
        return hivCare;
    }

    public void setHivCare(int hivCare) {
        this.hivCare = hivCare;
    }

    public int getChildAtRiskTreatment() {
        return childAtRiskTreatment;
    }

    public void setChildAtRiskTreatment(int childAtRiskTreatment) {
        this.childAtRiskTreatment = childAtRiskTreatment;
    }

    public int getRelationshipTypeConceptId() {
        return relationshipTypeConceptId;
    }

    public void setRelationshipTypeConceptId(int relationshipTypeConceptId) {
        this.relationshipTypeConceptId = relationshipTypeConceptId;
    }

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public void setIndexPatient(Patient indexPatient) {
        this.indexPatient = indexPatient;
    }

    public Patient getIndexPatient() {
        return indexPatient;
    }

    public void setContactPerson(Person contactPerson) {
        this.contactPerson = contactPerson;
    }

    public Person getContactPerson() {
        return contactPerson;
    }

    public void setObsDateTime(Date obsDateTime) {
        this.obsDateTime = obsDateTime;
    }

    public Date getObsDateTime() {
        return obsDateTime;
    }
    public Date getEncounterDateTime() {
        return encounterDateTime;
    }
    public void setEncounterDateTime(Date encounterDateTime) {
        this.encounterDateTime = encounterDateTime;
    }
}
