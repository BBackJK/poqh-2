package bback.module.poqh2.impl;

import bback.module.poqh2.Predictor;
import bback.module.poqh2.exceptions.DMLValidationException;

public class And implements Predictor {

    private final Predictor predictorA;
    private final Predictor predictorB;

    public And(Predictor predictorA, Predictor predictorB) {
        this.predictorA = predictorA;
        this.predictorB = predictorB;
    }


    @Override
    public String toQuery() {
        this.validationQuery();
        return String.format("%s and %s", this.predictorA.toQuery(), this.predictorB.toQuery());
    }


    private void validationQuery() {
        if ( this.predictorB == null ) {
            throw new DMLValidationException(" Predictor is null exception ");
        }
    }

    @Override
    public boolean isConnector() {
        return true;
    }
}
