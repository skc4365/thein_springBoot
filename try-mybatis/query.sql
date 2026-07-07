select current_database();

create table member2(
	id serial primary key,
	name varchar(20)
);
select * from member2;

insert into member2(name)
values('수연');