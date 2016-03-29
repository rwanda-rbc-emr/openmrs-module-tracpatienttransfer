package org.openmrs.module.tracpatienttransfer.service;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class PatientTransferServiceTest extends BaseModuleContextSensitiveTest {

	@Test
	public void currentServiceMustNotInitialized() {
		Assert.assertNotNull(Context.getService(PatientTransferService.class));
	}
}
