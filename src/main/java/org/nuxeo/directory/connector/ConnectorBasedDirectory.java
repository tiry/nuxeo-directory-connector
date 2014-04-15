/*
 * (C) Copyright 2006-2007 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Florent Guillaume
 *
 * $Id: MemoryDirectory.java 30381 2008-02-20 20:12:09Z gracinet $
 */

package org.nuxeo.directory.connector;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.schema.SchemaManager;
import org.nuxeo.ecm.core.schema.types.Field;
import org.nuxeo.ecm.core.schema.types.Schema;
import org.nuxeo.ecm.directory.AbstractDirectory;
import org.nuxeo.ecm.directory.DirectoryException;
import org.nuxeo.ecm.directory.Session;
import org.nuxeo.runtime.api.Framework;

/**
 * @author tiry
 */
public class ConnectorBasedDirectory extends AbstractDirectory {

    public final String schemaName;

    public final Set<String> schemaSet;

    public final String idField;

    public final String passwordField;

    public Map<String, Object> map;

    public ConnectorBasedDirectorySession session;

    protected ConnectorBasedDirectoryDescriptor descriptor;

    public ConnectorBasedDirectory(ConnectorBasedDirectoryDescriptor descriptor)
            throws DirectoryException {
        super(descriptor.getName());
        this.descriptor = descriptor;
        this.schemaName = descriptor.getSchemaName();
        this.schemaSet = new HashSet<String>();
        this.idField = descriptor.getIdField();
        this.passwordField = descriptor.getPasswordField();

        SchemaManager sm = Framework.getLocalService(SchemaManager.class);
        Schema sch = sm.getSchema(descriptor.getSchemaName());
        if (sch == null) {
            throw new DirectoryException("Unknown schema : "
                    + descriptor.getSchemaName());
        }
        Collection<Field> fields = sch.getFields();
        for (Field f : fields) {
            schemaSet.add(f.getName().getLocalName());
        }

        try {
            addReferences(descriptor.getInverseReferences());
        } catch (ClientException e) {
            log.error("Error during Connector based Directory initialization",
                    e);
        }
    }

    public String getName() {
        return name;
    }

    public String getSchema() {
        return schemaName;
    }

    public String getParentDirectory() {
        return null;
    }

    public String getIdField() {
        return idField;
    }

    public String getPasswordField() {
        return passwordField;
    }

    public Session getSession() {
        if (session == null) {
            session = new ConnectorBasedDirectorySession(this,
                    descriptor.getConnector());
        }
        return session;
    }

    public void shutdown() {
        if (session != null) {
            session.close();
        }
        session = null;
    }

}
