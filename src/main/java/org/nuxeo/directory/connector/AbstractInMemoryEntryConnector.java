package org.nuxeo.directory.connector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * This is an base class that help implementing directory API on top of a pure
 * in memory structure.
 *
 * This can be useful for testing of for service that does not support any
 * search feature.
 *
 * @author <a href="mailto:tdelprat@nuxeo.com">Tiry</a>
 *
 */
public abstract class AbstractInMemoryEntryConnector extends
        AbstractEntryConnector implements EntryConnector {

    // Dummy in memory query
    public List<String> queryEntryIds(Map<String, Serializable> filter,
            Set<String> fulltext) {

        List<String> ids = new ArrayList<String>();

        // do the search
        data_loop: for (String id : getEntryIds()) {

            Map<String, Object> map = getEntryMap(id);
            for (Entry<String, Serializable> e : filter.entrySet()) {
                String fieldName = e.getKey();
                Object expected = e.getValue();
                Object value = map.get(fieldName);
                if (value == null) {
                    if (expected != null) {
                        continue data_loop;
                    }
                } else {
                    if (fulltext != null && fulltext.contains(fieldName)) {
                        if (!value.toString().toLowerCase().startsWith(
                                expected.toString().toLowerCase())) {
                            continue data_loop;
                        }
                    } else {
                        if (!value.equals(expected)) {
                            continue data_loop;
                        }
                    }
                }
            }
            // this entry matches
            ids.add(id);
        }
        return ids;
    }
}
