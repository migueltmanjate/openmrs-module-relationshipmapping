package org.openmrs.module.relationshipmapping.api.db;

import org.openmrs.module.relationshipmapping.model.MasterCardObsGroup;
import org.openmrs.module.relationshipmapping.model.MasterCardObsGroupMember;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public interface MasterCardRecordDao {
    @Transactional
    List<MasterCardObsGroup> getNewMasterCardObsGroups(final Date previousExecutionDate);

    @Transactional
    List<MasterCardObsGroupMember> getMasterCardObsGroupMembers(final int obsGroupId);
}
