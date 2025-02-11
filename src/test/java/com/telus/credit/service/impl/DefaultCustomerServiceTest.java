package com.telus.credit.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.PlatformTransactionManager;

import com.telus.credit.exceptions.ReadStoreGenericException;
import com.telus.credit.firestore.AssessmentCollectionService;
import com.telus.credit.firestore.CustomerCollectionService;
import com.telus.credit.model.Customer;
import com.telus.credit.model.TelusCreditProfile;
import com.telus.credit.service.CreditProfileService;
import com.telus.credit.service.EngagedPartyService;
import com.telus.credit.service.ValidationService;

@ExtendWith(MockitoExtension.class)
class DefaultCustomerServiceTest {

    @Mock
    private PlatformTransactionManager platformTransactionManager;

   
    @Mock
    private ValidationService validationService;

    @Mock
    private CustomerCollectionService customerCollectionService;

    @Mock
    private CreditProfileService<TelusCreditProfile> creditProfileService;

    @Mock
    private EngagedPartyService engagedPartyService;

   

    @Mock
    private ResponseInterceptorService responseProcessor;

    @Mock
    private AssessmentCollectionService assesmentDB;

    @InjectMocks
    private DefaultCustomerService underTest;

   
	@Test
	void testCustomerNotFoundInStore() {
		String customerId = "customerId";
		doReturn(Optional.ofNullable(null)).when(customerCollectionService).findDocument(customerId);
		assertThat(underTest.findCustomerFromStore(customerId, "en")).isEmpty();
	}

	@Test
	void testCustomerFoundInStore() throws ReadStoreGenericException {
        doReturn(Optional.empty()).when(assesmentDB).getCurrentAssesmentCode(anyList());

		String customerId = "customerId";
		Customer testCustomer = new Customer();
		doReturn(Optional.ofNullable(testCustomer)).when(customerCollectionService).findDocument(customerId);
		assertThat(underTest.findCustomerFromStore(customerId, "en")).isNotEmpty();
		//verify(responseProcessor, times(1)).resolveMissingFields(eq(testCustomer), eq("en"));
	}
}