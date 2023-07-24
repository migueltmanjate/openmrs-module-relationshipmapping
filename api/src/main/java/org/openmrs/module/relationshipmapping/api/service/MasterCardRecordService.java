package org.openmrs.module.relationshipmapping.api.service;

import java.util.List;

import org.openmrs.api.OpenmrsService;
import org.openmrs.module.relationshipmapping.model.MasterCardObsGroupMember;
import org.openmrs.module.relationshipmapping.model.MasterCardRecordMappingExecutionCycle;
import org.openmrs.module.relationshipmapping.model.MasterCardRecordMappingLog;

public interface MasterCardRecordService extends OpenmrsService{
    boolean isTaskRunning();
    void processUnmappedMastercardRecords();
    MasterCardRecordMappingExecutionCycle getLatestExecutionCycle();
    List<MasterCardRecordMappingExecutionCycle> getAllExecutionCycles();
    List<MasterCardRecordMappingLog> getMasterCardRecordlogsByContactPerson(int personId);
    List<MasterCardObsGroupMember> getMasterCardObsGroupMembers(int obsGroupId);
}
