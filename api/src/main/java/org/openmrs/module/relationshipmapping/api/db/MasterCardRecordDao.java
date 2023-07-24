package org.openmrs.module.relationshipmapping.api.db;

import java.util.List;

import org.openmrs.module.relationshipmapping.model.MasterCardObsGroup;
import org.openmrs.module.relationshipmapping.model.MasterCardObsGroupMember;
import org.springframework.transaction.annotation.Transactional;

public interface MasterCardRecordDao {
    @Transactional
    List<MasterCardObsGroup> getNewMasterCardObsGroups();

    @Transactional
    List<MasterCardObsGroupMember> getMasterCardObsGroupMembers(final int obsGroupId);
}
