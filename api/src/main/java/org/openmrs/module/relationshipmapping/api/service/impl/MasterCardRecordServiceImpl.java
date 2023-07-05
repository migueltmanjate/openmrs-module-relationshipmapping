package org.openmrs.module.relationshipmapping.api.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.relationshipmapping.api.db.MasterCardRecordDao;
import org.openmrs.module.relationshipmapping.api.db.MasterCardRecordMappingLogDao;
import org.openmrs.module.relationshipmapping.api.service.MasterCardRecordService;
import org.openmrs.module.relationshipmapping.model.MasterCardObsGroup;
import org.openmrs.module.relationshipmapping.model.MasterCardObsGroupMember;
import org.openmrs.module.relationshipmapping.model.MasterCardPersonGenerator;
import org.openmrs.module.relationshipmapping.model.MasterCardRecordMappingExecutionCycle;
import org.openmrs.module.relationshipmapping.model.MasterCardRecordMappingLog;

public class MasterCardRecordServiceImpl extends BaseOpenmrsService implements MasterCardRecordService {
	private Log log = LogFactory.getLog(this.getClass());
	private static boolean isTaskRunning = false;
	private MasterCardRecordDao masterCardRecordDao;
	private MasterCardRecordMappingLogDao masterCardRecordMappingLogDao;

	public MasterCardRecordDao getMasterCardRecordDao() {
		return masterCardRecordDao;
	}

	public void setMasterCardRecordDao(MasterCardRecordDao masterCardRecordDao) {
		this.masterCardRecordDao = masterCardRecordDao;
	}

	public MasterCardRecordMappingLogDao getMasterCardRecordMappingLogDao() {
		return masterCardRecordMappingLogDao;
	}

	public void setMasterCardRecordMappingLogDao(MasterCardRecordMappingLogDao masterCardRecordMappingLogDao) {
		this.masterCardRecordMappingLogDao = masterCardRecordMappingLogDao;
	}

	public boolean isTaskRunning() {
		return isTaskRunning;
	}

	public void processUnmappedMastercardRecords() {
		if (isTaskRunning) {
			return;
		}
		isTaskRunning = true;
		try {
			log.info("Mastercard Relationship mapping task started.");

			MasterCardRecordMappingExecutionCycle executionCycle = new MasterCardRecordMappingExecutionCycle();

			List<MasterCardObsGroup> groups = masterCardRecordDao.getNewMasterCardObsGroups();
            log.info("Mastercard Relationship mapping found ["+groups.size()+"] groups.");
			if (groups != null && groups.size() > 0) {
				Collections.sort(groups);
				masterCardRecordMappingLogDao.saveExecutionCycle(executionCycle);

				List<MasterCardPersonGenerator> masterCardPersonGenerators = new ArrayList<MasterCardPersonGenerator>();

				for (MasterCardObsGroup masterCardObsGroup : groups) {
					masterCardPersonGenerators
							.add(new MasterCardPersonGenerator(masterCardObsGroup, masterCardRecordDao));
				}

				for (MasterCardPersonGenerator masterCardPersonGenerator : masterCardPersonGenerators) {
					MasterCardRecordMappingLog masterCardRecordMappingLog = new MasterCardRecordMappingLog();
					masterCardRecordMappingLog.setExecutionCycle(executionCycle);
					try {
						masterCardRecordMappingLog = masterCardPersonGenerator.migrate(executionCycle);
					} catch (Exception e) {
						masterCardRecordMappingLog
								.setMigrationResult("An error occurred during the migration process: " + e.getMessage());
					} finally {
						masterCardRecordMappingLogDao.save(masterCardRecordMappingLog);
						this.processUnmappedMastercardRecords();
					}
				}
            log.info("Mastercard Relationship mapping: Completed Relationship mapping session: "+executionCycle.toString());
			}
			executionCycle.setQueryEndTime(new Date());
			masterCardRecordMappingLogDao.saveExecutionCycle(executionCycle);
			log.info("Mastercard Relationship mapping task finished.");
		} finally {
			isTaskRunning = false;
		}
	}

	public MasterCardRecordMappingExecutionCycle getLatestExecutionCycle() {
		return masterCardRecordMappingLogDao.getLatestExecutionCycle();
	}

	public List<MasterCardRecordMappingExecutionCycle> getAllExecutionCycles() {
		return masterCardRecordMappingLogDao.getAllExecutionCycles();
	}

	@Override
	public List<MasterCardRecordMappingLog> getMasterCardRecordlogsByContactPerson(int personId) {
		return masterCardRecordMappingLogDao.getMasterCardRecordlogsByContactPerson(personId);
	}

	@Override
	public List<MasterCardObsGroupMember> getMasterCardObsGroupMembers(int obsGroupId) {
		return masterCardRecordDao.getMasterCardObsGroupMembers(obsGroupId);
	}
}
