package bback.module.poqh2.impl;

import bback.module.poqh2.From;
import bback.module.poqh2.Join;
import bback.module.poqh2.Table;
import bback.module.poqh2.exceptions.DMLValidationException;
import bback.module.poqh2.utils.Objects;


import javax.persistence.criteria.JoinType;
import java.util.ArrayList;
import java.util.List;

class FromImpl implements From {

    private final Table root;
    private final List<Join> joinList = new ArrayList<>();

    public FromImpl(Table root) {
        this.root = root;
    }


    @Override
    public String toQuery() {
        if (root == null) {
            throw new DMLValidationException(" root table is null ");
        }
        StringBuilder sb = new StringBuilder(" from ");
        sb.append(this.root.toQuery());
        sb.append(" ");
        sb.append(this.root.getAlias());

        if (!this.joinList.isEmpty()) {
            sb.append("\n");
            this.joinList.forEach(j -> {
                sb.append(j.toQuery());
                sb.append("\n");
            });
        }

        return sb.toString();
    }

    @Override
    public Join JOIN(Table joinTable, JoinType joinType) {
        if ( Objects.orEmpty( joinTable, joinType ) ) {
            throw new DMLValidationException("join table or join type is null.");
        }

        Join join = null;
        switch (joinType) {
            case LEFT:
                join = new LeftJoin(this, joinTable);
                break;
            case RIGHT:
                join = new RightJoin(this, joinTable);
                break;
            default:
                join = new InnerJoin(this, joinTable);
                break;
        }
        this.joinList.add(join);

        if ( !joinTable.hasAlias() ) {
            int joinCount = this.joinList.size();
            joinTable.AS(joinCount + 1);
        }

        return join;
    }

    @Override
    public Table getRoot() {
        return this.root;
    }


}
