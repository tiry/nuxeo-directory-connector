package org.nuxeo.directory.connector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.directory.Directory;
import org.nuxeo.ecm.directory.DirectoryException;
import org.nuxeo.ecm.directory.DirectoryFactory;
import org.nuxeo.ecm.directory.api.DirectoryService;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.ComponentName;
import org.nuxeo.runtime.model.DefaultComponent;
import org.nuxeo.runtime.model.Extension;

/**
 * @author tiry
 * 
 */
public class ConnectorBasedDirectoryFactory extends DefaultComponent implements
        DirectoryFactory {

    public static final ComponentName NAME = new ComponentName(
            "org.nuxeo.directory.connector.ConnectorBasedDirectoryFactory");

    private final Map<String, ConnectorBasedDirectory> directories;

    private static DirectoryService directoryService;

    private static final Log log = LogFactory.getLog(ConnectorBasedDirectoryFactory.class);

    public ConnectorBasedDirectoryFactory() throws DirectoryException {
        directories = new HashMap<String, ConnectorBasedDirectory>();
        // GR now NXRuntime provides the local one by default
        try {
            directoryService = Framework.getService(DirectoryService.class);
        } catch (Exception e) {
            throw new DirectoryException("Error in Directory Service lookup", e);
        }
    }

    public String getName() {
        return NAME.getName();
    }

    public void registerDirectory(ConnectorBasedDirectory directory) {
        String directoryName = directory.getName();
        directories.put(directoryName, directory);
        directoryService.registerDirectory(directoryName, this);
    }

    public void unregisterDirectory(ConnectorBasedDirectory directory) {
        String directoryName = directory.getName();
        directoryService.unregisterDirectory(directoryName, this);
        directories.remove(directoryName);
    }

    public Directory getDirectory(String name) {
        return directories.get(name);
    }

    public static DirectoryService getDirectoryService() {
        directoryService = (DirectoryService) Framework.getRuntime().getComponent(
                DirectoryService.NAME);
        if (directoryService == null) {
            directoryService = Framework.getLocalService(DirectoryService.class);
            if (directoryService == null) {
                try {
                    directoryService = Framework.getService(DirectoryService.class);
                } catch (Exception e) {
                    log.error("Can't find Directory Service", e);
                }
            }
        }
        return directoryService;
    }

    public void shutdown() {
    }

    public List<Directory> getDirectories() {
        return new ArrayList<Directory>(directories.values());
    }

    @Override
    public void registerExtension(Extension extension) {
        Object[] contribs = extension.getContributions();
        DirectoryService dirService = getDirectoryService();
        for (Object contrib : contribs) {
            ConnectorBasedDirectoryDescriptor descriptor = (ConnectorBasedDirectoryDescriptor) contrib;
            String name = descriptor.name;
            ConnectorBasedDirectory directory;
            try {
                directory = new ConnectorBasedDirectory(descriptor);
                directories.put(name, directory);
                dirService.registerDirectory(name, this);
                log.info("Directory registered: " + name);
            } catch (DirectoryException e) {
                log.error("Error while registring directory", e);
            }
        }
    }
}
