package org.openmrs.module.relationshipmapping.model;

import static org.apache.commons.lang3.StringUtils.isEmpty;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.api.context.Context;
import org.openmrs.module.relationshipmapping.api.db.MasterCardRecordDao;

public class MasterCardPersonGenerator {

	private MasterCardObsGroup masterCardObsGroup;

	private MasterCardRecordDao masterCardRecordDao;

	private Concept hivTestConcept;
	private Concept hivCareConcept;
	private Concept childAtRiskTreatmentConcept;
	private Concept nidConcept;
	
    private Calendar cutOffBirthDate = Calendar.getInstance();
    
    private Log log = LogFactory.getLog(this.getClass());

	public MasterCardPersonGenerator(MasterCardObsGroup masterCardObsGroup, MasterCardRecordDao masterCardRecordDao) {
		super();
		this.masterCardObsGroup = masterCardObsGroup;
		this.masterCardRecordDao = masterCardRecordDao;
	}

	public MasterCardRecordMappingLog migrate(MasterCardRecordMappingExecutionCycle executionCycle) {
		
        cutOffBirthDate.add(Calendar.YEAR, -15);
        cutOffBirthDate.set(Calendar.MONTH,0);
        cutOffBirthDate.set(Calendar.DAY_OF_MONTH,1);

        hivTestConcept = Context.getConceptService().getConcept(23779);
        hivCareConcept = Context.getConceptService().getConcept(23780);
        childAtRiskTreatmentConcept = Context.getConceptService().getConcept(1885);
        nidConcept = Context.getConceptService().getConcept(23781);

		final List<Patient> indexCasesInvolved = new ArrayList<Patient>();
		final Set<Person> personsForObsCreated = new HashSet<Person>();

		final PatientIdentifierType nidIdentifierType = Context.getPatientService().getPatientIdentifierType(2);
		List<PatientIdentifierType> identifierTypes = new ArrayList<PatientIdentifierType>() {
			{
				add(nidIdentifierType);
			}
		};

		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");

		MasterCardRecordMappingLog masterCardRecordMappingLog = new MasterCardRecordMappingLog(); //
		masterCardRecordMappingLog.setExecutionCycle(executionCycle);
		Patient indexPatient = Context.getPatientService().getPatient(this.masterCardObsGroup.getIndexCaseId());
		MasterCardRecord masterCardRecord = new MasterCardRecord();
		masterCardRecord.setIndexPatient(indexPatient);
		masterCardRecord.setObsDateTime(this.masterCardObsGroup.getObsDateTime());
		List<MasterCardObsGroupMember> members = masterCardRecordDao.getMasterCardObsGroupMembers(this.masterCardObsGroup.getObsId());
		for (MasterCardObsGroupMember member : members) {
			int conceptId = member.getConceptId();
			if (conceptId == 23779) {
				masterCardRecord.setHivTest(member.getValueCoded());
				masterCardRecordMappingLog.setHivTestObsId(member.getObsId());
			} else if (conceptId == 23777 && member.getValueNumeric() != null) {
				masterCardRecord.setAge(member.getValueNumeric().intValue());
				masterCardRecordMappingLog.setContactAgeObsId(member.getObsId());
			} else if (conceptId == 23704) {
				masterCardRecord.setRelationshipTypeConceptId(member.getValueCoded());
				masterCardRecordMappingLog.setRelationshipObsId(member.getObsId());
			} else if (conceptId == 23778) {
				masterCardRecord.setName(member.getValueText());
				masterCardRecordMappingLog.setContactNameObsId(member.getObsId());
			} else if (conceptId == 5622 && masterCardRecord.getRelationshipTypeConceptId() <= 0) {
				if (StringUtils.equalsIgnoreCase(member.getValueText(), "FIlha")
						|| StringUtils.equalsIgnoreCase(member.getValueText(), "FIlho")) {
					masterCardRecord.setRelationshipTypeConceptId(23707);
				} else if (StringUtils.equalsIgnoreCase(member.getValueText(), "Pai")) {
					masterCardRecord.setRelationshipTypeConceptId(971);
				} else if (StringUtils.equalsIgnoreCase(member.getValueText(), "Mae")) {
					masterCardRecord.setRelationshipTypeConceptId(970);
				} else if (StringUtils.equalsIgnoreCase(member.getValueText(), "Parceiro")) {
					masterCardRecord.setRelationshipTypeConceptId(1921);
				} else {
					masterCardRecord.setRelationshipTypeConceptId(5622);
				}
				masterCardRecordMappingLog.setRelationshipObsId(member.getObsId());
			} else if (conceptId == 23780) {
				masterCardRecord.setHivCare(member.getValueCoded());
				masterCardRecordMappingLog.setHivCareObsId(member.getObsId());
			} else if (conceptId == 23781) {
				masterCardRecord.setNid(member.getValueText());
				masterCardRecordMappingLog.setContactNidObsId(member.getObsId());
			} else if (conceptId == 1885) {
				masterCardRecord.setChildAtRiskTreatment(member.getValueCoded());
				masterCardRecordMappingLog.setChildAtRiskTreatmentObsId(member.getObsId());
			}
		}

		// evaluate if is record of interest, else move to next group
		if (isNonTestedPositiveParceiro(masterCardRecord) || isBelow15AndNonTestedPositiveFilho(masterCardRecord)
				|| isParent(masterCardRecord)) {

			if (StringUtils.isNotBlank(masterCardRecord.getNid())) {
				// search for existing person by NID. if not found,create a new person and
				// relationship.
				// If found, make it the relationship person

				List<Patient> contacts = Context.getPatientService().getPatients(null, masterCardRecord.getNid(),
						identifierTypes, true);

				if (contacts.size() == 1) {
					// Contact patient was found.
					masterCardRecord.setContactPerson(contacts.get(0));
					masterCardRecordMappingLog.setContactPerson(contacts.get(0));
					// Check if there exists a relationship of the same type already before creating
					// a new one.
					int relationshipTypeId = getRelationshipTypeId(masterCardRecord.getRelationshipTypeConceptId());
					RelationshipType relationshipType = Context.getPersonService()
							.getRelationshipType(relationshipTypeId);

					List<Relationship> existingRelationships = Context.getPersonService()
							.getRelationships(contacts.get(0), indexPatient, relationshipType);
					if (existingRelationships.size() == 0) {
						// Relationship does not exist. Creating a new one.
						createRelationship(masterCardRecord, masterCardRecordMappingLog, executionCycle);

						// Creating obs too
						createPersonObs(masterCardRecord, masterCardRecordMappingLog, executionCycle,
								personsForObsCreated);

						if (!indexCasesInvolved.contains(indexPatient)) {
							indexCasesInvolved.add(indexPatient);
						}
					} else {
						// Do nothing. The relationship already exists
						// ToDo: How about obs? Update them if mastercard is newer and has different
						// value?
						masterCardRecordMappingLog.setMigrationResult("Mastercard Relationship mapping: Relationship already exists between index case ["+masterCardRecord.getIndexPatient().getId()+
								"] and contact person ["+contacts.get(0).getId()+"]");
                        log.info("Mastercard Relationship mapping: Relationship already exists between index case ["+masterCardRecord.getIndexPatient().getId()+
                                "] and contact person ["+contacts.get(0).getId()+"]");
					}
				} else {
					// Patient with the NID in the mastercard record could not be uniquely
					// identified
					// Search for existing related person with matching names as the mastercard.
					// If not found, create a new person record, Create a relationship with index
					// case, and then create Obs.
					Person contactPerson = searchForExistingRelatedPersonWithSimilarNames(masterCardRecord);
					if (contactPerson == null) {
						contactPerson = createNewRelationshipPerson(masterCardRecord, masterCardRecordMappingLog,
								executionCycle);
						masterCardRecord.setContactPerson(contactPerson);
						createPersonObs(masterCardRecord, masterCardRecordMappingLog, executionCycle,
								personsForObsCreated);

						createRelationship(masterCardRecord, masterCardRecordMappingLog, executionCycle);

						if (!indexCasesInvolved.contains(indexPatient)) {
							indexCasesInvolved.add(indexPatient);
						}
					} else {
						// Do nothing. The relationship already exists
						// ToDo: How about obs? Update them if mastercard is newer and has different
						// value?
						masterCardRecordMappingLog.setMigrationResult("Mastercard Relationship mapping: Relationship already exists between index case ["+masterCardRecord.getIndexPatient().getId()+
								"] and contact person ["+contactPerson.getPersonId()+"]");
                        log.info("Mastercard Relationship mapping: Relationship already exists between index case ["+masterCardRecord.getIndexPatient().getId()+
                                "] and contact person ["+contactPerson.getPersonId()+"]");
					}
				}
			} else {
				// Mastercard record does not have NID
				// Search for existing related person with matching names as the mastercard.
				// If not found, create a new person record, Create a relationship with index
				// case, and then create Obs.
				Person contactPerson = searchForExistingRelatedPersonWithSimilarNames(masterCardRecord);
				if (contactPerson == null) {
					contactPerson = createNewRelationshipPerson(masterCardRecord, masterCardRecordMappingLog,
							executionCycle);
					masterCardRecord.setContactPerson(contactPerson);
					createPersonObs(masterCardRecord, masterCardRecordMappingLog, executionCycle, personsForObsCreated);
					createRelationship(masterCardRecord, masterCardRecordMappingLog, executionCycle);

					if (!indexCasesInvolved.contains(indexPatient)) {
						indexCasesInvolved.add(indexPatient);
					}
				} else {

					// Do nothing. The relationship already exists
					// ToDo: How about obs? Update them if mastercard is newer and has different
					// value?
					masterCardRecordMappingLog.setMigrationResult("Mastercard Relationship mapping: Relationship already exists between index case ["+masterCardRecord.getIndexPatient().getId()+
							"] and contact person ["+contactPerson.getPersonId()+"]");
                    log.info("Mastercard Relationship mapping: Relationship already exists between index case ["+masterCardRecord.getIndexPatient().getId()+
                            "] and contact person ["+contactPerson.getPersonId()+"]");
				}
			}
		}

		masterCardRecordMappingLog.setIndexPatient(indexPatient);

		executionCycle.setIndexCasesInvolved(indexCasesInvolved.size());
		executionCycle.setPersonsForObsCreated(personsForObsCreated.size());
		executionCycle.setLatestRecordCreationDate(this.masterCardObsGroup.getCreationDate());


		return masterCardRecordMappingLog;

	}

	private boolean isNonTestedPositiveParceiro(MasterCardRecord masterCardRecord) {
		return masterCardRecord.getRelationshipTypeConceptId() == 1921 && masterCardRecord.getHivTest() != 703;
	}

	private boolean isBelow15AndNonTestedPositiveFilho(MasterCardRecord masterCardRecord) {
		Calendar contactBirthDate = Calendar.getInstance();
		contactBirthDate.setTime(masterCardRecord.getObsDateTime());
		contactBirthDate.add(Calendar.YEAR, -1 * masterCardRecord.getAge());
		contactBirthDate.set(Calendar.MONTH, 0);
		contactBirthDate.set(Calendar.DAY_OF_MONTH, 1);

		return masterCardRecord.getRelationshipTypeConceptId() == 23707 && contactBirthDate.after(cutOffBirthDate)
				&& masterCardRecord.getHivTest() != 703 && masterCardRecord.getHivTest() != 664;
	}

	private boolean isParent(MasterCardRecord masterCardRecord) {
		return masterCardRecord.getRelationshipTypeConceptId() == 970
				|| masterCardRecord.getRelationshipTypeConceptId() == 971;
	}

	private void createPersonObs(MasterCardRecord masterCardRecord,
			MasterCardRecordMappingLog masterCardRecordMappingLog, MasterCardRecordMappingExecutionCycle executionCycle,
			Set<Person> personsForObsCreated) {

		Person contactPerson = masterCardRecord.getContactPerson();

		if (masterCardRecord.getHivTest() > 0) {
			Obs obs = new Obs();
			obs.setConcept(hivTestConcept);

			Concept hivTestValueCodedConcept = Context.getConceptService().getConcept(masterCardRecord.getHivTest());
			obs.setValueCoded(hivTestValueCodedConcept);
			obs.setPerson(contactPerson);
			obs.setObsDatetime(masterCardRecord.getObsDateTime());

			Context.getObsService().saveObs(obs, "mastercard mapping");
			masterCardRecordMappingLog.setPersonObsWasCreated(true);
			executionCycle.setObsCreated(executionCycle.getObsCreated() + 1);
			personsForObsCreated.add(contactPerson);
		}

		if (masterCardRecord.getHivCare() > 0) {
			Obs obs = new Obs();
			obs.setConcept(hivCareConcept);

			Concept hivCareValueCodedConcept = Context.getConceptService().getConcept(masterCardRecord.getHivCare());
			obs.setValueCoded(hivCareValueCodedConcept);
			obs.setPerson(contactPerson);
			obs.setObsDatetime(masterCardRecord.getObsDateTime());

			Context.getObsService().saveObs(obs, "mastercard mapping");
			masterCardRecordMappingLog.setPersonObsWasCreated(true);
			executionCycle.setObsCreated(executionCycle.getObsCreated() + 1);
			personsForObsCreated.add(contactPerson);
		}
		if (masterCardRecord.getChildAtRiskTreatment() > 0) {
			Obs obs = new Obs();
			obs.setConcept(childAtRiskTreatmentConcept);

			Concept childAtRiskTreatmentValueCodedConcept = Context.getConceptService()
					.getConcept(masterCardRecord.getChildAtRiskTreatment());
			obs.setValueCoded(childAtRiskTreatmentValueCodedConcept);
			obs.setPerson(contactPerson);
			obs.setObsDatetime(masterCardRecord.getObsDateTime());

			Context.getObsService().saveObs(obs, "mastercard mapping");
			masterCardRecordMappingLog.setPersonObsWasCreated(true);
			executionCycle.setObsCreated(executionCycle.getObsCreated() + 1);
			personsForObsCreated.add(contactPerson);
		}
		if (StringUtils.isNotBlank(masterCardRecord.getNid())) {
			Obs obs = new Obs();
			obs.setConcept(nidConcept);
			obs.setValueText(masterCardRecord.getNid());
			obs.setPerson(contactPerson);
			obs.setObsDatetime(masterCardRecord.getObsDateTime());

			Context.getObsService().saveObs(obs, "mastercard mapping");
			masterCardRecordMappingLog.setPersonObsWasCreated(true);
			executionCycle.setObsCreated(executionCycle.getObsCreated() + 1);
			personsForObsCreated.add(contactPerson);
		}
	}

	private void createRelationship(MasterCardRecord masterCardRecord,
			MasterCardRecordMappingLog masterCardRecordMappingLog,
			MasterCardRecordMappingExecutionCycle executionCycle) {
		Relationship relationship = new Relationship();
		int relationshipConcept = masterCardRecord.getRelationshipTypeConceptId();

		if (relationshipConcept == 970 || relationshipConcept == 971 || relationshipConcept == 973) {
			relationship.setPersonA(masterCardRecord.getContactPerson());
			relationship.setPersonB(masterCardRecord.getIndexPatient());
		} else {
			relationship.setPersonA(masterCardRecord.getIndexPatient());
			relationship.setPersonB(masterCardRecord.getContactPerson());
		}

		int relationshipTypeId = getRelationshipTypeId(relationshipConcept);

		RelationshipType relationshipType = Context.getPersonService().getRelationshipType(relationshipTypeId);
		relationship.setRelationshipType(relationshipType);

		relationship = Context.getPersonService().saveRelationship(relationship);
		masterCardRecordMappingLog.setRelationship(relationship);
		executionCycle.setRelationshipsCreated(executionCycle.getRelationshipsCreated() + 1);
	}

	private int getRelationshipTypeId(int relationshipConcept) {
		int relationshipTypeId = 22;

		if (relationshipConcept == 970 || relationshipConcept == 971) {
			relationshipTypeId = 3;
		} else if (relationshipConcept == 23707) {
			relationshipTypeId = 3;
		} else if (relationshipConcept == 975) {
			relationshipTypeId = 4;
		} else if (relationshipConcept == 972) {
			relationshipTypeId = 7;
		} else if (relationshipConcept == 973) {
			relationshipTypeId = 10;
		} else if (relationshipConcept == 23708) {
			relationshipTypeId = 10;
		} else if (relationshipConcept == 1930) {
			relationshipTypeId = 16;
		} else if (relationshipConcept == 2036) {
			relationshipTypeId = 17;
		} else if (relationshipConcept == 23705) {
			relationshipTypeId = 18;
		} else if (relationshipConcept == 23706) {
			relationshipTypeId = 19;
		} else if (relationshipConcept == 2034) {
			relationshipTypeId = 20;
		} else if (relationshipConcept == 1921) {
			relationshipTypeId = 21;
		}
		return relationshipTypeId;
	}

	private Person searchForExistingRelatedPersonWithSimilarNames(MasterCardRecord masterCardRecord) {
		if (masterCardRecord.getIndexPatient() != null) {
			List<Person> similarNamedPersons = Context.getPersonService().getPeople(masterCardRecord.getName(), false);

			for (Person person : similarNamedPersons) {

				List<Relationship> relationships = Context.getPersonService().getRelationshipsByPerson(person);

				for (Relationship relationship : relationships) {
					try {
						if (relationship.getPersonA().getId() == masterCardRecord.getIndexPatient().getId()
								|| relationship.getPersonB().getId() == masterCardRecord.getIndexPatient().getId()) {

							return person;
						}
					} catch (Exception e) {
						log.error("Error while comparing relationships", e);
					}
				}
			}
		}
		return null;
	}

	private Person createNewRelationshipPerson(MasterCardRecord masterCardRecord,
			MasterCardRecordMappingLog masterCardRecordMappingLog,
			MasterCardRecordMappingExecutionCycle executionCycle) {
		Person person = new Person();
		int age;

		if (masterCardRecord.getAge() > 0 && masterCardRecord.getAge() <= 100) {
			age = masterCardRecord.getAge();
			Calendar birthdateCalendar = Calendar.getInstance();
			birthdateCalendar.setTime(masterCardRecord.getObsDateTime());
			birthdateCalendar.add(Calendar.YEAR, -1 * age);
			birthdateCalendar.set(Calendar.MONTH, 0);
			birthdateCalendar.set(Calendar.DAY_OF_MONTH, 1);

			person.setBirthdate(birthdateCalendar.getTime());
			person.setBirthdateEstimated(true);
		}

		PersonName personName = createPersonNameFromMastercardNameField(masterCardRecord);
		if (personName != null) {
			person.addName(personName);
			person = Context.getPersonService().savePerson(person);

			if (personName.getFamilyName().equals("FamilyName") || personName.getGivenName().equals("GivenName")) {
				if (personName.getFamilyName().equals("FamilyName"))
					personName.setFamilyName(personName.getFamilyName() + person.getId());

				if (personName.getGivenName().equals("GivenName"))
					personName.setGivenName(personName.getGivenName() + person.getId());
				personName.setPerson(person);
				Context.getPersonService().savePersonName(personName);
			}

			masterCardRecordMappingLog.setContactPersonWasCreated(true);
			masterCardRecordMappingLog.setContactPerson(person);
			executionCycle.setPersonsCreated(executionCycle.getPersonsCreated() + 1);
		}

		return person;
	}

	private PersonName createPersonNameFromMastercardNameField(MasterCardRecord masterCardRecord) {
		String givenName = null;
		String middleName = null;
		String familyName = null;
		PersonName personName = new PersonName();
		if (!isEmpty(masterCardRecord.getName())) {
			String str = masterCardRecord.getName().trim().replaceAll(",", "");
			String[] names = str.split("\\s+");
			if (names.length > 0) {
				familyName = names[0];
			}
			if (names.length > 2) {
				givenName = names[2];
				middleName = names[1];
			} else if (names.length > 1) {
				givenName = names[1];
			} else {
				givenName = "GivenName";
			}

			personName.setFamilyName(familyName);
			personName.setGivenName(givenName);
			personName.setMiddleName(middleName);
		} else {
			personName.setFamilyName("FamilyName");
			personName.setGivenName("GivenName");
		}
		return personName;
	}

}
