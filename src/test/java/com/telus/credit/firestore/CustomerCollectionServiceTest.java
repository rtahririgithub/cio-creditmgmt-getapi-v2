package com.telus.credit.firestore;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.cloud.firestore.Firestore;
import com.telus.credit.exceptions.ReadStoreGenericException;
import com.telus.credit.model.Customer;
import com.telus.credit.model.Individual;
import com.telus.credit.model.Organization;
import com.telus.credit.model.OrganizationIdentification;
import com.telus.credit.model.TelusIndividualIdentification;
import com.telus.credit.model.common.IdentificationType;


@ExtendWith(MockitoExtension.class)
class CustomerCollectionServiceTest {
	
	@Mock
	private Firestore firestore;

	@InjectMocks
    private CustomerCollectionService customerCollectionService;
	
	
	@Test
	void testUpdateCustomerCollection() throws ReadStoreGenericException, InterruptedException, ExecutionException {
		Customer customer = new Customer();
		Individual individual = new Individual();
		List<TelusIndividualIdentification> idtLst = new ArrayList<>(1);
		TelusIndividualIdentification idt = new TelusIndividualIdentification();
		idt.setIdentificationType(IdentificationType.DL.getDesc());
		idtLst.add(idt);
		individual.setIndividualIdentification(idtLst);
		customer.setId("10001");
		CustomerCollectionService srcService = spy(customerCollectionService);
		String docId= "firestoreDocId";
		doNothing().when(srcService).updateFireStoreDocument(any(), any());
		doReturn(docId).when(srcService).getCurrentDocumentId(customer.getId());
		assertEquals(docId, srcService.addorUpdateCustomerCollection(customer));
		verify(srcService, times(1)).addorUpdateCustomerCollection(customer);
	}
	
	@Test
	void testAddCustomerCollection() throws ReadStoreGenericException, InterruptedException, ExecutionException {
		Customer customer = new Customer();
		Organization org = new Organization();
		List<OrganizationIdentification> idtLst = new ArrayList<>(1);
		OrganizationIdentification idt = new OrganizationIdentification();
		idt.setIdentificationType(IdentificationType.DL.getDesc());
		idtLst.add(idt);
		org.setOrganizationIdentification(idtLst);
		customer.setId("10001");
		CustomerCollectionService srcService = spy(customerCollectionService);
		String docId= "firestoreDocId";
		doReturn(docId).when(srcService).addFireStoreDocument(any());
		doReturn(null).when(srcService).getCurrentDocumentId(customer.getId());
		assertEquals(docId, srcService.addorUpdateCustomerCollection(customer));
		verify(srcService, times(1)).addorUpdateCustomerCollection(customer);
	}
	
	@Test
	void testExceptionFromStore() throws ReadStoreGenericException, InterruptedException, ExecutionException {
		Customer customer = new Customer();
		Organization org = new Organization();
		List<OrganizationIdentification> idtLst = new ArrayList<>(1);
		OrganizationIdentification idt = new OrganizationIdentification();
		idt.setIdentificationType(IdentificationType.DL.getDesc());
		idtLst.add(idt);
		org.setOrganizationIdentification(idtLst);
		customer.setId("10001");
		CustomerCollectionService srcService = spy(customerCollectionService);
		doReturn(null).when(srcService).getCurrentDocumentId(customer.getId());
		doThrow(InterruptedException.class).when(srcService).addFireStoreDocument(any());
		assertThrows(ReadStoreGenericException.class, () -> srcService.addorUpdateCustomerCollection(customer));
		assertNotNull(srcService.getCollectionName());
	}
	
}
