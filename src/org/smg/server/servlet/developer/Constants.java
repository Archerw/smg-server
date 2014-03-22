package org.smg.server.servlet.developer;

public final class Constants {
  private Constants() { }  // Prevent instantiation/subclassing
  
  // Developer entity
  static final String DEVELOPER = "Developer";
  
  // Various properties in the table
  static final String EMAIL = "email";
  static final String PASSWORD = "password";
  static final String FIRST_NAME = "firstName";
  static final String MIDDLE_NAME = "middleName";
  static final String LAST_NAME = "lastName";
  static final String NICKNAME = "nickname";
  static final String DEVELOPER_ID = "developerId";
  static final String ACCESS_SIGNATURE = "accessSignature";
  
  // Error messages
  static final String ERROR = "error";
  static final String MISSING_INFO = "MISSING_INFO";
  static final String EMAIL_EXISTS = "EMAIL_EXISTS";
  static final String WRONG_DEVELOPER_ID = "WRONG_DEVELOPER_ID";
  static final String WRONG_PASSWORD = "WRONG_PASSWORD";
  static final String WRONG_ACCESS_SIGNATURE = "WRONG_ACCESS_SIGNATURE";
  
  // Success messages
  static final String SUCCESS = "success";
  static final String DELETED_DEVELOPER = "DELETED_DEVELOPER";
}
