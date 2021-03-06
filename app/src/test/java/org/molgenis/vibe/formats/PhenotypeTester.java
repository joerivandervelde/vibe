package org.molgenis.vibe.formats;

import org.molgenis.vibe.exceptions.InvalidStringFormatException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PhenotypeTester {
    @Test
    public void useValidIdWithLowercasePrefix() throws InvalidStringFormatException {
        Phenotype phenotype = new Phenotype("hp:0012345");
        testIfValid(phenotype);
    }

    @Test
    public void useValidIdWithUppercasePrefix() throws InvalidStringFormatException {
        Phenotype phenotype = new Phenotype("HP:0012345");
        testIfValid(phenotype);
    }

    @Test(expectedExceptions = InvalidStringFormatException.class)
    public void useValidIdWithSingleUpperCasePrefix1() throws InvalidStringFormatException {
        new Phenotype("Hp:0012345");
    }

    @Test(expectedExceptions = InvalidStringFormatException.class)
    public void useValidIdWithSingleUpperCasePrefix2() throws InvalidStringFormatException {
        new Phenotype("hP:0012345");
    }

    @Test(expectedExceptions = InvalidStringFormatException.class)
    public void useValidIdWithInvalidPrefix() throws InvalidStringFormatException {
        new Phenotype("ph:0012345");
    }

    @Test(expectedExceptions = InvalidStringFormatException.class)
    public void useValidIdWithoutPrefix() throws InvalidStringFormatException {
        new Phenotype("0012345");
    }

    @Test(expectedExceptions = InvalidStringFormatException.class)
    public void useUriAsIdInput() throws InvalidStringFormatException {
        new Phenotype("http://purl.obolibrary.org/obo/HP_0012345");
    }

    @Test
    public void useValidUri() {
        Phenotype phenotype = new Phenotype(URI.create("http://purl.obolibrary.org/obo/HP_0012345"));
        testIfValid(phenotype);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void useInvalidUri() {
        new Phenotype(URI.create("http://purl.obolibrary.org/obo/id/HP_0012345"));
    }

    @Test(expectedExceptions = InvalidStringFormatException.class)
    public void useTooShortPhenotypeIdWithPrefix() throws InvalidStringFormatException {
        new Phenotype("hp:0012");
    }

    @Test(expectedExceptions = InvalidStringFormatException.class)
    public void useTooShortPhenotypeIdWithoutPrefix() throws InvalidStringFormatException {
        new Phenotype("0012");
    }

    @Test
    public void testSort() {
        List<Phenotype> actualOrder = new ArrayList<>( Arrays.asList(
                new Phenotype("hp:0000003"),
                new Phenotype("hp:0000008"),
                new Phenotype("hp:0000001")
        ));

        List<Phenotype> expectedOrder = new ArrayList<>( Arrays.asList(
                actualOrder.get(2),
                actualOrder.get(0),
                actualOrder.get(1)
        ));

        Collections.sort(actualOrder);
        Assert.assertEquals(actualOrder, expectedOrder);
    }

    private void testIfValid(Phenotype phenotype) {
        Assert.assertEquals(phenotype.getId(), "0012345");
        Assert.assertEquals(phenotype.getFormattedId(), "hp:0012345");
        Assert.assertEquals(phenotype.getUri(), URI.create("http://purl.obolibrary.org/obo/HP_0012345"));
    }
}
