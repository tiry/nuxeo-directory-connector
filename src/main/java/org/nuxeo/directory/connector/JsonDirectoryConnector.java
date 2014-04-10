package org.nuxeo.directory.connector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nuxeo.directory.connector.AbstractInMemoryEntryConnector;
import org.nuxeo.directory.connector.ConnectorBasedDirectoryDescriptor;
import org.nuxeo.directory.connector.EntryConnector;
import org.nuxeo.ecm.core.api.ClientException;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

public class JsonDirectoryConnector extends AbstractInMemoryEntryConnector
        implements EntryConnector {
    
    protected Map<String, String> params;
    protected ArrayList<HashMap<String, Object>> results;
	protected Log log = LogFactory.getLog("hi");

    public ArrayList<HashMap<String, Object>> getJsonStream()
    {

    	ArrayList<HashMap<String, Object>> mapList = new ArrayList<HashMap<String, Object>>();
    	
    	Client client = Client.create();
    	WebResource webResource = client.resource(params.get("url"));
    	ClientResponse response = webResource.accept("application/json")
    			.get(ClientResponse.class);

    	if (response.getStatus() != 200) {
    		throw new RuntimeException("Failed : HTTP error code : "
    				+ response.getStatus());
    	} 

    	// response to map code from RestResponse 
    	ObjectMapper objectMapper = new ObjectMapper();
    	JsonNode responseAsJson = null;

    	try {
    		responseAsJson = objectMapper.readTree(response.getEntityInputStream());
    	} catch (JsonProcessingException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	} catch (IOException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
		JsonNode resultsNode = null;
		resultsNode = responseAsJson.get("results");
    	TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {};
    	for (int i = 0; i < resultsNode.size(); i++) {
            try {
            	Map<String, Object> map = new HashMap<String, Object>();
            	map = objectMapper.readValue(resultsNode.get(i), typeRef);
            	mapList.add((HashMap<String, Object>) map);

            } catch (IOException e) {
            	e.printStackTrace();
            }            
        }
        return mapList;
    }
    
    public List<String> getEntryIds() {
        List<String> ids = new ArrayList<String>();
        for (int i = 0; i < results.size(); i++) {
        	ids.add(results.get(i).get("trackId").toString());
        }
        return ids;
    }
    
    public Map<String, Object> getEntryMap(String id) {
    	Map<String, Object> rc = new HashMap<String,Object>();
    	rc = null;
    	
    	for (int i = 0; i < results.size(); i++) {
        	if (results.get(i).get("trackId").toString().equals(id)){
        		rc = results.get(i);
        		break;
        	}
        }
        return rc;
    }

    public boolean hasEntry(String id) throws ClientException {
        for (int i = 0; i < results.size(); i++) {
        	if (results.get(i).get("trackId").equals(id)){
        		return true;
        	}
        }
        return false;
    }

    @Override
    public void init(ConnectorBasedDirectoryDescriptor descriptor) {
        super.init(descriptor);
        params = descriptor.getParameters();
        results = getJsonStream();
    }


}
