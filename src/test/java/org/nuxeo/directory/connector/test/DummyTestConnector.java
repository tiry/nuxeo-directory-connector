package org.nuxeo.directory.connector.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nuxeo.directory.connector.AbstractInMemoryEntryConnector;
import org.nuxeo.directory.connector.ConnectorBasedDirectoryDescriptor;
import org.nuxeo.directory.connector.EntryConnector;
import org.nuxeo.ecm.core.api.ClientException;

public class DummyTestConnector extends AbstractInMemoryEntryConnector
        implements EntryConnector {

    protected Map<String, String> params;

    public List<String> getEntryIds() {
        List<String> ids = new ArrayList<String>();
        ids.addAll(params.keySet());
        return ids;
    }

    public Map<String, Object> getEntryMap(String id) {

        Map<String, Object> map = null;

        String data = params.get(id);
        if (data != null) {
            map = new HashMap<String, Object>();
            String[] parts = data.split("\\|");
            for (String part : parts) {
                String[] kv = part.split("=");
                if (kv.length == 2) {
                    map.put(kv[0], kv[1]);
                }
            }
        }
        return map;
    }

    public boolean hasEntry(String id) throws ClientException {
        return params.keySet().contains(id);
    }

    @Override
    public void init(ConnectorBasedDirectoryDescriptor descriptor) {
        super.init(descriptor);
        params = descriptor.getParameters();
    }

    @Override
    public Set<String> getFullTextConfig() {
        Set<String> ft = new HashSet<String>();

        ft.add("username");

        return ft;
    }

}
