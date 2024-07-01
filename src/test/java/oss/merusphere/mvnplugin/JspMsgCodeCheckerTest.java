package oss.merusphere.mvnplugin;

import static org.junit.Assert.assertTrue;

import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugin.testing.WithoutMojo;
import org.junit.Rule;
import org.junit.Test;

public class JspMsgCodeCheckerTest {
	@Rule
	public MojoRule rule = new MojoRule() {
		@Override
		protected void before() throws Throwable {
		}

		@Override
		protected void after() {
		}
	};

	/**
	 * @throws Exception if any
	 */
	@Test
	public void testLocalServiceImplPackagePositiveCase() throws Exception {
		assertTrue(true);
	}

	/** Do not need the MojoRule. */
	@WithoutMojo
	@Test
	public void testLocalServiceImplPackageNegativeCase() {
		assertTrue(true);
	}

}
