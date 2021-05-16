/*
 * (c) Copyright 2021 ~ Trust Nexus, Inc.
 * All technologies described here in are "Patent Pending". 
 * License information:  http://www.trustnexus.io/license.htm
 * 
 * AS LONG AS THIS NOTICE IS MAINTAINED THE LICENSE PERMITS REDISTRIBUTION OR RE-POSTING  
 * OF THIS SOURCE CODE TO A PUBLIC REPOSITORY (WITH OR WITHOUT MODIFICATIONS)! 
 * 
 * Report License Violations:  trustnexus.io@austin.rr.com 
 * 
 * This is a beta version:  0.0.1
 * Suggestions for code improvements:  trustnexus.io@austin.rr.com
 */

package io.trustnexus.util;

public class Constants {

    // TODO: Change for your system.
    public static final String COOKIE_PATH = "/foundation/";

    // Database constants
    public static final String DRIVER_CLASS = "DRIVER_CLASS";
    public static final String JDBC_URL = "JDBC_URL";
    public static final String DATA_BASE_USER = "DATA_BASE_USER";
    public static final String DATA_BASE_PASSWORD = "DATA_BASE_PASSWORD";

    public static final int MASTER_USER = 1;
    public static final int DATA_SOURCE_APPLICATION = 1;

    public static final String TNX_SECURE_KEY_OWNER = "TNX_SECURE";
    public static final String MOBILE_APP_PROVIDER_KEY_OWNER = "MOBILE_APP_PROVIDER";
    public static final String COMMUNITY_WORLD_BANK = "COMMUNITY_WORLD_BANK";

    // ------------------------------------------------------------------------------------------------------------------

    /*
     * The following constants are used in the the four initialization classes:
     * 
     * InitializeA_Types InitializeB_Keys InitializeC_CredentialProvider
     * InitializeD_Types
     * 
     * Since these classes will most likely be run from within Eclipse in a secure
     * administrative environment, it makes sense to keep the following constants in
     * code, rather than create a properties file that would be referenced from a
     * jar file.
     * 
     * Some of these same values which are used in the web application are also
     * found in /resources/config.properties
     */
    public static final String SYSTEM_INIT_DATABASE_URL = "jdbc:mysql://localhost:3306/tnxsecure?useSSL=false";
    public static final String SYSTEM_INIT_DATABASE_USERNAME = "root";
    public static final String SYSTEM_INIT_DATABASE_PASSWORD = "tnxsecure!234";

    /*
     * If you create a test financial credential provider and a test financial
     * credential type (".Financial"), set the credentialProviderUuid here,
     * 
     */
    public static final String TEST_FINANCIAL_CREDENTIAL_PROVIDER_UUID = "1563454259872-A8EE1E6B-A89D-4474-A29D-9ADEC38FAED4";

    // ------------------------------------------------------------------------------------------------------------------

    // config.properties

    public static final String LOG_FILE = "LOG_FILE";
    public static final String LOG_FILE_LINE_LENGTH = "LOG_FILE_LINE_LENGTH";

    public static final String MAIL_SESSION = "MAIL_SESSION";

    public final static String FIREBASE_AUTH_KEY = "FIREBASE_AUTH_KEY";
    public final static String FIREBASE_API_URL = "FIREBASE_API_URL";

    // ------------------------------------------------------------------------------------------------------------------

    /*
     * application.properties Labels and messages that would be translated for
     * internationalization.
     */

    public static final String PLEASE_REFRESH = "PLEASE_REFRESH";
    public static final String PROBLEM_WITH_AUTHENTICATION_SERVER = "PROBLEM_WITH_AUTHENTICATION_SERVER";
    public static final String PROBLEM_WITH_VERIFICATION_SERVER = "PROBLEM_WITH_VERIFICATION_SERVER";
    public static final String CREDENTIAL_CREATED = "CREDENTIAL_CREATED";
    public static final String USER_ALREADY_EXISTS = "USER_ALREADY_EXISTS";
    public static final String USER_ALREADY_HAS_CREDENTIAL = "USER_ALREADY_HAS_CREDENTIAL";
    public static final String USER_DOES_NOT_EXIST = "USER_DOES_NOT_EXIST";
    public static final String USER_SUCCESSFULLY_CREATED = "USER_SUCCESSFULLY_CREATED";

    public static final String CREDENTIAL_DOES_NOT_EXIST = "CREDENTIAL_DOES_NOT_EXIST";
    public static final String CREDENTIAL_ALREADY_DELETED = "CREDENTIAL_ALREADY_DELETED";
    public static final String CREDENTIAL_CREATION_PROCESS_NOT_INITIALIZED = "CREDENTIAL_CREATION_PROCESS_NOT_INITIALIZED";
    public static final String OBFUSCATED_IDENTIFIER_DOES_NOT_EXIST = "OBFUSCATED_IDENTIFIER_DOES_NOT_EXIST";
    public static final String PROFILE_UPDATED = "PROFILE_UPDATED";
    public static final String PUBLIC_KEY_SAVED = "PUBLIC_KEY_SAVED";
    public static final String PUBLIC_KEY_ALREADY_CREATED = "PUBLIC_KEY_ALREADY_CREATED";
    public static final String SIGN_ON_SUCCESSFUL = "SIGN_ON_SUCCESSFUL";
    public static final String SIGN_ON_CANCELED = "SIGN_ON_CANCELED";
    public static final String SIGNATURE_SUCCESSFUL = "SIGNATURE_SUCCESSFUL";
    public static final String TRUSTED_SYSTEM = "TRUSTED_SYSTEM";

    public static final String MESSAGE_INTEGRITY_COMPROMISED = "MESSAGE_INTEGRITY_COMPROMISED";
    public static final String INCORRECT_PIN = "INCORRECT_PIN";
    public static final String SIGNATURE_VERIFIED = "SIGNATURE_VERIFIED";
    public static final String SIGNATURE_NOT_VERIFIED = "SIGNATURE_NOT_VERIFIED";
    public static final String SECURITY_KEY_ALREADY_EXISTS = "SECURITY_KEY_ALREADY_EXISTS";
    public static final String SECURITY_KEY_CREATED = "SECURITY_KEY_CREATED";
    public static final String IDENTITY_CREDENTIAL_DELETED = "IDENTITY_CREDENTIAL_DELETED";
    public static final String CREATE_IDENTITY_CREDENTIAL_CANCELED = "CREATE_IDENTITY_CREDENTIAL_CANCELED";
    public static final String NOTIFICATION_TITLE = "NOTIFICATION_TITLE";

    public static final String NOTIFICATION_BODY_CREATE_CREDENTIAL = "NOTIFICATION_BODY_CREATE_CREDENTIAL";
    public static final String NOTIFICATION_BODY_WEB_AUTHN_PLUS_SIGN_ON = "NOTIFICATION_BODY_WEB_AUTHN_PLUS_SIGN_ON";
    public static final String NOTIFICATION_BODY_FUNDS_TRANSFER_CONFIRMED = "NOTIFICATION_BODY_FUNDS_TRANSFER_CONFIRMED";
    public static final String FUNDS_TRANSFER_INITIALIZED = "FUNDS_TRANSFER_INITIALIZED";
    public static final String FUNDS_TRANSFER_ACCEPTED = "FUNDS_TRANSFER_ACCEPTED";
    public static final String DISTRIBUTED_LEDGER_COMPROMISED = "DISTRIBUTED_LEDGER_COMPROMISED";
    public static final String INVALID_CREDENTIAL = "INVALID_CREDENTIAL";
    public static final String ALREADY_SIGNED_ON = "ALREADY_SIGNED_ON";
    public static final String ALREADY_SIGNED = "ALREADY_SIGNED";
    public static final String NOT_SIGNED_ON = "NOT_SIGNED_ON";
    public static final String TRUSTED_SYSTEM_ESTABLISHED = "TRUSTED_SYSTEM_ESTABLISHED";

    public static final String ADDRESS_TYPE_LABEL_LEGAL = "ADDRESS_TYPE_LABEL_LEGAL";
    public static final String ADDRESS_TYPE_LABEL_MAILING = "ADDRESS_TYPE_LABEL_MAILING";
    public static final String ADDRESS_TYPE_LABEL_LEGAL_AND_MAILING = "ADDRESS_TYPE_LABEL_LEGAL_AND_MAILING";
    public static final String ADDRESS_TYPE_LABEL_ORGANIZATION = "ADDRESS_TYPE_LABEL_ORGANIZATION";
    public static final String INVALID_TRANSACTION_UUID = "INVALID_TRANSACTION_UUID";
    public static final String TRANSACTION_UUID_VERIFIED = "TRANSACTION_UUID_VERIFIED";

    public static final String ACTIVATION_SUCCESSFUL = "ACTIVATION_SUCCESSFUL";
    public static final String THANK_YOU_FOR_ACTIVATING = "THANK_YOU_FOR_ACTIVATING";
    public static final String CONTACT_EMAIL = "CONTACT_EMAIL";
    public static final String ACTIVATION_NOT_SUCCESSFUL = "ACTIVATION_NOT_SUCCESSFUL";
    public static final String ACTIVATION_PROBLEM = "ACTIVATION_PROBLEM";
    public static final String CONTACT_US = "CONTACT_US";

    public static final String SEND_EMAIL_ADDRESS = "SEND_EMAIL_ADDRESS";

    // ------------------------------------------------------------------------------------------------------------------

    // No "I" or "O" which eliminates numerical confusion.
    public static final char[] CHARACTER_ARRAY = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N', 'P',
            'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

    public static final String[] SENSITIVE_WORDS_FOUR = { "ABBO", "ALLA", "ANAL", "ANUS", "ARAB", "ARSE", "BANG",
            "BARF", "BAST", "BLOW", "BOMB", "BOMD", "BOOB", "BOOM", "BUNG", "BUTT", "CHAV", "CHIN", "CIGG", "CIGS",
            "CLIT", "COCK", "COHE", "COON", "CRAP", "CUMM", "CUNN", "CUNT", "DAGO", "DAMN", "DEAD", "DEGO", "DETH",
            "DIED", "DIES", "DIKE", "DINK", "DONG", "DOOM", "DOPE", "DRUG", "DUMB", "DYKE", "EVIL", "FAGG", "FART",
            "FEAR", "FIRE", "FLOO", "FORE", "FUCK", "FUKS", "GOOK", "GOYM", "GUNN", "GYPO", "GYPP", "HEBE", "HEEB",
            "HELL", "HOBO", "HOES", "HOLE", "HOMO", "HORE", "HORK", "ITCH", "JIGA", "JISM", "JIZM", "JIZZ", "JUGS",
            "KIKE", "KILL", "KINK", "KOON", "KRAP", "KUNT", "KYKE", "LEZZ", "LIES", "LIMY", "METH", "MILF", "MOFO",
            "MOKY", "MUFF", "MUNT", "NAZI", "NIGG", "NIGR", "NUDE", "NUKE", "ORAL", "ORGA", "ORGY", "PAKI", "PAYO",
            "PERV", "PHUK", "PHUQ", "PIMP", "PISS", "PIXY", "POHM", "POON", "POOP", "PORN", "PRIC", "PUBE", "PUDD",
            "PUKE", "PUSS", "PUSY", "QUIM", "RAPE", "RUMP", "SATN", "SCAG", "SCAT", "SCUM", "SEXX", "SEXY", "SHAG",
            "SHAT", "SHAV", "SHIT", "SICK", "SKUM", "SLAV", "SLUT", "SMUT", "SNOT", "SPIC", "SPIG", "SPIK", "SPIT",
            "SUCC", "SUCK", "TAFF", "TANG", "TARD", "TEAT", "TITT", "TITS", "TITY", "TURD", "TWAT", "VIBR", "WANK",
            "WHIT", "WHIZ", "WHOP", "WHOR", "WUSS", "XXXX" };

    public static final String[] SENSITIVE_WORDS_THREE = { "ALA", "CUM", "FAG", "FUC", "GOD", "JAP", "JIZ", "KIL",
            "LIE", "LDS", "LSD", "NIG", "PIS", "PEE", "PUD", "PUS", "SEX", "SAD", "SIC", "SUC", "SUX", "TIT", "WIZ",
            "XXX" };

    // ------------------------------------------------------------------------------------------------------------------

    public static final int DEFAULT_CREDENTIAL_EXPIRATION_MONTHS = 24;

    /*
     * Constants used in code, usually between the web application and the TNX
     * Secure mobile app.
     */

    public static final String SCREEN_NAME = "screenName";
    public static final String EMAIL = "email";
    public static final String USER_UUID = "userUuid";
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String PHONE = "phone";
    public static final String MOBILE_PHONE = "mobilePhone";
    public static final String FIRE_BASE_DEVICE_ID = "firebaseDeviceId";
    public static final String OBFUSCATED_IDENTIFIER = "obfuscatedIdentifier";
    public static final String CANCELED = "CANCELED";

    public static final int ADDRESS_TYPE_LEGAL = 1;
    public static final int ADDRESS_TYPE_MAILING = 2;
    public static final int ADDRESS_TYPE_LEGAL_AND_MAILING = 3;
    public static final int ADDRESS_TYPE_ORGANIZATION = 4;

    public static final String ADDRESS = "address";
    public static final String ADDRESS_TYPE = "addressType";

    public static final String ADDRESS_LINE_ONE = "addressLineOne";
    public static final String ADDRESS_LINE_TWO = "addressLineTwo";
    public static final String ADDRESS_CITY = "addressCity";
    public static final String ADDRESS_STATE_PROVINCE = "addressStateProvince";
    public static final String ADDRESS_POSTAL_CODE = "addressPostalCode";
    public static final String ADDRESS_COUNTRY = "addressCountry";

    public static final String LEGAL_ADDRESS_LINE_ONE = "legalAddressLineOne";
    public static final String LEGAL_ADDRESS_LINE_TWO = "legalAddressLineTwo";
    public static final String LEGAL_ADDRESS_CITY = "legalAddressCity";
    public static final String LEGAL_ADDRESS_STATE = "legalAddressState";
    public static final String LEGAL_ADDRESS_POSTAL_CODE = "legalAddressPostalCode";
    public static final String LEGAL_ADDRESS_COUNTRY = "legalAddressCountry";

    public static final String MAILING_ADDRESS_LINE_ONE = "mailingAddressLineOne";
    public static final String MAILING_ADDRESS_LINE_TWO = "mailingAddressLineTwo";
    public static final String MAILING_ADDRESS_CITY = "mailingAddressCity";
    public static final String MAILING_ADDRESS_STATE = "mailingAddressState";
    public static final String MAILING_ADDRESS_POSTAL_CODE = "mailingAddressPostalCode";
    public static final String MAILING_ADDRESS_COUNTRY = "mailingAddressCountry";

    public static final String ORGANIZATION_ADDRESS_LINE_ONE = "organizationAddressLineOne";
    public static final String ORGANIZATION_ADDRESS_LINE_TWO = "organizationAddressLineTwo";
    public static final String ORGANIZATION_ADDRESS_CITY = "organizationAddressCity";
    public static final String ORGANIZATION_ADDRESS_STATE = "organizationAddressState";
    public static final String ORGANIZATION_ADDRESS_POSTAL_CODE = "organizationAddressPostalCode";
    public static final String ORGANIZATION_ADDRESS_COUNTRY = "organizationAddressCountry";

    public static final String ORGANIZATION = "organization";
    public static final String ORGANIZATION_NAME = "organizationName";
    public static final String ORGANIZATION_URL = "organizationUrl";
    public static final String ORGANIZATION_TITLE = "organizationTitle";
    public static final String ORGANIZATION_PHONE = "organizationPhone";

    public static final String CREDENTIAL_PROVIDER_SIGNATURE_ALGORITHM = "credentialProviderSignatureAlgorithm";
    public static final String CREDENTIAL_PROVIDER_SECURE_HASH_ALGORITHM = "credentialProviderSecureHashAlgorithm";

    public static final String AUTHENTICATION_CODE = "authenticationCode";
    public static final String REGISTRATION_CODE = "registrationCode";
    public static final String SESSION_UUID = "sessionUuid";
    public static final String SESSION_UUID_SIGNED = "sessionUuidSigned";
    public static final String VERIFICATION_CODE = "verificationCode";
    public static final String SIGN_ON_UUID = "signOnUuid";
    public static final String SIGNATURE_COMPLETED_UUID = "signatureCompletedUuid";
    public static final String AUTHENTICATION_STATUS = "authenticationStatus";
    public static final String DISTRIBUTED_LEDGER_INITIALIZED = "distributedLedgerInitialized";
    public static final String NOT_AUTHENTICATED = "notAuthenticated";
    public static final String CREDENTIAL = "credential";
    public static final String CREDENTIAL_UUID = "credentialUuid";
    public static final String USER_LOG = "userLog";

    public static final String CREDENTIAL_PROVIDER_NAME = "credentialProviderName";
    public static final String CREDENTIAL_PROVIDER_UUID = "credentialProviderUuid";
    public static final String DOMAIN_NAME = "domainName";
    public static final String RETRIEVE_CREDENTIAL_META_DATA_URL = "retrieveCredentialMetaDataUrl";
    public static final String CREATE_CREDENTIAL_URL = "createCredentialUrl";
    public static final String DELETE_CREDENTIAL_URL = "deleteCredentialUrl";
    public static final String SIGN_ON_URL = "signOnUrl";
    public static final String CANCEL_SIGN_ON_URL = "cancelSignOnUrl";
    public static final String RETRIEVE_UNSIGNED_DISTRIBUTED_LEDGER_URL = "retrieveUnsignedDistributedLedgerUrl";
    public static final String RETURN_SIGNED_DISTRIBUTED_LEDGER_URL = "returnSignedDistributedLedgerUrl";
    public static final String SEND_FUNDS_URL = "sendFundsUrl";
    public static final String RECEIVE_FUNDS_URL = "receiveFundsUrl";
    public static final String ACCEPT_FUNDS_URL = "acceptFundsUrl";
    public static final String CONFIRM_FUNDS_URL = "confirmFundsUrl";
    public static final String RETRIEVE_TRANSACTION_UUID_URL = "retrieveTransactionUuidUrl";
    public static final String CREDENTIAL_ICON_URL = "credentialIconUrl";

    public static final String TYPE = "type";
    public static final String CREDENTIAL_TYPE = "credentialType";
    public static final String CREDENTIAL_DISPLAY_NAME = "credentialDisplayName";
    public static final String ACTIVATION_TIMESTAMP = "activationTimestamp";
    public static final String EXPIRATION_TIMESTAMP = "expirationTimestamp";
    public static final String CREDENTIAL_DATA = "credentialData";

    public static final String FIREBASE_MSG_TYPE_KEY = "firebaseMsgTypeKey";
    public static final String FIREBASE_MSG_TYPE_CREATE_CREDENTIAL = "createCredential";
    public static final String FIREBASE_MSG_TYPE_SIGN_ON = "signOn";
    public static final String FIREBASE_MSG_TYPE_SIGN_DISTRIBUTED_LEDGER = "signDistributedLedger";
    public static final String FIREBASE_MSG_TYPE_CONFIRM_FUNDS_TRANSFER = "confirmFundsTransfer";

    public static final String SESSION_INVALIDATED = "SESSION INVALIDATED";
    public static final String SESSION_TIMEOUT = "SESSION TIMEOUT";

    public static final String TRANSFER_KEY = "transferKey";
    public static final String SECRET_KEY = "secretKey";

    public static final String TRANSFER_KEY_ENCRYPTED_HEX = "transferKeyEncryptedHex";
    public static final String TRANSFER_DATA_ENCRYPTED_HEX = "transferDataEncryptedHex";
    public static final String TRANSFER_DATA_ENCRYPTED_HASHED_HEX = "transferDataEncryptedHashedHex";
    public static final String USER_SECURITY_KEY_ENCRYPTED = "userSecurityKeyEncrypted";
    public static final String PUBLIC_KEY_ALGORITHM = "publicKeyAlgorithm";
    public static final String PUBLIC_KEY_MODULUS = "publicKeyModulus";
    public static final String PUBLIC_KEY_HEX = "publicKeyHex";
    public static final String PUBLIC_KEY_UUID = "publicKeyUuid";
    public static final String TRANSACTION_UUID = "transactionUuid";
    public static final String TRANSACTION_UUID_SIGNED = "transactionUuidSigned";
    public static final String DISTRIBUTED_LEDGER = "distributedLedger";
    public static final String DISTRIBUTED_LEDGER_SIGNED = "distributedLedgerSigned";

    public static final String MESSAGE = "message";
    public static final String JSON_FUNDS_TRANSFER = "jsonFundsTransfer";
    public static final String ACCOUNT_BALANCE = "accountBalance";
    public static final String FUNDS_TRANSFER_UUID = "fundsTransferUuid";
    public static final String SENDER = "sender";
    public static final String REFERENCE = "ref";
    public static final String TIMESTAMP = "timestamp";

    public static final String RECIPIENT_DATA = "recipientData";
    public static final String RECIPIENT_NAME = "recipientName";
    public static final String RECIPIENT_PHONE_NUMBER = "recipientPhoneNumber";
    public static final String RECIPIENT_EMAIL = "recipientEmail";
    public static final String RECIPIENT_HASH = "recipientHash";
    public static final String RECIPIENT_SIGNED_HASH = "recipientSignedHash";
    public static final String RECIPIENT_SECURE_HASH_ALGORITHM = "recipientSecureHashAlgorithm";
    public static final String TRANSFER_AMOUNT = "transferAmount";
    public static final String SENDER_HASH = "senderHash";
    public static final String SENDER_SIGNED_HASH = "senderSignedHash";

    public static final String SENDING_BANK_NAME = "sendingBankName";
    public static final String SENDING_BANK_UUID = "sendingBankUuid";
    public static final String SENDING_BANK_SIGNATURE_ALGORITHM = "sendingBankSignatureAlgorithm";
    public static final String SENDING_BANK_SECURE_HASH_ALGORITHM = "sendingBankSecureHashAlgorithm";
    public static final String SENDING_BANK_HASH = "sendingBankHash";
    public static final String SENDING_BANK_SIGNED_HASH = "sendingBankSignedHash";

    public static final String RECEIVING_BANK_NAME = "receivingBankName";
    public static final String RECEIVING_BANK_UUID = "receivingBankUuid";
    public static final String RECEIVING_BANK_SIGNATURE_ALGORITHM = "receivingBankSignatureAlgorithm";
    public static final String RECEIVING_BANK_SECURE_HASH_ALGORITHM = "receivingBankSecureHashAlgorithm";
    public static final String RECEIVING_BANK_HASH = "receivingBankHash";
    public static final String RECEIVING_BANK_SIGNED_HASH = "receivingBankSignedHash";

    public static final String VISITOR_THREAT_SYSTEM_INITIALIZATION = "SYSTEM INITIALIZATION";
    public static final String VISITOR_THREAT_AUTHENTICATION = "AUTHENTICATION";
    public static final String VISITOR_CREATE_CREDENTIAL = "CREATE CREDENTIAL";
}
