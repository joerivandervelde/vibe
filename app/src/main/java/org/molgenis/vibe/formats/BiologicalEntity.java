package org.molgenis.vibe.formats;

import org.molgenis.vibe.exceptions.InvalidStringFormatException;

import java.net.URI;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;

/**
 * Describes a biological entity (such as a {@link Gene}, {@link Disease} or {@link Phenotype}).
 *
 * Be sure to update toString() to include all new fields when subclassing! Please refer to
 * org.molgenis.vibe.rdf_processing.GenesForPhenotypeRetrieverTester within the test code for more information.
 *
 */
public abstract class BiologicalEntity extends Entity {
    public BiologicalEntity(String id) {
        super(id);
    }

    public BiologicalEntity(URI uri) {
        super(uri);
    }

    public BiologicalEntity(String id, String name) {
        super(id, name);
    }

    public BiologicalEntity(URI uri, String name) {
        super(uri, name);
    }

    public BiologicalEntity(URI uri, String id, String name) throws InvalidStringFormatException {
        super(uri, id, name);
    }
}
