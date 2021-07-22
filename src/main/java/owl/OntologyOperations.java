package owl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;



/**
 * @author audunvennesland Date:02.02.2017
 * @version 1.0
 */
public class OntologyOperations {



	/**
	 * An OWLOntologyManagermanages a set of ontologies. It is the main point
	 * for creating, loading and accessing ontologies.
	 */
	static OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	/**
	 * The OWLReasonerFactory represents a reasoner creation point.
	 */
	static OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();

	/**
	 * 
	 */
	static PelletReasonerFactory pelletReasonerFactory = new PelletReasonerFactory();

	/**
	 * A HashMap holding an OWLEntity as key and an ArrayList of instances
	 * associated with the OWLEntity
	 */
	private static HashMap<OWLEntity, ArrayList<String>> instanceMap = new HashMap<OWLEntity, ArrayList<String>>();


	/**
	 * Default constructor
	 */
	public OntologyOperations() {

	}

	/**
	 * Converts from string to xsd:dateTime which is required in OWL
	 * @param manager
	 * @param input
	 * @return
	   Apr 19, 2021
	 */
	public static OWLLiteral convertToDateTime(OWLOntologyManager manager, String input) {	
				
		if (input.equals("NULL")) {
			input = "0000-00-00 00:00:00.0000000";
		}
		String dateTime = input.substring(0, input.lastIndexOf("."));
		
		//String dateTime = input.replaceAll(".0000000", "");
		
		dateTime = dateTime.replaceAll(" ", "T");
		
		OWLDataFactory df = manager.getOWLDataFactory();
		
		
		OWLLiteral dataTimeLiteral = df.getOWLLiteral(dateTime,
				OWL2Datatype.XSD_DATE_TIME);
		
		
		return dataTimeLiteral;
	}
	
	/**
	 * Retrieves an OWLClass from its class name represented as a string
	 * @param className
	 * @param ontology
	 * @return
	 */
	public static OWLClass getClass(String className, OWLOntology ontology) {

		OWLClass relevantClass = null;

		Set<OWLClass> classes = ontology.getClassesInSignature();

		for (OWLClass cls : classes) {
			if (cls.getIRI().getFragment().equals(className)) {
				relevantClass = cls;
				break;
			} else {
				relevantClass = null;
			}
		}

		return relevantClass;


	}

	
	/**
	 * Converts from string to xsd:int which is required in OWL
	 * @param manager
	 * @param input
	 * @return
	   Apr 19, 2021
	 */
	public static OWLLiteral convertToInt (OWLOntologyManager manager, String input) {
		
		OWLDataFactory df = manager.getOWLDataFactory();
		
		OWLLiteral intLiteral = df.getOWLLiteral(input,
				OWL2Datatype.XSD_INT);
		
		return intLiteral;
		
		
	}
	
	/**
	 * Converts from string to xsd:decimal which is required in OWL
	 * @param manager
	 * @param input
	 * @return
	   Apr 19, 2021
	 */
	public static OWLLiteral convertToDecimal (OWLOntologyManager manager, String input) {
		
		OWLDataFactory df = manager.getOWLDataFactory();
		
		OWLLiteral intLiteral = df.getOWLLiteral(input,
				OWL2Datatype.XSD_DECIMAL);
		
		return intLiteral;
		
		
	}

//	/**
//	 * Retrieves an OWLClass from its class name represented as a string
//	 * @param className
//	 * @param ontology
//	 * @return
//	 */
//	public static OWLClass getClass(String className, OWLOntology ontology) {
//
//		OWLClass relevantClass = null;
//
//		Set<OWLClass> classes = ontology.getClassesInSignature();
//
//		for (OWLClass cls : classes) {
//			//System.out.println("Test: Does cls: " + cls.getIRI().getFragment() + " equal " + className + " ?");
//			if (cls.getIRI().getFragment().equals(className)) {
//				relevantClass = cls;
//				break;
//			} else {
//				relevantClass = null;
//			}
//		}
//
//		//System.out.println("Test: Returning " + relevantClass);
//		return relevantClass;
//
//
//	}

	public static OWLObjectProperty getObjectProperty(String objectPropertyName, OWLOntology ontology) {

		OWLObjectProperty relevantOP = null;

		Set<OWLObjectProperty> ops = ontology.getObjectPropertiesInSignature();

		for (OWLObjectProperty op : ops) {
			if (op.getIRI().getFragment().equals(objectPropertyName)) {
				relevantOP = op;
				break;
			} else {
				relevantOP = null;
			}
		}

		return relevantOP;

	}

	public static OWLDataProperty getDataProperty(String objectPropertyName, OWLOntology ontology) {

		OWLDataProperty relevantDP = null;

		Set<OWLDataProperty> dps = ontology.getDataPropertiesInSignature();

		for (OWLDataProperty dp : dps) {
			if (dp.getIRI().getFragment().equals(objectPropertyName)) {
				relevantDP = dp;
				break;
			} else {
				relevantDP = null;
			}
		}

		return relevantDP;

	}

}