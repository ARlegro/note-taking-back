INSERT INTO users (
    id,
    email,
    provider,
    provider_id,
    created_at,
    updated_at
) VALUES (
             gen_random_uuid(),          -- PostgreSQL
             'test@example.com',
             'LOCAL',
             'test-user-001',
             now(),
             now()
         );
