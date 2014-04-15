package org.nuxeo.directory.connector;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.nuxeo.ecm.directory.DirectoryException;

/**
 * Abstract base class to Connector implementations
 *
 * @author tiry
 *
 */
public abstract class AbstractEntryConnector implements EntryConnector {

    protected ConnectorBasedDirectoryDescriptor descriptor;

    public boolean authenticate(String username, String password)
            throws DirectoryException {

        Map<String, Object> map = getEntryMap(username);
        if (map == null) {
            return false;
        }
        String pwd = (String) map.get(descriptor.getPasswordField());

        return password.equals(pwd);
    }

    public void close() {
        // TODO Auto-generated method stub

    }

    public void commit() {
        // TODO Auto-generated method stub

    }

    public void rollback() {
        // TODO Auto-generated method stub

    }

    public void init(ConnectorBasedDirectoryDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    public Set<String> getFullTextConfig() {
        return Collections.<String> emptySet();
    }

}
