package org.nuxeo.directory.connector.json.nasa;

import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.nuxeo.directory.connector.json.JsonInMemoryDirectoryConnector;

public class NasaInMemoryCategoryConnector extends
        JsonInMemoryDirectoryConnector {

    @Override
    protected JsonNode extractResult(JsonNode responseAsJson) {
        return responseAsJson.get("categories");
    }

    @Override
    public Map<String, Object> getEntryMap(String id) {
        Map<String, Object> entry = super.getEntryMap(id);
        // add the obsolete flag so that the default directory filters will work
        entry.put("obsolete", new Long(0));
        return entry;
    }
}
