package org.openmrs.module.relationshipmapping.api.db;

import org.openmrs.module.relationshipmapping.model.MasterCardRecordMappingExecutionCycle;
import org.openmrs.module.relationshipmapping.model.MasterCardRecordMappingLog;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MasterCardRecordMappingLogDao {

    @Transactional
    void save(MasterCardRecordMappingLog masterCardRecordMappingLog);

    @Transactional
    void saveExecutionCycle(MasterCardRecordMappingExecutionCycle executionCycle);

    @Transactional
    List<MasterCardRecordMappingExecutionCycle> getAllExecutionCycles();

    @Transactional
    MasterCardRecordMappingExecutionCycle getLatestExecutionCycle();
    List<MasterCardRecordMappingLog> getMasterCardRecordlogsByContactPerson(int personId);
}
