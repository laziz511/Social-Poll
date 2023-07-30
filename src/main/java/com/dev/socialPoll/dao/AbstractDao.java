package com.dev.socialPoll.dao;

import com.dev.socialPoll.dao.mapper.RowMapper;
import com.dev.socialPoll.entity.Identifiable;
import com.dev.socialPoll.exception.DaoException;

import java.util.List;
import java.util.Optional;

public abstract class AbstractDao<T extends Identifiable> extends AbstractQueryExecutor<T> implements Dao<T> {
    private final String tableName;

    public AbstractDao(RowMapper<T> rowMapper, String tableName) {
        super(rowMapper);
        this.tableName = tableName;
    }

    @Override
    public List<T> findAll() throws DaoException {
        String query = "SELECT * FROM " + tableName;
        return executeQuery(query);
    }

    @Override
    public Optional<T> findById(long id) throws DaoException {
        String query = "SELECT * FROM " + tableName + " WHERE id=?";
        return executeQueryForSingleResult(query, id);
    }
}