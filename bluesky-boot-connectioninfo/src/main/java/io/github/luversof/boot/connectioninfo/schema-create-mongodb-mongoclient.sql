db.createCollection("MongoClientConnectionConfig", {
	capped: false,
		validator: {
		"$jsonSchema": {
			"bsonType": "object",
			"required": [
				"_id",
				"connectionString",
				"connnection"
			],
			"properties": {
				"_id": {
					"bsonType": "objectId"
				},
				"connectionString": {
					"bsonType": "string"
				},
				"connnection": {
					"bsonType": "string"
				},
				"username": {
					"bsonType": "string"
				},
				"password": {
					"bsonType": "string"
				}
			}
		}
	}
})
