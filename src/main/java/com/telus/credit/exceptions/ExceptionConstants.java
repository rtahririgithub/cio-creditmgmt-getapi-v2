package com.telus.credit.exceptions;

public final class ExceptionConstants {
   public static final String ERR_CODE_1000 = "1000";
   public static final String ERR_CODE_1000_MSG = "Invalid or missing mandatory request parameter(s)";
   
   public static final String ERR_CODE_401_MSG = "{ \"message\":\"Either the Bearer Token is missing or expired\"}";
  
   public static final String ERR_CODE_1400 = "1400";
   public static final String ERR_CODE_1400_MSG = "Customer not found for the given Id";

   public static final String ERR_CODE_1402 = "1402";
   public static final String ERR_CODE_1402_MSG = "Credit profile not found for the given Id";
   
   public static final String ERR_CODE_1501 = "1501";
   public static final String ERR_CODE_1501_MSG = "birthDate if provided must be in format YYYY-MM-dd";
   
   public static final String ERR_CODE_1502 = "1502";
   public static final String ERR_CODE_1502_MSG = "Sort attribute specified that is not supported for sorting";

   public static final String ERR_CODE_1503 = "1503";
   public static final String ERR_CODE_1503_MSG = "Fields attribute specified that is not supported";

   public static final String ERR_CODE_1504 = "1504";
   public static final String ERR_CODE_1504_MSG = "Only one parameter is allowed to have multiple values and combination of OR and AND is not supported.";
   
   public static final String ERR_CODE_1505 = "1505";
   public static final String ERR_CODE_1505_MSG = "Invalid search criteria provided. Some or all fields param(s) are invalid";
   
   
   public static final String ERR_CODE_1506 = "1506";
   public static final String ERR_CODE_1506_MSG = "Each Search parameters value should not exceed more than 10 values";
   
   
   // DAO errors
   public static final String ERR_CODE_8000 = "8000";   
   public static final String ERR_CODE_8000_MSG = "Unexpected error occurred accessing database.";
   
   public static final String ERR_INVALID_PARTY = "Must be Individual or Organization";
   public static final String ERR_INVALID_BIRTHDATE = "Must from 1900-01-01";

   public static final String STACKDRIVER_METRIC = "CreditProfileAPIError";   
   public static final String STACKDRIVER_WARNING_METRIC = "CreditProfileAPIWarning"; 
   //Indented use is STATIC in Nature
   private ExceptionConstants() {
	   
   }
}
