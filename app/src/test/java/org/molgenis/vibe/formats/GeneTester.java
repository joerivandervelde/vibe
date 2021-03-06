package org.molgenis.vibe.formats;

import org.molgenis.vibe.exceptions.InvalidStringFormatException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GeneTester {
    private static final GeneSymbol symbol = new GeneSymbol("hgnc:ABC");

    @Test
    public void useValidIdWithLowercasePrefix() throws InvalidStringFormatException {
        Gene gene = new Gene("ncbigene:1234", symbol);
        testIfValid(gene);
    }

    @Test
    public void useValidIdWithUppercasePrefix() throws InvalidStringFormatException {
        Gene gene = new Gene("NCBIGENE:1234", symbol);
        testIfValid(gene);
    }

    @Test(expectedExceptions = InvalidStringFormatException.class)
    public void useValidIdWithSingleUpperCasePrefix1() throws InvalidStringFormatException {
        new Gene("Ncbigene:1234", symbol);
    }

    @Test(expectedExceptions = InvalidStringFormatException.class)
    public void useValidIdWithSingleUpperCasePrefix2() throws InvalidStringFormatException {
        new Gene("nCbigene:1234", symbol);
    }

    @Test(expectedExceptions = InvalidStringFormatException.class)
    public void useValidIdWithInvalidPrefix() throws InvalidStringFormatException {
        new Gene("ncbi:1234", symbol);
    }

    @Test(expectedExceptions = InvalidStringFormatException.class)
    public void useValidIdWithoutPrefix() throws InvalidStringFormatException {
        new Gene("1234", symbol);
    }

    @Test(expectedExceptions = InvalidStringFormatException.class)
    public void useUriAsIdInput() throws InvalidStringFormatException {
        new Gene("http://identifiers.org/ncbigene/1234", symbol);
    }

    @Test
    public void useValidUri() {
        Gene gene = new Gene(URI.create("http://identifiers.org/ncbigene/1234"), symbol);
        testIfValid(gene);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void useInvalidUri() {
        new Gene(URI.create("http://identifiers.org/ncbi/1234"), symbol);
    }

    @Test
    public void testSort() {
        List<Gene> actualOrder = new ArrayList<>( Arrays.asList(
                new Gene("ncbigene:3", symbol),
                new Gene("ncbigene:8", symbol),
                new Gene("ncbigene:1", symbol)
        ));

        List<Gene> expectedOrder = new ArrayList<>( Arrays.asList(
                actualOrder.get(2),
                actualOrder.get(0),
                actualOrder.get(1)
        ));

        Collections.sort(actualOrder);
        Assert.assertEquals(actualOrder, expectedOrder);
    }

    private void testIfValid(Gene gene) {
        Assert.assertEquals(gene.getId(), "1234");
        Assert.assertEquals(gene.getFormattedId(), "ncbigene:1234");
        Assert.assertEquals(gene.getUri(), URI.create("http://identifiers.org/ncbigene/1234"));
    }
}
