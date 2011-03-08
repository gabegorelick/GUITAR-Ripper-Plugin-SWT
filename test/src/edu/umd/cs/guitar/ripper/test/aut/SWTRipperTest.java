package edu.umd.cs.guitar.ripper.test.aut;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import edu.umd.cs.guitar.ripper.SWTRipper;

public class SWTRipperTest {

	@Test
	public void testSWTRipper() {
		SWTRipper ripper = new SWTRipper(null);
		assertNotNull(ripper.getMonitor());
	}
}
