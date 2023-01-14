INSERT INTO Users(user_id, email, enabled, password, first_name, second_name,
                  alias_name, new_email, profile_foto, foto_creation_Date,
                  activation_dead_line_date, confirm_password, creation_date)
VALUES (NEXTVAL('USER_SEQ'), 'kaproma@yahoo.de', 1,
        '$2a$10$kd02GLWJlGR94dyTT7xiLu07CejGocL0oqSSRsInjvwahu3d900ne',
        'baruc-david', 'rka', 'worker', 'kaproma@yahoo.de', null, null,
        NOW() - 0.003,
        '$2a$10$kd02GLWJlGR94dyTT7xiLu07CejGocL0oqSSRsInjvwahu3d900ne', NOW());

INSERT INTO Users(user_id, email, enabled, password, first_name, second_name,
                  alias_name, new_email, profile_foto, foto_creation_Date,
                  activation_dead_line_date, confirm_password, creation_date)
VALUES (NEXTVAL('USER_SEQ'), 'dascha@gmx.de', 1,
        '$2a$10$kd02GLWJlGR94dyTT7xiLu07CejGocL0oqSSRsInjvwahu3d900ne',
        'dascha', 'unknown', 'ceo', '', null, null, NOW() - 0.003,
        '$2a$10$kd02GLWJlGR94dyTT7xiLu07CejGocL0oqSSRsInjvwahu3d900ne', NOW());

INSERT INTO Users(user_id, email, enabled, password, first_name, second_name,
                  alias_name, new_email, profile_foto, foto_creation_Date,
                  activation_dead_line_date, confirm_password, creation_date)
VALUES (NEXTVAL('USER_SEQ'), 'grom@gmx.de', 1,
        '$2a$10$kd02GLWJlGR94dyTT7xiLu07CejGocL0oqSSRsInjvwahu3d900ne', 'grom',
        'grm', 'worker2', '', null, null, NOW() - 0.003,
        '$2a$10$kd02GLWJlGR94dyTT7xiLu07CejGocL0oqSSRsInjvwahu3d900ne', NOW());


INSERT INTO LINK(link_id, title, subtitle, description, url, COMMENT_COUNT,
                 VOTE_COUNT, creation_Date, user_id)
VALUES (NEXTVAL('LINK_SEQ'),
        'Securing Spring Boot APIs and SPAs with OAuth 2.0', 'spring security',
        'building apis',
        'https://auth0.com/blog/securing-spring-boot-apis-and-spas-with-oauth2/?utm_source=reddit&utm_medium=sc&utm_campaign=springboot_spa_securing',
        1, 5, NOW(), 1);
INSERT INTO LINK(link_id, title, subtitle, description, url, COMMENT_COUNT,
                 VOTE_COUNT, creation_Date, user_id)
VALUES (NEXTVAL('LINK_SEQ'), 'Device in Java Web Application Spring Mobile',
        'mobile development', 'nice tutorial for java mobile development',
        'https://www.opencodez.com/java/device-detection-using-spring-mobile.htm',
        2, 1, NOW(), 1);


INSERT INTO LINK(link_id, title, subtitle, description, url, COMMENT_COUNT,
                 VOTE_COUNT, creation_Date, user_id)
VALUES (NEXTVAL('LINK_SEQ'), 'microservices with SpringBoot',
        'microservices stuff',
        'Tutorial series about building microservices with SpringBoot (with Netflix OSS)',
        'https://medium.com/@marcus.eisele/implementing-a-microservice-architecture-with-spring-boot-intro-cdb6ad16806c',
        4, 2, NOW(), 1);
INSERT INTO LINK(link_id, title, subtitle, description, url, COMMENT_COUNT,
                 VOTE_COUNT, creation_Date, user_id)
VALUES (NEXTVAL('LINK_SEQ'), 'encrypted email using Java / Spring Boot',
        'encryption',
        'Detailed steps to send encrypted email using Java / Spring Boot',
        'https://www.opencodez.com/java/send-encrypted-email-using-java.htm',
        3, 32, NOW(), 1);
INSERT INTO LINK(link_id, title, subtitle, description, url, COMMENT_COUNT,
                 VOTE_COUNT, creation_Date, user_id)
VALUES (NEXTVAL('LINK_SEQ'), 'Progressive Web App With Spring Boot and React',
        '',
        'Build a Secure Progressive Web App With Spring Boot and React',
        'https://dzone.com/articles/build-a-secure-progressive-web-app-with-spring-boo',
        1, 12, NOW(), 1);
INSERT INTO LINK(link_id, title, subtitle, description, url, COMMENT_COUNT,
                 VOTE_COUNT, creation_Date, user_id)
VALUES (NEXTVAL('LINK_SEQ'), 'First Spring Boot Web Application',
        'for beginners',
        'Building Your First Spring Boot Web Application - DZone Java',
        'https://dzone.com/articles/building-your-first-spring-boot-web-application-ex',
        1, 14, NOW(), 2);
INSERT INTO LINK(link_id, title, subtitle, description, url, COMMENT_COUNT,
                 VOTE_COUNT, creation_Date, user_id)
VALUES (NEXTVAL('LINK_SEQ'), 'Building Microservices with Spring Boot',
        'learning for beginners',
        'Building Microservices with Spring Boot Fat (Uber) Jar',
        'https://jelastic.com/blog/building-microservices-with-spring-boot-fat-uber-jar/',
        2, 21, NOW(), 2);
INSERT INTO LINK(link_id, title, subtitle, description, url, COMMENT_COUNT,
                 VOTE_COUNT, creation_Date, user_id)
VALUES (NEXTVAL('LINK_SEQ'), 'Spring Cloud GCP 1.0 Released', 'cloud topic', '',
        'https://cloud.google.com/blog/products/gcp/calling-java-developers-spring-cloud-gcp-1-0-is-now-generally-available',
        0, 11, NOW(), 2);
INSERT INTO LINK(link_id, title, subtitle, description, url, COMMENT_COUNT,
                 VOTE_COUNT, creation_Date, user_id)
VALUES (NEXTVAL('LINK_SEQ'), 'Upload and Download Files in Java, Spring Boot',
        'handle files with spring-boot',
        'Simplest way to Upload and Download Files in Java with Spring Boot - Code to download from Github',
        'https://www.opencodez.com/uncategorized/file-upload-and-download-in-java-spring-boot.htm',
        0, -1, NOW(), 2);
INSERT INTO LINK(link_id, title, subtitle, description, url, COMMENT_COUNT,
                 VOTE_COUNT, creation_Date, user_id)
VALUES (NEXTVAL('LINK_SEQ'), 'Spring Boot Integration tests', 'test tutorial',
        '', 'https://www.baeldung.com/spring-security-integration-tests',
        0, 112, NOW(), 2);
INSERT INTO LINK(link_id, title, subtitle, description, url, COMMENT_COUNT,
                 VOTE_COUNT, creation_Date, user_id)
VALUES (NEXTVAL('LINK_SEQ'), 'File download using Spring REST',
        'how handle files with REST', '',
        'https://developer.okta.com/blog/2018/07/24/social-spring-boot',
        2, 17, NOW(), 3);


INSERT INTO user_clicked_links(link_id, user_id)
VALUES (1, 1);

INSERT INTO user_clicked_links(link_id, user_id)
VALUES (21, 1);

INSERT INTO user_clicked_links(link_id, user_id)
VALUES (41, 1);

INSERT INTO user_clicked_links(link_id, user_id)
VALUES (101, 2);

INSERT INTO Role(role_id, name)
VALUES (1, 'ROLE_USER');

INSERT INTO Role(role_id, name)
VALUES (2, 'ROLE_DBA');

INSERT INTO Role(role_id, name)
VALUES (3, 'ROLE_ADMIN');

INSERT INTO Role(role_id, name)
VALUES (4, 'ROLE_ACTUATOR');


INSERT INTO users_roles(user_Id, role_Id)
VALUES (1, 1);

INSERT INTO users_roles(user_Id, role_Id)
VALUES (1, 3);


INSERT INTO users_roles(user_Id, role_Id)
VALUES (1, 2);

INSERT INTO users_roles(user_Id, role_Id)
VALUES (2, 1);

INSERT INTO users_roles(user_Id, role_Id)
VALUES (2, 4);

INSERT INTO users_roles(user_Id, role_Id)
VALUES (2, 2);

INSERT INTO users_roles(user_Id, role_Id)
VALUES (3, 1);

INSERT INTO Comment(comment_id, comment_text, link_id, user_id, creation_Date)
VALUES (NEXTVAL('COMMENT_SEQ'), 'Erstes Kommentar von romakapt', 1, 1, NOW());

INSERT INTO Comment(comment_id, comment_text, link_id, user_id, creation_Date)
VALUES (NEXTVAL('COMMENT_SEQ'), 'zweites kommentar von dascha ', 21, 2, NOW());

INSERT INTO Comment(comment_id, comment_text, link_id, user_id, creation_Date)
VALUES (NEXTVAL('COMMENT_SEQ'), 'drittes kommentar von grom', 121, 3, NOW());

INSERT INTO Comment(comment_id, comment_text, link_id, user_id, creation_Date)
VALUES (NEXTVAL('COMMENT_SEQ'), '10 Kommentar von romakapt', 121, 1, NOW());

INSERT INTO Comment(comment_id, comment_text, link_id, user_id, creation_Date)
VALUES (NEXTVAL('COMMENT_SEQ'), 'Viertes Kommentar für zweiten Link von dascha',
        101, 2, NOW());

INSERT INTO Comment(comment_id, comment_text, link_id, user_id, creation_Date)
VALUES (NEXTVAL('COMMENT_SEQ'), 'fünftes Kommentar für zweiten Link von dascha',
        201, 2, NOW());

INSERT INTO Comment(comment_id, comment_text, link_id, user_id, creation_Date)
VALUES (NEXTVAL('COMMENT_SEQ'), 'sechstes kommentar von dascha.', 201, 2,
        NOW());

INSERT INTO Comment(comment_id, comment_text, link_id, user_id, creation_Date)
VALUES (NEXTVAL('COMMENT_SEQ'), 'siebtes kommentar von dascha.', 81, 2, NOW());

INSERT INTO Comment(comment_id, comment_text, link_id, user_id, creation_Date)
VALUES (NEXTVAL('COMMENT_SEQ'), 'achtes kommentar von dascha.', 21, 2, NOW());

INSERT INTO Comment(comment_id, comment_text, link_id, user_id, creation_Date)
VALUES (NEXTVAL('COMMENT_SEQ'), 'neuntes kommentar von grom.', 41, 3, NOW());

INSERT INTO Comment(comment_id, comment_text, link_id, user_id, creation_Date)
VALUES (NEXTVAL('COMMENT_SEQ'), 'zwölftes kommentar von grom.', 41, 3, NOW());

INSERT INTO Comment(comment_id, comment_text, link_id, user_id, creation_Date)
VALUES (NEXTVAL('COMMENT_SEQ'), '22 kommentar von grom.', 41, 3, NOW());

INSERT INTO Comment(comment_id, comment_text, link_id, user_id, creation_Date)
VALUES (NEXTVAL('COMMENT_SEQ'), '23 kommentar von grom', 41, 3, NOW());

INSERT INTO Comment(comment_id, comment_text, link_id, user_id, creation_Date)
VALUES (NEXTVAL('COMMENT_SEQ'), '24 kommentar von grom', 61, 3, NOW());

INSERT INTO Comment(comment_id, comment_text, link_id, user_id, creation_Date)
VALUES (NEXTVAL('COMMENT_SEQ'), '25 kommentar von grom', 61, 3, NOW());

INSERT INTO Comment(comment_id, comment_text, link_id, user_id, creation_Date)
VALUES (NEXTVAL('COMMENT_SEQ'), '26 kommentar von grom', 61, 3, NOW());

INSERT INTO Tag(tag_id, tag_name)
VALUES (NEXTVAL('TAG_SEQ'), 'Java');

INSERT INTO Tag(tag_id, tag_name)
VALUES (NEXTVAL('TAG_SEQ'), 'TypeScript');

INSERT INTO Tag(tag_id, tag_name)
VALUES (NEXTVAL('TAG_SEQ'), 'JavaScript');

INSERT INTO Tag(tag_id, tag_name)
VALUES (NEXTVAL('TAG_SEQ'), 'C');

INSERT INTO Tag(tag_id, tag_name)
VALUES (NEXTVAL('TAG_SEQ'), 'C++');

INSERT INTO Tag(tag_id, tag_name)
VALUES (NEXTVAL('TAG_SEQ'), 'PHP');

INSERT INTO Tag(tag_id, tag_name)
VALUES (NEXTVAL('TAG_SEQ'), 'Phyton');

INSERT INTO Tag(tag_id, tag_name)
VALUES (NEXTVAL('TAG_SEQ'), 'Delphi/Object Pascal');

INSERT INTO Tag(tag_id, tag_name)
VALUES (NEXTVAL('TAG_SEQ'), 'Swift');

INSERT INTO Tag(tag_id, tag_name)
VALUES (NEXTVAL('TAG_SEQ'), 'SQL');

INSERT INTO Tag(tag_id, tag_name)
VALUES (NEXTVAL('TAG_SEQ'), 'HTML-5');

INSERT INTO Tag(tag_id, tag_name)
VALUES (NEXTVAL('TAG_SEQ'), 'CSS');

INSERT INTO link_tags(link_id, tag_id)
VALUES (1, 2);

INSERT INTO link_tags(link_id, tag_id)
VALUES (21, 3);

INSERT INTO link_tags(link_id, tag_id)
VALUES (201, 3);

INSERT INTO link_tags(link_id, tag_id)
VALUES (201, 1);

INSERT INTO link_tags(link_id, tag_id)
VALUES (41, 1);


