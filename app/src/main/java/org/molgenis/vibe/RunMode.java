package org.molgenis.vibe;

import org.apache.jena.ext.com.google.common.base.Stopwatch;
import org.molgenis.vibe.formats.GeneDiseaseCollection;
import org.molgenis.vibe.formats.Phenotype;
import org.molgenis.vibe.io.input.OntologyModelFilesReader;
import org.molgenis.vibe.io.options_digestion.CommandLineOptionsParser;
import org.molgenis.vibe.io.options_digestion.OptionsParser;
import org.molgenis.vibe.io.output.format.OutputFormatWriter;
import org.molgenis.vibe.io.input.ModelReader;
import org.molgenis.vibe.io.input.TripleStoreDbReader;
import org.molgenis.vibe.ontology_processing.PhenotypesRetriever;
import org.molgenis.vibe.query_output_digestion.prioritization.gene.GenePrioritizer;
import org.molgenis.vibe.tdb_processing.GenesForPhenotypeRetriever;

import java.io.IOException;
import java.util.Set;

/**
 * Describes what the application should do.
 */
public enum RunMode {
    NONE("none") {
        @Override
        protected void runMode() {
            CommandLineOptionsParser.printHelpMessage();
        }
    }, GENES_FOR_PHENOTYPES_WITH_ASSOCIATED_PHENOTYPES("Retrieves genes for input phenotypes and phenotypes associated to input phenotypes.") {
        @Override
        protected void runMode() throws IOException {
            OntologyModelFilesReader ontologyReader = loadPhenotypeOntology();
            PhenotypesRetriever hpoRetriever = retrieveAssociatedPhenotypes(ontologyReader);
            ModelReader disgenetReader = loadDisgenetDatabase();
            GeneDiseaseCollection geneDiseaseCollection = retrieveDisgenetData(disgenetReader, hpoRetriever.getPhenotypeNetworkCollection().getPhenotypes());
            GenePrioritizer prioritizer = orderGenes(geneDiseaseCollection);
            writePrioritizedGenesOutput(geneDiseaseCollection, prioritizer);
        }
    }, GENES_FOR_PHENOTYPES("Retrieves genes for input phenotypes.") {
        @Override
        protected void runMode() throws Exception {
            ModelReader disgenetReader = loadDisgenetDatabase();
            GeneDiseaseCollection geneDiseaseCollection = retrieveDisgenetData(disgenetReader, getAppOptions().getPhenotypes());
            GenePrioritizer prioritizer = orderGenes(geneDiseaseCollection);
            writePrioritizedGenesOutput(geneDiseaseCollection, prioritizer);
        }
    };

    protected OntologyModelFilesReader loadPhenotypeOntology() {
        getAppOptions().printVerbose("# Preparing HPO dataset.");
        OntologyModelFilesReader ontologyReader = new OntologyModelFilesReader(getAppOptions().getHpoOntology().toString());
        printElapsedTime();

        return ontologyReader;
    }

    protected PhenotypesRetriever retrieveAssociatedPhenotypes(OntologyModelFilesReader ontologyReader) {
        getAppOptions().printVerbose("# " + getAppOptions().getPhenotypesRetrieverFactory().getDescription());
        PhenotypesRetriever hpoRetriever = getAppOptions().getPhenotypesRetrieverFactory().create(
                ontologyReader.getModel(), getAppOptions().getPhenotypes(), getAppOptions().getOntologyMaxDistance()
        );
        hpoRetriever.run();
        getAppOptions().printVerbose("Retrieved number of phenotypes: " + hpoRetriever.getPhenotypeNetworkCollection().getPhenotypes().size());
        printElapsedTime();

        return hpoRetriever;
    }

    protected ModelReader loadDisgenetDatabase() throws IOException {
        getAppOptions().printVerbose("# Preparing main dataset.");
        ModelReader disgenetReader = new TripleStoreDbReader(getAppOptions().getDatabase());
        printElapsedTime();

        return disgenetReader;
    }

    protected GeneDiseaseCollection retrieveDisgenetData(ModelReader disgenetReader, Set<Phenotype> phenotypes) {
        getAppOptions().printVerbose("# Retrieving data from main dataset.");
        GenesForPhenotypeRetriever genesForPhenotypeRetriever = new GenesForPhenotypeRetriever(
                disgenetReader, phenotypes
        );
        genesForPhenotypeRetriever.run();
        printElapsedTime();

        return genesForPhenotypeRetriever.getGeneDiseaseCollection();
    }

    protected GenePrioritizer orderGenes(GeneDiseaseCollection geneDiseaseCollection) {
        getAppOptions().printVerbose("# Ordering genes based on priority.");
        GenePrioritizer prioritizer = getAppOptions().getGenePrioritizerFactory().create(geneDiseaseCollection);
        prioritizer.run();
        printElapsedTime();

        return prioritizer;
    }

    protected void writePrioritizedGenesOutput(GeneDiseaseCollection geneDiseaseCollection, GenePrioritizer prioritizer) throws IOException {
        getAppOptions().printVerbose("# Writing genes to " + getAppOptions().getOutputWriter().target());
        OutputFormatWriter outputFormatWriter = getAppOptions().getGenePrioritizedOutputFormatWriterFactory().create(getAppOptions().getOutputWriter(), geneDiseaseCollection, prioritizer);
        outputFormatWriter.run();
        printElapsedTime();
    }

    private OptionsParser appOptions;

    private String description;

    private Stopwatch stopwatch;

    protected OptionsParser getAppOptions() {
        return appOptions;
    }

    protected void setAppOptions(OptionsParser appOptions) {
        this.appOptions = appOptions;
    }

    protected String getDescription() {
        return description;
    }

    RunMode(String description) {
        this.description = description;
        stopwatch = Stopwatch.createStarted();
    }

    public final void run(OptionsParser appOptions) throws Exception {
        // Sets the OptionsParser.
        setAppOptions(appOptions);
        // Prints the RunMode description if verbose.
        getAppOptions().printVerbose(getDescription());
        // Runs mode-specific code.
        runMode();
    }

    protected abstract void runMode() throws Exception;

    protected void printElapsedTime() {
        getAppOptions().printVerbose("Elapsed time: " + stopwatch.toString());
    }
}
