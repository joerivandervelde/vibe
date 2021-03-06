package org.molgenis.vibe.io.output.format.gene_prioritized;

import org.molgenis.vibe.formats.*;
import org.molgenis.vibe.io.output.ValuesSeparator;
import org.molgenis.vibe.io.output.target.OutputWriter;
import org.molgenis.vibe.query_output_digestion.prioritization.Prioritizer;

import java.io.IOException;
import java.util.List;

/**
 * A {@link ResultsPerGeneSeparatedValuesOutputFormatWriter} where {@link BiologicalEntity#getId()} is used for generating the output.
 */
public class ResultsPerGeneSeparatedValuesOutputFormatWriterUsingIds extends ResultsPerGeneSeparatedValuesOutputFormatWriter {

    public ResultsPerGeneSeparatedValuesOutputFormatWriterUsingIds(OutputWriter writer, Prioritizer<Gene> prioritizer,
                                                                   GeneDiseaseCollection collection, ValuesSeparator primarySeparator,
                                                                   ValuesSeparator keyValuePairSeparator, ValuesSeparator keyValueSeparator,
                                                                   ValuesSeparator valuesSeparator) {
        super(writer, prioritizer, collection, primarySeparator, keyValuePairSeparator, keyValueSeparator, valuesSeparator);
    }

    @Override
    protected String writeGene(Gene gene) throws IOException {
        return gene.getId();
    }

    @Override
    protected String writeGeneSymbol(Gene gene) throws IOException {
        return gene.getSymbol().getId();
    }

    @Override
    protected String writeDisease(Disease disease) throws IOException {
        return disease.getId();
    }

    @Override
    protected List<String> writeEvidence(GeneDiseaseCombination gdc) throws IOException {
        return gdc.getAllEvidenceSimplifiedOrdered();
    }
}
