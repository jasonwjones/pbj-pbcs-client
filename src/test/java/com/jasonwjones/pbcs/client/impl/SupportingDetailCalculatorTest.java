package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.api.v3.dataslices.DataSlice;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

/**
 * The totals here are the ones the Planning UI saved for detail entered through it on the Vision app, and
 * the ones in Oracle's "Order of Supporting Detail" examples - not values worked out by hand.
 */
public class SupportingDetailCalculatorTest {

    @Test
    public void addsLinesInOrder() {
        // Vision 7410, Plan/Working/FY22/000/No Product, Jan
        assertThat(total(line("Gas", "+", "300"), line("Water", "+", "100"), line("Electric", "+", "240")), is("640"));
    }

    @Test
    public void multipliesTheRunningTotal() {
        // Vision 7420 and 7450
        assertThat(total(line("Square Feet", "+", "3000"), line("Rate", "*", "20")), is("60000"));
        assertThat(total(line("Cost Per Employee", "+", "30"), line("Number of Employees", "*", "25")), is("750"));
    }

    /** Vision 7510: the UI saved the parent line as 4386, its children's product, and the cell as 3886. */
    @Test
    public void aParentLineTakesItsChildrensTotal() {
        SupportingDetailCalculator calculation = SupportingDetailCalculator.calculate(Arrays.asList(
                line("Total Postage", "+", null),
                line("Employee Count", "+", "43", 1),
                line("Postage Allocation", "*", "102", 1),
                line("Rebate", "-", "500")));

        assertThat(calculation.getTotal(), is("3886"));
        assertThat(calculation.getLines().get(0).getValue(), is("4386"));
    }

    /** Oracle's "incorrect order" example, Feb: Rate + 250, then Unit * with no value, saves 250. */
    @Test
    public void aLineWithNoValueIsSkipped() {
        assertThat(total(line("Rate", "+", "250"), line("Unit", "*", null)), is("250"));
    }

    /** Oracle's "correct order" example, Feb: Unit + with no value, then Rate * 250, saves nothing. */
    @Test
    public void multiplyingBeforeAnyValueLeavesNothing() {
        assertThat(total(line("Unit", "+", null), line("Rate", "*", "250")), is(nullValue()));
    }

    @Test
    public void ignoresTilde() {
        assertThat(total(line("a", "+", "100"), line("b", "*", "4"), line("c", "/", "2"), line("d", "-", "10"), line("e", "~", "999")), is("190"));
    }

    /**
     * Row order rather than operator precedence, as Oracle describes it ("the calculation order first adds
     * the Rate and then multiplies by the Unit"). None of the examples above tells the two apart - only a +
     * ahead of a * does - so this one was entered through the Planning UI on purpose: 10 +, 2 +, 3 * shows
     * 36 there, where precedence would give 16.
     */
    @Test
    public void evaluatesInRowOrderNotByPrecedence() {
        assertThat("(10 + 2) * 3", total(line("a", "+", "10"), line("b", "+", "2"), line("c", "*", "3")), is("36"));
    }

    @Test
    public void aLeadingMinusNegates() {
        assertThat(total(line("refund", "-", "40"), line("fee", "+", "15")), is("-25"));
    }

    @Test
    public void divisionKeepsSixteenDigits() {
        assertThat(total(line("a", "+", "10"), line("b", "/", "3")), is("3.333333333333333"));
    }

    @Test
    public void positionsComeFromTheOrderAndTheGivenLinesAreUntouched() {
        DataSlice.SupportingDetail first = line("a", "+", "1");
        first.setPosition(7);
        DataSlice.SupportingDetail parent = line("b", "+", "999");
        List<DataSlice.SupportingDetail> given = Arrays.asList(first, parent, line("c", "+", "2", 1));

        SupportingDetailCalculator calculation = SupportingDetailCalculator.calculate(given);

        assertThat(calculation.getLines().stream().map(DataSlice.SupportingDetail::getPosition).toList(), contains(0, 1, 2));
        assertThat(first.getPosition(), is(7));
        assertThat("the caller's parent value is replaced in the copy only", parent.getValue(), is("999"));
        assertThat(calculation.getLines().get(1).getValue(), is("2"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void anUnknownOperatorIsRefused() {
        total(line("a", "x", "1"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void aNonNumericValueIsRefused() {
        total(line("a", "+", "abc"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void aLineCannotSkipAGeneration() {
        total(line("a", "+", "1"), line("b", "+", "2", 2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void theFirstLineMustBeTopLevel() {
        total(line("a", "+", "1", 1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void divisionByZeroIsRefused() {
        total(line("a", "+", "1"), line("b", "/", "0"));
    }

    private static String total(DataSlice.SupportingDetail... lines) {
        return SupportingDetailCalculator.calculate(Arrays.asList(lines)).getTotal();
    }

    private static DataSlice.SupportingDetail line(String label, String operator, String value) {
        return new DataSlice.SupportingDetail(label, operator, value);
    }

    private static DataSlice.SupportingDetail line(String label, String operator, String value, int generation) {
        return new DataSlice.SupportingDetail(label, operator, value, generation);
    }

}
