create table cars (
car_id serial primary key,
brand varchar(30) not null,
model varchar(30) not null,
price decimal(10, 2) not null check(price>=0);

create table people(
person_id serial primary key,
name varchar(100) not null,
age integer not null check(age>=0),
has_license boolean not null default false,
car_id integer,
foreign key(car_id) references cars(car_id) on delete set null);