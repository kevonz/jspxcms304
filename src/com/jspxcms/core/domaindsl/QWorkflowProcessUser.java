package com.jspxcms.core.domaindsl;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.jspxcms.core.domain.WorkflowProcessUser;
import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;

import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QWorkflowProcessUser is a Querydsl query type for WorkflowProcessUser
 */

public class QWorkflowProcessUser extends EntityPathBase<WorkflowProcessUser> {

    private static final long serialVersionUID = -765993411;

    private static final PathInits INITS = PathInits.DIRECT;

    public static final QWorkflowProcessUser workflowProcessUser = new QWorkflowProcessUser("workflowProcessUser");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final QWorkflowProcess process;

    public final QUser user;

    public QWorkflowProcessUser(String variable) {
        this(WorkflowProcessUser.class, forVariable(variable), INITS);
    }

    @SuppressWarnings("all")
    public QWorkflowProcessUser(Path<? extends WorkflowProcessUser> path) {
        this((Class)path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QWorkflowProcessUser(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QWorkflowProcessUser(PathMetadata<?> metadata, PathInits inits) {
        this(WorkflowProcessUser.class, metadata, inits);
    }

    public QWorkflowProcessUser(Class<? extends WorkflowProcessUser> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.process = inits.isInitialized("process") ? new QWorkflowProcess(forProperty("process"), inits.get("process")) : null;
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user"), inits.get("user")) : null;
    }

}

