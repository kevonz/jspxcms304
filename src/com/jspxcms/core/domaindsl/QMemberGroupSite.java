package com.jspxcms.core.domaindsl;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.jspxcms.core.domain.MemberGroupSite;
import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;

import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QMemberGroupSite is a Querydsl query type for MemberGroupSite
 */

public class QMemberGroupSite extends EntityPathBase<MemberGroupSite> {

    private static final long serialVersionUID = -1795632498;

    private static final PathInits INITS = PathInits.DIRECT;

    public static final QMemberGroupSite memberGroupSite = new QMemberGroupSite("memberGroupSite");

    public final BooleanPath allContri = createBoolean("allContri");

    public final BooleanPath allExemption = createBoolean("allExemption");

    public final BooleanPath allView = createBoolean("allView");

    public final QMemberGroup group;

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final QSite site;

    public QMemberGroupSite(String variable) {
        this(MemberGroupSite.class, forVariable(variable), INITS);
    }

    @SuppressWarnings("all")
    public QMemberGroupSite(Path<? extends MemberGroupSite> path) {
        this((Class)path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QMemberGroupSite(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QMemberGroupSite(PathMetadata<?> metadata, PathInits inits) {
        this(MemberGroupSite.class, metadata, inits);
    }

    public QMemberGroupSite(Class<? extends MemberGroupSite> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.group = inits.isInitialized("group") ? new QMemberGroup(forProperty("group")) : null;
        this.site = inits.isInitialized("site") ? new QSite(forProperty("site"), inits.get("site")) : null;
    }

}

