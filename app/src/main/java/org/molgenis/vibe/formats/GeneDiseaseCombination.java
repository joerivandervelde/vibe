package org.molgenis.vibe.formats;

import static java.util.Objects.requireNonNull;

import java.net.URI;
import java.util.*;

/**
 * A combination of a {@link Gene} and a {@link Disease}.
 */
public class GeneDiseaseCombination extends BiologicalEntityCombination<Gene, Disease> {
    /**
     * The score belonging to the gene-disease combination from the DisGeNET database.
     */
    private Double disgenetScore;

    /**
     * A {@link Map} storing which {@link Source}{@code s} contains this combination and how often.
     */
    private Map<Source, Integer> sourcesCount = new HashMap<>();

    /**
     * A {@link Map} storing per {@link Source} the {@link URI}{@code s} to the evidence (if available).
     */
    private Map<Source, List<URI>> sourcesEvidence = new HashMap<>();

    /**
     * @return the {@link Gene}
     * @see #getT1()
     */
    public Gene getGene() {
        return getT1();
    }

    /**
     * @return the {@link Disease}
     * @see #getT2()
     */
    public Disease getDisease() {
        return getT2();
    }

    public double getDisgenetScore() {
        return disgenetScore;
    }

    /**
     * The number of occurrences for this gene-disease combination per {@link Source}.
     * @return an unmodifiable {@link Map}
     */
    public Map<Source, Integer> getSourcesCount() {
        return Collections.unmodifiableMap(sourcesCount);
    }

    public Set<Source> getSourcesWithCount() {
        return Collections.unmodifiableSet(sourcesCount.keySet());
    }

    /**
     * The count for the defined {@link Source}
     * @param source the {@link Source} to retrieve the count for
     * @return an {@code int} containing the frequency of this source found (if {@link Source} is not present returns a 0)
     */
    public int getCountForSource(Source source) {
        Integer count = sourcesCount.get(source);
        if(count == null) {
            count = 0;
        }
        return count;
    }

    /**
     * All {@link Source}{@code s} for this gene-disease combination that have evidence {@link URI}{@code s}.
     * @return an unmodifiable {@link Set} containing {@link Source}{@code s}
     */
    public Set<Source> getSourcesWithEvidence() {
        return Collections.unmodifiableSet(sourcesEvidence.keySet());
    }

    /**
     * The evidence {@link URI}{@code s} for the defined {@link Source}
     * @param source
     * @return an unmodifiable {@link List} containing evidence {@link URI}{@code s}, or {@code null} if {@link Source} does not have any evidence
     */
    public List<URI> getEvidenceForSource(Source source) {
        List<URI> evidence = sourcesEvidence.get(source);
        if(evidence != null) {
            evidence = Collections.unmodifiableList(evidence);
        }
        return evidence;
    }

    /**
     * The evidence of all {@link Source}{@code s} combined.
     * @return a {@link Set} containing all the evidence {@link URI}{@code s}
     */
    public Set<URI> getAllEvidence() {
        Set<URI> allSources = new HashSet<>();
        for(List<URI> evidenceForSingleSource : sourcesEvidence.values()) {
            allSources.addAll(evidenceForSingleSource);
        }

        return allSources;
    }

    /**
     * Wrapper for {@link #getAllEvidence()} that returns an ordered list.
     * @return a {@link List} containing all the evidence {@link URI}{@code s}
     */
    public List<URI> getAllEvidenceOrdered() {
        List<URI> sources = new ArrayList<>();
        sources.addAll(getAllEvidence());
        Collections.sort(sources);

        return sources;
    }

    /**
     * Wrapper for {@link #getAllEvidenceOrdered()} that converts the {@link URI}{@code s} to {@link String}{@code s}.
     * @return a {@link List} containing all the evidence {@link URI}{@code s} as {@link String}{@code s}
     */
    public List<String> getAllEvidenceOrderedStrings() {
        List<String> stringList = new ArrayList<>();
        getAllEvidenceOrdered().forEach(uri -> stringList.add(uri.toString()));

        return stringList;
    }

    /**
     * Wrapper for {@code #getAllEvidence} where {@link URI}{@code s} starting with {@code http://identifiers.org/pubmed/}
     * are reduced to only the number behind it. If a source starts with a different {@link URI}, the full {@link URI} is
     * still returned (though after being converted to a {@link String}).
     * @return a {@link Set} containing numbers for PubMed IDs (starting with {@code http://identifiers.org/pubmed/} as
     * {@link URI}) and for other sources the full {@link URI} as a {@link String}
     */
    public Set<String> getAllEvidenceSimplified() {
        Set<String> simplifiedSources = new HashSet<>();
        simplifyEvidence(this.getAllEvidence(), simplifiedSources);

        return simplifiedSources;
    }

    /**
     * The same as {@link #getAllEvidenceSimplified()}, only returns an alphabetically ordered {@link List} instead of a
     * {@link Set}.
     * @return a {@link List} containing numbers for PubMed IDs (starting with {@code http://identifiers.org/pubmed/} as
     * {@link URI}) and for other sources the full {@link URI} as a {@link String}
     * @see #getAllEvidenceSimplified()
     */
    public List<String> getAllEvidenceSimplifiedOrdered() {
        List<String> simplifiedSources = new ArrayList<>();
        simplifyEvidence(this.getAllEvidence(), simplifiedSources);
        Collections.sort(simplifiedSources);

        return simplifiedSources;
    }

    /**
     * Goes through all source {@link URI}{@code s} given and stores them (if possible simplifified) in the given {@link Collection}.
     * @param simplifiedSources where the (simplified) sources should be stored in as {@link String}
     * @param allSourceUris the {@link Set} that should be simplified
     */
    private void simplifyEvidence(Set<URI> allSourceUris, Collection<String> simplifiedSources) {
        for(URI source : allSourceUris) {
            String sourceString = source.toString();
            simplifiedSources.add((sourceString.startsWith("http://identifiers.org/pubmed/") ? sourceString.substring(30) : sourceString));
        }
    }

    /**
     * Simple constructor allowing for easy comparison of collections.
     * @param gene
     * @param disease
     */
    public GeneDiseaseCombination(Gene gene, Disease disease) {
        super(gene, disease);
    }

    public GeneDiseaseCombination(Gene gene, Disease disease, double disgenetScore) {
        super(gene, disease);
        this.disgenetScore = requireNonNull(disgenetScore);
    }

    /**
     * Adds a {@link Source} to this gene-disease combination with an evidence {@link URI}.
     * @param source
     * @param evidence
     */
    public void add(Source source, URI evidence) {
        // Increments counter for source.
        add(source);

        // Stores evidence URI.
        List<URI> evidenceList = sourcesEvidence.get(source);
        if(evidenceList == null) {
            evidenceList = new ArrayList<>();
            sourcesEvidence.put(source, evidenceList);
        }
        evidenceList.add(evidence);
    }

    /**
     * Adds a {@link Source} to this gene-disease combination without an evidence {@link URI}.
     * @param source
     */
    public void add(Source source) {
        Integer count = sourcesCount.get(source);
        if(count == null) {
            sourcesCount.put(source, 1);
        } else {
            sourcesCount.put(source, count + 1);
        }
    }

    @Override
    public String toString() {
        return "GeneDiseaseCombination{" +
                "disgenetScore=" + disgenetScore +
                ", sourcesCount=" + sourcesCount +
                ", sourcesEvidence=" + sourcesEvidence +
                "} " + super.toString();
    }
}
