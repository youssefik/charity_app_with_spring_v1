-- Insérer les catégories d'actions par défaut
INSERT INTO action_categories (name, description) VALUES
('Éducation', 'Actions liées à l''éducation et à la formation'),
('Santé', 'Actions liées à la santé et au bien-être'),
('Environnement', 'Actions liées à la protection de l''environnement'),
('Social', 'Actions liées à l''aide sociale et au développement communautaire'),
('Culture', 'Actions liées à la culture et aux arts'),
('Sports', 'Actions liées aux sports et aux activités physiques'),
('Autre', 'Autres types d''actions caritatives');

-- Insérer les rôles par défaut
INSERT INTO roles (name, description) VALUES
('ROLE_ADMIN', 'Administrateur du système'),
('ROLE_ORGANIZATION', 'Organisation caritative'),
('ROLE_USER', 'Utilisateur standard');

-- Insérer l'administrateur par défaut (mot de passe: admin123)
INSERT INTO users (first_name, last_name, email, password, enabled, active, created_at, updated_at)
VALUES ('Admin', 'System', 'admin@charity.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Associer le rôle admin à l'administrateur
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email = 'admin@charity.com' AND r.name = 'ROLE_ADMIN'; 