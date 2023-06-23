package org.openmrs.module.relationshipmapping.task;

import org.openmrs.api.context.Context;
import org.openmrs.scheduler.tasks.AbstractTask;

public class RelationshipMappingTask extends AbstractTask{

    private RelationshipMapper processor;
    public RelationshipMappingTask(){
        this.processor = RelationshipMapper.getInstance();
    }
    /**
     * @see org.openmrs.scheduler.Task#execute()
     */
    @Override
    public void execute() {
        Context.openSession();
        processor.processUnmappedMastercardRecords();
        Context.closeSession();
    }
}
