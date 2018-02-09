package org.molgenis.vibe.formats;

import org.molgenis.vibe.exceptions.InvalidStringFormatException;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Defines a HPO term.
 */
public class Hpo {
    private static final String PREFIX = "hp:";

    /**
     * The numbers representing the ID.
     */
    private String id;

    public Hpo(String id) throws InvalidStringFormatException {
        this.id = retrieveIdNumbers(id);
    }

    public String getId() {
        return id;
    }

    /**
     * @return a {@link String} containing the ID with "hp:" prefix.
     */
    public String getFormattedId() {
        return PREFIX + id;
    }

    /**
     * Validates and retrieves the ID without prefix from a {@link String} describing an HPO id.
     * @param hpoTerm a {@link String} containing the HPO id (with prefix)
     * @return an {@code int} with the HPO id without prefix (if present)
     * @throws InvalidStringFormatException if {@code hpoTerm} does not adhere to the regex: ^(hp:)?([0-9]{7})$
     */
    private String retrieveIdNumbers(String hpoTerm) throws InvalidStringFormatException {
        Matcher m = Pattern.compile("^(hp:)?([0-9]{7})$").matcher(hpoTerm);
        if(m.matches()) {
            return m.group(2);
        } else {
            throw new InvalidStringFormatException(hpoTerm + " does not adhere the required format: ^(hp:)?([0-9]{7})$");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hpo hpo = (Hpo) o;
        return id == hpo.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
