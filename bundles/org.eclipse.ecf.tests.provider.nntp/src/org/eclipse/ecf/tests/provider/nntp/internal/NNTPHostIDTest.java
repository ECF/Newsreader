package org.eclipse.ecf.tests.provider.nntp.internal;

import static org.junit.Assert.fail;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.provider.nntp.internal.NNTPNameSpace;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

@SuppressWarnings("restriction")
public class NNTPHostIDTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testNamespaceCompareTo() {
		fail("Not yet implemented");
	}

	@Test
	public void testNamespaceEquals() {
		fail("Not yet implemented");
	}

	@Test
	public void testNamespaceGetName() {
		fail("Not yet implemented");
	}

	@Test
	public void testNamespaceHashCode() {
		fail("Not yet implemented");
	}

	@Test
	public void testNamespaceToExternalForm() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetAdapter() {
		fail("Not yet implemented");
	}

	@Test
	public void testNNTPHostID() {
		Namespace ns = getValidNameSpace();
		ns.createInstance(new String[] { "nntp://news.eclipse.org:119" });
	}

	@Test(expected = Exception.class)
	public void testNNTPHostID2() {
		createID("news://dddddd");
	}

	@Test(expected = Exception.class)
	public void testNNTPHostID3() {
		createID("nntp://dddddd");
	}

	@Test
	public void testNNTPHostID4() {
		createID("nntp://dddddd:119");
	}

	private Namespace getValidNameSpace() {
		return new NNTPNameSpace();
	}

	private ID createID(final String uri) {
		return getValidNameSpace().createInstance(new String[] { uri });
	}

	@Test
	public void testGetPort() {
		fail("Not yet implemented");
	}

	@Test
	public void testToString() {
		fail("Not yet implemented");
	}

}
