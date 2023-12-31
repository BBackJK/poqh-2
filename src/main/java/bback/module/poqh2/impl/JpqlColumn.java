package bback.module.poqh2.impl;

import bback.module.poqh2.Table;
import bback.module.poqh2.utils.Strings;

class JpqlColumn extends AbstractPredictorColumn {

    private final Table table;
    private final String field;
    private final String alias;

    public JpqlColumn(Table table, String field, String alias) {
        this.table = table;
        this.field = field;
        this.alias = alias;
    }

    @Override
    public String toQuery() {
        return String.format("%s.%s", this.table.getAlias(), Strings.toCamel(field));
    }

    @Override
    public String getAttr() {
        return hasAlias() ? this.alias : this.field;
    }

    @Override
    public boolean hasAlias() {
        return this.alias != null && !this.alias.isEmpty();
    }
}
