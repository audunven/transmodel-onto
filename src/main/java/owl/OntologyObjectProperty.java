package owl;


public class OntologyObjectProperty {

	private String id;
	private String name;
	private String sourceClassName;
	private String sourceClassId;
	private String targetClassName;
	private String targetClassId;
	private boolean functional;

	private OntologyObjectProperty (OntologyObjectPropertyBuilder builder) {

		this.id = builder.id;
		this.name = builder.name;
		this.sourceClassName = builder.sourceClassName;
		this.sourceClassId = builder.sourceClassId;
		this.targetClassName = builder.targetClassName;
		this.targetClassId = builder.targetClassId;
		this.functional = builder.functional;
	}

	public static class OntologyObjectPropertyBuilder {

		private String id;
		private String name;
		private String sourceClassName;
		private String sourceClassId;
		private String targetClassName;
		private String targetClassId;
		private boolean functional;

		public OntologyObjectPropertyBuilder() {}

		public OntologyObjectPropertyBuilder setId (String id) {
			this.id = id;
			return this;
		}

		public OntologyObjectPropertyBuilder setName (String name) {
			this.name = name;
			return this;
		}

		public OntologyObjectPropertyBuilder setSourceClassName (String sourceClassName) {
			this.sourceClassName = sourceClassName;
			return this;
		}

		public OntologyObjectPropertyBuilder setSourceClassId (String sourceClassId) {
			this.sourceClassId = sourceClassId;
			return this;
		}

		public OntologyObjectPropertyBuilder setTargetClassName (String targetClassName) {
			this.targetClassName = targetClassName;
			return this;
		}

		public OntologyObjectPropertyBuilder setTargetClassId (String targetClassId) {
			this.targetClassId = targetClassId;
			return this;
		}

		public OntologyObjectPropertyBuilder setFunctional (boolean functional) {
			this.functional = functional;
			return this;
		}

		public OntologyObjectProperty build() {
			return new OntologyObjectProperty(this);
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

	public String getTargetClassName() {
		return targetClassName;
	}

	public String getTargetClassId() {
		return targetClassId;
	}

	public boolean isFunctional() {
		return functional;
	}




}
