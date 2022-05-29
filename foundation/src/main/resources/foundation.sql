/*
 * Good info:
 * Database, Table and Column Naming Conventions?
 * http://stackoverflow.com/questions/7662/database-table-and-column-naming-conventions
 * 
 * We have selected "PascalCase" for table names and "camelCase" for column names;
 * this corresponds to Java object and element names.  We do not apply "plurals" to
 * database table names for the same reason.
 * 
 * Unfortunately setting the variable for "lower_case_table_names=2" on Windows is
 * now problematic in MySQL so we will leave the value as the default = 1 which means
 * "table names are stored in lowercase on disk and comparisons are not case sensitive."
 * https://stackoverflow.com/questions/28540573/lower-case-table-names-set-to-2-workbench-still-does-not-allow-lowercase-databa
 * 
 * This means the table names are stored and displayed in lower case and you will not see "PascalCase" in the MySQL Workbench.
 */

/*
 * Run the CREATE SCHEMA command separately.  Then set the default schema as tnxsecure.
 */

CREATE SCHEMA `tnxsecure`;

/*
 * The CREATE TABLE commands can be run as a batch.
 */

CREATE TABLE `AddressType` (
  `addressTypeId` BIGINT unsigned NOT NULL AUTO_INCREMENT,
  `created` DATETIME NOT NULL,
  `updated` DATETIME NOT NULL,
  `updatedById` BIGINT unsigned NOT NULL,
  `dataSourceTypeValue` BIGINT unsigned NOT NULL,
  `addressType` BIGINT unsigned DEFAULT NULL,
  `addressTypeLabel` VARCHAR(512) DEFAULT NULL,
  PRIMARY KEY (`addressTypeId`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=latin1;

CREATE TABLE `Authentication` (
  `authenticationId` BIGINT unsigned NOT NULL AUTO_INCREMENT,
  `created` DATETIME NOT NULL,
  `updated` DATETIME NOT NULL,
  `updatedById` BIGINT unsigned NOT NULL,
  `dataSourceTypeValue` BIGINT unsigned NOT NULL,
  `serverSessionId` VARCHAR(256) DEFAULT NULL,
  `credentialProviderUuid` VARCHAR(64) DEFAULT NULL,
  `credentialType` VARCHAR(256) DEFAULT NULL,
  `credentialUuid` VARCHAR(256) DEFAULT NULL,
  `authenticationCode` VARCHAR(256) DEFAULT NULL,
  `verificationCode` VARCHAR(256) DEFAULT NULL,
  `userUuid` VARCHAR(256) DEFAULT NULL,
  `screenName` VARCHAR(64) DEFAULT NULL,
  `email` VARCHAR(256) DEFAULT NULL,
  `sessionUuid` VARCHAR(64) DEFAULT NULL,
  `remoteAddress` VARCHAR(45) DEFAULT NULL,
  `remoteHost` VARCHAR(45) DEFAULT NULL,
  `signOnUuid` VARCHAR(64) DEFAULT NULL,
  PRIMARY KEY (`authenticationId`)
) ENGINE=InnoDB AUTO_INCREMENT=439 DEFAULT CHARSET=latin1;

CREATE TABLE  `tnxsecure`.`Counter` (
  `counterId` INT unsigned NOT NULL AUTO_INCREMENT,
  `counter` INT unsigned DEFAULT NULL,
  PRIMARY KEY (`counterId`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

CREATE TABLE `Credential` (
  `credentialId` BIGINT unsigned NOT NULL AUTO_INCREMENT,
  `created` DATETIME NOT NULL,
  `updated` DATETIME NOT NULL,
  `updatedById` BIGINT unsigned NOT NULL,
  `dataSourceTypeValue` BIGINT unsigned NOT NULL,
  `credentialTypeId` BIGINT DEFAULT NULL,
  `credentialUuid` VARCHAR(64) DEFAULT NULL,
  `credentialProviderSignatureAlgorithm` VARCHAR(256) DEFAULT NULL,
  `credentialProviderSecureHashAlgorithm` VARCHAR(256) DEFAULT NULL,
  `activationTimestamp` VARCHAR(64) DEFAULT NULL,
  `expirationTimestamp` VARCHAR(64) DEFAULT NULL,
  `screenName` VARCHAR(256) DEFAULT NULL,
  `email` VARCHAR(256) DEFAULT NULL,
  `userUuid` VARCHAR(64) DEFAULT NULL,
  `firstName` VARCHAR(64) DEFAULT NULL,
  `lastName` VARCHAR(64) DEFAULT NULL,
  `mobilePhone` VARCHAR(64) DEFAULT NULL,
  `authenticationCode` VARCHAR(64) DEFAULT NULL,
  `sessionUuid` VARCHAR(64) DEFAULT NULL,
  `remoteAddress` VARCHAR(45) DEFAULT NULL,
  `remoteHost` VARCHAR(45) DEFAULT NULL,
  `verificationCode` VARCHAR(64) DEFAULT NULL,
  `publicKeyHex` blob,
  `json` blob,
  `inactiveFlag` TINYINT unsigned DEFAULT '0',
  PRIMARY KEY (`credentialId`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=60 DEFAULT CHARSET=latin1;

CREATE TABLE `CredentialAddress` (
  `credentialAddressId` BIGINT unsigned NOT NULL AUTO_INCREMENT,
  `created` DATETIME NOT NULL,
  `updated` DATETIME NOT NULL,
  `updatedById` BIGINT unsigned NOT NULL,
  `dataSourceTypeValue` BIGINT unsigned NOT NULL,
  `credentialId` BIGINT unsigned DEFAULT NULL,
  `addressType` BIGINT unsigned DEFAULT NULL,
  `addressLineOne` VARCHAR(256) DEFAULT NULL,
  `addressLineTwo` VARCHAR(256) DEFAULT NULL,
  `city` VARCHAR(256) DEFAULT NULL,
  `stateOrProvince` VARCHAR(64) DEFAULT NULL,
  `postalCode` VARCHAR(64) DEFAULT NULL,
  `country` VARCHAR(256) DEFAULT NULL,
  PRIMARY KEY (`credentialAddressId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `CredentialProvider` (
  `credentialProviderId` BIGINT unsigned NOT NULL AUTO_INCREMENT,
  `created` DATETIME NOT NULL,
  `updated` DATETIME NOT NULL,
  `updatedById` BIGINT unsigned NOT NULL,
  `dataSourceTypeValue` BIGINT unsigned NOT NULL,
  `credentialProviderUuid` VARCHAR(64) DEFAULT NULL,
  `credentialProviderName` VARCHAR(512) NOT NULL,
  `domainName` VARCHAR(512) NOT NULL,
  `adminFirstName` VARCHAR(64) DEFAULT NULL,
  `adminLastName` VARCHAR(64) DEFAULT NULL,
  `adminEmail` VARCHAR(256) DEFAULT NULL,
  `retrieveCredentialMetaDataUrl` VARCHAR(512) DEFAULT NULL,
  `createCredentialUrl` VARCHAR(512) DEFAULT NULL,
  `signOnUrl` VARCHAR(512) DEFAULT NULL,
  `cancelSignOnUrl` VARCHAR(512) DEFAULT NULL,
  `retrieveUnsignedDistributedLedgerUrl` VARCHAR(512) DEFAULT NULL,
  `returnSignedDistributedLedgerUrl` VARCHAR(512) DEFAULT NULL,
  `sendFundsUrl` VARCHAR(512) DEFAULT NULL,
  `receiveFundsUrl` VARCHAR(512) DEFAULT NULL,
  `acceptFundsUrl` VARCHAR(512) DEFAULT NULL,
  `confirmFundsUrl` VARCHAR(512) DEFAULT NULL,
  `deleteCredentialUrl` VARCHAR(512) DEFAULT NULL,
  `retrieveTransactionUuidUrl` VARCHAR(512) DEFAULT NULL,
  PRIMARY KEY (`credentialProviderId`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=latin1;

CREATE TABLE `CredentialType` (
  `credentialTypeId` BIGINT unsigned NOT NULL AUTO_INCREMENT,
  `created` DATETIME NOT NULL,
  `updated` DATETIME NOT NULL,
  `updatedById` BIGINT unsigned NOT NULL,
  `dataSourceTypeValue` BIGINT unsigned NOT NULL,
  `credentialProviderUuid` VARCHAR(512) DEFAULT NULL,
  `credentialType` VARCHAR(512) DEFAULT NULL,
  `displayName` VARCHAR(512) DEFAULT NULL,
  `credentialIconUrl` VARCHAR(512) DEFAULT NULL,
  `publicPrivateKeyUuid` VARCHAR(512) NOT NULL DEFAULT '0',
  `expirationMonths` INT DEFAULT '0',
  PRIMARY KEY (`credentialTypeId`) USING BTREE,
  UNIQUE KEY `credentialType_UNIQUE` (`credentialType`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=latin1;

CREATE TABLE `DistributedLedger` (
  `distributedLedgerId` BIGINT unsigned NOT NULL AUTO_INCREMENT,
  `created` DATETIME NOT NULL,
  `updated` DATETIME NOT NULL,
  `updatedById` BIGINT unsigned NOT NULL,
  `dataSourceTypeValue` BIGINT unsigned NOT NULL,
  `distributedLedgerUuid` VARCHAR(256) DEFAULT NULL,
  `jsonDistributedLedger` blob,
  `credentialProviderUuid` VARCHAR(64) DEFAULT NULL,
  `credentialType` VARCHAR(256) DEFAULT NULL,
  `credentialUuid` VARCHAR(256) DEFAULT NULL,
  `authenticationCode` VARCHAR(256) DEFAULT NULL,
  `verificationCode` VARCHAR(256) DEFAULT NULL,
  `userUuid` VARCHAR(256) DEFAULT NULL,
  `screenName` VARCHAR(64) DEFAULT NULL,
  `email` VARCHAR(256) DEFAULT NULL,
  `sessionUuid` VARCHAR(64) DEFAULT NULL,
  `signatureCompleteUuid` VARCHAR(64) DEFAULT NULL,
  PRIMARY KEY (`distributedLedgerId`)
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=latin1;

CREATE TABLE `Organization` (
  `organizationId` BIGINT unsigned NOT NULL AUTO_INCREMENT,
  `created` DATETIME NOT NULL,
  `updated` DATETIME NOT NULL,
  `updatedById` BIGINT unsigned NOT NULL,
  `dataSourceTypeValue` BIGINT unsigned NOT NULL,
  `credentialId` BIGINT unsigned DEFAULT NULL,
  `organizationName` VARCHAR(64) DEFAULT NULL,
  `organizationUrl` VARCHAR(256) DEFAULT NULL,
  `title` VARCHAR(256) DEFAULT NULL,
  `phone` VARCHAR(64) DEFAULT NULL,
  PRIMARY KEY (`organizationId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `PublicPrivateKey` (
  `publicPrivateKeyId` BIGINT unsigned NOT NULL AUTO_INCREMENT,
  `created` DATETIME NOT NULL,
  `updated` DATETIME NOT NULL,
  `updatedById` BIGINT unsigned NOT NULL,
  `dataSourceTypeValue` BIGINT unsigned NOT NULL,
  `keyOwner` VARCHAR(256) DEFAULT NULL,
  `publicKeyHex` blob,
  `privateKeyHex` blob,
  `inactiveFlag` TINYINT unsigned DEFAULT NULL,
  `uuid` VARCHAR(64) DEFAULT NULL,
  PRIMARY KEY (`publicPrivateKeyId`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=latin1;

CREATE TABLE `ReceiveFunds` (
  `fundsTransferId` BIGINT unsigned NOT NULL AUTO_INCREMENT,
  `created` DATETIME NOT NULL,
  `updated` DATETIME NOT NULL,
  `updatedById` BIGINT unsigned NOT NULL,
  `dataSourceTypeValue` BIGINT unsigned NOT NULL,
  `fundsTransferUuid` VARCHAR(64) DEFAULT NULL,
  `userUuid` VARCHAR(64) DEFAULT NULL,
  `credentialUuid` VARCHAR(64) DEFAULT NULL,
  `recipientName` VARCHAR(64) DEFAULT NULL,
  `recipientEmail` VARCHAR(64) DEFAULT NULL,
  `recipientPhoneNumber` VARCHAR(64) DEFAULT NULL,
  `transferAmount` decimal(10,2) DEFAULT NULL,
  `recipientCredentialProviderUuid` VARCHAR(64) DEFAULT NULL,
  `recipientCredentialUuid` VARCHAR(64) DEFAULT NULL,
  `recipientUuid` VARCHAR(64) DEFAULT NULL,
  `json` blob,
  PRIMARY KEY (`fundsTransferId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `SecurityKey` (
  `securityKeyId` BIGINT unsigned NOT NULL AUTO_INCREMENT,
  `created` DATETIME NOT NULL,
  `updated` DATETIME NOT NULL,
  `updatedById` BIGINT unsigned NOT NULL,
  `dataSourceTypeValue` BIGINT unsigned NOT NULL,
  `obfuscatedIdentifier` VARCHAR(256) DEFAULT NULL,
  `userSecurityKeyEncrypted` VARCHAR(512) DEFAULT NULL,
  PRIMARY KEY (`securityKeyId`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=124 DEFAULT CHARSET=latin1;

CREATE TABLE `SendFunds` (
  `fundsTransferId` BIGINT unsigned NOT NULL AUTO_INCREMENT,
  `created` DATETIME NOT NULL,
  `updated` DATETIME NOT NULL,
  `updatedById` BIGINT unsigned NOT NULL,
  `dataSourceTypeValue` BIGINT unsigned NOT NULL,
  `fundsTransferUuid` VARCHAR(64) DEFAULT NULL,
  `userUuid` VARCHAR(64) DEFAULT NULL,
  `credentialUuid` VARCHAR(64) DEFAULT NULL,
  `recipientName` VARCHAR(64) DEFAULT NULL,
  `recipientEmail` VARCHAR(64) DEFAULT NULL,
  `recipientPhoneNumber` VARCHAR(64) DEFAULT NULL,
  `transferAmount` decimal(10,2) DEFAULT NULL,
  `recipientCredentialProviderUuid` VARCHAR(64) DEFAULT NULL,
  `recipientCredentialUuid` VARCHAR(64) DEFAULT NULL,
  `recipientUuid` VARCHAR(64) DEFAULT NULL,
  `json` blob,
  PRIMARY KEY (`fundsTransferId`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=latin1;

CREATE TABLE `Transaction` (
  `transactionId` BIGINT unsigned NOT NULL AUTO_INCREMENT,
  `created` DATETIME NOT NULL,
  `updated` DATETIME NOT NULL,
  `updatedById` BIGINT unsigned NOT NULL,
  `dataSourceTypeValue` BIGINT unsigned NOT NULL,
  `userUuid` VARCHAR(256) DEFAULT NULL,
  `transactionUuid` VARCHAR(256) DEFAULT NULL,
  `inactiveFlag` TINYINT unsigned DEFAULT NULL,
  PRIMARY KEY (`transactionId`)
) ENGINE=InnoDB AUTO_INCREMENT=500 DEFAULT CHARSET=latin1;

CREATE TABLE `TrustedSystem` (
  `trustedSystemId` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `created` datetime NOT NULL,
  `updated` datetime NOT NULL,
  `updatedById` bigint(20) unsigned NOT NULL,
  `dataSourceTypeValue` bigint(20) unsigned NOT NULL,
  `email` varchar(512) DEFAULT NULL,
  `userUuid` varchar(512) DEFAULT NULL,
  `trustedSystemUuid` varchar(512) DEFAULT NULL,
  `passwordHash` varchar(512) DEFAULT NULL,
  `credentialProviderUuid` varchar(512) DEFAULT NULL,
  `credentialType` varchar(512) DEFAULT NULL,
  `credentialUuid` varchar(512) DEFAULT NULL,
  PRIMARY KEY (`trustedSystemId`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=latin1;

CREATE TABLE `User` (
  `userId` BIGINT unsigned NOT NULL AUTO_INCREMENT,
  `created` DATETIME NOT NULL,
  `updated` DATETIME NOT NULL,
  `updatedById` BIGINT unsigned NOT NULL,
  `dataSourceTypeValue` BIGINT unsigned NOT NULL,
  `screenName` VARCHAR(64) DEFAULT NULL,
  `email` VARCHAR(256) DEFAULT NULL,
  `phone` VARCHAR(64) DEFAULT NULL,
  `firstName` VARCHAR(64) DEFAULT NULL,
  `lastName` VARCHAR(64) DEFAULT NULL,
  `userUuid` VARCHAR(64) DEFAULT NULL,
  `publicKey` blob,
  `inactiveFlag` TINYINT unsigned DEFAULT NULL,
  `refCode` VARCHAR(64) DEFAULT NULL,
  `firebaseDeviceId` VARCHAR(256) DEFAULT NULL,
  PRIMARY KEY (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=125 DEFAULT CHARSET=latin1;

CREATE TABLE `UserAddress` (
  `userAddressId` BIGINT unsigned NOT NULL AUTO_INCREMENT,
  `created` DATETIME NOT NULL,
  `updated` DATETIME NOT NULL,
  `updatedById` BIGINT unsigned NOT NULL,
  `dataSourceTypeValue` BIGINT unsigned NOT NULL,
  `userId` BIGINT unsigned NOT NULL,
  `addressType` BIGINT unsigned NOT NULL,
  `addressLineOne` VARCHAR(256) DEFAULT NULL,
  `addressLineTwo` VARCHAR(256) DEFAULT NULL,
  `city` VARCHAR(256) DEFAULT NULL,
  `state` VARCHAR(64) DEFAULT NULL,
  `postalCode` VARCHAR(64) DEFAULT NULL,
  `country` VARCHAR(256) DEFAULT NULL,
  PRIMARY KEY (`userAddressId`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=125 DEFAULT CHARSET=latin1;

CREATE TABLE `UserSessionTracking` (
  `userSessionTrackingId` BIGINT unsigned NOT NULL AUTO_INCREMENT,
  `created` DATETIME NOT NULL,
  `updated` DATETIME NOT NULL,
  `updatedById` BIGINT unsigned NOT NULL,
  `dataSourceTypeValue` BIGINT unsigned NOT NULL,
  `userIdentifier` VARCHAR(256) DEFAULT NULL,
  `sessionIdentifier` VARCHAR(512) DEFAULT NULL,
  PRIMARY KEY (`userSessionTrackingId`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=43470 DEFAULT CHARSET=latin1;

CREATE TABLE `Visitor` (
  `visitorId` INT unsigned NOT NULL AUTO_INCREMENT,
  `created` DATETIME NOT NULL,
  `updated` DATETIME NOT NULL,
  `updatedById` INT unsigned DEFAULT NULL,
  `dataSourceTypeValue` INT unsigned DEFAULT NULL,
  `remoteAddress` VARCHAR(45) DEFAULT NULL,
  `remoteHost` VARCHAR(45) DEFAULT NULL,
  `refCode` VARCHAR(45) DEFAULT NULL,
  PRIMARY KEY (`visitorId`)
) ENGINE=InnoDB AUTO_INCREMENT=7053 DEFAULT CHARSET=latin1;

CREATE TABLE `VisitorThreat` (
  `visitorId` INT unsigned NOT NULL AUTO_INCREMENT,
  `created` DATETIME NOT NULL,
  `updated` DATETIME NOT NULL,
  `updatedById` INT unsigned DEFAULT NULL,
  `dataSourceTypeValue` INT unsigned DEFAULT NULL,
  `remoteAddress` VARCHAR(45) DEFAULT NULL,
  `remoteHost` VARCHAR(45) DEFAULT NULL,
  `refCode` VARCHAR(45) DEFAULT NULL,
  PRIMARY KEY (`visitorId`)
) ENGINE=InnoDB AUTO_INCREMENT=6100 DEFAULT CHARSET=latin1;

/* 
 *Clears the user data from existing non-type tables: 
 */

DELETE FROM tnxsecure.Authentication WHERE authenticationID > 0;
DELETE FROM tnxsecure.Credential WHERE credentialId > 0;
DELETE FROM tnxsecure.CredentialAddress WHERE credentialAddressID > 0;
DELETE FROM tnxsecure.DistributedLedger WHERE distributedLedgerId > 0;
DELETE FROM tnxsecure.Organization WHERE organizationId > 0;
DELETE FROM tnxsecure.ReceiveFunds WHERE fundsTransferId > 0;
DELETE FROM tnxsecure.SecurityKey WHERE securityKeyId > 0;
DELETE FROM tnxsecure.SendFunds WHERE fundsTransferId > 0;
DELETE FROM tnxsecure.Transaction WHERE transactionId > 0;
DELETE FROM tnxsecure.TrustedSystem WHERE trustedSystemId > 0;
DELETE FROM tnxsecure.User WHERE userId > 0;
DELETE FROM tnxsecure.UserAddress WHERE userAddressId > 0;
DELETE FROM tnxsecure.UserSessionTracking WHERE userSessionTrackingId > 0;







