package bback.module.poqh2;


import bback.module.poqh2.impl.JpqlTable;

public interface JPQL extends SQL {

    static Table TABLE(Class<?> entityType) {
        return TABLE(entityType, null);
    }

    static Table TABLE(Class<?> entityType, String alias) {
        return new JpqlTable(entityType, alias);
    }
}
