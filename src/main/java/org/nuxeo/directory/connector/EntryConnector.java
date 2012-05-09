package org.nuxeo.directory.connector;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.directory.DirectoryException;

public interface EntryConnector {

    boolean authenticate(String username, String password)
            throws DirectoryException;

    boolean hasEntry(String id) throws ClientException;

    Map<String, Object> getEntryMap(String id);

    List<String> getEntryIds();

    List<String> queryEntryIds(Map<String, Serializable> filter,
            Set<String> fulltext);

    void close();

    void commit();

    void rollback();

    void init(ConnectorBasedDirectoryDescriptor descriptor);

    Set<String> getFullTextConfig();

}
