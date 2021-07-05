package owl;


public class OntologyClass {
	
	private String id;
	private String name;
	private String definition;
	private String parentClass;
	
	private OntologyClass (OntologyClassBuilder builder) {
		this.id = builder.id;
		this.name = builder.name;
		this.definition = builder.definition;
		this.parentClass = builder.parentClass;
	}
	
	public static class OntologyClassBuilder {
		
		private String id;
		private String name;
		private String definition;
		private String parentClass;
		
		public OntologyClassBuilder() {}
		
		public OntologyClassBuilder setId (String id) {
			this.id = id;
			return this;
		}
		
		public OntologyClassBuilder setName (String name) {
			this.name = name;
			return this;
		}
		
		public OntologyClassBuilder setDefinition (String definition) {
			this.definition = definition;
			return this;
		}
		
		public OntologyClassBuilder setParentClass (String parentClass) {
			this.parentClass = parentClass;
			return this;
		}
		
		public OntologyClass build() {
			return new OntologyClass(this);
		}
		
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDefinition() {
		return definition;
	}

	public String getParentClass() {
		return parentClass;
	}
	
	public String toString() {
		return this.id + " : " + this.name + " : " + this.definition;
	}
	

}
