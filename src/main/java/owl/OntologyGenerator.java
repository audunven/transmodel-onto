package owl;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.util.SimpleIRIMapper;
import org.xml.sax.SAXException;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import utilities.StringUtilities;
import xml.TransmodelParser;

//see examples on how to create domain and ranges here: https://www.javatips.net/api/org.semanticweb.owlapi.model.owldisjointclassesaxiom
//see examples on how to create property restrictions here (e.g. line 870): https://github.com/phillord/owl-api/blob/master/contract/src/test/java/org/coode/owlapi/examples/Examples.java
public class OntologyGenerator {

	public static void main(String[] args) throws OWLOntologyCreationException, ParserConfigurationException, SAXException, IOException, OWLOntologyStorageException {
		
		String xmiFile = "./files/Transmodel_Combined.xml";
		String ontologyIRI = "http://www.reisenavet.no/ontologies/transmodelOntoNewModes.owl";
		String owlFile = "./files/TransmodelOntoCombined2.owl";

		createOntology(xmiFile, owlFile, ontologyIRI);

	}
	
	public static void createOntology(String xmiFile, String owlFile, String ontoIRI) throws OWLOntologyCreationException, ParserConfigurationException, SAXException, IOException, OWLOntologyStorageException {
		
		//String file = "./files/Transmodel_Combined.xml";
		IRI ontologyIRI = IRI.create(ontoIRI);
		File owl_file = new File(owlFile);
		
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		
		IRI documentIRI = IRI.create(owl_file.toURI());
		SimpleIRIMapper mapper = new SimpleIRIMapper(ontologyIRI, documentIRI);
		manager.addIRIMapper(mapper);

		OWLOntology onto = manager.createOntology(ontologyIRI);

		OWLDataFactory factory = manager.getOWLDataFactory();

		
		Set<Relation> relations = TransmodelParser.getRelations(xmiFile);
		Map<String, String> class2ParentClassMap = TransmodelParser.getClass2ParentClassMap(relations);

		//DECLARE CLASSES, DEFINITIONS AND LABELS (PROPER NAME FROM UML MODEL) 
		Set<OntologyClass> classes = TransmodelParser.getOntologyClasses(xmiFile, class2ParentClassMap);

		OWLDataFactory df = OWLManager.getOWLDataFactory();

		OWLEntity classEntity = null;

		OWLAxiom declarationAxiom = null;

		OWLAnnotation annotation = null;
		OWLAxiom annotationAxiom = null;

		OWLAnnotation label = null;
		OWLAxiom labelAxiom = null;
		
		System.out.println("Printing classes: ");
		for (OntologyClass c : classes) {
			System.out.println(c.getName());
		}

		for (OntologyClass cls : classes) {
			classEntity = df.getOWLEntity(EntityType.CLASS, IRI.create(ontologyIRI + "#" + StringUtilities.toCamelCase(cls.getName())));
			//classEntity = df.getOWLEntity(EntityType.CLASS, IRI.create(ontologyIRI + "#" + cls.getName()));
			annotation = df.getOWLAnnotation(df.getRDFSComment(), df.getOWLLiteral(cls.getDefinition(), "en"));			
			declarationAxiom = df.getOWLDeclarationAxiom(classEntity);
			annotationAxiom = df.getOWLAnnotationAssertionAxiom(classEntity.asOWLClass().getIRI(), annotation);
			label = df.getOWLAnnotation(df.getRDFSLabel(), df.getOWLLiteral(cls.getName(), "en"));
			labelAxiom = df.getOWLAnnotationAssertionAxiom(classEntity.asOWLClass().getIRI(), label);
			manager.addAxiom(onto, declarationAxiom);
			manager.addAxiom(onto, annotationAxiom);
			manager.addAxiom(onto, labelAxiom);

		}

		manager.saveOntology(onto, documentIRI);


		//CREATE TAXONOMY
		OWLEntity parentClassEntity = null;
		OWLAxiom subclassOfAxiom = null;
		OWLEntity moduleClassEntity = null;
		OWLAxiom moduleAxiom = null;

		for (OntologyClass cls : classes) {
			classEntity = df.getOWLEntity(EntityType.CLASS, IRI.create(ontologyIRI + "#" + StringUtilities.toCamelCase(cls.getName())));
			//classEntity = df.getOWLEntity(EntityType.CLASS, IRI.create(ontologyIRI + "#" + cls.getName()));

			if (cls.getParentClass() != null) {//TODO: Check why null is returned in some cases!
				parentClassEntity = df.getOWLEntity(EntityType.CLASS, IRI.create(ontologyIRI + "#" + StringUtilities.toCamelCase(cls.getParentClass())));
				//parentClassEntity = df.getOWLEntity(EntityType.CLASS, IRI.create(ontologyIRI + "#" + cls.getParentClass()));
				subclassOfAxiom = df.getOWLSubClassOfAxiom(classEntity.asOWLClass(), parentClassEntity.asOWLClass());
				manager.addAxiom(onto, subclassOfAxiom);
			}

			if (cls.getModule() != null) {

				moduleClassEntity = df.getOWLEntity(EntityType.CLASS, IRI.create(ontologyIRI + "#" + cls.getModule()));
				moduleAxiom = df.getOWLSubClassOfAxiom(classEntity.asOWLClass(), moduleClassEntity.asOWLClass());
				manager.addAxiom(onto, moduleAxiom);

			}
		}

		//DECLARE OBJECT PROPERTIES AND SET THEIR DOMAIN AND RANGE CLASSES
		Set<OntologyObjectProperty> objectProperties = TransmodelParser.getOntologyObjectProperties(xmiFile);
		
		System.out.println("Printing object properties: ");
		for (OntologyObjectProperty op : objectProperties) {
			System.out.println(op.getName());
		}

		//FIXME: StringUtilities.toMixedCase is only relevant for the Transmodel XMI where associations and attributes contain white space, e.g. "prepared by".
		for (OntologyObjectProperty op : objectProperties) {
			if (!op.getName().equals("")) {
				classEntity = df.getOWLEntity(EntityType.OBJECT_PROPERTY, IRI.create(ontologyIRI + "#" + StringUtilities.toLowerCase(op.getName())));
				//classEntity = df.getOWLEntity(EntityType.OBJECT_PROPERTY, IRI.create(ontologyIRI + "#" + op.getName()));
				System.out.println("Printing classEntity: " + classEntity);
				declarationAxiom = df.getOWLDeclarationAxiom(classEntity);
				manager.addAxiom(onto, declarationAxiom);			
			}

		}

		//create domain and range maps holding an object property as key and the set of domain/range classes as values		
		Multimap<String, String> opDomainMap = createOPDomainMap(objectProperties);
		Multimap<String, String> opRangeMap = createOPRangeMap(objectProperties);

		Set<OWLAxiom> opDomainsAndRanges = new HashSet<OWLAxiom>();

		for (OWLObjectProperty o : onto.getObjectPropertiesInSignature()) {			

			//add domain classes associated with object property o from the domain map
			Collection<String> domainClasses = opDomainMap.get(o.getIRI().getFragment());
			Set<OWLClass> owlClassesDomain = new HashSet<OWLClass>();
			for (String s : domainClasses) {
				owlClassesDomain.add(OntologyOperations.getClass(s, onto));
			}

			for (OWLClass c : owlClassesDomain) {
				if (c != null) {
					opDomainsAndRanges.add(df.getOWLObjectPropertyDomainAxiom(o, c));
				} 
			}

			//add range classes associated with object property o from the range map
			Collection<String> rangeClasses = opRangeMap.get(o.getIRI().getFragment());						
			Set<OWLClass> owlClassesRange = new HashSet<OWLClass>();
			for (String s : rangeClasses) {
				owlClassesRange.add(OntologyOperations.getClass(s, onto));
			}

			for (OWLClass c : owlClassesRange) {
				if (c != null) {
					opDomainsAndRanges.add(df.getOWLObjectPropertyRangeAxiom(o, c));
				} 
			}

		}
		
		System.out.println("Printing domains and ranges axioms for OPs: ");
		System.out.println(opDomainsAndRanges);
		
		onto.add(opDomainsAndRanges);

		//TODO: SET CORRECT RANGE DATATYPE FOR DATA PROPERTIES
		Set<OntologyDataProperty> dataProperties = TransmodelParser.getOntologyDataProperties(xmiFile);
		
		System.out.println("Printing data properties: ");
		for (OntologyDataProperty dp : dataProperties) {
			System.out.println(dp.getName());
		}

		for (OntologyDataProperty dp : dataProperties) {
			if (!dp.getName().equals("")) {
				//classEntity = df.getOWLEntity(EntityType.DATA_PROPERTY, IRI.create(ontologyIRI + "#" + StringUtilities.toMixedCase(dp.getName())));
				classEntity = df.getOWLEntity(EntityType.DATA_PROPERTY, IRI.create(ontologyIRI + "#" + dp.getName()));
				declarationAxiom = df.getOWLDeclarationAxiom(classEntity);
				manager.addAxiom(onto, declarationAxiom);
			}
		}

		//create domain map holding a data property as key and the set of domain classes as values
		Multimap<String, String> dpDomainMap = createDPDomainMap(dataProperties);
		Set<OWLAxiom> dpDomainsAndRanges = new HashSet<OWLAxiom>();

		for (OWLDataProperty d : onto.getDataPropertiesInSignature()) {

			//add domain classes associated with data property d from the domain map
			Collection<String> domainClasses = dpDomainMap.get(d.getIRI().getFragment());
			Set<OWLClass> owlClassesDomain = new HashSet<OWLClass>();
			for (String s : domainClasses) {
				owlClassesDomain.add(OntologyOperations.getClass(s, onto));
			}

			for (OWLClass c : owlClassesDomain) {
				if (c != null) {
					dpDomainsAndRanges.add(df.getOWLDataPropertyDomainAxiom(d, c));
				}
			}

			//add range type associated with data property d from type conversion 
			//TODO: Using only xsd:string for now, but must add the appropriate data type
			OWLDatatype stringDatatype = factory.getStringOWLDatatype();
			dpDomainsAndRanges.add(df.getOWLDataPropertyRangeAxiom(d, stringDatatype));

		}

		System.out.println("Printing domains and ranges axioms for DPs: ");
		System.out.println(dpDomainsAndRanges);

		onto.add(dpDomainsAndRanges);

		//SAVE ONTOLOGY WITH CLASSES, OBJECT PROPERTIES AND DATA PROPERTIES
		manager.saveOntology(onto, documentIRI);
		
	}

	public static Multimap<String, String> createDPDomainMap (Set<OntologyDataProperty> dataProperties) {

		Multimap<String, String> domainMap = LinkedHashMultimap.create();

		for (OntologyDataProperty dp : dataProperties) {
			if (!dp.getName().equals("")) { 
				domainMap.put(StringUtilities.toMixedCase(dp.getName()), StringUtilities.toCamelCase(dp.getSourceClassName()));
				//domainMap.put(dp.getName(), dp.getSourceClassName());
			}
		}

		return domainMap;

	}

	public static Multimap<String, String> createOPDomainMap (Set<OntologyObjectProperty> objectProperties) {

		Multimap<String, String> domainMap = LinkedHashMultimap.create();

		for (OntologyObjectProperty op : objectProperties) {
			if (!op.getName().equals("")) { //TODO: Double-check why there are blank op.getName() entries in the XMI
				domainMap.put(StringUtilities.toMixedCase(op.getName()), StringUtilities.toCamelCase(op.getSourceClassName()));
				//domainMap.put(op.getName(), op.getSourceClassName());
			}
		}


		return domainMap;

	}

	public static Multimap<String, String> createOPRangeMap (Set<OntologyObjectProperty> objectProperties) {

		Multimap<String, String> rangeMap = LinkedHashMultimap.create();

		for (OntologyObjectProperty op : objectProperties) {
			if (!op.getName().equals("")) { //TODO: Double-check why there are blank op.getName() entries in the XMI
				rangeMap.put(StringUtilities.toMixedCase(op.getName()), StringUtilities.toCamelCase(op.getTargetClassName()));
				//rangeMap.put(op.getName(), op.getTargetClassName());
			}
		}


		return rangeMap;

	}


}
