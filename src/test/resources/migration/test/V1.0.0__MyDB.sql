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
  id int NOT NULL,
  source_account_id int,
  target_account_id int,
  amount decimal,
  currency varchar(3),
  CONSTRAINT money_transfer_pk PRIMARY KEY (id)
);
