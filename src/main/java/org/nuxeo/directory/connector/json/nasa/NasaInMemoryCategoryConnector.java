package org.nuxeo.directory.connector.json.nasa;

import org.codehaus.jackson.JsonNode;
import org.nuxeo.directory.connector.json.JsonInMemoryDirectoryConnector;

public class NasaInMemoryCategoryConnector extends
        JsonInMemoryDirectoryConnector {

    @Override
    protected JsonNode extractResult(JsonNode responseAsJson) {
        return responseAsJson.get("categories");
    }

}
