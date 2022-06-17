package owl;


public class Relation {

	private String relationId;
	private String relationType;
	private String direction;
	
	private String sourceClassId;
	private String sourceClassName;
	private String sourceRoleName;
	private String sourceEndType;
	private String sourceCardinality;
	
	private String targetClassId;
	private String targetClassName;
	private String targetRoleName;
	private String targetEndType;	
	private String targetCardinality;

	private Relation (RelationBuilder builder) {

		this.relationId = builder.relationId;
		this.sourceClassId = builder.sourceClassId;
		this.sourceClassName = builder.sourceClassName;
		this.sourceRoleName = builder.sourceRoleName;
		this.targetClassId = builder.targetClassId;
		this.targetClassName = builder.targetClassName;
		this.targetRoleName = builder.targetRoleName;
		this.relationType = builder.relationType;
		this.direction = builder.direction;
		this.sourceEndType = builder.sourceEndType;
		this.targetEndType = builder.targetEndType;
		this.sourceCardinality = builder.sourceCardinality;
		this.targetCardinality = builder.targetCardinality;

	}

	public static class RelationBuilder {

		private String relationId;
		private String sourceClassId;
		private String sourceClassName;
		private String sourceRoleName;
		private String targetClassId;
		private String targetClassName;
		private String targetRoleName;
		private String relationType;
		private String direction;
		private String sourceEndType;
		private String targetEndType;
		private String sourceCardinality;
		private String targetCardinality;
		
		public RelationBuilder() {}

		public RelationBuilder setRelationId (String relationId) {
			this.relationId = relationId;
			return this;
		}
		
		public RelationBuilder setSourceClassId (String sourceClassId) {
			this.sourceClassId = sourceClassId;
			return this;
		}
		
		public RelationBuilder setSourceClassName (String sourceClassName) {
			this.sourceClassName = sourceClassName;
			return this;
		}
		
		public RelationBuilder setSourceRoleName (String sourceRoleName) {
			this.sourceRoleName = sourceRoleName;
			return this;
		}

		public RelationBuilder setTargetClassId (String targetClassId) {
			this.targetClassId = targetClassId;
			return this;
		}
		
		public RelationBuilder setTargetClassName (String targetClassName) {
			this.targetClassName = targetClassName;
			return this;
		}
		
		public RelationBuilder setTargetRoleName (String targetRoleName) {
			this.targetRoleName = targetRoleName;
			return this;
		}

		public RelationBuilder setRelationType (String relationType) {
			this.relationType = relationType;
			return this;
		}
		
		public RelationBuilder setDirection (String direction) {
			this.direction = direction;
			return this;
		}
		

		public RelationBuilder setSourceEndType(String sourceEndType) {
			this.sourceEndType = sourceEndType;
			return this;
		}

		public RelationBuilder setTargetEndType(String targetEndType) {
			this.targetEndType = targetEndType;
			return this;
		}

		public RelationBuilder setSourceCardinality(String sourceCardinality) {
			this.sourceCardinality = sourceCardinality;
			return this;
		}

		public RelationBuilder setTargetCardinality(String targetCardinality) {
			this.targetCardinality = targetCardinality;
			return this;
		}

		public Relation build() {
			return new Relation(this);
		}

	}

	public String getRelationId() {
		return relationId;
	}

	public String getSourceClassId() {
		return sourceClassId;
	}

	public String getSourceClassName() {
		return sourceClassName;
	}
	
	public String getSourceRoleName() {
		return sourceRoleName;
	}

	public String getTargetClassId() {
		return targetClassId;
	}
	
	public String getTargetClassName() {
		return targetClassName;
	}
	
	public String getTargetRoleName() {
		return targetRoleName;
	}

	public String getRelationType() {
		return relationType;
	}
	
	public String getDirection () {
		return direction;
	}

	public String getSourceEndType() {
		return sourceEndType;
	}

	public String getTargetEndType() {
		return targetEndType;
	}

	public String getSourceCardinality() {
		return sourceCardinality;
	}

	public String getTargetCardinality() {
		return targetCardinality;
	}

	public String toString() {
		return this.sourceClassId + " : " + this.targetClassId + " : " + this.relationType;
	}

}
