package com.revolut.api;

import com.revolut.model.MoneyTransfer;
import com.revolut.service.MoneyTransferService;
import lombok.extern.slf4j.Slf4j;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.sql.SQLException;

@Path("/moneyTransfer")
@Slf4j
public class MoneyTransferApi {
    private MoneyTransferService moneyTransferService;

    public MoneyTransferApi(MoneyTransferService moneyTransferService) {
        this.moneyTransferService = moneyTransferService;
    }

    @POST
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response transferMoney(@Valid MoneyTransfer moneyTransfer) throws SQLException {
        MoneyTransfer moneyTransferResponse = moneyTransferService.transferMoney(moneyTransfer);
        return Response
                .status(Response.Status.CREATED)
                .entity(moneyTransferResponse)
                .build();
    }
}
