package com.jspxcms.core.domaindsl;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.jspxcms.core.domain.RoleNodeNode;
import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;

import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QRoleNodeNode is a Querydsl query type for RoleNodeNode
 */

public class QRoleNodeNode extends EntityPathBase<RoleNodeNode> {

    private static final long serialVersionUID = -552352936;

    private static final PathInits INITS = PathInits.DIRECT;

    public static final QRoleNodeNode roleNodeNode = new QRoleNodeNode("roleNodeNode");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final QNode node;

    public final QRoleSite roleSite;

    public QRoleNodeNode(String variable) {
        this(RoleNodeNode.class, forVariable(variable), INITS);
    }

    @SuppressWarnings("all")
    public QRoleNodeNode(Path<? extends RoleNodeNode> path) {
        this((Class)path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QRoleNodeNode(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QRoleNodeNode(PathMetadata<?> metadata, PathInits inits) {
        this(RoleNodeNode.class, metadata, inits);
    }

    public QRoleNodeNode(Class<? extends RoleNodeNode> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.node = inits.isInitialized("node") ? new QNode(forProperty("node"), inits.get("node")) : null;
        this.roleSite = inits.isInitialized("roleSite") ? new QRoleSite(forProperty("roleSite"), inits.get("roleSite")) : null;
    }

}

