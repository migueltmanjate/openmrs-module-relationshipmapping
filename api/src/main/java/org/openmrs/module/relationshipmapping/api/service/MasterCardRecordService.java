package org.openmrs.module.relationshipmapping.api.service;

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.relationshipmapping.model.MasterCardObsGroupMember;
import org.openmrs.module.relationshipmapping.model.MasterCardRecordMappingExecutionCycle;
import org.openmrs.module.relationshipmapping.model.MasterCardRecordMappingLog;

import java.util.Date;
import java.util.List;

public interface MasterCardRecordService extends OpenmrsService{
    boolean isTaskRunning();
    void processUnmappedMastercardRecords();
    void mapMastercardRecords(Date previousExecutionDate);
    MasterCardRecordMappingExecutionCycle getLatestExecutionCycle();
    List<MasterCardRecordMappingExecutionCycle> getAllExecutionCycles();
    List<MasterCardRecordMappingLog> getMasterCardRecordlogsByContactPerson(int personId);
    List<MasterCardObsGroupMember> getMasterCardObsGroupMembers(int obsGroupId);
}
