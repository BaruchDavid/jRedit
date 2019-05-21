INSERT INTO LINK(link_id, user_id, title, url, created_On, VOTE_COUNT)
VALUES(1, 1, 'Securing Spring Boot APIs and SPAs with OAuth 2.0', 'https://auth0.com/blog/securing-spring-boot-apis-and-spas-with-oauth2/?utm_source=reddit&utm_medium=sc&utm_campaign=springboot_spa_securing', NOW(), 0);
INSERT INTO LINK(link_id, user_id, title, url, created_On, VOTE_COUNT)
VALUES(2, 1, 'Easy way to detect Device in Java Web Application using Spring Mobile - Source code to download from GitHub', 'https://www.opencodez.com/java/device-detection-using-spring-mobile.htm', NOW(), 1);
INSERT INTO LINK(link_id, user_id, title, url, created_On, VOTE_COUNT)
VALUES(3, 1, 'Tutorial series about building microservices with SpringBoot (with Netflix OSS)', 'https://medium.com/@marcus.eisele/implementing-a-microservice-architecture-with-spring-boot-intro-cdb6ad16806c', NOW(), 2);
INSERT INTO LINK(link_id, user_id, title, url, created_On, VOTE_COUNT)
VALUES(4, 1, 'Detailed steps to send encrypted email using Java / Spring Boot - Source code to download from GitHub', 'https://www.opencodez.com/java/send-encrypted-email-using-java.htm', NOW(), 32);
INSERT INTO LINK(link_id, user_id, title, url, created_On, VOTE_COUNT)
VALUES(5, 1, 'Build a Secure Progressive Web App With Spring Boot and React', 'https://dzone.com/articles/build-a-secure-progressive-web-app-with-spring-boo', NOW(), 12);
INSERT INTO LINK(link_id, user_id, title, url, created_On, VOTE_COUNT)
VALUES(6, 1, 'Building Your First Spring Boot Web Application - DZone Java', 'https://dzone.com/articles/building-your-first-spring-boot-web-application-ex', NOW(), 14);
INSERT INTO LINK(link_id, user_id, title, url, created_On, VOTE_COUNT)
VALUES(7, 1, 'Building Microservices with Spring Boot Fat (Uber) Jar', 'https://jelastic.com/blog/building-microservices-with-spring-boot-fat-uber-jar/', NOW(), 21);
INSERT INTO LINK(link_id, user_id, title, url, created_On, VOTE_COUNT)
VALUES(8, 1, 'Spring Cloud GCP 1.0 Released', 'https://cloud.google.com/blog/products/gcp/calling-java-developers-spring-cloud-gcp-1-0-is-now-generally-available', NOW(), 11);
INSERT INTO LINK(link_id, user_id, title, url, created_On, VOTE_COUNT)
VALUES(9, 1, 'Simplest way to Upload and Download Files in Java with Spring Boot - Code to download from Github', 'https://www.opencodez.com/uncategorized/file-upload-and-download-in-java-spring-boot.htm', NOW(), -1);
INSERT INTO LINK(link_id, user_id, title, url, created_On, VOTE_COUNT)
VALUES(10, 1, 'Add Social Login to Your Spring Boot 2.0 app', 'https://developer.okta.com/blog/2018/07/24/social-spring-boot', NOW(), 112);
INSERT INTO LINK(link_id, user_id, title, url, created_On, VOTE_COUNT)
VALUES(11, 1, 'File download example using Spring REST Controller', 'https://developer.okta.com/blog/2018/07/24/social-spring-boot', NOW(), 17);

INSERT INTO Role(role_id, name)
VALUES(1, 'ROLE_USER');

INSERT INTO Role(role_id, name)
VALUES(2, 'ROLE_DBA');

INSERT INTO Role(role_id, name)
VALUES(3, 'ROLE_ADMIN');

INSERT INTO Role(role_id, name)
VALUES(4, 'ROLE_ACTUATOR');

INSERT INTO User(user_id, email, enabled, password)
VALUES(1, 'romakapt@gmx.de', 1, '$2a$10$kd02GLWJlGR94dyTT7xiLu07CejGocL0oqSSRsInjvwahu3d900ne');

INSERT INTO User(user_id, email, enabled, password)
VALUES(2, 'dascha@gmx.de', 1, '$2a$10$huJEV8HA6ty9BzNlRqHyG.QMPE//p4lyMyfcTqnSpTe7fxlxybs2e');

INSERT INTO users_roles(user_Id, role_Id)
VALUES(1, 1);

INSERT INTO users_roles(user_Id, role_Id)
VALUES(1, 3);

INSERT INTO users_roles(user_Id, role_Id)
VALUES(1, 4);


INSERT INTO users_roles(user_Id, role_Id)
VALUES(1, 2);
/*
INSERT INTO users_roles(user_Id, role_Id)
VALUES(1, 3);

INSERT INTO users_roles(user_Id, role_Id)
VALUES(1, 4);

INSERT INTO users_roles(user_Id, role_Id)
VALUES(2, 3);
*/
