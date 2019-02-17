### sequence  
```
drop table if exists sequence;
create table sequence (
	name varchar(50) not null,
	current_value int not null,
	increment	int not null default 1,
	primary key (name)
) 
engine=innodb;
insert into sequence values ('movieseq',3,5);
drop function if exists currval;

delimiter $
create function currval (seq_name varchar(50)) returns integer
contains sql
begin
  declare value integer;
  set value = 0;
  select current_value into value
  from sequence
  where name = seq_name;
  return value;
end$
delimiter ;


drop function if exists nextval;
delimiter $
create function nextval (seq_name varchar(50)) returns integer
contains sql
begin
   update sequence
   set current_value = current_value + increment
   where name = seq_name;
   return currval(seq_name);
end$
delimiter ;

drop function if exists setval;
delimiter $
create function setval (seq_name varchar(50), value integer) returns integer
contains sql
begin
	update sequence
	set current_value = value
	where name = seq_name;
	return currval(seq_name);
end$
delimiter ;

select nextval('movieseq');
select nextval('movieseq');
select nextval('movieseq');
select currval('movieseq');
select currval('x');
select setval('movieseq',150);
select curval('movieseq');
select nextval('movieseq');

```

