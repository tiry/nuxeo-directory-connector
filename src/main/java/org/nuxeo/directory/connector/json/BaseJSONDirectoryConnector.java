package org.nuxeo.directory.connector.json;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.MapType;
import org.codehaus.jackson.type.TypeReference;
import org.nuxeo.directory.connector.AbstractEntryConnector;
import org.nuxeo.directory.connector.ConnectorBasedDirectoryDescriptor;
import org.nuxeo.ecm.core.api.ClientRuntimeException;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public abstract class BaseJSONDirectoryConnector extends
        AbstractEntryConnector {

    protected Client client;
    protected Map<String, String> params;
    protected ObjectMapper objectMapper = null;

    protected static final Log log = LogFactory.getLog(BaseJSONDirectoryConnector.class);

    public BaseJSONDirectoryConnector() {
        super();
    }

    protected ObjectMapper getMapper() {
        if (objectMapper==null) {
            objectMapper = new ObjectMapper();
        }
        return objectMapper;
    }

    protected Map<String, Object> readAsMap(JsonNode node) throws IOException {
        MapType type = getMapper().getTypeFactory().constructMapType(
                Map.class, String.class, Object.class);        
        return getMapper().readValue(node, type);
    }

    protected JsonNode call(String url) {
        WebResource webResource = client.resource(url);
        ClientResponse response = webResource.accept("application/json").get(
                ClientResponse.class);

        if (response.getStatus() != 200) {
            throw new ClientRuntimeException(
                    "Failed to call remote service : HTTP error code : "
                            + response.getStatus());
        }

        try {
            return getMapper().readTree(response.getEntityInputStream());
        } catch (Exception e) {
            throw new ClientRuntimeException(
                    "Erroe while reading JSON response", e);
        }

    }

    @Override
    public void init(ConnectorBasedDirectoryDescriptor descriptor) {
        client = Client.create();
        params = descriptor.getParameters();
    }

    @Override
    public void close() {
        if (client != null) {
            client.destroy();
        }
    }

}