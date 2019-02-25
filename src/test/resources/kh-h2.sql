-- ==================== DELETE ALL RECORDS ====================
DELETE FROM KOALIBEE_ALBUM;
DELETE FROM REVIEW;
DELETE FROM MOMENT;
DELETE FROM TRACK;
DELETE FROM ALBUM;
DELETE FROM KOALIBEE;
DELETE FROM CREDENTIALS;
DELETE FROM GENRE;

COMMIT;

-- ==================== DROP SEQUENCES ====================
DROP SEQUENCE SEQ_GENRE_ID;
DROP SEQUENCE SEQ_KOALIBEE_ID;
DROP SEQUENCE SEQ_CREDENTIALS_ID;
DROP SEQUENCE SEQ_MOMENT_ID;
DROP SEQUENCE SEQ_ALBUM_ID;
DROP SEQUENCE SEQ_TRACK_ID;
DROP SEQUENCE SEQ_REVIEW_ID;

-- ==================== RECREATE SEQUENCES ====================
CREATE SEQUENCE SEQ_GENRE_ID
START WITH 1
INCREMENT BY 1
NOCACHE;
CREATE SEQUENCE SEQ_KOALIBEE_ID
START WITH 1
INCREMENT BY 1
NOCACHE;
CREATE SEQUENCE SEQ_CREDENTIALS_ID
START WITH 1
INCREMENT BY 1
NOCACHE;
CREATE SEQUENCE SEQ_MOMENT_ID
START WITH 1
INCREMENT BY 1
NOCACHE;
CREATE SEQUENCE SEQ_ALBUM_ID
START WITH 1
INCREMENT BY 1
NOCACHE;
CREATE SEQUENCE SEQ_TRACK_ID
START WITH 1
INCREMENT BY 1
NOCACHE;
CREATE SEQUENCE SEQ_REVIEW_ID
START WITH 1
INCREMENT BY 1
NOCACHE;

-- ==================== TEST INSERT CREDENTIALS ====================
INSERT INTO CREDENTIALS (CREDENTIALS_ID, EMAIL, PASSWORD_HASH, PASSWORD_SALT)
VALUES (NEXTVAL('SEQ_CREDENTIALS_ID'), 'good.user@koalibee.com', 'CEB11D2AB244A259D27007E69C18F5AFD287F8D3AB4E1DB916C3F70E83BC8146', 'rfE4');
INSERT INTO CREDENTIALS (CREDENTIALS_ID, EMAIL, PASSWORD_HASH, PASSWORD_SALT)
VALUES (NEXTVAL('SEQ_CREDENTIALS_ID'), 'my.music@koalibee.com', '0444B4A904C26E19C5E5951D44359068E2E001339FE8193F91787EE36E182CCE', 'C58u');
INSERT INTO CREDENTIALS (CREDENTIALS_ID, EMAIL, PASSWORD_HASH, PASSWORD_SALT)
VALUES (NEXTVAL('SEQ_CREDENTIALS_ID'), 'esoma.aws@jenkins.com', '26BEF48AD5C232CE7E50A3106461188617AC7AB514479F26AD20F00FD99CAD81', 'f5vg');

-- ==================== TEST INSERT KOALIBEE ====================
INSERT INTO KOALIBEE (KOALIBEE_ID, FIRST_NAME, LAST_NAME, EMAIL, ETA_BALANCE, AVATAR, AVATAR_TYPE, CREDENTIALS_ID)
VALUES (NEXTVAL('SEQ_KOALIBEE_ID'), 'John', 'Smith', 'good.user@koalibee.com', 0, NULL, NULL, 1);
INSERT INTO KOALIBEE (KOALIBEE_ID, FIRST_NAME, LAST_NAME, EMAIL, ETA_BALANCE, AVATAR, AVATAR_TYPE, CREDENTIALS_ID)
VALUES (NEXTVAL('SEQ_KOALIBEE_ID'), 'Julie', 'Seals', 'my.music@koalibee.com', 0, '72616e646f6d6279746573', 'JPG', 2);
INSERT INTO KOALIBEE (KOALIBEE_ID, FIRST_NAME, LAST_NAME, EMAIL, ETA_BALANCE, AVATAR, AVATAR_TYPE, CREDENTIALS_ID)
VALUES (NEXTVAL('SEQ_KOALIBEE_ID'), 'Eddy', 'Soma', 'esoma.aws@jenkins.com', 50, NULL, NULL, 3);

-- ==================== TEST INSERT MOMENT ====================
INSERT INTO MOMENT (MOMENT_ID, POST_DATE, POST_COMMENT, KOALIBEE_ID)
VALUES (NEXTVAL('SEQ_MOMENT_ID'), PARSEDATETIME('2018-10-09', 'yyyy-MM-dd'), 'Jesus!', 1);
INSERT INTO MOMENT (MOMENT_ID, POST_DATE, POST_COMMENT, KOALIBEE_ID)
VALUES (NEXTVAL('SEQ_MOMENT_ID'), PARSEDATETIME('2019-01-16', 'yyyy-MM-dd'), 'I love tianshi dongman', 3);
INSERT INTO MOMENT (MOMENT_ID, POST_DATE, POST_COMMENT, KOALIBEE_ID)
VALUES (NEXTVAL('SEQ_MOMENT_ID'), PARSEDATETIME('2017-06-05', 'yyyy-MM-dd'), 'Nothing I want to say', 2);
INSERT INTO MOMENT (MOMENT_ID, POST_DATE, POST_COMMENT, KOALIBEE_ID)
VALUES (NEXTVAL('SEQ_MOMENT_ID'), PARSEDATETIME('2019-02-24', 'yyyy-MM-dd'), 'man, JavaScript is garbage', 2);
INSERT INTO MOMENT (MOMENT_ID, POST_DATE, POST_COMMENT, KOALIBEE_ID)
VALUES (NEXTVAL('SEQ_MOMENT_ID'), PARSEDATETIME('2018-11-30', 'yyyy-MM-dd'), 'Angular is way better than React.', 1);

-- ==================== TEST INSERT GENRE ====================

-- ==================== TEST INSERT ALBUM ====================

-- ==================== TEST INSERT TRACK ====================

-- ==================== TEST INSERT REVIEW ====================

COMMIT;
