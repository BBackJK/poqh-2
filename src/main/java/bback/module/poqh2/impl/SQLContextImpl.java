package bback.module.poqh2.impl;

import bback.module.poqh2.*;
import bback.module.poqh2.Column;
import bback.module.poqh2.Table;
import bback.module.poqh2.logger.Log;
import bback.module.poqh2.logger.LogFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

//@Slf4j
public class SQLContextImpl<T> implements SQLContext<T> {

    private static final Log LOGGER = LogFactory.getLog(SQLContext.class);

    private final ObjectMapper om;
    private final Class<T> resultType;
    private final EntityManager entityManager;
    private final List<Column> selectColumnList = new ArrayList<>();
    private final List<Predictor> whereList = new ArrayList<>();
    private From from;
    private Select select;
    private boolean isJpql;

    public SQLContextImpl(EntityManager entityManager, Class<T> resultType, ObjectMapper om) {
        this.entityManager = entityManager;
        this.resultType = resultType;
        this.om = om;
    }

    @Override
    public String toQuery() {
        StringBuilder sb = new StringBuilder();
        this.select.setSelectColumnList(this.selectColumnList);
        sb.append(this.select.toQuery());
        sb.append("\n");

        if (hasTable()) {
            sb.append(from.toQuery());
            sb.append("\n");
        }

        if (hasWhere()) {
            sb.append(" where ");
            this.whereList.forEach(w -> {
                sb.append(w.toQuery());
                sb.append("\n");
            });
        }

        return sb.toString();
    }



    @Override
    public From FROM(Table table) {
        if (!table.hasAlias()) {
            table.AS(1);
        }
        this.isJpql = table.isJpql();
        this.select = table.isJpql() ? new JpqlSelect(this.resultType) : new NativeSelect(this.resultType);
        this.from = new FromImpl(table);
        return this.from;
    }

    @Override
    public void SELECT(Column... columns) {
        this.selectColumnList.addAll(Arrays.stream(columns).collect(Collectors.toList()));
    }

    @Override
    public List<T> toResultList() throws PersistenceException {
        String query = this.toQuery();

        QueryResultHandler<T> resultHandler = this.isJpql
                ? new JpqlResultHandler<>(this.entityManager, this.resultType)
                : new NativeResultHandler<>(this.entityManager, this.resultType, this.om, this.selectColumnList);

        try {
            return resultHandler.list(query);
        } catch (IllegalStateException | PersistenceException e) {
            LOGGER.error("error query :: " + query);
            LOGGER.error(e.getMessage());
            throw new PersistenceException(e);
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public Optional<T> toResult() throws PersistenceException {
        String query = this.toQuery();

        QueryResultHandler<T> resultHandler = this.isJpql
                ? new JpqlResultHandler<>(this.entityManager, this.resultType)
                : new NativeResultHandler<>(this.entityManager, this.resultType, this.om, this.selectColumnList);

        try {
            return resultHandler.detail(query);
        } catch (IllegalStateException | PersistenceException e) {
            LOGGER.error("error query :: " + query);
            LOGGER.error(e.getMessage());
            throw new PersistenceException(e);
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public void WHERE(Predictor... predictors) {
        this.whereList.addAll(Arrays.asList(predictors));
    }

    private boolean hasTable() {
        return this.from != null;
    }

    private boolean hasWhere() {
        return !this.whereList.isEmpty();
    }
}
