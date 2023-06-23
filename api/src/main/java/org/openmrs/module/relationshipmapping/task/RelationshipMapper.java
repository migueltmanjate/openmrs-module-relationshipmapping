package org.openmrs.module.relationshipmapping.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.relationshipmapping.api.service.MasterCardRecordService;
import org.openmrs.module.relationshipmapping.model.MasterCardRecordMappingExecutionCycle;

import java.util.Date;

public class RelationshipMapper {
    private static RelationshipMapper relationshipMapper;
    private Log log = LogFactory.getLog(this.getClass());
    private RelationshipMapper(){
        super();
    }

    public static RelationshipMapper getInstance(){
        if(relationshipMapper == null){
            relationshipMapper = new RelationshipMapper();
        }
        return relationshipMapper;
    }

    public void processUnmappedMastercardRecords(){
        MasterCardRecordService service = Context.getService(MasterCardRecordService.class);
        service.processUnmappedMastercardRecords();
    }
}
