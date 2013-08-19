package com.jspxcms.core.domaindsl;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.jspxcms.core.domain.RoleNodeInfo;
import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;

import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QRoleNodeInfo is a Querydsl query type for RoleNodeInfo
 */

public class QRoleNodeInfo extends EntityPathBase<RoleNodeInfo> {

    private static final long serialVersionUID = -552502780;

    private static final PathInits INITS = PathInits.DIRECT;

    public static final QRoleNodeInfo roleNodeInfo = new QRoleNodeInfo("roleNodeInfo");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final QNode node;

    public final QRoleSite roleSite;

    public QRoleNodeInfo(String variable) {
        this(RoleNodeInfo.class, forVariable(variable), INITS);
    }

    @SuppressWarnings("all")
    public QRoleNodeInfo(Path<? extends RoleNodeInfo> path) {
        this((Class)path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QRoleNodeInfo(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QRoleNodeInfo(PathMetadata<?> metadata, PathInits inits) {
        this(RoleNodeInfo.class, metadata, inits);
    }

    public QRoleNodeInfo(Class<? extends RoleNodeInfo> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.node = inits.isInitialized("node") ? new QNode(forProperty("node"), inits.get("node")) : null;
        this.roleSite = inits.isInitialized("roleSite") ? new QRoleSite(forProperty("roleSite"), inits.get("roleSite")) : null;
    }

}

