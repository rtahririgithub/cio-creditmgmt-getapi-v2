package com.telus.credit.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.telus.credit.exceptions.CreditException;
import com.telus.credit.exceptions.ExceptionConstants;
import com.telus.credit.model.common.ApplicationConstants;
import com.telus.credit.model.common.SearchableParams;

@Service
public class ValidationService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ValidationService.class);

	public MultiValueMap<String, String> validSearchParamsPresent(MultiValueMap<String, String> searchParams) {
		LOGGER.info("Start ValidationService validSearchParamsPresent");
		int countSet = 0;
		if(searchParams.size() == 0) {
			String details ="[searchParams=" + searchParams +"]"; 
			throw new CreditException(HttpStatus.BAD_REQUEST, ExceptionConstants.ERR_CODE_1505,ExceptionConstants.ERR_CODE_1505_MSG, "INVALID SEARCH PARAM",details);
		}
		MultiValueMap<String, String> shallowClone = validateFiltering(validateLimitAndSort(searchParams));
		boolean allKnownParamsExist = shallowClone.entrySet().stream()
				.allMatch(
						obj -> 
						ObjectUtils.isNotEmpty(SearchableParams.getParamType(obj.getKey()))
						&& !CollectionUtils.isEmpty(obj.getValue())
						);
		
		System.out.println("ALL KNOWN PARAMS " + allKnownParamsExist);
		if (!allKnownParamsExist) {
			String details ="[searchParams=" + searchParams +"]";
			throw new CreditException(HttpStatus.BAD_REQUEST, ExceptionConstants.ERR_CODE_1505,ExceptionConstants.ERR_CODE_1505_MSG, "INVALID SEARCH PARAM",details);
		}
		for (String key : shallowClone.keySet()) {
			for (String item : searchParams.get(key)) {
				List<String> values = Stream.of(StringUtils.split(item, ",")).map(String::trim)
						.filter(StringUtils::isNotBlank).collect(Collectors.toList());
				System.out.println("VALUES " + values);
				if (values.size() > 1) {
					countSet++;
				}
				if (!CollectionUtils.isEmpty(values) && values.size() > 9) {
					String details ="[searchParams=" + searchParams +"]";
					throw new CreditException(HttpStatus.BAD_REQUEST, ExceptionConstants.ERR_CODE_1506,ExceptionConstants.ERR_CODE_1506_MSG, "SEARCH PARAM EXCEEDS LENGTH",details);
				}
				if(!CollectionUtils.isNotEmpty(values) || values.size() == 0) {
					String details ="[searchParams=" + searchParams +"]";
					throw new CreditException(HttpStatus.BAD_REQUEST, ExceptionConstants.ERR_CODE_1000,ExceptionConstants.ERR_CODE_1000_MSG, "At least one valid search parameter is required.",details);
				}
			}
		}
		if (!CollectionUtils.isEmpty(shallowClone.keySet()) && shallowClone.keySet().size() > 1 && countSet > 0) {
			String details ="[searchParams=" + searchParams +"]";
			throw new CreditException(HttpStatus.BAD_REQUEST, ExceptionConstants.ERR_CODE_1504,ExceptionConstants.ERR_CODE_1504_MSG, "ONE PARAMETER OR’ING IS ALLOWED",details);
		}
		if (searchParams.containsKey(ApplicationConstants.BIRTH_DATE_KEY)) {
			boolean vals = Stream
					.of(StringUtils.split(searchParams.get(ApplicationConstants.BIRTH_DATE_KEY).get(0), ","))
					.map(String::trim).allMatch(val -> StringUtils.isNotBlank(val)
							&& val.matches("(19|[2-9][0-9])[0-9]{2}\\-[0-9]{2}\\-[0-9]{2}"));
			if (!vals) {
				String details ="[searchParams=" + searchParams +"]";
				throw new CreditException(HttpStatus.BAD_REQUEST, ExceptionConstants.ERR_CODE_1501,ExceptionConstants.ERR_CODE_1501_MSG, "INVALID BIRTHDATE",details);
			}
		}

		return searchParams;
	}

	private MultiValueMap<String, String> validateFiltering(MultiValueMap<String, String> searchParams) {
		if (searchParams.containsKey(ApplicationConstants.FILTER_KEY)) {
			boolean vals = Stream.of(StringUtils.split(searchParams.get(ApplicationConstants.FILTER_KEY).get(0), ","))
					.map(String::trim)
					.allMatch(val -> StringUtils.isNotBlank(val)
							&& StringUtils.containsIgnoreCase(val, ApplicationConstants.FILTER_BY_PROFILE)
									|| StringUtils.containsIgnoreCase(val, ApplicationConstants.FILTER_BY_NONE));
			if (!vals) {
				throw new CreditException(HttpStatus.BAD_REQUEST, ExceptionConstants.ERR_CODE_1503,
						ExceptionConstants.ERR_CODE_1503_MSG, "INVALID ATTRIBUTE SPECIFIED FOR FIELDS FILTERING");
			}
			searchParams.remove(ApplicationConstants.FILTER_KEY);
		}
		return searchParams;
	}

	private MultiValueMap<String, String> validateLimitAndSort(MultiValueMap<String, String> searchParams) {
		MultiValueMap<String, String> shallowClone = new LinkedMultiValueMap<>(searchParams);
		if ((CollectionUtils.isNotEmpty(searchParams.keySet()))
				&& searchParams.containsKey(ApplicationConstants.LIMIT_KEY)
				|| searchParams.containsKey(ApplicationConstants.SORT_KEY)) {
			if (ObjectUtils.isNotEmpty(searchParams.get(ApplicationConstants.LIMIT_KEY))
					&& NumberUtils.toInt(searchParams.get(ApplicationConstants.LIMIT_KEY).get(0)) > 0) {
				shallowClone.remove(ApplicationConstants.LIMIT_KEY);
			}
			if (CollectionUtils.isNotEmpty(searchParams.get(ApplicationConstants.SORT_KEY))) {
					boolean vals = Stream
							.of(StringUtils.split(searchParams.get(ApplicationConstants.SORT_KEY).get(0), ","))
							.map(String::trim)
							.allMatch(val -> StringUtils.isNotBlank(val) && (StringUtils.containsIgnoreCase(val,
									ApplicationConstants.SORT_BY_RISK_RATING)
									|| StringUtils.containsIgnoreCase(val, ApplicationConstants.SORT_BY_START_DTM)));
					if (!vals) {
						throw new CreditException(HttpStatus.BAD_REQUEST, ExceptionConstants.ERR_CODE_1502,
								ExceptionConstants.ERR_CODE_1502_MSG, "INVALID ATTRIBUTE SPECIFIED FOR SORTING");
					}
					shallowClone.remove(ApplicationConstants.SORT_KEY);
			}
		}
		return shallowClone;
	}

}
