package org.openmrs.module.relationshipmapping.handler;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.User;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.exception.QueueProcessorException;
import org.openmrs.module.muzima.model.QueueData;
import org.openmrs.module.muzima.model.handler.QueueDataHandler;
import org.openmrs.module.muzima.utils.JsonUtils;
import org.openmrs.module.relationshipmapping.api.service.MasterCardRecordService;
import org.openmrs.module.relationshipmapping.model.MasterCardObsGroupMember;
import org.openmrs.module.relationshipmapping.model.MasterCardRecordMappingLog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Handler(supports = QueueData.class, order = 100)
public class MastercardCustomPayloadHandler  implements QueueDataHandler {
    public static final String DISCRIMINATOR_VALUE = "mastercard-record-mapper";

    private final Log log = LogFactory.getLog(MastercardCustomPayloadHandler.class);
    private QueueProcessorException queueProcessorException;
    private final List<Obs> obsToSave = new ArrayList<Obs>();
    private final List<Obs> obsToVoid = new ArrayList<Obs>();

    @Override
    public void process(QueueData queueData) throws QueueProcessorException {
        log.info("Processing demographics update form data: " + queueData.getUuid());
        try {
        if (validate(queueData)) {
            /*for(Obs obs:obsToSave) {
                Context.getObsService().saveObs(obs, "mastercard record update");
            }
            
            for(Obs obs:obsToVoid) {
                Context.getObsService().voidObs(obs, "mastercard record update");
            }*/
        }
    } catch (Exception e) {
        if (!e.getClass().equals(QueueProcessorException.class)) {
            log.error("Encountered Exception while processing mastercard record", e);
            queueProcessorException.addException(e);
        }
    } finally {
        if (queueProcessorException.anyExceptions()) {
            throw queueProcessorException;
        }
    }
}
    
    @Override
    public boolean accept(final QueueData queueData) {
        return StringUtils.equals(DISCRIMINATOR_VALUE, queueData.getDiscriminator());
    }

    @Override
    public boolean validate(QueueData queueData) {
        log.info("Processing custom mastercatd update payload: " + queueData.getUuid());
        queueProcessorException = new QueueProcessorException();
        try {
            obsToVoid.clear();
            obsToSave.clear();
            String payload = queueData.getPayload();
            String personUuid = getCandidatePersonUuidFromPayload(payload);
            Person person = Context.getPersonService().getPersonByUuid(personUuid);
            if(person != null){
                MasterCardRecordService masterCardRecordService = Context.getService(MasterCardRecordService.class);
                List<MasterCardRecordMappingLog> mappingLogList = masterCardRecordService.getMasterCardRecordlogsByContactPerson(person.getPersonId());
                for(MasterCardRecordMappingLog log:mappingLogList){
                    Obs mastercardGroupObs = null;
                    Concept contactNameConcept = Context.getConceptService().getConcept(23778);
                    if(log.getContactNameObsId()!= null && log.getContactNameObsId()>0){
                        Obs contactNameObs = Context.getObsService().getObs(log.getContactNameObsId());
                        mastercardGroupObs = contactNameObs.getObsGroup();
                    } else if(log.getRelationshipObsId()!= null && log.getRelationshipObsId()>0){
                        Obs relationshipObs = Context.getObsService().getObs(log.getRelationshipObsId());
                        mastercardGroupObs = relationshipObs.getObsGroup();
                    } else if(log.getHivCareObsId()!= null && log.getHivCareObsId()>0){
                        Obs hivCareObs = Context.getObsService().getObs(log.getHivCareObsId());
                        mastercardGroupObs = hivCareObs.getObsGroup();
                    } else if(log.getHivTestObsId()!= null && log.getHivTestObsId()>0){
                        Obs hivTestObs = Context.getObsService().getObs(log.getHivCareObsId());
                        mastercardGroupObs = hivTestObs.getObsGroup();
                    } else if(log.getChildAtRiskTreatmentObsId()!= null && log.getChildAtRiskTreatmentObsId()>0){
                        Obs hivTestObs = Context.getObsService().getObs(log.getChildAtRiskTreatmentObsId());
                        mastercardGroupObs = hivTestObs.getObsGroup();
                    }
                    
                    if(mastercardGroupObs != null) {
                        List<MasterCardObsGroupMember> groupMembers = masterCardRecordService.getMasterCardObsGroupMembers(mastercardGroupObs.getObsId());

                        PersonName updatedPersonName = getUpdatedPersonNameFromPayload(payload);
                        String combinedNameString = null;
                        if (StringUtils.isNotBlank(updatedPersonName.getFamilyName())) {
                            combinedNameString += updatedPersonName.getFamilyName();
                        }
                        if (StringUtils.isNotBlank(updatedPersonName.getMiddleName())) {
                            combinedNameString += " " + updatedPersonName.getMiddleName();
                        }
                        if (StringUtils.isNotBlank(updatedPersonName.getGivenName())) {
                            combinedNameString += " " + updatedPersonName.getGivenName();
                        }

                        if (StringUtils.isNotBlank(combinedNameString)) {
                            Obs obs = new Obs();
                            obs.setConcept(contactNameConcept);
                            obs.setObsGroup(mastercardGroupObs);
                            obs.setPerson(person);
                            obs.setValueText(combinedNameString);
                            obsToSave.add(obs);

                            for (MasterCardObsGroupMember member : groupMembers) {
                                if (member.getConceptId() == 23778) {
                                    Obs oldObs = Context.getObsService().getObs(member.getObsId());
                                    obsToVoid.add(oldObs);
                                }
                            }
                        }

                        String hivTest = JsonUtils.readAsString(payload, "$['observation']['23779^Teste de HIV^99DCT']");
                        if (StringUtils.isNotBlank(hivTest)) {
                            String[] valueCodedElements = StringUtils.split(hivTest, "\\^");
                            if (valueCodedElements.length > 1) {
                                Concept valueCodedConcept = getConceptByUuidOrId(valueCodedElements[0]);
                                Concept hivTestConcept = Context.getConceptService().getConcept(23779);
                                Obs obs = new Obs();
                                obs.setConcept(hivTestConcept);
                                obs.setObsGroup(mastercardGroupObs);
                                obs.setPerson(person);
                                obs.setValueCoded(valueCodedConcept);
                                obsToSave.add(obs);

                                for (MasterCardObsGroupMember member : groupMembers) {
                                    if (member.getConceptId() == 23779) {
                                        Obs oldObs = Context.getObsService().getObs(member.getObsId());
                                        obsToVoid.add(oldObs);
                                    }
                                }
                            }
                        }

                        String hivCare = JsonUtils.readAsString(payload, "$['observation']['23780^Cuidados HIV^99DCT']");
                        if (StringUtils.isNotBlank(hivCare)) {
                            String[] valueCodedElements = StringUtils.split(hivCare, "\\^");
                            if (valueCodedElements.length > 1) {
                                Concept valueCodedConcept = getConceptByUuidOrId(valueCodedElements[0]);
                                Concept hivCareConcept = Context.getConceptService().getConcept(23780);
                                Obs obs = new Obs();
                                obs.setConcept(hivCareConcept);
                                obs.setObsGroup(mastercardGroupObs);
                                obs.setPerson(person);
                                obs.setValueCoded(valueCodedConcept);
                                obsToSave.add(obs);

                                for (MasterCardObsGroupMember member : groupMembers) {
                                    if (member.getConceptId() == 23780) {
                                        Obs oldObs = Context.getObsService().getObs(member.getObsId());
                                        obsToVoid.add(oldObs);
                                    }
                                }
                            }
                        }

                        String tratementoCCR = JsonUtils.readAsString(payload, "$['observation']['1885^TRATEMENTO (CCR)^99DCT']");
                        if (StringUtils.isNotBlank(tratementoCCR)) {
                            String[] valueCodedElements = StringUtils.split(tratementoCCR, "\\^");
                            if (valueCodedElements.length > 1) {
                                Concept valueCodedConcept = getConceptByUuidOrId(valueCodedElements[0]);
                                Concept tratementoCCRConcept = Context.getConceptService().getConcept(1885);
                                Obs obs = new Obs();
                                obs.setConcept(tratementoCCRConcept);
                                obs.setObsGroup(mastercardGroupObs);
                                obs.setPerson(person);
                                obs.setValueCoded(valueCodedConcept);
                                obsToSave.add(obs);

                                for (MasterCardObsGroupMember member : groupMembers) {
                                    if (member.getConceptId() == 1885) {
                                        Obs oldObs = Context.getObsService().getObs(member.getObsId());
                                        obsToVoid.add(oldObs);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            String userString = JsonUtils.readAsString(payload, "$['encounter']['encounter.user_system_id']");
            User user = Context.getUserService().getUserByUsername(userString);
            if(user == null) {
                queueProcessorException.addException(new Exception("Unable to find user using the User Id: " + userString));
            }

            String deviceTimeZone = JsonUtils.readAsString(payload, "$['encounter']['encounter.device_time_zone']");
            DateFormat dateTimeFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            Date obsDatetime = readAsDateTime(payload, "$['encounter']['encounter.encounter_datetime']",dateTimeFormat,deviceTimeZone);

            for(Obs obs:obsToSave){
                if(obs.getObsDatetime() == null){
                    obs.setObsDatetime(obsDatetime);
                }
                obs.setCreator(user);
            }
            
            return true;
        } catch (Exception e) {
            log.error("Encountered Exception while validating mastercard record", e);
            queueProcessorException.addException(e);
            return false;
        } finally {
            if (queueProcessorException.anyExceptions()) {
                throw queueProcessorException;
            }
        }
    }
    private PersonName getUpdatedPersonNameFromPayload(String payload){
        PersonName personName = new PersonName();
        String givenName = JsonUtils.readAsString(payload, "$['demographicsupdate']['demographicsupdate.given_name']");
        if(StringUtils.isNotBlank(givenName)){
            personName.setGivenName(givenName);
        }
        String familyName = JsonUtils.readAsString(payload, "$['demographicsupdate']['demographicsupdate.family_name']");
        if(StringUtils.isNotBlank(familyName)){
            personName.setFamilyName(familyName);
        }

        String middleName= JsonUtils.readAsString(payload, "$['demographicsupdate']['demographicsupdate.middle_name']");
        if(StringUtils.isNotBlank(middleName)){
            personName.setMiddleName(middleName);
        }

        return personName;
    }
    
    private Concept getConceptByUuidOrId(String uuidOrId){
        Concept concept;
        if (StringUtils.isNumeric(uuidOrId)) {
            int conceptId = Integer.parseInt(uuidOrId);
            concept = Context.getConceptService().getConcept(conceptId);
        } else {
            concept = Context.getConceptService().getConceptByUuid(uuidOrId);
        }
        return concept;
    }
    @Override
    public String getDiscriminator() {
        return DISCRIMINATOR_VALUE;
    }

    private String getCandidatePersonUuidFromPayload(String payload){
        return JsonUtils.readAsString(payload, "$['patient']['patient.uuid']");
    }

    private Date readAsDateTime(final String jsonObject, final String path, final DateFormat datetimeFormat, final String jsonPayloadTimezone) {
        Date returnedDate = null;
        try {
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            String dateAsString = JsonUtils.readAsString(jsonObject, path);
            if(jsonPayloadTimezone != null) {
                if(dateAsString.length()==10){
                    returnedDate = dateFormat.parse(dateAsString);
                }else{
                    datetimeFormat.setTimeZone(TimeZone.getTimeZone(jsonPayloadTimezone));
                    returnedDate = datetimeFormat.parse(dateAsString);
                }
            }else{
                if(dateAsString.length()==10){
                    returnedDate = dateFormat.parse(dateAsString);
                } else {
                    returnedDate = datetimeFormat.parse(dateAsString);
                }
            }
        } catch (Exception e) {
            log.error("Unable to create date value from path: " + path);
        }
        return returnedDate;
    }
}
