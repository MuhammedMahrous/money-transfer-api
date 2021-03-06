package com.fintech.api;

import com.fintech.model.MoneyTransfer;
import com.fintech.service.MoneyTransferService;
import lombok.extern.slf4j.Slf4j;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

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
    public Response transferMoney(@Valid MoneyTransfer moneyTransfer) throws Exception {
        log.debug("Received moneyTransfer: {}", moneyTransfer);
        MoneyTransfer moneyTransferResponse = moneyTransferService.transferMoney(moneyTransfer);
        return Response
                .status(Response.Status.CREATED)
                .entity(moneyTransferResponse)
                .build();
    }
}
