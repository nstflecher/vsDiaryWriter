CREATE TABLE Journal(Id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT, Timestamp TIMESTAMP NOT NULL, Subject VARCHAR(2000), CONTENT CLOB(5242880));
CREATE TABLE Journal_CryptoInfo(Journal BIGINT NOT NULL, Key VARCHAR(200) NOT NULL, CryptoInfo_Key BINARY(600), CryptoInfo_Nounce BINARY(300), CryptoInfo_Algorithm VARCHAR(50), CryptoInfo_Mode VARCHAR(20), CONSTRAINT Journal_CryptoInfo FOREIGN KEY (Journal) REFERENCES Journal(Id) ON DELETE CASCADE);