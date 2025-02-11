/**
 * 
 */
package com.telus.credit.firestore;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.telus.credit.firestore.model.AssesmentDocumentCompact;

@SpringBootTest
class AssessmentCollectionServiceTest {

	@Autowired
	AssessmentCollectionService aAssessmentCollectionService;
/*
	@Test
	void testGetAssmentDocuments() {
		//not a mock test
		List<String> customerIds = new ArrayList<String>();
		customerIds.add("7000034");
		try {
			Optional<Map<String, AssesmentDocumentCompact>> x = aAssessmentCollectionService.getAssmentDocuments(customerIds);
			System.out.println("AssesmentDocumentCompact="+ x.get());
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	*/

}
