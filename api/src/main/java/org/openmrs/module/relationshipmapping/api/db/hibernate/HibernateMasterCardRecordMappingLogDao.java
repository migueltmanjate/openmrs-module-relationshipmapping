package org.openmrs.module.relationshipmapping.api.db.hibernate;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.relationshipmapping.api.db.MasterCardRecordMappingLogDao;
import org.openmrs.module.relationshipmapping.model.MasterCardRecordMappingExecutionCycle;
import org.openmrs.module.relationshipmapping.model.MasterCardRecordMappingLog;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class HibernateMasterCardRecordMappingLogDao implements MasterCardRecordMappingLogDao {
    private DbSessionFactory dbSessionFactory;

    HibernateMasterCardRecordMappingLogDao(DbSessionFactory dbSessionFactory){
        this.dbSessionFactory = dbSessionFactory;
    }

    private DbSession session() {
        return dbSessionFactory.getCurrentSession();
    }

    @Transactional
    public void save(MasterCardRecordMappingLog masterCardRecordMappingLog){
        session().save(masterCardRecordMappingLog);
    }

    @Transactional
    public void saveExecutionCycle(MasterCardRecordMappingExecutionCycle executionCycle){
        session().saveOrUpdate(executionCycle);
    }

    @Transactional
    public List<MasterCardRecordMappingExecutionCycle> getAllExecutionCycles(){
        Criteria criteria = session().createCriteria(MasterCardRecordMappingExecutionCycle.class);
        criteria.addOrder(Order.desc("queryEndTime"));
        return criteria.list();
    }

    @Transactional
    public MasterCardRecordMappingExecutionCycle getLatestExecutionCycle(){
        return (MasterCardRecordMappingExecutionCycle) session().createCriteria(MasterCardRecordMappingExecutionCycle.class)
                .addOrder(Order.desc("latestRecordCreationDate"))
                .setMaxResults(1)
                .uniqueResult();
    }
    
    public List<MasterCardRecordMappingLog> getMasterCardRecordlogsByContactPerson(int personId){
        Criteria criteria = session().createCriteria(MasterCardRecordMappingLog.class)
                .add(Restrictions.eq("contactPerson.personId",personId));
        return criteria.list();
    }
}
