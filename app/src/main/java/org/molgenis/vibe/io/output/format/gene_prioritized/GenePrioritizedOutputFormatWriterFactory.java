package org.molgenis.vibe.io.output.format.gene_prioritized;


import org.molgenis.vibe.formats.Gene;
import org.molgenis.vibe.formats.GeneDiseaseCollection;
import org.molgenis.vibe.io.output.format.OutputFormatWriter;
import org.molgenis.vibe.io.output.ValuesSeparator;
import org.molgenis.vibe.io.output.format.PrioritizedOutputFormatWriterFactory;
import org.molgenis.vibe.io.output.target.OutputWriter;
import org.molgenis.vibe.query_output_digestion.prioritization.Prioritizer;

/**
 * A factory for {@link Gene} prioritized {@link org.molgenis.vibe.io.output.format.PrioritizedOutputFormatWriter}{@code s}.
 */
public enum GenePrioritizedOutputFormatWriterFactory implements PrioritizedOutputFormatWriterFactory<Gene> {
    SIMPLE {
        @Override
        public OutputFormatWriter create(OutputWriter outputWriter, GeneDiseaseCollection geneDiseaseCollection, Prioritizer<Gene> prioritizer) {
            return new OrderedGenesOutputFormatWriter(outputWriter, prioritizer, ValuesSeparator.COMMA);
        }
    },
    REGULAR_ID {
        @Override
        public OutputFormatWriter create(OutputWriter outputWriter, GeneDiseaseCollection geneDiseaseCollection, Prioritizer<Gene> prioritizer) {
            return new ResultsPerGeneSeparatedValuesOutputFormatWriterUsingIds(outputWriter, prioritizer, geneDiseaseCollection,
                    ValuesSeparator.TAB, ValuesSeparator.VERTICAL_LINE, ValuesSeparator.COLON, ValuesSeparator.COMMA);
        }
    },
    REGULAR_URI {
        @Override
        public OutputFormatWriter create(OutputWriter outputWriter, GeneDiseaseCollection geneDiseaseCollection, Prioritizer<Gene> prioritizer) {
            return new ResultsPerGeneSeparatedValuesOutputFormatWriterUsingUris(outputWriter, prioritizer, geneDiseaseCollection,
                    ValuesSeparator.TAB, ValuesSeparator.VERTICAL_LINE, ValuesSeparator.COLON, ValuesSeparator.COMMA);
        }
    };
}
