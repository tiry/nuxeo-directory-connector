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
import org.nuxeo.runtime.model.ComponentContext;
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

    private final List<ConnectorBasedDirectoryDescriptor> pendingDirectories;

    private DirectoryService directoryService;

    private static final Log log = LogFactory.getLog(ConnectorBasedDirectoryFactory.class);

    public ConnectorBasedDirectoryFactory() throws DirectoryException {
        directories = new HashMap<String, ConnectorBasedDirectory>();
        pendingDirectories = new ArrayList<ConnectorBasedDirectoryDescriptor>();
        directoryService = Framework.getLocalService(DirectoryService.class);
    }

    public String getName() {
        return NAME.getName();
    }

    public void unregisterDirectory(ConnectorBasedDirectory directory) {
        String directoryName = directory.getName();
        directoryService.unregisterDirectory(directoryName, this);
        directories.remove(directoryName);
    }

    public Directory getDirectory(String name) {
        return directories.get(name);
    }


    public List<Directory> getDirectories() {
        return new ArrayList<Directory>(directories.values());
    }

    @Override
    public void applicationStarted(ComponentContext context) throws Exception {
        if (Framework.isTestModeSet()) {
            // when testing, DatabaseHelper init hasn't occurred yet,
            // so keep to lazy initialization
            return;
        }
        if (pendingDirectories.size()>0) {
            for (ConnectorBasedDirectoryDescriptor descriptor: pendingDirectories) {
                try {
                    log.info("Register lazy directory " + descriptor.getName());
                    registerDirectory(descriptor);
                } catch (DirectoryException e) {
                    log.error("Error while registring directory", e);
                }
            }
        }
    }

    @Override
    public void registerExtension(Extension extension) {
        Object[] contribs = extension.getContributions();
        for (Object contrib : contribs) {
            ConnectorBasedDirectoryDescriptor descriptor = (ConnectorBasedDirectoryDescriptor) contrib;
            try {
                registerDirectory(descriptor);
            } catch (DirectoryException e) {
                log.error("Error while registring directory", e);
            }
        }
    }

    protected void registerDirectory(ConnectorBasedDirectoryDescriptor descriptor) throws DirectoryException {
        ConnectorBasedDirectory directory = new ConnectorBasedDirectory(descriptor);
        registerDirectory(directory);
    }

    public void registerDirectory(ConnectorBasedDirectory directory) {
        String directoryName = directory.getName();
        directories.put(directoryName, directory);
        directoryService.registerDirectory(directoryName, this);
        log.info("Directory registered: " + directoryName);
    }

    @Override
    public void shutdown() throws DirectoryException {
        for (ConnectorBasedDirectory dir : directories.values()) {
            dir.shutdown();
        }
    }


}
