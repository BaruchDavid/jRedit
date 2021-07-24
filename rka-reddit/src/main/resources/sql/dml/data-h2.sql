INSERT INTO User(user_id, email, enabled, password, first_name, second_name, alias_name, new_email, profile_foto, foto_creation_Date, confirm_password,creation_date)
VALUES(1, 'kaproma@yahoo.de', 1, '$2a$10$kd02GLWJlGR94dyTT7xiLu07CejGocL0oqSSRsInjvwahu3d900ne', 'baruc-david', 'rka', 'worker', 'kaproma@yahoo.de', null, null, '$2a$10$kd02GLWJlGR94dyTT7xiLu07CejGocL0oqSSRsInjvwahu3d900ne', NOW());

INSERT INTO User(user_id, email, enabled, password, first_name, second_name, alias_name, new_email, profile_foto, foto_creation_Date, confirm_password, creation_date)
VALUES(2, 'dascha@gmx.de', 1, '$2a$10$kd02GLWJlGR94dyTT7xiLu07CejGocL0oqSSRsInjvwahu3d900ne', 'dascha', 'unknown', 'ceo', '', null, null, '$2a$10$kd02GLWJlGR94dyTT7xiLu07CejGocL0oqSSRsInjvwahu3d900ne', NOW());

INSERT INTO User(user_id, email, enabled, password, first_name, second_name, alias_name, new_email, profile_foto, foto_creation_Date, confirm_password,creation_date)
VALUES(3, 'grom@gmx.de', 1, '$2a$10$kd02GLWJlGR94dyTT7xiLu07CejGocL0oqSSRsInjvwahu3d900ne', 'grom', 'grm', 'worker2', '', null, null, '$2a$10$kd02GLWJlGR94dyTT7xiLu07CejGocL0oqSSRsInjvwahu3d900ne', NOW());


INSERT INTO LINK(link_id,  title,  subtitle, description, url,  COMMENT_COUNT,VOTE_COUNT, creation_Date, user_id)
VALUES(1,  'Securing Spring Boot APIs and SPAs with OAuth 2.0', 'spring security', 'building apis',
			'https://auth0.com/blog/securing-spring-boot-apis-and-spas-with-oauth2/?utm_source=reddit&utm_medium=sc&utm_campaign=springboot_spa_securing',
			5, 5,NOW(),1);
INSERT INTO LINK(link_id,  title,  subtitle, description, url,  COMMENT_COUNT,VOTE_COUNT, creation_Date, user_id)
VALUES(2,  'Device in Java Web Application Spring Mobile','mobile development', 'nice tutorial for java mobile development',
			'https://www.opencodez.com/java/device-detection-using-spring-mobile.htm', 4, 1,NOW(),1);
INSERT INTO LINK(link_id,  title,  subtitle, description, url,  COMMENT_COUNT,VOTE_COUNT, creation_Date, user_id)
VALUES(3,  'microservices with SpringBoot','microservices stuff', 
			'Tutorial series about building microservices with SpringBoot (with Netflix OSS)',
			'https://medium.com/@marcus.eisele/implementing-a-microservice-architecture-with-spring-boot-intro-cdb6ad16806c', 2, 2,NOW(),1);
INSERT INTO LINK(link_id,  title,  subtitle, description, url,  COMMENT_COUNT,VOTE_COUNT, creation_Date, user_id)
VALUES(4,  'encrypted email using Java / Spring Boot', 'encryption',
            'Detailed steps to send encrypted email using Java / Spring Boot',
            'https://www.opencodez.com/java/send-encrypted-email-using-java.htm', 2, 32,NOW(),1);
INSERT INTO LINK(link_id,  title,  subtitle, description, url,  COMMENT_COUNT,VOTE_COUNT, creation_Date, user_id)
VALUES(5,  'Progressive Web App With Spring Boot and React', '', 
			'Build a Secure Progressive Web App With Spring Boot and React', 
			'https://dzone.com/articles/build-a-secure-progressive-web-app-with-spring-boo', 2,  12,NOW(),1);
INSERT INTO LINK(link_id,  title,  subtitle, description, url,  COMMENT_COUNT,VOTE_COUNT, creation_Date, user_id)
VALUES(6,  'First Spring Boot Web Application', 'for beginners', 
			'Building Your First Spring Boot Web Application - DZone Java', 
			'https://dzone.com/articles/building-your-first-spring-boot-web-application-ex', 1, 14,NOW(),2);
INSERT INTO LINK(link_id,  title,  subtitle, description, url,  COMMENT_COUNT,VOTE_COUNT, creation_Date, user_id)
VALUES(7,  'Building Microservices with Spring Boot', 'learning for beginners', 
			'Building Microservices with Spring Boot Fat (Uber) Jar', 
			'https://jelastic.com/blog/building-microservices-with-spring-boot-fat-uber-jar/', 0, 21,NOW(),2);
INSERT INTO LINK(link_id,  title,  subtitle, description, url,  COMMENT_COUNT,VOTE_COUNT, creation_Date, user_id)
VALUES(8,  'Spring Cloud GCP 1.0 Released','cloud topic', '',
			'https://cloud.google.com/blog/products/gcp/calling-java-developers-spring-cloud-gcp-1-0-is-now-generally-available', 0,  11,NOW(),2);
INSERT INTO LINK(link_id,  title,  subtitle, description, url,  COMMENT_COUNT,VOTE_COUNT, creation_Date, user_id)
VALUES(9,  'Upload and Download Files in Java, Spring Boot', 'handle files with spring-boot',
			 'Simplest way to Upload and Download Files in Java with Spring Boot - Code to download from Github', 
			 'https://www.opencodez.com/uncategorized/file-upload-and-download-in-java-spring-boot.htm', 0, -1,NOW(),2);
INSERT INTO LINK(link_id,  title,  subtitle, description, url,  COMMENT_COUNT,VOTE_COUNT, creation_Date, user_id)
VALUES(10,  'Spring Boot Integration tests', 'test tutorial', '','https://www.baeldung.com/spring-security-integration-tests', 0, 112,NOW(),2);
INSERT INTO LINK(link_id,  title,  subtitle, description, url,  COMMENT_COUNT,VOTE_COUNT, creation_Date, user_id)
VALUES(11, 'File download using Spring REST','how handle files with REST', '','https://developer.okta.com/blog/2018/07/24/social-spring-boot', 0, 17,NOW(),3);

INSERT INTO User_Clicked_Links(link_id, user_id)
VALUES(2,1);

INSERT INTO User_Clicked_Links(link_id, user_id)
VALUES(4,1);

INSERT INTO User_Clicked_Links(link_id, user_id)
VALUES(3,1);

INSERT INTO User_Clicked_Links(link_id, user_id)
VALUES(3,2);

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

INSERT INTO Comment(comment_id, comment_text, link_link_id, user_id, creation_Date)
VALUES(1, 'Erstes Kommentar von romakapt',1, 1, NOW());

INSERT INTO Comment(comment_id, comment_text, link_link_id, user_id,  creation_Date)
VALUES(2, 'zweites kommentar von dascha ',1, 2, NOW());

INSERT INTO Comment(comment_id, comment_text, link_link_id, user_id,  creation_Date)
VALUES(3, 'drittes kommentar von grom',1, 3, NOW());

INSERT INTO Comment(comment_id, comment_text, link_link_id, user_id,  creation_Date)
VALUES(10, '10 Kommentar von romakapt',1, 1, NOW());

INSERT INTO Comment(comment_id, comment_text, link_link_id, user_id,  creation_Date)
VALUES(4, 'Viertes Kommentar für zweiten Link von dascha',2, 2, NOW());

INSERT INTO Comment(comment_id, comment_text, link_link_id, user_id, creation_Date)
VALUES(5, 'fünftes Kommentar für zweiten Link von dascha',2, 2, NOW());

INSERT INTO Comment(comment_id, comment_text, link_link_id, user_id, creation_Date)
VALUES(6, 'sechstes kommentar von dascha.',2, 2, NOW());

INSERT INTO Comment(comment_id, comment_text, link_link_id, user_id, creation_Date)
VALUES(7, 'siebtes kommentar von dascha.',1, 2, NOW());

INSERT INTO Comment(comment_id, comment_text, link_link_id, user_id, creation_Date)
VALUES(8, 'achtes kommentar von dascha.',2, 2, NOW());

INSERT INTO Comment(comment_id, comment_text, link_link_id, user_id, creation_Date)
VALUES(9, 'neuntes kommentar von grom.', 3,3, NOW());

INSERT INTO Comment(comment_id, comment_text, link_link_id, user_id, creation_Date)
VALUES(12, 'zwölftes kommentar von grom.', 3,3, NOW());

INSERT INTO Comment(comment_id, comment_text, link_link_id, user_id, creation_Date)
VALUES(22, '22 kommentar von grom.',  4, 3,NOW());

INSERT INTO Comment(comment_id, comment_text, link_link_id, user_id, creation_Date)
VALUES(23, '23 kommentar von grom', 4, 3, NOW());

INSERT INTO Comment(comment_id, comment_text, link_link_id, user_id, creation_Date)
VALUES(24, '24 kommentar von grom',  5, 3,NOW());

INSERT INTO Comment(comment_id, comment_text, link_link_id, user_id, creation_Date)
VALUES(25, '25 kommentar von grom',  5, 3,NOW());

INSERT INTO Comment(comment_id, comment_text, link_link_id, user_id, creation_Date)
VALUES(26, '26 kommentar von grom',  6, 3,NOW());

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


