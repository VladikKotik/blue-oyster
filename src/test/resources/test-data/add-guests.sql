INSERT INTO guests (id, first_name, last_name, created_at, updated_at)
VALUES
    (gen_random_uuid(), 'Улов',   'Кальмаров', now(), now()),
    (gen_random_uuid(), 'Поджог', 'Амбаров',   now(), now()),
    (gen_random_uuid(), 'Нурлан', 'Сысоев',   now(), now()),
    (gen_random_uuid(), 'Рулон', 'Обоев',   now(), now()),
    ('37c1f40d-801b-42e9-be7b-6013ddd5dae4', 'Покупатель', 'Черностоев',   now(), now());