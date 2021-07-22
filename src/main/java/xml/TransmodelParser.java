package xml;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import owl.OntologyClass;
import owl.OntologyDataProperty;
import owl.OntologyObjectProperty;
import owl.Relation;

public class TransmodelParser {
	
	public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException {

		String file = "./files/Transmodel.xml";
		
		long startTime = System.nanoTime();
		Set<Relation> relations = getRelations(file);
		
//		for (Relation rel : relations) {
//			System.out.println(rel.getRelationId() + ", " + rel.getSourceRoleName() + ", " + rel.getTargetClassId());
//		}
		
		long endTime = System.nanoTime();
		long timeElapsed = endTime - startTime;
		System.out.println("Execution time in milliseconds for relations: " + timeElapsed / 1000000);
		
		startTime = System.nanoTime();
		Map<String, String> class2ParentClassMap = getClass2ParentClassMap(relations);
		endTime = System.nanoTime();
		timeElapsed = endTime - startTime;
		System.out.println("Execution time in milliseconds for generalisations: " + timeElapsed / 1000000);

		startTime = System.nanoTime();
		Set<OntologyClass> classes = getOntologyClasses(file, class2ParentClassMap);
		
		System.out.println("Classes");
		for (OntologyClass cls : classes) {
			System.out.println(cls.getId() + "|" + cls.getName() + "|" + cls.getParentClass() + "|" + cls.getDefinition());
		}
		System.out.println("End classes\n");
		
		endTime = System.nanoTime();
		timeElapsed = endTime - startTime;
		System.out.println("Execution time in milliseconds for classes: " + timeElapsed / 1000000);
		
		startTime = System.nanoTime();
		Set<OntologyDataProperty> dataProperties = getOntologyDataProperties(file);		
		
		System.out.println("Data properties");
		for (OntologyDataProperty dp : dataProperties) {
			System.out.println(dp.getId() + "|" + dp.getName() + "|" + dp.getDataType() + "|" + dp.getSourceClassId() + "|" + dp.getSourceClassName());
		}
		System.out.println("End data properties\n");
		
		endTime = System.nanoTime();
		timeElapsed = endTime - startTime;
		System.out.println("Execution time in milliseconds for data properties: " + timeElapsed / 1000000);
		
		startTime = System.nanoTime();
		Set<OntologyObjectProperty> objectProperties = getOntologyObjectProperties(file);
		System.out.println("\nObject properties");
		for (OntologyObjectProperty op : objectProperties) {
			System.out.println(op.getId() + "|" + op.getName() + "|" + op.getSourceClassId() + "|" + op.getSourceClassName() + "|" + op.getTargetClassId() + "|" + op.getTargetClassName());
		}
		System.out.println("End object properties\n");
		endTime = System.nanoTime();
		timeElapsed = endTime - startTime;
		System.out.println("Execution time in milliseconds for object properties: " + timeElapsed / 1000000);

		System.out.println("Number of classes: " + classes.size());
		System.out.println("Number of data properties: " + dataProperties.size());		
		System.out.println("Number of relations: " + relations.size());				
		System.out.println("Number of generalizations: " + class2ParentClassMap.size());		
		System.out.println("Number of object properties: " + objectProperties.size());

	
	}
	
	//TODO: Investigate if relation-types "Unspecified" and "Bi-Directional" also should be included + how to deal with inverse object properties.
	public static Set<OntologyObjectProperty> getOntologyObjectProperties (String fileName) throws ParserConfigurationException, SAXException, IOException {
		
		Set<OntologyObjectProperty> objectProperties = new HashSet<OntologyObjectProperty>();
		
		Set<Relation> relations = getRelations(fileName);
		
		OntologyObjectProperty op = null;
		
		for (Relation rel : relations) {
						
			if (rel.getRelationType().equals("Association")) {
								
				if (rel.getDirection().equals("Source -> Destination")) {
					
					op = new OntologyObjectProperty.OntologyObjectPropertyBuilder()
							.setId(rel.getRelationId())
							.setName(rel.getSourceRoleName())
							.setSourceClassId(rel.getSourceClassId())
							.setSourceClassName(rel.getSourceClassName())
							.setTargetClassId(rel.getTargetClassId())
							.setTargetClassName(rel.getTargetClassName())
							.build();
					
					objectProperties.add(op);
					
				} if (rel.getRelationType().equals("Destination -> Source")) {
					
					op = new OntologyObjectProperty.OntologyObjectPropertyBuilder()
							.setId(rel.getRelationId())
							.setName(rel.getTargetRoleName())
							.setSourceClassName(rel.getTargetClassName())
							.setTargetClassName(rel.getSourceClassName())
							.build();
					
					objectProperties.add(op);
					
				}			
			}
		}		
		
		return objectProperties;
		
	}



	public static Set<OntologyDataProperty> getOntologyDataProperties (String fileName) throws ParserConfigurationException, SAXException, IOException {

		Set<OntologyDataProperty> dataProperties = new HashSet<OntologyDataProperty>();

		File xmlDoc = new File(fileName);
		DocumentBuilderFactory dbFact = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuild = dbFact.newDocumentBuilder();
		Document doc = dBuild.parse(xmlDoc);

		NodeList nList = doc.getElementsByTagName("element");

		String classId = null;
		String className = null;

		for (int i = 0; i < nList.getLength(); i++) {

			Node nNode = nList.item(i);

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {

				Element eElement = (Element) nNode;

				if (eElement.getAttribute("xmi:type").equals("uml:Class")) {

					classId = eElement.getAttribute("xmi:idref");
					className = eElement.getAttribute("name");

					NodeList childNodes = eElement.getChildNodes();

					for (int j = 0; j < childNodes.getLength(); j++) {

						Node childNode = childNodes.item(j);

						if (childNode.getNodeName().equals("attributes")) {

							Element attributeElement = (Element) childNode;
							NodeList attributeNodes = attributeElement.getChildNodes();

							OntologyDataProperty dp = null;

							String attributeId = null;
							String attributeName = null;
							String dataType = null;
							String minCardinality = null;
							boolean functional = false;

							for (int k = 0; k < attributeNodes.getLength(); k++) {

								Node attributeNode = attributeNodes.item(k);

								if (attributeNode.getNodeName().equals("attribute")) {

									Element attElement = (Element) attributeNode;

									attributeId = attElement.getAttribute("xmi:idref");
									attributeName = attElement.getAttribute("name");

									NodeList attributeChildNodes = attElement.getChildNodes();

									for (int l = 0; l < attributeChildNodes.getLength(); l++) {

										Node attChildNode = attributeChildNodes.item(l);

										if (attChildNode.getNodeName().equals("properties")) {
											Element propertiesElement = (Element) attChildNode;
											dataType = propertiesElement.getAttribute("type");
										}

										if (attChildNode.getNodeName().equals("bounds")) {
											Element boundsElement = (Element) attChildNode;
											minCardinality = boundsElement.getAttribute("lower");
										}

									}

									if (!minCardinality.equals("0")) {
										functional = true;
									}

									dp = new OntologyDataProperty.OntologyDataPropertyBuilder()
											.setSourceClassId(classId)
											.setSourceClassName(className)
											.setId(attributeId)
											.setName(attributeName)
											.setDataType(dataType)
											.setFunctional(functional)
											.build();


									dataProperties.add(dp);		

								}
							}
						}
					}
				}
			}
		}


		return dataProperties;

	}
	
	public static Map<String, String> getClass2ParentClassMap (Set<Relation> relations) {
		
		Map<String, String> class2ParentClassMap = new HashMap<String, String>();
		
		for (Relation rel : relations) {
			if (rel.getRelationType().equals("Generalization")) {
				class2ParentClassMap.put(rel.getSourceClassName(), rel.getTargetClassName());
			}
		}

		return class2ParentClassMap;
	}

	public static Set<OntologyClass> getOntologyClasses (String fileName, Map<String, String> class2ParentClassMap) throws SAXException, IOException, ParserConfigurationException {

		Set<OntologyClass> classes = new HashSet<OntologyClass>();

		File xmlDoc = new File(fileName);
		DocumentBuilderFactory dbFact = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuild = dbFact.newDocumentBuilder();
		Document doc = dBuild.parse(xmlDoc);

		OntologyClass cls = null;
		String classId = null;
		String className = null;
		String documentation = null;

		NodeList elementList = doc.getElementsByTagName("element");

		for (int i = 0; i < elementList.getLength(); i++) {

			Node elementNode = elementList.item(i);

			if (elementNode.getNodeType() == Node.ELEMENT_NODE) {

				Element eElement = (Element) elementNode;

				if (eElement.getAttribute("xmi:type").equals("uml:Class")) {

					classId = eElement.getAttribute("xmi:idref");
					className = eElement.getAttribute("name");

					NodeList childNodes = eElement.getChildNodes();

					for (int j = 0; j < childNodes.getLength(); j++) {

						Node childNode = childNodes.item(j);

						if (childNode.getNodeName().equals("properties")) {

							Element propertiesElement = (Element) childNode;
							documentation = propertiesElement.getAttribute("documentation");
						}

					}		

					cls = new OntologyClass.OntologyClassBuilder()
							.setId(classId)
							.setName(className)
							.setDefinition(documentation)
							.setParentClass(class2ParentClassMap.get(className))
							.build();

					classes.add(cls);
				}

			}

		}

		return classes;

	}

	public static Set<Relation> getRelations (String fileName) throws ParserConfigurationException, SAXException, IOException {

		Set<Relation> relations = new HashSet<Relation>();

		File xmlDoc = new File(fileName);
		DocumentBuilderFactory dbFact = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuild = dbFact.newDocumentBuilder();
		Document doc = dBuild.parse(xmlDoc);

		Relation rel = null;
		String relationId = null;
		String sourceId = null;
		String sourceName = null;
		String sourceRoleName = null;
		String targetId = null;
		String targetName = null;
		String targetRoleName = null;
		String relationType = null;
		String direction = null;

		NodeList connectorList = doc.getElementsByTagName("connector");

		for (int k = 0; k < connectorList.getLength(); k++) {

			Node connectorNode = connectorList.item(k);

			if (connectorNode.getNodeType() == Node.ELEMENT_NODE) {

				Element nElement = (Element) connectorNode;
				relationId = nElement.getAttribute("xmi:idref");

				NodeList connectorNodes = nElement.getChildNodes();

				for (int l = 0; l < connectorNodes.getLength(); l++) {

					Node connector = connectorNodes.item(l);

					if (connector.getNodeName().equals("source")) {

						Element sourceElement = (Element) connector;
						sourceId = sourceElement.getAttribute("xmi:idref");
						
						NodeList sourceNodes = sourceElement.getChildNodes();
						
						for (int m = 0; m < sourceNodes.getLength(); m++) {
							Node sourceChildNode = sourceNodes.item(m);
							
							if (sourceChildNode.getNodeName().equals("model")) {
								
								Element sourceModelElement = (Element) sourceChildNode;
								sourceName = sourceModelElement.getAttribute("name");
							}
							
							if (sourceChildNode.getNodeName().equals("role")) {
								
								Element sourceRoleElement = (Element) sourceChildNode;
								sourceRoleName = sourceRoleElement.getAttribute("name");
							}
						}
					}

					if (connector.getNodeName().equals("target")) {
						
						Element targetElement = (Element) connector;
						targetId = targetElement.getAttribute("xmi:idref");
						
						NodeList targetNodes = targetElement.getChildNodes();
						
						for (int n = 0; n < targetNodes.getLength(); n++) {
							Node targetChildNode = targetNodes.item(n);
							
							if (targetChildNode.getNodeName().equals("model")) {
								
								Element targetModelElement = (Element) targetChildNode;
								targetName = targetModelElement.getAttribute("name");
							}
							
							if (targetChildNode.getNodeName().equals("role")) {
								
								Element targetRoleElement = (Element) targetChildNode;
								targetRoleName = targetRoleElement.getAttribute("name");
							}
						}	
					}

					if (connector.getNodeName().equals("properties")) {
						Element propertiesElement = (Element) connector;
						relationType = propertiesElement.getAttribute("ea_type");
						direction = propertiesElement.getAttribute("direction");
					}

				}

			}

			rel = new Relation.RelationBuilder()
					.setRelationId(relationId)
					.setSourceClassId(sourceId)
					.setSourceClassName(sourceName)
					.setSourceRoleName(sourceRoleName)
					.setTargetClassId(targetId)
					.setTargetClassName(targetName)
					.setTargetRoleName(targetRoleName)
					.setRelationType(relationType)
					.setDirection(direction)
					.build();

			relations.add(rel);


		}

		return relations;	

	}




}
