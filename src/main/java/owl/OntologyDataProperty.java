package owl;

public class OntologyDataProperty {

	private String id;
	private String name;
	private String sourceClassName;
	private String sourceClassId;
	private String dataType;
	private boolean functional;

	private OntologyDataProperty (OntologyDataPropertyBuilder builder) {
		this.id = builder.id;
		this.name = builder.name;
		this.sourceClassName = builder.sourceClassName;
		this.sourceClassId = builder.sourceClassId;
		this.dataType = builder.dataType;
		this.functional = builder.functional;
	}

	public static class OntologyDataPropertyBuilder {

		private String id;
		private String name;
		private String sourceClassName;
		private String sourceClassId;
		private String dataType;
		private boolean functional;

		public OntologyDataPropertyBuilder() {}
		
		public OntologyDataPropertyBuilder setId (String id) {
			this.id = id;
			return this;
		}
		
		public OntologyDataPropertyBuilder setName (String name) {
			this.name = name;
			return this;
		}
		
		public OntologyDataPropertyBuilder setSourceClassName (String sourceClassName) {
			this.sourceClassName = sourceClassName;
			return this;
		}
		
		public OntologyDataPropertyBuilder setSourceClassId (String sourceClassId) {
			this.sourceClassId = sourceClassId;
			return this;
		}
		
		public OntologyDataPropertyBuilder setDataType (String dataType) {
			this.dataType = dataType;
			return this;
		}
		
		public OntologyDataPropertyBuilder setFunctional (boolean functional) {
			this.functional = functional;
			return this;
		}
		
		public OntologyDataProperty build() {
			return new OntologyDataProperty(this);
		}

	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getSourceClassName() {
		return sourceClassName;
	}

	public String getSourceClassId() {
		return sourceClassId;
	}

	public String getDataType() {
		return dataType;
	}

	public boolean isFunctional() {
		return functional;
	}
	
	

}
