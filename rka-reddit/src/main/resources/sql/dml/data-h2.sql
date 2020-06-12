INSERT INTO User(user_id, email, enabled, password, first_name, second_name, alias_name, new_email, profile_foto, confirm_password,creation_date)
VALUES(1, 'romakapt@gmx.de', 1, '$2a$10$kd02GLWJlGR94dyTT7xiLu07CejGocL0oqSSRsInjvwahu3d900ne', 'baruc-david', 'rka', 'worker', 'kaproma@yahoo.de', null, '$2a$10$kd02GLWJlGR94dyTT7xiLu07CejGocL0oqSSRsInjvwahu3d900ne', NOW());

INSERT INTO User(user_id, email, enabled, password, first_name, second_name, alias_name, new_email, profile_foto, confirm_password, creation_date)
VALUES(2, 'dascha@gmx.de', 1, '$2a$10$kd02GLWJlGR94dyTT7xiLu07CejGocL0oqSSRsInjvwahu3d900ne', 'dascha', 'unknown', 'ceo', '', null, '$2a$10$kd02GLWJlGR94dyTT7xiLu07CejGocL0oqSSRsInjvwahu3d900ne', NOW());

INSERT INTO User(user_id, email, enabled, password, first_name, second_name, alias_name, new_email, profile_foto, confirm_password,creation_date)
VALUES(3, 'grom@gmx.de', 1, '$2a$10$kd02GLWJlGR94dyTT7xiLu07CejGocL0oqSSRsInjvwahu3d900ne', 'grom', 'grm', 'worker2', '', null, '$2a$10$kd02GLWJlGR94dyTT7xiLu07CejGocL0oqSSRsInjvwahu3d900ne', NOW());


INSERT INTO LINK(link_id,  title, description, url,  VOTE_COUNT, creation_Date, user_user_id)
VALUES(1,  'Securing Spring Boot APIs and SPAs with OAuth 2.0', '','https://auth0.com/blog/securing-spring-boot-apis-and-spas-with-oauth2/?utm_source=reddit&utm_medium=sc&utm_campaign=springboot_spa_securing',  0,NOW(),1);
INSERT INTO LINK(link_id,  title, description, url,  VOTE_COUNT, creation_Date, user_user_id)
VALUES(2,  'Device in Java Web Application Spring Mobile', '','https://www.opencodez.com/java/device-detection-using-spring-mobile.htm',  1,NOW(),1);
INSERT INTO LINK(link_id,  title, description, url,  VOTE_COUNT, creation_Date, user_user_id)
VALUES(3,  'microservices with SpringBoot','Tutorial series about building microservices with SpringBoot (with Netflix OSS)', 'https://medium.com/@marcus.eisele/implementing-a-microservice-architecture-with-spring-boot-intro-cdb6ad16806c',  2,NOW(),1);
INSERT INTO LINK(link_id,  title, description, url,  VOTE_COUNT, creation_Date, user_user_id)
VALUES(4,  'encrypted email using Java / Spring Boot', 'Detailed steps to send encrypted email using Java / Spring Boot', 'https://www.opencodez.com/java/send-encrypted-email-using-java.htm',  32,NOW(),1);
INSERT INTO LINK(link_id,  title, description, url,  VOTE_COUNT, creation_Date, user_user_id)
VALUES(5,  'Progressive Web App With Spring Boot and React', 'Build a Secure Progressive Web App With Spring Boot and React', 'https://dzone.com/articles/build-a-secure-progressive-web-app-with-spring-boo',  12,NOW(),1);
INSERT INTO LINK(link_id,  title, description, url,  VOTE_COUNT, creation_Date, user_user_id)
VALUES(6,  'First Spring Boot Web Application', 'Building Your First Spring Boot Web Application - DZone Java', 'https://dzone.com/articles/building-your-first-spring-boot-web-application-ex',  14,NOW(),2);
INSERT INTO LINK(link_id,  title, description, url,  VOTE_COUNT, creation_Date, user_user_id)
VALUES(7,  'Building Microservices with Spring Boot', 'Building Microservices with Spring Boot Fat (Uber) Jar', 'https://jelastic.com/blog/building-microservices-with-spring-boot-fat-uber-jar/',  21,NOW(),2);
INSERT INTO LINK(link_id,  title, description, url,  VOTE_COUNT, creation_Date, user_user_id)
VALUES(8,  'Spring Cloud GCP 1.0 Released', '','https://cloud.google.com/blog/products/gcp/calling-java-developers-spring-cloud-gcp-1-0-is-now-generally-available',  11,NOW(),2);
INSERT INTO LINK(link_id,  title, description, url,  VOTE_COUNT, creation_Date, user_user_id)
VALUES(9,  'Upload and Download Files in Java, Spring Boot','Simplest way to Upload and Download Files in Java with Spring Boot - Code to download from Github', 'https://www.opencodez.com/uncategorized/file-upload-and-download-in-java-spring-boot.htm',  -1,NOW(),2);
INSERT INTO LINK(link_id,  title, description, url,  VOTE_COUNT, creation_Date, user_user_id)
VALUES(10,  'Social Login to Your Spring Boot 2.0', '','https://developer.okta.com/blog/2018/07/24/social-spring-boot',  112,NOW(),2);
INSERT INTO LINK(link_id,  title, description, url,  VOTE_COUNT, creation_Date, user_user_id)
VALUES(11, 'File download using Spring REST', '','https://developer.okta.com/blog/2018/07/24/social-spring-boot',  17,NOW(),3);

INSERT INTO Role(role_id, name)
VALUES(1, 'ROLE_USER');

INSERT INTO Role(role_id, name)
VALUES(2, 'ROLE_DBA');

INSERT INTO Role(role_id, name)
VALUES(3, 'ROLE_ADMIN');

INSERT INTO Role(role_id, name)
VALUES(4, 'ROLE_ACTUATOR');


INSERT INTO users_roles(user_Id, role_Id)
VALUES(1, 1);

INSERT INTO users_roles(user_Id, role_Id)
VALUES(1, 3);


INSERT INTO users_roles(user_Id, role_Id)
VALUES(1, 2);

INSERT INTO users_roles(user_Id, role_Id)
VALUES(2, 1);

INSERT INTO users_roles(user_Id, role_Id)
VALUES(3, 1);

INSERT INTO Comment(comment_id, comment_text, link_link_id, user_user_id, creation_Date)
VALUES(1, 'Erstes Kommentar für Ersten Link für ersten romakapt', 1, 1,NOW());

INSERT INTO Comment(comment_id, comment_text, link_link_id, user_user_id, creation_Date)
VALUES(2, 'Erstes Kommentar für zweiten Link für romakapt', 2, 1,NOW());

INSERT INTO Comment(comment_id, comment_text, link_link_id, user_user_id, creation_Date)
VALUES(3, 'Drittes Kommentar für dritten link', 3, 1,NOW());

INSERT INTO Comment(comment_id, comment_text, link_link_id, user_user_id, creation_Date)
VALUES(32, '32tes Kommentar für ersten Link', 1, 2,NOW());

INSERT INTO Comment(comment_id, comment_text, link_link_id, user_user_id, creation_Date)
VALUES(4, 'Viertes Kommentar für zweiten Link von romakapt', 2, 1,NOW());

INSERT INTO Comment(comment_id, comment_text, link_link_id, user_user_id, creation_Date)
VALUES(5, 'Lorem ipsum sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua.', 2, 1,NOW());

INSERT INTO Comment(comment_id, comment_text, link_link_id, user_user_id, creation_Date)
VALUES(6, 'Lorem ipsum sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua.', 2, 2,NOW());

INSERT INTO Comment(comment_id, comment_text, link_link_id, user_user_id, creation_Date)
VALUES(7, 'Lorem ipsum sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua.', 2, 1,NOW());

INSERT INTO Comment(comment_id, comment_text, link_link_id, user_user_id, creation_Date)
VALUES(8, 'Lorem ipsum sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua.', 2, 2,NOW());

INSERT INTO Comment(comment_id, comment_text, link_link_id, user_user_id, creation_Date)
VALUES(9, 'Lorem ipsum sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua.', 3, 1,NOW());

INSERT INTO Comment(comment_id, comment_text, link_link_id, user_user_id, creation_Date)
VALUES(12, 'Lorem ipsum sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua.', 3, 2,NOW());

INSERT INTO Comment(comment_id, comment_text, link_link_id, user_user_id, creation_Date)
VALUES(22, 'Lorem ipsum sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua.', 4, 1,NOW());

INSERT INTO Comment(comment_id, comment_text, link_link_id, user_user_id, creation_Date)
VALUES(23, 'Lorem ipsum sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua.', 4, 2,NOW());

INSERT INTO Comment(comment_id, comment_text, link_link_id, user_user_id, creation_Date)
VALUES(24, 'Lorem ipsum sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua.', 5, 2,NOW());

INSERT INTO Comment(comment_id, comment_text, link_link_id, user_user_id, creation_Date)
VALUES(25, 'Lorem ipsum sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua.', 5, 1,NOW());

INSERT INTO Comment(comment_id, comment_text, link_link_id, user_user_id, creation_Date)
VALUES(26, 'Lorem ipsum sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua.', 6, 1,NOW());

INSERT INTO Vote(vote_id, created_by, creation_date, last_modified_by, last_modified_date, direction, link_link_id)
VALUES(1, 'romakapt@gmx.de', NOW(),'romakapt@gmx.de', NOW(), 1, 1);

INSERT INTO Vote(vote_id, created_by, creation_date, last_modified_by, last_modified_date, direction, link_link_id)
VALUES(2, 'romakapt@gmx.de', NOW(),'romakapt@gmx.de', NOW(), 1, 1);

INSERT INTO Vote(vote_id, created_by, creation_date, last_modified_by, last_modified_date, direction, link_link_id)
VALUES(3, 'dascha@gmx.de', NOW(),'dascha@gmx.de', NOW(), 1, 1);

INSERT INTO Vote(vote_id, created_by, creation_date, last_modified_by, last_modified_date, direction, link_link_id)
VALUES(4, 'dascha@gmx.de', NOW(),'dascha@gmx.de', NOW(), 1, 1);

INSERT INTO Vote(vote_id, created_by, creation_date, last_modified_by, last_modified_date, direction, link_link_id)
VALUES(5, 'dascha@gmx.de', NOW(),'dascha@gmx.de', NOW(), 1, 3);

INSERT INTO Vote(vote_id, created_by, creation_date, last_modified_by, last_modified_date, direction, link_link_id)
VALUES(6, 'dascha@gmx.de', NOW(),'dascha@gmx.de', NOW(), 1, 3);

INSERT INTO Vote(vote_id, created_by, creation_date, last_modified_by, last_modified_date, direction, link_link_id)
VALUES(7, 'dascha@gmx.de', NOW(),'dascha@gmx.de', NOW(), 1, 5);

INSERT INTO Vote(vote_id, created_by, creation_date, last_modified_by, last_modified_date, direction, link_link_id)
VALUES(8, 'dascha@gmx.de', NOW(),'dascha@gmx.de', NOW(), 1, 5);

INSERT INTO Vote(vote_id, created_by, creation_date, last_modified_by, last_modified_date, direction, link_link_id)
VALUES(9, 'dascha@gmx.de', NOW(),'dascha@gmx.de', NOW(), 1, 5);


INSERT INTO Tag(tag_id, tag_name)
VALUES(1, 'Java');

INSERT INTO Tag(tag_id, tag_name)
VALUES(2, 'TypeScript');

INSERT INTO Tag(tag_id, tag_name)
VALUES(3, 'JavaScript');

INSERT INTO Tag(tag_id, tag_name)
VALUES(4, 'C');

INSERT INTO Tag(tag_id, tag_name)
VALUES(5, 'C++');

INSERT INTO Tag(tag_id, tag_name)
VALUES(6, 'PHP');

INSERT INTO Tag(tag_id, tag_name)
VALUES(7, 'Phyton');

INSERT INTO Tag(tag_id, tag_name)
VALUES(8, 'Delphi/Object Pascal');

INSERT INTO Tag(tag_id, tag_name)
VALUES(9, 'Swift');

INSERT INTO Tag(tag_id, tag_name)
VALUES(10, 'SQL');

INSERT INTO Tag(tag_id, tag_name)
VALUES(11, 'HTML-5');

INSERT INTO Tag(tag_id, tag_name)
VALUES(12, 'CSS');

INSERT INTO link_tags(link_id, tag_id)
VALUES(1, 2);

INSERT INTO link_tags(link_id, tag_id)
VALUES(1, 3);

INSERT INTO link_tags(link_id, tag_id)
VALUES(2, 3);

INSERT INTO link_tags(link_id, tag_id)
VALUES(2, 1);

INSERT INTO link_tags(link_id, tag_id)
VALUES(4, 1);

INSERT INTO user_clicked_links(user_id, link_id)
VALUES(1, 1);

INSERT INTO user_clicked_links(user_id, link_id)
VALUES(1, 2);

INSERT INTO user_clicked_links(user_id, link_id)
VALUES(1, 3);

INSERT INTO user_clicked_links(user_id, link_id)
VALUES(2, 1);

INSERT INTO user_clicked_links(user_id, link_id)
VALUES(2, 3);

