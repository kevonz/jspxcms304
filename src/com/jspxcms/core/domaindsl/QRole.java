package com.jspxcms.core.domaindsl;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.jspxcms.core.domain.Role;
import com.jspxcms.core.domain.RoleSite;
import com.jspxcms.core.domain.UserRole;
import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;

import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QRole is a Querydsl query type for Role
 */

public class QRole extends EntityPathBase<Role> {

    private static final long serialVersionUID = 719762452;

    private static final PathInits INITS = PathInits.DIRECT;

    public static final QRole role = new QRole("role");

    public final StringPath description = createString("description");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath name = createString("name");

    public final SetPath<RoleSite, QRoleSite> roleSites = this.<RoleSite, QRoleSite>createSet("roleSites", RoleSite.class, QRoleSite.class, PathInits.DIRECT);

    public final NumberPath<Integer> seq = createNumber("seq", Integer.class);

    public final QSite site;

    public final SetPath<UserRole, QUserRole> userRoles = this.<UserRole, QUserRole>createSet("userRoles", UserRole.class, QUserRole.class, PathInits.DIRECT);

    public QRole(String variable) {
        this(Role.class, forVariable(variable), INITS);
    }

    @SuppressWarnings("all")
    public QRole(Path<? extends Role> path) {
        this((Class)path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QRole(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QRole(PathMetadata<?> metadata, PathInits inits) {
        this(Role.class, metadata, inits);
    }

    public QRole(Class<? extends Role> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.site = inits.isInitialized("site") ? new QSite(forProperty("site"), inits.get("site")) : null;
    }

}

