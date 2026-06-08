INSERT INTO users (
    first_name,
    last_name,
    email,
    password,
    phone_number,
    role,
    user_status,
    created_at,
    updated_at
) VALUES (
             'Super',
             'Admin',
             'admin@securebank.com',
             '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi',
             '0000000000',
             'ADMIN',
             'ACTIVE',
             NOW(),
             NOW()
         );