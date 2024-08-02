package org.openmrs.module.relationshipmapping.api.db.hibernate;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.relationshipmapping.api.db.MasterCardRecordDao;
import org.openmrs.module.relationshipmapping.model.MasterCardObsGroup;
import org.openmrs.module.relationshipmapping.model.MasterCardObsGroupMember;
import org.springframework.transaction.annotation.Transactional;

public class HibernateMasterCardRecordDao implements MasterCardRecordDao {
    protected DbSessionFactory sessionFactory;

    HibernateMasterCardRecordDao(DbSessionFactory dbSessionFactory){
        this.sessionFactory = dbSessionFactory;
    }

    @Transactional
    public List<MasterCardObsGroup> getNewMasterCardObsGroups() {
        Query query = sessionFactory.getCurrentSession().createSQLQuery(
                "SELECT obsGrupo.obs_id as obsId,obsGrupo.obs_datetime as obsDateTime,obsGrupo.person_id as indexCaseId,obsGrupo.date_created as creationDate, e.encounter_datetime as encounterDateTime " +
                        " FROM obs obsGrupo  " +
                        "       INNER JOIN obs b ON obsGrupo.person_id=b.person_id and b.voided=0  " +
                        "       INNER JOIN person p ON p.person_id = obsGrupo.person_id  " +
                        "       inner join encounter e on obsGrupo.encounter_id =e.encounter_id " +
                        "       inner join obs c ON obsGrupo.person_id=c.person_id and c.concept_id = 23778 and c.obs_group_id = obsGrupo.obs_id and c.voided=0 " +
                        "       inner join obs obsParentesco on obsParentesco.obs_group_id=obsGrupo.obs_id and obsParentesco.concept_id=23704 and obsParentesco.value_coded in (23707,971,970,1921) and obsParentesco.voided=0  " +
                        "       left join obs obsIdade on  obsIdade.obs_group_id=obsGrupo.obs_id and obsIdade.concept_id=23777 and obsIdade.voided=0  " +
                        "       left join obs obsTeste on  obsTeste.obs_group_id=obsGrupo.obs_id and obsTeste.concept_id=23779 and obsTeste.voided=0  " +
                        " WHERE obsGrupo.concept_id = 23782 AND obsGrupo.voided = 0   " +
                        " and e.encounter_type=53 and e.location_id = (select value_string from muzima_setting where property = 'Encounter.DefaultLocationId') " +
                        " AND b.concept_id=21155 AND b.value_coded IN (21154,6403) AND p.dead = 0  " +
                        " and (obsTeste.value_coded is null or obsTeste.value_coded<>703) and  " +
                        "         (  " +
                        "         (obsParentesco.value_coded<>23707) or  " +
                        "         (obsParentesco.value_coded=23707 and obsIdade.value_numeric<15) or  " +
                        "         (obsParentesco.value_coded=23707 and obsIdade.value_numeric is null)  " +
                        "                  ) and (obsTeste.value_coded is null or obsTeste.value_coded=1067)  " +
                        " AND not EXISTS (SELECT *   " +
                        "                 from mastercard_relationship_mapping_log mrml   " +
                        "                 where mrml.index_patient = obsGrupo.person_id   " +
                        "                       and mrml.contact_name_obs = c.obs_id)   " +
                        " GROUP BY obsId");
        query.setResultTransformer(Transformers.aliasToBean(MasterCardObsGroup.class));
        return query.list();
    }

    @Transactional
    public List<MasterCardObsGroupMember> getMasterCardObsGroupMembers(final int obsGroupId){
        Query query = sessionFactory.getCurrentSession().createSQLQuery(
                "SELECT obs_id as obsId, concept_id as conceptId,value_coded as valueCoded, value_numeric as valueNumeric," +
                        "value_text as valueText FROM obs WHERE obs_group_id = " + obsGroupId);
        query.setResultTransformer(Transformers.aliasToBean(MasterCardObsGroupMember.class));
        return query.list();
    }
}
