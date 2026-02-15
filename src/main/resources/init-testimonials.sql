CREATE TABLE IF NOT EXISTS `testimonials` (
  `id` int NOT NULL AUTO_INCREMENT,
  `image_url` varchar(255) DEFAULT NULL,
  `message` text,
  `name` varchar(255) DEFAULT NULL,
  `rating` int DEFAULT NULL,
  `role` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `testimonials` (id, name, role, message, rating, image_url) VALUES 
(1, 'John Doe', 'CEO, TechStart', 'Jobify has completely transformed how match with talent. The process is seamless and the quality of candidates is outstanding. Highly recommended!', 5, NULL),
(2, 'Sarah Johnson', 'UX Designer', 'As a freelancer, I''ve found my best long-term clients here. The platform is intuitive, reliable, and actually cares about its users.', 5, NULL),
(3, 'Michael Ross', 'HR Manager', 'The support team is incredible. Any issue I faced was resolved within minutes. Validated users mean I can trust who I work with.', 5, NULL);
