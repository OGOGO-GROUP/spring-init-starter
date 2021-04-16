create table if not exists og_user
(
    id             bigserial primary key ,
    created_at     timestamp,
    updated_at     timestamp,
    activated      boolean     not null,
    activation_key varchar(20),
    email          varchar(254) not null unique,
    first_name     varchar(50),
    last_name      varchar(50),
    login          varchar(50) not null unique,
    password_hash  varchar(60) not null,
    reset_date     timestamp,
    reset_key      varchar(20)
);

create table if not exists og_user_group
(
    code  varchar(50) primary key,
    title varchar(255) not null
);


create table if not exists m2m_user_groups
(
    user_id    bigint references og_user(id) on update cascade on delete no action,
    group_code varchar(50) references og_user_group(code) on update cascade on delete no action
);
