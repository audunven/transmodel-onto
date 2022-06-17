package xml;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.common.collect.ArrayListMultimap;

public class UseCaseParser {

	public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException {
		
		//FIXME: The use case generation does not generate sub-use cases, so this needs to be fixed, probably using recursion.

		String fileName = "./files/ContextView_16122021.xml";

		ArrayListMultimap<String, String> useCasesPerActorMap = getUseCasesPerActor(fileName);

		String actor = "MaaS Provider";

		Set<String> useCases = getUseCases(useCasesPerActorMap, actor);


		System.out.println("Number of use cases for actor " + actor + ": " + useCases.size());

		System.out.println("\nUse cases: ");

		for (String s : useCases) {



			System.out.println(s);
		}


		ArrayListMultimap<String, String> includedUseCaseMap = getIncludedUseCases(fileName);


		String useCase = "Provide MaaS";

		Set<String> includedUseCases = getIncludedUseCases(includedUseCaseMap, useCase);

		System.out.println("\nNumber of sub use cases for use case " + useCase + ": " + includedUseCases.size());

		System.out.println("\nSub use cases: ");
		for (String s : includedUseCases) {
			System.out.println(s);
		}


	}
	
	public static Set<String> getUseCases(ArrayListMultimap<String, String> map, String actor) {

		Set<String> useCases = new HashSet<String>();
		Set<String> allUseCases = new HashSet<String>();
		Map<String, Set<String>> useCasesAndSubUseCases = new HashMap<String, Set<String>>();

		if (!map.containsKey(actor)) {
			System.err.println("There is no actor with that name!");
		} else {	

			useCases = new HashSet<String>(map.get(actor));
			
			System.out.println("useCases size: " + useCases.size());

			Set<String> subUseCases = new HashSet<String>();
			for (String s : useCases) {

				subUseCases = getIncludedUseCases(map, s);
				System.out.println("There are " + subUseCases.size() + " sub use cases to " + s);
				useCasesAndSubUseCases.put(s, subUseCases);

			}

		}

		for (Entry<String, Set<String>> e : useCasesAndSubUseCases.entrySet()) {
			System.out.println(e.getKey() + ": " + e.getValue());
			allUseCases.add(e.getKey());

			for (String s : e.getValue()) {
				allUseCases.add(s);
			}

		}



		return allUseCases;


	}

	public static Set<String> getIncludedUseCases(ArrayListMultimap<String, String> map, String parentUseCase) {

		Set<String> includedUseCases = new HashSet<String>();

		if (!map.containsKey(parentUseCase)) {
			System.err.println("There is no use case with that name!");
		} else {	
			includedUseCases = new HashSet<String>(map.get(parentUseCase));
		}

		return includedUseCases;


	}


	public static ArrayListMultimap<String, String> getIncludedUseCases (String fileName) throws SAXException, IOException, ParserConfigurationException {

		File xmlDoc = new File(fileName);
		DocumentBuilderFactory dbFact = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuild = dbFact.newDocumentBuilder();
		Document doc = dBuild.parse(xmlDoc);

		NodeList connectorList = doc.getElementsByTagName("connector");

		String sourceModelType = null;
		String targetModelType = null;

		ArrayListMultimap<String, String> includedUseCaseMap = ArrayListMultimap.create();


		for (int i = 0; i < connectorList.getLength(); i++) {

			Node connectorNode = connectorList.item(i);

			if (connectorNode.getNodeType() == Node.ELEMENT_NODE) {

				Element eElement = (Element) connectorNode;

				NodeList connectorNodes = eElement.getChildNodes();

				String useCaseName = null;
				String subUseCasename = null;


				for (int j = 0; j < connectorNodes.getLength(); j++) {

					boolean includeAssociation = false;

					Node connector = connectorNodes.item(j);

					//					if (connector.getNodeName().equals("properties")) {
					//						
					//						Element propertiesElement = (Element) connector;
					//						
					//						if (propertiesElement.getAttribute("stereotype").equals("include")) {
					//							
					//							includeAssociation = true;			
					//
					//						}
					//					}

					if (connector.getNodeName().equals("source")) {

						Element sourceElement = (Element) connector;
						NodeList sourceNodes = sourceElement.getChildNodes();


						for (int k = 0; k < sourceNodes.getLength(); k++) {

							Node sourceChildNode = sourceNodes.item(k);

							if (sourceChildNode.getNodeName().equals("model")) {

								Element modelTypeElement = (Element) sourceChildNode;
								sourceModelType = modelTypeElement.getAttribute("type");

								if (modelTypeElement.getAttribute("type").equals("UseCase")) {

									useCaseName = modelTypeElement.getAttribute("name");
								}
							}							
						}						
					}	

					if (connector.getNodeName().equals("target")) {

						Element targetElement = (Element) connector;
						NodeList targetNodes = targetElement.getChildNodes();

						for (int k = 0; k < targetNodes.getLength(); k++) {

							Node targetChildNode = targetNodes.item(k);

							if (targetChildNode.getNodeName().equals("model")) {

								Element modelTypeElement = (Element) targetChildNode;
								targetModelType = modelTypeElement.getAttribute("type");

								if (modelTypeElement.getAttribute("type").equals("UseCase")) {

									subUseCasename = modelTypeElement.getAttribute("name");
								}

							}							
						}						
					}

					if (connector.getNodeName().equals("properties")) {

						Element propertiesElement = (Element) connector;

						if (propertiesElement.getAttribute("stereotype").equals("include")) {

							includeAssociation = true;			

						}
					}

					if (includeAssociation == true && useCaseName != null && subUseCasename != null) {
						includedUseCaseMap.put(useCaseName, subUseCasename);
					}	

				}//end for
			}		
		}

		System.out.println("includedUseCaseMap includes " + includedUseCaseMap.size() + " entries");

		return includedUseCaseMap;

	}

	public static ArrayListMultimap<String, String> getUseCasesPerActor (String fileName) throws SAXException, IOException, ParserConfigurationException {


		File xmlDoc = new File(fileName);
		DocumentBuilderFactory dbFact = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuild = dbFact.newDocumentBuilder();
		Document doc = dBuild.parse(xmlDoc);

		NodeList connectorList = doc.getElementsByTagName("connector");

		String sourceModelType = null;
		String targetModelType = null;

		ArrayListMultimap<String, String> useCaseMap = ArrayListMultimap.create();

		for (int i = 0; i < connectorList.getLength(); i++) {

			Node connectorNode = connectorList.item(i);

			if (connectorNode.getNodeType() == Node.ELEMENT_NODE) {

				Element eElement = (Element) connectorNode;

				NodeList connectorNodes = eElement.getChildNodes();

				String actorName = null;
				String useCaseName = null;

				for (int j = 0; j < connectorNodes.getLength(); j++) {

					Node connector = connectorNodes.item(j);

					if (connector.getNodeName().equals("source")) {

						Element sourceElement = (Element) connector;
						NodeList sourceNodes = sourceElement.getChildNodes();


						for (int k = 0; k < sourceNodes.getLength(); k++) {

							Node sourceChildNode = sourceNodes.item(k);

							if (sourceChildNode.getNodeName().equals("model")) {

								Element modelTypeElement = (Element) sourceChildNode;
								sourceModelType = modelTypeElement.getAttribute("type");

								if (modelTypeElement.getAttribute("type").equals("Actor")) {

									actorName = modelTypeElement.getAttribute("name");
								}
							}							
						}						
					}	

					if (connector.getNodeName().equals("target")) {

						Element targetElement = (Element) connector;
						NodeList targetNodes = targetElement.getChildNodes();

						for (int k = 0; k < targetNodes.getLength(); k++) {

							Node targetChildNode = targetNodes.item(k);

							if (targetChildNode.getNodeName().equals("model")) {

								Element modelTypeElement = (Element) targetChildNode;
								targetModelType = modelTypeElement.getAttribute("type");

								if (modelTypeElement.getAttribute("type").equals("UseCase")) {

									useCaseName = modelTypeElement.getAttribute("name");
								}

							}							
						}						
					}

					if (actorName != null && useCaseName != null) {
						useCaseMap.put(actorName, useCaseName);
					}				

				}

			}

		}

		return useCaseMap;


	}

	


	public static Map<String, String> getUseCases (String fileName) throws SAXException, IOException, ParserConfigurationException {

		File xmlDoc = new File(fileName);
		DocumentBuilderFactory dbFact = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuild = dbFact.newDocumentBuilder();
		Document doc = dBuild.parse(xmlDoc);

		NodeList nList = doc.getElementsByTagName("element");

		String useCaseName = null;
		String useCaseId = null;
		Map<String,String> useCaseMap = new HashMap<String, String>();

		for (int i = 0; i < nList.getLength(); i++) {

			Node nNode = nList.item(i);

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {

				Element eElement = (Element) nNode;

				if (eElement.getAttribute("xmi:type").equals("uml:UseCase")) {

					useCaseId = eElement.getAttribute("xmi:idref");
					useCaseName = eElement.getAttribute("name");

					useCaseMap.put(useCaseId, useCaseName);
				}
			}

		}

		return useCaseMap;


	}


	public static Map<String, String> getActors (String fileName) throws SAXException, IOException, ParserConfigurationException {

		File xmlDoc = new File(fileName);
		DocumentBuilderFactory dbFact = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuild = dbFact.newDocumentBuilder();
		Document doc = dBuild.parse(xmlDoc);

		NodeList nList = doc.getElementsByTagName("element");

		String actorName = null;
		String actorId = null;
		Map<String,String> actorMap = new HashMap<String, String>();


		for (int i = 0; i < nList.getLength(); i++) {

			Node nNode = nList.item(i);

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {

				Element eElement = (Element) nNode;

				if (eElement.getAttribute("xmi:type").equals("uml:Actor")) {

					actorId = eElement.getAttribute("xmi:idref");
					actorName = eElement.getAttribute("name");

					actorMap.put(actorId, actorName);
				}
			}

		}

		return actorMap;


	}

}
