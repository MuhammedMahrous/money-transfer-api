create table account
(
  id int NOT NULL,
  balance decimal,
  currency varchar(3),
  CONSTRAINT account_pk PRIMARY KEY (id)
);

insert into account(id, balance, currency) values (1233, 100.39, 'EUR');
insert into account(id, balance, currency) values (456, 20, 'USD');

create table money_transfer
(
  id int NOT NULL AUTO_INCREMENT,
  source_account_id int,
  target_account_id int,
  amount decimal,
  currency varchar(3),
  CONSTRAINT money_transfer_pk PRIMARY KEY (id)
);

insert into money_transfer(id, source_account_id, target_account_id, amount, currency)
 values (1234567, 1233, 456, 30,'USD');

create SEQUENCE money_transfer_id_seq INCREMENT 1 START 1;