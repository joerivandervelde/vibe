package org.molgenis.vibe.tdb_processing;

import org.molgenis.vibe.TestData;
import org.molgenis.vibe.formats.*;
import org.molgenis.vibe.io.input.ModelReader;
import org.molgenis.vibe.io.input.TripleStoreDbReader;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URI;
import java.util.*;

/**
 * Note that these tests use data from DisGeNET for validation. However, this was kept as minimal as possible while still
 * being able to actually test the functioning of the code and only reflects what is EXPECTED to be found within the
 * DisGeNET dataset when using the query (on a technical basis). Additionally, the actual data on which these tests are
 * executed on are available externally and are not included in the repository itself.
 *
 * The full DisGeNET RDF dataset can be downloaded from: http://rdf.disgenet.org/download/
 * The license can be found on: http://www.disgenet.org/ds/DisGeNET/html/legal.html
 */
public class GenesForPhenotypeRetrieverTester {
    private ModelReader reader;
    private GenesForPhenotypeRetriever retriever;

    @BeforeClass
    public void beforeClass() throws IOException {
        reader = new TripleStoreDbReader(TestData.TDB_FULL.getDir());

    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        retriever = null;
    }

    @Test
    public void retrieveGeneDiseaseCollectionForHpo0008438() {
        /**
         * obo:HP_0008438  a        sio:SIO_010056 ;
         *         sio:SIO_000001   ordo:Orphanet_85184 ;
         *         sio:SIO_000212   pda:DGN8a0e701b56199eca12831b38431d7959 ;
         *         skos:exactMatch  umls:C1835764 .
         */
        Disease[] diseases = new Disease[]{
                new Disease("umls:C0265292"), // ordo:Orphanet_85184
                new Disease("umls:C0220687"), // pda:DGN8a0e701b56199eca12831b38431d7959
                new Disease("umls:C1835764"), // skos:exactMatch
        };

        Gene[] genes = new Gene[]{
                new Gene("ncbigene:479", new GeneSymbol("hgnc:ATP12A")),
                new Gene("ncbigene:56172", new GeneSymbol("hgnc:ANKH")),
                new Gene("ncbigene:286", new GeneSymbol("hgnc:ANK1")),
                new Gene("ncbigene:2697", new GeneSymbol("hgnc:GJA1")),
                new Gene("ncbigene:29123", new GeneSymbol("hgnc:ANKRD11")),
        };

        GeneDiseaseCombination[] geneDiseaseCombinations = new GeneDiseaseCombination[]{
                // GDAs umls:C0265292
                new GeneDiseaseCombination(genes[0], diseases[0], 0.02E0),
                new GeneDiseaseCombination(genes[1], diseases[0], 0.37E0),
                new GeneDiseaseCombination(genes[2], diseases[0], 0.03E0),
                new GeneDiseaseCombination(genes[3], diseases[0], 0.31E0),

                // GDAs umls:C0220687
                new GeneDiseaseCombination(genes[4], diseases[1], 0.8E0), // [4]

                // GDAs umls:C1835764
                new GeneDiseaseCombination(genes[4], diseases[2], 0.1E0), // [5]
        };

        Source[] sources = new Source[] {
                new Source(URI.create("http://rdf.disgenet.org/v6.0.0/void/BEFREE"), "BeFree 2018 Dataset Distribution", Source.Level.LITERATURE),
                new Source(URI.create("http://rdf.disgenet.org/v6.0.0/void/ORPHANET"), "Orphanet 2017 Dataset Distribution", Source.Level.CURATED),
                new Source(URI.create("http://rdf.disgenet.org/v6.0.0/void/CLINVAR"), "ClinVar 2018 Dataset Distribution", Source.Level.CURATED),
                new Source(URI.create("http://rdf.disgenet.org/v6.0.0/void/UNIPROT"), "UniProt 2018 Dataset Distribution", Source.Level.CURATED),
                new Source(URI.create("http://rdf.disgenet.org/v6.0.0/void/CTD_human"), "CTD_human 2018 Dataset Distribution", Source.Level.CURATED),
                new Source(URI.create("http://rdf.disgenet.org/v6.0.0/void/HPO"), "HPO", Source.Level.CURATED),
        };

        // GDAs umls:C0265292
        geneDiseaseCombinations[0].add(sources[0], URI.create("http://identifiers.org/pubmed/8630510"));
        geneDiseaseCombinations[0].add(sources[0], URI.create("http://identifiers.org/pubmed/7678608"));
        geneDiseaseCombinations[1].add(sources[0], URI.create("http://identifiers.org/pubmed/16462526"));
        geneDiseaseCombinations[1].add(sources[0], URI.create("http://identifiers.org/pubmed/20943778"));
        geneDiseaseCombinations[1].add(sources[0], URI.create("http://identifiers.org/pubmed/20358596"));
        geneDiseaseCombinations[1].add(sources[0], URI.create("http://identifiers.org/pubmed/19257826"));
        geneDiseaseCombinations[1].add(sources[0], URI.create("http://identifiers.org/pubmed/21149338"));
        geneDiseaseCombinations[2].add(sources[0], URI.create("http://identifiers.org/pubmed/16462526"));
        geneDiseaseCombinations[1].add(sources[0], URI.create("http://identifiers.org/pubmed/26820766"));
        geneDiseaseCombinations[1].add(sources[1]);
        geneDiseaseCombinations[3].add(sources[1]);
        geneDiseaseCombinations[3].add(sources[0], URI.create("http://identifiers.org/pubmed/23951358"));
        geneDiseaseCombinations[1].add(sources[0], URI.create("http://identifiers.org/pubmed/20186813"));
        geneDiseaseCombinations[2].add(sources[0], URI.create("http://identifiers.org/pubmed/19257826"));
        geneDiseaseCombinations[2].add(sources[0], URI.create("http://identifiers.org/pubmed/21149338"));

        // GDAs umls:C0220687
        geneDiseaseCombinations[4].add(sources[0], URI.create("http://identifiers.org/pubmed/25413698"));
        geneDiseaseCombinations[4].add(sources[0], URI.create("http://identifiers.org/pubmed/28250421"));
        geneDiseaseCombinations[4].add(sources[0], URI.create("http://identifiers.org/pubmed/23369839"));
        geneDiseaseCombinations[4].add(sources[2], URI.create("http://identifiers.org/pubmed/25741868"));
        geneDiseaseCombinations[4].add(sources[2], URI.create("http://identifiers.org/pubmed/15523620"));
        geneDiseaseCombinations[4].add(sources[2], URI.create("http://identifiers.org/pubmed/27900361"));
        geneDiseaseCombinations[4].add(sources[2], URI.create("http://identifiers.org/pubmed/15378538"));
        geneDiseaseCombinations[4].add(sources[2], URI.create("http://identifiers.org/pubmed/21782149"));
        geneDiseaseCombinations[4].add(sources[3], URI.create("http://identifiers.org/pubmed/21782149"));
        geneDiseaseCombinations[4].add(sources[0], URI.create("http://identifiers.org/pubmed/23184435"));
        geneDiseaseCombinations[4].add(sources[0], URI.create("http://identifiers.org/pubmed/25424714"));
        geneDiseaseCombinations[4].add(sources[0], URI.create("http://identifiers.org/pubmed/22307766"));
        geneDiseaseCombinations[4].add(sources[0], URI.create("http://identifiers.org/pubmed/21782149"));
        geneDiseaseCombinations[4].add(sources[2], URI.create("http://identifiers.org/pubmed/27667800"));
        geneDiseaseCombinations[4].add(sources[2], URI.create("http://identifiers.org/pubmed/29565525"));
        geneDiseaseCombinations[4].add(sources[4]);
        geneDiseaseCombinations[4].add(sources[3], URI.create("http://identifiers.org/pubmed/25413698"));
        geneDiseaseCombinations[4].add(sources[2]);
        geneDiseaseCombinations[4].add(sources[0], URI.create("http://identifiers.org/pubmed/25464108"));
        geneDiseaseCombinations[4].add(sources[2], URI.create("http://identifiers.org/pubmed/28708303"));
        geneDiseaseCombinations[4].add(sources[0], URI.create("http://identifiers.org/pubmed/25125236"));
        geneDiseaseCombinations[4].add(sources[0], URI.create("http://identifiers.org/pubmed/25187894"));
        geneDiseaseCombinations[4].add(sources[0], URI.create("http://identifiers.org/pubmed/24838796"));
        geneDiseaseCombinations[4].add(sources[2], URI.create("http://identifiers.org/pubmed/24088041"));
        geneDiseaseCombinations[4].add(sources[2], URI.create("http://identifiers.org/pubmed/25125236"));
        geneDiseaseCombinations[4].add(sources[0], URI.create("http://identifiers.org/pubmed/29224748"));
        geneDiseaseCombinations[4].add(sources[0], URI.create("http://identifiers.org/pubmed/23463723"));
        geneDiseaseCombinations[4].add(sources[0], URI.create("http://identifiers.org/pubmed/23494856"));
        geneDiseaseCombinations[4].add(sources[2], URI.create("http://identifiers.org/pubmed/26633545"));
        geneDiseaseCombinations[4].add(sources[1]);

        // GDAs umls:C1835764
        geneDiseaseCombinations[5].add(sources[5]);

        // Create collection.
        GeneDiseaseCollection expectedCollection = new GeneDiseaseCollection();
        expectedCollection.addAll(Arrays.asList(geneDiseaseCombinations));

        retriever = new GenesForPhenotypeRetriever(reader, new HashSet<>(Arrays.asList(new Phenotype("hp:0008438"))));
        retriever.run();
        GeneDiseaseCollection actualCollection = retriever.getGeneDiseaseCollection();

        assertGeneDiseaseCombination(actualCollection, expectedCollection);
    }

    /**
     * Compares if the actual collection is correct. Note that TestNG assumes a {@link Collection} is sorted (it has
     * {@link Map} and {@link Set} specific methods but others will de defaulted to the {@link Collection} method instead
     * of having their specific methods. For this reason, a direct comparisong using Assert.assertEquals could result in
     * a fail due to no guarantee is given in {@link BiologicalEntityCollection} that the iterator returns a sorted {@link Iterator}.
     * <br /><br />
     * To cite <A href="https://docs.oracle.com/javase/8/docs/api/java/util/Collection.html">https://docs.oracle.com/javase/8/docs/api/java/util/Collection.html</A>:
     *
     * <pre><a href="../../java/util/Iterator.html" title="interface in java.util">Iterator</a>&lt;<a href="../../java/util/Collection.html" title="type parameter in Collection">E</a>&gt;&nbsp;iterator()</pre>
     * <div class="block">Returns an iterator over the elements in this collection.  There are no
     *  guarantees concerning the order in which the elements are returned
     *  (unless this collection is an instance of some class that provides a
     *  guarantee).</div>
     * @param actualCollection
     * @param expectedCollection
     */
    private void assertGeneDiseaseCombination(GeneDiseaseCollection actualCollection, GeneDiseaseCollection expectedCollection) {
        Assert.assertEquals(actualCollection.getGeneDiseaseCombinations(), expectedCollection.getGeneDiseaseCombinations());
        Assert.assertEquals(actualCollection.getDiseases(), expectedCollection.getDiseases());
        Assert.assertEquals(actualCollection.getGenes(), expectedCollection.getGenes());

        // Above assert only compares equals. Certain classes might store additional data that should not play a role
        // when validating equality but should be checked on whether they were loaded from the database correctly. An
        // example of this would be a score belonging to a Gene. While it should not make it a "different" Gene, it does
        // describe the Gene. For this reason, toString() is used as extra validation (with the assumption that these
        // extra fields are mentioned in toString()).
        Assert.assertEquals(actualCollection.getGeneDiseaseCombinationsOrdered().toString(),
                expectedCollection.getGeneDiseaseCombinationsOrdered().toString());
    }
}
