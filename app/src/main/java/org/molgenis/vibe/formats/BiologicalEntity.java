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
public abstract class BiologicalEntity implements ResourceUri, Comparable<BiologicalEntity> {
    /**
     * The entity prefix.
     * @return a {@link String} containing the prefix.
     */
    protected abstract String getIdPrefix();

    /**
     * A regular expression an input {@link String} should adhere to when deriving the {@link BiologicalEntity} from it.
     * @return
     */
    protected abstract String getIdRegex();

    /**
     * The group within the regular expression the actual {@link BiologicalEntity#id} is stored in.
     * @return
     */
    protected abstract int getRegexIdGroup();

    /**
     * The regular expression describing the URI prefix it should match with.
     * @return
     */
    protected abstract String getUriPrefix();

    /**
     * The unique id.
     */
    private String id;

    /**
     * Value used for comparing {@link BiologicalEntity}{@code s} ({@link #compareTo(BiologicalEntity)}).
     */
    private int compareValue;

    /**
     * The name.
     */
    private String name;

    /**
     * The {@link URI}. Note that while in general triplets use IRIs, Apache Jena refers to URIs.
     * @see org.apache.jena.rdf.model.Resource#getURI()
     */
    private URI uri;

    /**
     * @return the {@link BiologicalEntity} ID without prefix.
     */
    public String getId() {
        return id;
    }

    /**
     * @return the {@link BiologicalEntity} ID with prefix.
     */
    public String getFormattedId() {
        return getIdPrefix() + id;
    }

    protected int getCompareValue() {
        return compareValue;
    }

    protected void setCompareValue(int compareValue) {
        this.compareValue = requireNonNull(compareValue);
    }

    protected void setId(String id) {
        this.id = requireNonNull(id);
    }

    public String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = requireNonNull(name);
    }

    @Override
    public URI getUri() {
        return uri;
    }

    public BiologicalEntity(String id) {
        this.id = retrieveIdFromString(requireNonNull(id));
        uri = URI.create( getUriPrefix() + this.id );
        generateCompareValue();
    }

    public BiologicalEntity(URI uri) {
        this.uri = uri;
        String uriString = this.uri.toString();
        validateUri(uriString);
        id = uriString.split(getUriPrefix())[1];
        generateCompareValue();
    }

    public BiologicalEntity(String id, String name) {
        this(id);
        this.name = requireNonNull(name);
        generateCompareValue();
    }

    public BiologicalEntity(URI uri, String name) {
        this(uri);
        this.name = requireNonNull(name);
        generateCompareValue();
    }

    public BiologicalEntity(URI uri, String id, String name) throws InvalidStringFormatException {
        this.id = retrieveIdFromString(requireNonNull(id));
        this.name = requireNonNull(name);
        this.uri = requireNonNull(uri);
        validateUri(this.uri.toString());
        checkIfIdAndUriAreEqual(this.id, this.uri);
        generateCompareValue();
    }

    private void validateUri(String uriString) {
        if(!uriString.startsWith(getUriPrefix())) {
            throw new IllegalArgumentException("The URI \"" + uriString + "\" does not start with: " + getUriPrefix());
        }
    }

    private void checkIfIdAndUriAreEqual(String id, URI uri) {
        if(!uri.toString().endsWith(id)) {
            throw new IllegalArgumentException("The URI does not refer to the same BiologicalEntity as the id.");
        }
    }

    /**
     * Validates and retrieves the ID from a {@link String} describing an id (that also includes other information such as a prefix).
     * @param fullString the {@link String} to be digested
     * @return a {@link String} containing the id only
     * @throws InvalidStringFormatException if {@code fullString} did not adhere to the regular expression
     */
    protected String retrieveIdFromString(String fullString) throws InvalidStringFormatException {
        Matcher m = Pattern.compile(getIdRegex()).matcher(fullString);
        if(m.matches()) {
            return m.group(getRegexIdGroup());
        } else {
            throw new InvalidStringFormatException(fullString + " does not adhere the required format: " + getIdRegex());
        }
    }

    /**
     * While uniqueness is based on the {@link #id} (as each {@link #id} should only occur once and data belonging to it
     * should be consistent), {@link #toString()} can be used for testing whether data retrieval from external sources
     * yielded the expected results.
     * @return
     */
    @Override
    public String toString() {
        return "BiologicalEntity{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", uri=" + uri +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BiologicalEntity that = (BiologicalEntity) o;
        return Objects.equals(uri, that.uri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uri);
    }

    /**
     * Converts a piece of information defining the {@link BiologicalEntity} into an {@code int} and stores this for easy
     * comparison of {@link BiologicalEntity}{@code s}.
     */
    protected abstract void generateCompareValue();

    @Override
    public int compareTo(BiologicalEntity o) {
        return getCompareValue() - o.getCompareValue();
    }
}
