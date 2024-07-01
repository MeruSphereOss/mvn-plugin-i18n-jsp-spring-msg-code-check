package oss.merusphere.mvnplugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

/**
 * Checks whether Enum Field Types in model classes contain Enumerated
 * Annotation or not and also checks that for enum fields containing enumerated
 * annotation, whether enumerated annotation contains Enum String Value or not
 */
@Mojo(name = "i18n-jsp-spring-msg-code-check", defaultPhase = LifecyclePhase.COMPILE, requiresDependencyResolution = ResolutionScope.TEST)
public class JspMsgCodeCheckerMojo extends AbstractMojo {
	public static Set<String> msgPropertyCodeSet = new HashSet<>();

	public static List<String> errorList = new ArrayList<>();

	/**
	 * Scan the classes from the Package name from the configuration parameter named
	 * pkg
	 */
	@Parameter(property = "jspFilesPath")
	String jspFilesPath;

	/**
	 * Path of message properties
	 */
	@Parameter(property = "msgPropertiesPath")
	String msgPropertiesPath;

	/**
	 * Gives access to the Maven project information.
	 */
	@Parameter(defaultValue = "${project}", required = true, readonly = true)
	MavenProject project;

	/**
	 * Checks the number of methods not having Security Annotations
	 */
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			if (jspFilesPath == null || jspFilesPath.trim().length() <= 0) {
				new MojoExecutionException("Jsp Files path can't be null for the project : " + project.getName());
			}

			if (getLog().isInfoEnabled()) {
				getLog().info(new StringBuffer("Using Jsp Files path " + jspFilesPath));
			}

			if (msgPropertiesPath == null || msgPropertiesPath.trim().isEmpty()) {
				new MojoExecutionException(
						"Msg properties path cannot be empty for the project : " + project.getName());
			}

			loadMessageProperties(msgPropertiesPath);
			
			processJspFiles(jspFilesPath);

			if (errorList != null && !errorList.isEmpty()) {
				getLog().error("The following jsp message codes does not exists in message properties");
				for (String error : errorList) {
					getLog().error(error);
				}
			}

			if (!errorList.isEmpty() || !errorList.isEmpty()) {
				new MojoExecutionException("Found Message Code Errors");
			}

		} catch (Exception e) {
			getLog().error(e.getMessage(), e);
			new MojoExecutionException(e.getMessage(), e);
		}
	}

	private static void loadMessageProperties(String msgPropertiesPath) {
		// Method 1: Using BufferedReader
		try (BufferedReader br = new BufferedReader(new FileReader(msgPropertiesPath))) {
			String line;
			while ((line = br.readLine()) != null) {
				String split[] = line.split("=");
				if (split != null && split.length > 0) {
					msgPropertyCodeSet.add(split[0].trim());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void processJspFiles(String filePath) {
		File file = new File(filePath);
		File[] jspSubFiles = file.listFiles();
		for (File jspSubFile : jspSubFiles) {
			if (jspSubFile.isDirectory() == true) {
				processJspFiles(jspSubFile.getPath());
			} else {
				if (jspSubFile.getName().endsWith(".jsp")) {
					checkMsgPropertyExists(jspSubFile.getPath());
				}
			}
		}
	}

	private static void checkMsgPropertyExists(String jspFile) {

		try {
			Set<String> fileMsgCodeSet = new HashSet<>();
			String content = Files.readString(Paths.get(jspFile));
			content = content.replaceAll("\\n\\r?", "");

			String macroValuePatternStr = "<c:set\\s*var=\"MACRO\"\\s*value=\\s*\\\"([a-zA-Z]+)\\\"\\s*\\/>";

			Pattern macroValuePattern = Pattern.compile(macroValuePatternStr);
			Matcher matcherMacroLine = macroValuePattern.matcher(content);

			String macroValue = "";
			if (matcherMacroLine.find()) {
				macroValue = matcherMacroLine.group(1);
			}
			// System.out.println(macroValue);

			String msgCodePatternStr = "<spring:message\\s*code\\=\\\"([\\{\\}\\$A-Za-z\\.]+)\\\"\\s*";

			Pattern msgCodePattern = Pattern.compile(msgCodePatternStr);
			Matcher msgCodeMatcher = msgCodePattern.matcher(content);

			while (msgCodeMatcher.find()) {
				String fileMsgCode = msgCodeMatcher.group(1);
				if (fileMsgCode.contains("${MACRO}")) {
					fileMsgCode = fileMsgCode.replace("${MACRO}", macroValue);
				}
				fileMsgCodeSet.add(fileMsgCode);
			}

			for (String fileMsgCode : fileMsgCodeSet) {
				if (!msgPropertyCodeSet.contains(fileMsgCode)) {
					errorList.add(fileMsgCode);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
