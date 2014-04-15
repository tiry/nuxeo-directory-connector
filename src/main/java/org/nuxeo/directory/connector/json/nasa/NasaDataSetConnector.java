package org.nuxeo.directory.connector.json.nasa;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.JsonNode;
import org.nuxeo.directory.connector.json.BaseJSONDirectoryConnector;
import org.nuxeo.ecm.core.api.ClientException;

public class NasaDataSetConnector extends BaseJSONDirectoryConnector {

    @Override
    public boolean hasEntry(String id) throws ClientException {
        return getEntryMap(id)!=null;
    }

    @Override
    public Map<String, Object> getEntryMap(String id) {

        String getDataSetUrl = params.get("url") + "get_dataset?id=" + id;
        JsonNode responseAsJson = call(getDataSetUrl);

        JsonNode result = responseAsJson.get("post");

        try {
            return readAsMap(result);
        } catch (IOException e) {
            log.error("Unable to handle mapping from JSON", e);
            return null;
        }
    }

    @Override
    public List<String> getEntryIds() {
        return new ArrayList<String>();
    }

    @Override
    public List<String> queryEntryIds(Map<String, Serializable> filter,
            Set<String> fulltext) {

        if (filter.containsKey("category")) {
            String getDataSetUrl = params.get("url") + "get_category_datasets/?id=" + filter.get("category");
            JsonNode responseAsJson = call(getDataSetUrl);

            JsonNode result = responseAsJson.get("posts");

            List<String> ids = new ArrayList<>();

            for (int i = 0; i < result.size(); i++) {
                ids.add(result.get(i).get("id").getValueAsText());
            }
            return ids;
        }
        return null;
    }

}
