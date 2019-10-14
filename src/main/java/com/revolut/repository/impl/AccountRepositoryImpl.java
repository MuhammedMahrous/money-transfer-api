package com.revolut.repository.impl;

import com.revolut.exceptions.NoSuchAccountException;
import com.revolut.model.Account;
import com.revolut.repository.AccountRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Currency;

@ApplicationScoped
public class AccountRepositoryImpl implements AccountRepository {

    private DataSource dataSource;

    public AccountRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Account getAccountById(Integer getAccountId) throws SQLException {
        Account account = null;
        Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection
                .prepareStatement("SELECT * FROM account WHERE id = ?");
        preparedStatement.setInt(1, getAccountId);

        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            account = Account.builder()
                    .id(resultSet.getInt(1))
                    .balance(resultSet.getBigDecimal(2))
                    .currency(Currency.getInstance(resultSet.getString(3)))
                    .build();
        } else {
            throw new NoSuchAccountException(getAccountId);
        }

        return account;
    }

    @Override
    public boolean updateBalance(Account account) throws SQLException {
        boolean updated = false;
        Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection
                .prepareStatement("UPDATE account SET balance = ? WHERE id = ?");
        preparedStatement.setBigDecimal(1, account.getBalance());
        preparedStatement.setInt(2, account.getId());

        int updateResult = preparedStatement.executeUpdate();
        updated = updateResult > 0;
        return updated;
    }
}
