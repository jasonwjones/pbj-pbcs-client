package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.api.v3.dataslices.DataSlice;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Works out the value a cell's supporting detail adds up to, the way the Planning UI does before it saves.
 *
 * <p>importdataslice will not: it stores whatever value it is sent beside the lines, matching or not, and
 * ignores the lines altogether when the value is blank. So a write has to arrive with the total already
 * worked out, and with each parent line's value filled in from its children, as the UI saves them.
 *
 * <p>The rules are Planning's, checked against Oracle's documented examples and against detail entered
 * through the UI on a live pod:
 * <ul>
 *     <li>Lines are evaluated in order, not by operator precedence: each operator combines the line's value
 *     with the running total of the lines before it at the same generation. In the UI, 10 +, 2 +, 3 * comes
 *     to 36, not 16.</li>
 *     <li>A line with no value, or with the {@code ~} operator, is skipped.</li>
 *     <li>Before any line has contributed there is no total, and {@code *} or {@code /} leave it that way:
 *     Oracle's "Unit +" (blank) then "Rate *" 250 saves nothing, while "Rate +" 250 then "Unit *" (blank)
 *     saves 250.</li>
 *     <li>A line followed by lines one generation deeper is a parent, and its value is their total.</li>
 * </ul>
 */
public final class SupportingDetailCalculator {

	// Public so that a caller editing supporting detail can show what the cell is about to become.
	// Writing detail replaces the cell's value with this total, and an editor that did not say so
	// would let someone discover it afterwards. Exposed rather than described, because the arithmetic
	// is not the one anybody guesses: it runs in row order rather than by operator precedence, so
	// 10 +, 2 +, 3 * is 36 and not 16, and a second implementation of that would be wrong quietly.

	private static final Set<String> OPERATORS = Set.of("+", "-", "*", "/", "~");

	private final List<DataSlice.SupportingDetail> lines;

	private final BigDecimal total;

	private SupportingDetailCalculator(List<DataSlice.SupportingDetail> lines) {
		this.lines = lines;
		int[] cursor = {0};
		this.total = evaluate(cursor, 0);
	}

	/**
	 * Validates the given lines and works out their total. The lines are copied, not changed: the copies are
	 * numbered by position in the order given, and each parent line's value is replaced with its children's
	 * total.
	 *
	 * @param lines the supporting detail lines, in order
	 * @return the calculation
	 * @throws IllegalArgumentException if a line has an unknown operator, a non-numeric value, or a
	 *                                  generation that does not nest under the line before it, or if the
	 *                                  lines divide by zero
	 */
	public static SupportingDetailCalculator calculate(List<DataSlice.SupportingDetail> lines) {
		List<DataSlice.SupportingDetail> copies = new ArrayList<>(lines.size());
		int previousGeneration = -1;
		for (int position = 0; position < lines.size(); position++) {
			DataSlice.SupportingDetail line = lines.get(position);
			if (!OPERATORS.contains(line.getOperator())) {
				throw new IllegalArgumentException("Supporting detail line " + position + " has operator '" + line.getOperator() + "'; expected one of " + OPERATORS);
			}
			int generation = line.getGeneration();
			if (generation < 0 || generation > previousGeneration + 1) {
				throw new IllegalArgumentException("Supporting detail line " + position + " is at generation " + generation + " but can be at most " + (previousGeneration + 1) + ", one deeper than the line before it");
			}
			previousGeneration = generation;
			parse(line.getValue(), position);
			// Positions must be distinct - two lines at the same position fail the import on a database
			// unique constraint - so they are numbered here rather than trusted from the caller.
			DataSlice.SupportingDetail copy = new DataSlice.SupportingDetail(line.getLabel(), line.getOperator(), line.getValue(), generation);
			copy.setPosition(position);
			copies.add(copy);
		}
		return new SupportingDetailCalculator(copies);
	}

	/**
	 * Gets the validated lines, numbered by position and with parent values filled in.
	 *
	 * @return the lines to send
	 */
	public List<DataSlice.SupportingDetail> getLines() {
		return lines;
	}

	/**
	 * Gets the value the lines add up to, formatted for an import.
	 *
	 * @return the total, or null if the lines add up to nothing (no line contributed a value)
	 */
	public String getTotal() {
		return total == null ? null : format(total);
	}

	private BigDecimal evaluate(int[] cursor, int generation) {
		BigDecimal runningTotal = null;
		while (cursor[0] < lines.size() && lines.get(cursor[0]).getGeneration() == generation) {
			int position = cursor[0]++;
			DataSlice.SupportingDetail line = lines.get(position);
			if (cursor[0] < lines.size() && lines.get(cursor[0]).getGeneration() > generation) {
				BigDecimal children = evaluate(cursor, generation + 1);
				line.setValue(children == null ? null : format(children));
			}
			runningTotal = apply(runningTotal, line.getOperator(), parse(line.getValue(), position), position);
		}
		return runningTotal;
	}

	private static BigDecimal apply(BigDecimal runningTotal, String operator, BigDecimal value, int position) {
		if (value == null || "~".equals(operator)) {
			return runningTotal;
		}
		if (runningTotal == null) {
			switch (operator) {
				case "+": return value;
				case "-": return value.negate();
				default: return null;
			}
		}
		switch (operator) {
			case "+": return runningTotal.add(value);
			case "-": return runningTotal.subtract(value);
			case "*": return runningTotal.multiply(value);
			default:
				if (value.signum() == 0) {
					throw new IllegalArgumentException("Supporting detail line " + position + " divides by zero");
				}
				return runningTotal.divide(value, MathContext.DECIMAL64);
		}
	}

	private static BigDecimal parse(String value, int position) {
		if (value == null || value.isBlank()) {
			return null;
		}
		try {
			return new BigDecimal(value.trim());
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Supporting detail line " + position + " has non-numeric value '" + value + "'", e);
		}
	}

	private static String format(BigDecimal value) {
		return value.stripTrailingZeros().toPlainString();
	}

}
