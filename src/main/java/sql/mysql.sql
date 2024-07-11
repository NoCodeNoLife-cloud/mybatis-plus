create database test;
use test;
create table students
(
    `id`    integer primary key,
    `name`  varchar(32) not null,
    `grade` integer     not null
)