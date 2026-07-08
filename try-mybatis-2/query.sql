select current_database();

create table board2(
	id BIGSERIAL primary key ,
	title varchar(200) not null,
	writer varchar(100) not null,
	pass varchar(100) not null,
	contents text
);
select * from board2;

insert into board2(title, writer, pass, contents)
values('좋은아침', '선경', '1234', '오늘은 비 오는날');
