package org.nuxeo.directory.connector.json;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.nuxeo.directory.connector.AbstractEntryConnector;
import org.nuxeo.directory.connector.ConnectorBasedDirectoryDescriptor;
import org.nuxeo.ecm.core.api.ClientRuntimeException;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public abstract class BaseJSONDirectoryConnector extends
        AbstractEntryConnector {

    protected Client client;

    public BaseJSONDirectoryConnector() {
        super();
    }

    protected JsonNode call(String url, ObjectMapper objectMapper) {
        WebResource webResource = client.resource(url);
        ClientResponse response = webResource.accept("application/json").get(
                ClientResponse.class);

        if (response.getStatus() != 200) {
            throw new ClientRuntimeException(
                    "Failed to call remote service : HTTP error code : "
                            + response.getStatus());
        }

        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
        }

        try {
            return objectMapper.readTree(response.getEntityInputStream());
        } catch (Exception e) {
            throw new ClientRuntimeException(
                    "Erroe while reading JSON response", e);
        }

    }

    @Override
    public void init(ConnectorBasedDirectoryDescriptor descriptor) {
        client = Client.create();
    }

    @Override
    public void close() {
        if (client != null) {
            client.destroy();
        }
    }

}