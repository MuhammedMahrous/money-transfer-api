package com.revolut.repository.impl;

import com.revolut.exceptions.NoSuchMoneyTransfer;
import com.revolut.model.MoneyTransfer;
import com.revolut.repository.MoneyTransferRepository;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Currency;

@ApplicationScoped
@Slf4j
public class MoneyTransferRepositoryImpl implements MoneyTransferRepository {

    private DataSource dataSource;

    public MoneyTransferRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public MoneyTransfer getById(Integer id) throws SQLException {
        MoneyTransfer moneyTransfer = null;
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("SELECT * FROM money_transfer WHERE id = ?");
            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                moneyTransfer = MoneyTransfer.builder()
                        .id(resultSet.getInt(1))
                        .sourceAccountId(resultSet.getInt(2))
                        .targetAccountId(resultSet.getInt(3))
                        .amount(resultSet.getBigDecimal(4))
                        .currency(Currency.getInstance(resultSet.getString(5)))
                        .build();
            } else {
                NoSuchMoneyTransfer exception = new NoSuchMoneyTransfer(id);
                log.error(exception.getMessage());
                throw exception;
            }
        }

        return moneyTransfer;
    }

    @Override
    public MoneyTransfer create(MoneyTransfer moneyTransfer) throws SQLException {
        log.info("Inserting the successful transaction history: {}", moneyTransfer);
        try (Connection connection = dataSource.getConnection()) {
            Integer id = getNextSequenceValue(connection);
            PreparedStatement preparedStatement = connection
                    .prepareStatement("INSERT INTO money_transfer(id, source_account_id, target_account_id, amount, currency)" +
                            " values (?, ?, ?, ?,?)");
            preparedStatement.setInt(1, id);
            preparedStatement.setInt(2, moneyTransfer.getSourceAccountId());
            preparedStatement.setInt(3, moneyTransfer.getTargetAccountId());
            preparedStatement.setBigDecimal(4, moneyTransfer.getAmount());
            preparedStatement.setString(5, moneyTransfer.getCurrency().toString());

            int update = preparedStatement.executeUpdate();
            if (update > 0) {
                moneyTransfer.setId(id);
            } else {
                NoSuchMoneyTransfer exception = new NoSuchMoneyTransfer(id);
                log.error(exception.getMessage());
                throw exception;
            }
        }
        return moneyTransfer;
    }

    private Integer getNextSequenceValue(Connection connection) throws SQLException {
        ResultSet resultSet = connection
                .createStatement()
                .executeQuery("SELECT nextval('money_transfer_id_seq')");
        if (resultSet.next()) {
            return resultSet.getInt(1);
        } else {
            String errorMessage = "FATAL ERROR, COULDN'T GET NEW MONEYTRANSFER ID";
            log.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }
    }
}
