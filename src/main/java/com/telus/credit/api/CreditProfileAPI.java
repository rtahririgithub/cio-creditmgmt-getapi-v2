package com.telus.credit.api;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotEmpty;

import com.telus.credit.exceptions.CreditException;
import com.telus.credit.model.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.telus.credit.common.CommonConstants;
import com.telus.credit.common.RequestContext;
import com.telus.credit.controllers.CreditProfileController;
import com.telus.credit.exceptions.ExceptionConstants;
import com.telus.credit.exceptions.ExceptionHelper;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import static org.apache.commons.lang3.StringUtils.isNumeric;

@RestController
@Api(tags = { "Customer Credit Profile" }, produces = "application/json")
@RequestMapping(path = "/customer/creditprofile-mgmt", produces = "application/json")

@Validated
public class CreditProfileAPI {

   private final Bucket bucket;
   public CreditProfileAPI() {	 
	 //Rate limit how many HTTP requests can be made in a given period of 1 minute
       long capacity = 150000;
       long tokens=150000;
       Duration period = Duration.ofMinutes(1);      
       Refill speedOfTokenRegeneration = Refill.greedy(tokens, period); 
       Bandwidth limit = Bandwidth.classic(capacity, speedOfTokenRegeneration);
       this.bucket = Bucket4j.builder()
           .addLimit(limit)
           .build();
   }   
	   
   private static final Logger LOGGER = LoggerFactory.getLogger(CreditProfileAPI.class);
   @Autowired
   CreditProfileController creditProfileController;


   @GetMapping("/creditProfile")
   @ApiOperation(value = "Find the customer profile based on search criteria", response = String.class, notes = "Search api.")
   @ApiImplicitParams(value = {
           @ApiImplicitParam(name = CommonConstants.HEADER_ACCEPT_LANG, required = false, value = "Language preference.", allowableValues = "en, fr", defaultValue = "en", example = "en", paramType = "header"),
           @ApiImplicitParam(name = CommonConstants.HEADER_CORR_ID, required = false, value = "Correlation Id, which is a UUID. Used for tracing.", example = "2e15baa3-d272-11e7-a479-05de8af2b6bd", paramType = "header") })
   @ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = Customer.class, responseContainer = "List"),
           @ApiResponse(code = 400, message = "Bad Request.", response = ErrorResponse.class),
           @ApiResponse(code = 500, message = "Unknown exception occurred.", response = ErrorResponse.class) })
   public ResponseEntity searchCreditProfile(
           HttpServletRequest request,
           @RequestParam(required = true) @NotEmpty(message = "1500") MultiValueMap<String, String> params
   ) {
		//Rate limit how many HTTP requests can be made in a given period of 1 minute
		//limit is  exceeded
	    if (!bucket.tryConsume(1)) {
	    	return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
	    }

      LOGGER.info("Start searchCustomer by params:"+params);

      RequestContext requestContext = new RequestContext(request);
      List<TelusCreditProfile> creditProfileList = creditProfileController.searchCreditProfile(requestContext, request.getQueryString());
      //return creditProfileList;
      ResponseEntity<List<TelusCreditProfile>> response = new ResponseEntity<>(creditProfileList, HttpStatus.OK);
      if(CollectionUtils.isEmpty(creditProfileList)) {
    	 String details = "[searchCustomer by params:" + params +"]";
         throw new CreditException(HttpStatus.NOT_FOUND, ExceptionConstants.ERR_CODE_1402, "Credit profile not found", ExceptionConstants.ERR_CODE_1402_MSG,details); 
      }
      return response;      
   }
   @Autowired
   private Environment environment;
   
   @GetMapping("/version")
   @ApiOperation(value = "Provides the current version info of the api.", response = String.class, notes = " version info.")
   @ApiImplicitParams(value = {
         @ApiImplicitParam(name = CommonConstants.HEADER_ACCEPT_LANG, required = false, value = "Language preference.", allowableValues = "en, fr", defaultValue = "en", example = "en", paramType = "header"),
         @ApiImplicitParam(name = CommonConstants.HEADER_CORR_ID, required = false, value = "Correlation Id, which is a UUID. Used for tracing.", example = "2e15baa3-d272-11e7-a479-05de8af2b6bd", paramType = "header") })
   @ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = String.class),
         @ApiResponse(code = 400, message = "Bad Request.", response = ErrorResponse.class),
         @ApiResponse(code = 500, message = "Unknown exception occurred.", response = ErrorResponse.class) })
   public ResponseEntity<String> getVersionInfo() {
		//Rate limit how many HTTP requests can be made in a given period of 1 minute
		//limit is  exceeded
	    if (!bucket.tryConsume(1)) {
	    	return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
	    }
      Properties prop = new Properties();
      String s;
	  s ="creditmgmt-getapi version info(ph2.2):";
	  String[] activeProfiles = this.environment.getActiveProfiles();
		 s = s +"[";
		 s = s + "ActiveProfile: " + Arrays.toString(activeProfiles);
		 s = s +"]";       
      try {
         prop.load(CreditProfileController.class.getClassLoader().getResourceAsStream("git.properties"));
         s = s +"[ ";
         s = s + "Git information: " + prop;
         s = s +"]";
      } catch (Exception e) {
         s = e.getMessage();
      }
      ResponseEntity<String> response = new ResponseEntity<>(s, HttpStatus.OK);
      return response;
   }

   @GetMapping("/creditProfile/{Id}")
   @ApiOperation(value = "Retrieves the customer credit profile for the given credit profile Id", response = CreditProfile.class)
   @ApiImplicitParams(value = {
           @ApiImplicitParam(name = CommonConstants.HEADER_ACCEPT_LANG, required = false, value = "Language preference.", allowableValues = "en, fr", defaultValue = "en", example = "en", paramType = "header"),
           @ApiImplicitParam(name = CommonConstants.HEADER_AUTHORIZATION, required = false, value = "Bearer token", example = "eyJhbGciOiJI...", paramType = "header"),
           @ApiImplicitParam(name = CommonConstants.HEADER_CORR_ID, required = false, value = "Correlation Id, which is a UUID. Used for tracing.", example = "2e15baa3-d272-11e7-a479-05de8af2b6bd", paramType = "header") })
   @ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = CreditProfile.class, responseContainer = "List"),
           @ApiResponse(code = 400, message = "Bad Request.", response = ErrorResponse.class),
           @ApiResponse(code = 500, message = "Unknown exception occurred.", response = ErrorResponse.class) })
   public ResponseEntity getCreditProfileByCreditProfileUiid(
           HttpServletRequest request,
           @ApiParam(value = "customer Id", type = "string", required = true, example = "16") @PathVariable("Id") String creditProfileId
   ) {

      if (StringUtils.isBlank(creditProfileId)) {
         throw new CreditException(HttpStatus.BAD_REQUEST, ExceptionConstants.ERR_CODE_1402, "Missing credit profile Id", ExceptionConstants.ERR_CODE_1402_MSG);
      }
      if (creditProfileId.startsWith("-")) {
     	 String details = "[creditProfileId:" + creditProfileId +"]";
         throw new CreditException(HttpStatus.BAD_REQUEST, ExceptionConstants.ERR_CODE_1402, "Invalid credit profile Id", ExceptionConstants.ERR_CODE_1402_MSG,details);
      }
      try {
         UUID.fromString(creditProfileId);
      } catch (Throwable e) {
    	  String details = "[creditProfileId:" + creditProfileId +"]";
         throw new CreditException(HttpStatus.BAD_REQUEST, ExceptionConstants.ERR_CODE_1402, "Invalid credit profile Id", ExceptionConstants.ERR_CODE_1402_MSG,details);
      }

      LOGGER.info("Start getCreditProfileByCreditProfileUiid:creditProfileId={}", creditProfileId);
      //Rate limit how many HTTP requests can be made in a given period of 1 minute
      //limit is  exceeded
      if (!bucket.tryConsume(1)) {
         return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
      }
      RequestContext requestContext = new RequestContext(request);
      TelusCreditProfile creditProfile = null;
      try {
         creditProfile = creditProfileController.getCreditProfile(requestContext, creditProfileId);
      } catch (Exception e) {
    	  String details = "[creditProfileId:" + creditProfileId +"]";
         throw new CreditException(HttpStatus.NOT_FOUND, ExceptionConstants.ERR_CODE_1402, "Credit profile not found for the given Id", ExceptionConstants.ERR_CODE_1402_MSG,details);
      }
      if (Objects.isNull(creditProfile)) {
    	  String details = "[creditProfileId:" + creditProfileId +"]";
         throw new CreditException(HttpStatus.NOT_FOUND, ExceptionConstants.ERR_CODE_1402, "Credit profile not found for the given Id", ExceptionConstants.ERR_CODE_1402_MSG,details);
      }
      //return creditProfile;
      ResponseEntity<TelusCreditProfile> response = new ResponseEntity<>(creditProfile, HttpStatus.OK);
      return response;
   }
   
   


}
