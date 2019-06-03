package com.github.oliverpavey.projectsearch;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import lombok.extern.java.Log;

/**
 * Finder for projects in JetBrains IDEs (both IntelliJ and Android Studio)
 * 
 * @author user
 *
 */
@Component
@Log
public class JetBrainsFinder {

	public void walk() {
		File homeFolder = new File(System.getProperty("user.home"));
		if (homeFolder.exists()) {
			Arrays.stream( homeFolder.listFiles(
					f -> f.isDirectory() && f.getName().startsWith(".")) )
			.filter(fldr-> jetBrainsConfig(fldr).exists())
			.forEach(fldr-> walkJetBrainsIde(fldr));
		}
	}
	
	private File jetBrainsConfig(File fldr) {
		return new File( new File( new File( fldr , "config" ), "options" ), "recentProjects.xml" );
	}

	private static final String XPATH_PROJECT_FOLDER_EXTRACTOR =
			"/application/component/option[@name='recentPaths']/list/option/@value";
	
	private void walkJetBrainsIde(File fldr) {
		File config = jetBrainsConfig(fldr);
		System.out.printf("IDE: %s%n", fldr);
		
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(config);
			
			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpath = xPathfactory.newXPath();
			XPathExpression expr = xpath.compile(XPATH_PROJECT_FOLDER_EXTRACTOR);
			
			NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			for (int i=0; i<nl.getLength(); i++) {
				Node node = nl.item(i);
				String projectLocation = cleanLocation( node.getTextContent() );
				System.out.printf("\t%s%n", projectLocation);
			}
			
		} catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException e) {
			log.log(Level.WARNING, e.getMessage(), e);
		}
	}

	private String cleanLocation(String location) {
		if (location == null)
			return null;
		String diskLocation = location.replace("$USER_HOME$", System.getProperty("user.home"));
		File folder = new File(diskLocation);
		if (folder.canExecute() && folder.isDirectory())
			return folder.getAbsolutePath();
		else
			return location;
	}
	
}
