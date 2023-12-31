package bback.module.poqh2.impl;



import bback.module.poqh2.From;
import bback.module.poqh2.Join;
import bback.module.poqh2.Predictor;
import bback.module.poqh2.Table;
import bback.module.poqh2.exceptions.DMLValidationException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

abstract class AbstractJoin implements Join {

    protected final From from;
    protected final Table joinTable;
    protected final List<Predictor> onList = new ArrayList<>();

    protected AbstractJoin(From from, Table joinTable) {
        this.from = from;
        this.joinTable = joinTable;
    }

    protected abstract String getExpression();

    @Override
    public From ON(Predictor... predictors) {
        this.onList.addAll(Arrays.stream(predictors).collect(Collectors.toList()));
        return this.from;
    }

    @Override
    public Table getJoinTable() {
        return this.joinTable;
    }

    @Override
    public String toQuery() {
        this.validationQuery();
        StringBuilder sb = new StringBuilder();
        sb.append(this.getExpression());
        sb.append(this.joinTable.toQuery());
        sb.append(" ");
        sb.append(this.joinTable.getAlias());
        sb.append("\n\ton ");

        int onCount = this.onList.size();
        for (int i=0; i<onCount; i++) {
            int n = i+1;
            boolean isLast = n == onCount;
            Predictor predictor = this.onList.get(i);
            sb.append(predictor.toQuery());
            if (!isLast) sb.append("\n\t and ");
        }
        return sb.toString();
    }

    private void validationQuery() {
        if ( this.from == null || this.joinTable == null || this.onList.isEmpty() ) {
            throw new DMLValidationException(" join table empty or not used 'ON' keyword ");
        }
    }
}
