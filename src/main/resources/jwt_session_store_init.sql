create table jwt_session_store (
    token varchar(512) not null primary key,
    device_id char(36) not null,
    remote_address varchar(256) not null,
    username varchar(256) not null,
    status varchar(12) not null,
    issued timestamp not null,
    expires timestamp not null
);