-- Insert test users
INSERT INTO users (id, name, surname, email, password, user_type)
VALUES
    ('a1b2c3d4-e5f6-7890-abcd-ef1234567890'::uuid, 'John', 'Doe', 'admin@pki.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J8f0JFO.6KjB8R5N1.1hNQ9jI3LQXK', 'ADMIN'),
    ('b2c3d4e5-a6b7-8901-bcde-a12345678901'::uuid, 'Jane', 'Smith', 'ca@pki.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J8f0JFO.6KjB8R5N1.1hNQ9jI3LQXK', 'CA'),
    ('c3d4e5a6-b7c8-9012-cdef-123456789012'::uuid, 'Bob', 'Wilson', 'user@pki.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J8f0JFO.6KjB8R5N1.1hNQ9jI3LQXK', 'REGULAR'),
    ('d4e5a6b7-c8d9-ef01-2345-678901234567'::uuid, 'Vanja', 'Kostić', 'vanjakostic03@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J8f0JFO.6KjB8R5N1.1hNQ9jI3LQXK', 'ADMIN');

-- Insert test organizations
INSERT INTO organizations (id, name, description, ca_user_id, enc_key, key_iv)
VALUES
    ('d4e5a6b7-c8d9-0123-defa-234567890123'::uuid, 'dfgh', 'Test Organization DFGH', 'b2c3d4e5-a6b7-8901-bcde-a12345678901'::uuid, 'oOXKTJ9QUHafPsaN21PU1tWBuilZE52ms1IkHOhxOeiU4ZP6Ps70wNfyHpFh8Me9zdzWqOeODU5jrIim', 'K1OFdrlFYsP28Quc'),
    ('e5a6b7c8-d9e0-1234-eabc-345678901234'::uuid, 'Acme Corp', 'Acme Corporation', 'b2c3d4e5-a6b7-8901-bcde-a12345678901'::uuid, 'YWNtZUVuY3J5cHRpb25LZXkxMjM0NTY3ODkwMTIzNDU2Nzg5MDEyMzQ1Njc4OTA=', 'YWNtZUl2MTIzNDU2Nzg5MA=='),
    ('a6b7c8d9-e0a1-2345-abcd-456789012345'::uuid, 'TechStart', 'Technology Startup Inc', 'b2c3d4e5-a6b7-8901-bcde-a12345678901'::uuid, 'dGVjaFN0YXJ0S2V5MTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIz', 'dGVjaFN0YXJ0SXYxMjM0NQ==');


