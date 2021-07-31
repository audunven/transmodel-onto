package owl;


public class OntologyClass {
	
	private String id;
	private String name;
	private String definition;
	private String parentClass;
	private String module;
	
	private OntologyClass (OntologyClassBuilder builder) {
		this.id = builder.id;
		this.name = builder.name;
		this.definition = builder.definition;
		this.parentClass = builder.parentClass;
		this.module = builder.module;
	}
	
	public static class OntologyClassBuilder {
		
		private String id;
		private String name;
		private String definition;
		private String parentClass;
		private String module;
		
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
		
		public OntologyClassBuilder setModule (String module) {
			this.module = module;
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
	
	
	public String getModule() {
		return module;
	}

	public String toString() {
		return this.id + " : " + this.name + " : " + this.definition;
	}
	

}
