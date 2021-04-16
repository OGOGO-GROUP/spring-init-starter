insert into og_user_group(code, title)
values ('ROLE_ADMIN', 'Администратор'),
       ('ROLE_USER', 'Пользователь');

insert into og_user(created_at, updated_at, first_name, last_name, login, email, password_hash, activated, activation_key, reset_date, reset_key)
values (now(), null , 'Улукбек', 'Самаков', 'usamakov', 'usamakov@example.xom', '$2y$12$L.FYHSvh03/GwMu0AUoGju.SwmTtVkxamJH/7E8Q8aiopa7GHL/te', true, null, null, null);

insert into m2m_user_groups(user_id, group_code)
values (
           (select id from og_user where login='usamakov'),
           'ROLE_ADMIN'
       );
