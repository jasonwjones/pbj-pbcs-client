package com.jasonwjones.pbcs.api.v3.dataslices;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Models the data slice response from an Export Data Slice operation
 *
 * @author jasonwjones
 *
 */
public class DataSlice {

	private List<String> pov;

	private List<List<String>> columns;

	/**
	 * Represents the members and data for a given row (the two lower OLAP grid quadrants).
	 */
	private List<HeaderDataRow> rows;

	/**
	 * Constructs an empty instance for deserialization.
	 */
	public DataSlice() {}

	/**
	 * Each element of the columns parameter represents an axis, e.g., if there are three dimensions
	 * in the top/columns area, then columns will have three elements, and each array in that list will be the list of
	 * members going across the top.
	 *
	 * @param pov the pov
	 * @param columns the columns
	 * @param rows the rows
	 */
	public DataSlice(List<String> pov, List<List<String>> columns, List<HeaderDataRow> rows) {
		this.pov = pov;
		this.columns = columns;
		this.rows = rows;
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", "data slice [", "]")
				.add("rows=" + rows.size())
				.toString();
	}

	/**
	 * Convenience constructor that builds a data slice for a single cell POV.
	 *
	 * @param pov the POV
	 * @param value the cell value
	 */
	public DataSlice(List<String> pov, String value) {
		if (pov.size() < 3) throw new IllegalArgumentException("Must provide at least three members");
		this.pov = pov.subList(0, pov.size() - 2);
		this.columns = Collections.singletonList(Collections.singletonList(pov.get(pov.size() - 2)));
		this.rows = Collections.singletonList(new HeaderDataRow(pov.get(pov.size() - 1), value));
	}

	/**
	 * Gets the POV members for this data slice.
	 *
	 * @return the POV
	 */
	public List<String> getPov() {
		return pov;
	}

	/**
	 * Sets the POV members for this data slice.
	 *
	 * @param pov the POV
	 */
	public void setPov(List<String> pov) {
		this.pov = pov;
	}

	/**
	 * Returns the columns (top axis). If there are three dimensions represented, then the outer
	 * list will have three items. Each contained list will be the elements spanning the entire row.
	 *
	 * @return the columns
	 */
	public List<List<String>> getColumns() {
		return columns;
	}

	/**
	 * Sets the columns (top axis).
	 *
	 * @param columns the columns
	 */
	public void setColumns(List<List<String>> columns) {
		this.columns = columns;
	}

	/**
	 * Gets the rows of this data slice.
	 *
	 * @return the rows
	 */
	public List<HeaderDataRow> getRows() {
		return rows;
	}

	/**
	 * Sets the rows of this data slice.
	 *
	 * @param rows the rows
	 */
	public void setRows(List<HeaderDataRow> rows) {
		this.rows = rows;
	}

	/**
	 * Represents a single row of a {@link DataSlice}: the left-axis member headers for the row, along with
	 * the row's data and any supporting detail.
	 */
	public static class HeaderDataRow {

		private List<String> headers;

		private List<String> data;

		private List<SupportingDetailWrapper> supportingDetail;

		// Left out of the JSON when null so an import that carries no notes sends the same payload it
		// always has. Null elements inside the list are still written: they are how a single cell says
		// "leave my notes alone" under the Overwrite option.
		@JsonInclude(JsonInclude.Include.NON_NULL)
		private List<List<CellNote>> cellNotes;

		/**
		 * Constructs an empty instance for deserialization.
		 */
		public HeaderDataRow() {}

		/**
		 * Constructs an instance with the given headers and data.
		 *
		 * @param headers the left-axis member headers for this row
		 * @param data the data values for this row
		 */
		public HeaderDataRow(List<String> headers, List<String> data) {
			this.headers = headers;
			this.data = data;
		}

		/**
		 * Convenience constructor for making a header data row with a single item.
		 *
		 * @param header the header item
		 * @param item the item value
		 */
		public HeaderDataRow(String header, String item) {
			this(Collections.singletonList(header), Collections.singletonList(item));
		}

		/**
		 * Gets the left-axis member headers for this row.
		 *
		 * @return the headers
		 */
		public List<String> getHeaders() {
			return headers;
		}

		/**
		 * Sets the left-axis member headers for this row.
		 *
		 * @param headers the headers
		 */
		public void setHeaders(List<String> headers) {
			this.headers = headers;
		}

		/**
		 * Gets the data values for this row.
		 *
		 * @return the data
		 */
		public List<String> getData() {
			return data;
		}

		/**
		 * Sets the data values for this row.
		 *
		 * @param data the data
		 */
		public void setData(List<String> data) {
			this.data = data;
		}

		/**
		 * Gets the supporting detail for this row, if requested and returned by the export. Like
		 * {@link #getCellNotes()}, the list is positional against {@link #getData()}, with a null element for
		 * each cell that has no supporting detail.
		 *
		 * @return the supporting detail, may be null if not requested/returned
		 */
		public List<SupportingDetailWrapper> getSupportingDetail() {
			return supportingDetail;
		}

		/**
		 * Sets the supporting detail for this row, for sending with an import. The list is positional against
		 * {@link #getData()} and must be exactly as long; the server fails the whole request otherwise. For
		 * each cell:
		 * <ul>
		 *     <li>a wrapper with lines replaces the cell's supporting detail, but only alongside a non-blank
		 *     value in {@code data}; with a blank value the lines are silently ignored. The server stores the
		 *     value as sent and does not check it against the lines, so the caller must send their total;</li>
		 *     <li>a wrapper with no lines deletes the cell's supporting detail, and a blank value then leaves the
		 *     cell's value as it was;</li>
		 *     <li>a null element leaves the cell's supporting detail alone, provided its value is blank.</li>
		 * </ul>
		 *
		 * <p>Writing any non-blank value to a cell deletes its supporting detail unless new lines are sent with
		 * it, even when the value is unchanged and whether or not this list is set. Supporting detail on an
		 * upper-level member is rejected with the rest of the cell.
		 *
		 * @param supportingDetail the supporting detail, one (possibly null) wrapper per data cell
		 */
		public void setSupportingDetail(List<SupportingDetailWrapper> supportingDetail) {
			this.supportingDetail = supportingDetail;
		}

		/**
		 * Gets the cell notes (shown as "comments" in the Planning UI) for this row. The list is positional
		 * against {@link #getData()}: element {@code i} holds the notes for data cell {@code i}, and a cell
		 * may have several notes. An export only returns notes when it is made with
		 * {@link ExportDataSlice#setExportPlanningData(boolean)} set to true, and even then omits this
		 * list entirely for a row whose cells have no notes.
		 *
		 * @return the cell notes, may be null if not requested/returned
		 */
		public List<List<CellNote>> getCellNotes() {
			return cellNotes;
		}

		/**
		 * Sets the cell notes for this row, for sending with an import. What the server does with them depends
		 * on {@link ImportDataSlice#getCellNotesOption()}:
		 * <ul>
		 *     <li>{@code Overwrite} replaces a cell's notes with the given list; an empty list deletes the cell's
		 *     notes, and a null element leaves that cell's notes as they are.</li>
		 *     <li>{@code Append} adds the given notes to the cell's existing ones, duplicates included.</li>
		 *     <li>{@code Skip} ignores them.</li>
		 * </ul>
		 *
		 * <p>The list must be exactly as long as {@link #getData()}; the server fails the whole request
		 * otherwise. Each noted cell still needs an entry in {@code data}, and a blank entry leaves the cell's
		 * value alone. The server silently drops notes it will not store, without rejecting the cell: notes on
		 * upper-level members and notes with empty contents.
		 *
		 * @param cellNotes the cell notes, one (possibly null or empty) list per data cell, or null to send none
		 */
		public void setCellNotes(List<List<CellNote>> cellNotes) {
			this.cellNotes = cellNotes;
		}

	}

	/**
	 * A single cell note, shown as a "comment" in the Planning UI. The REST API carries only the note's
	 * contents: no author, timestamp, or identifier. The contents are stored and returned exactly as sent,
	 * with no HTML conversion, although notes entered through the Planning UI may hold HTML markup.
	 *
	 * <p>The import endpoint rejects the whole request if a note has any field besides {@code contents},
	 * so this class serializes that field only.
	 */
	public static class CellNote {

		private String contents;

		/**
		 * Constructs an empty instance for deserialization.
		 */
		public CellNote() {}

		/**
		 * Constructs a note with the given contents.
		 *
		 * @param contents the contents of the note
		 */
		public CellNote(String contents) {
			this.contents = contents;
		}

		/**
		 * Gets the contents of this note.
		 *
		 * @return the contents
		 */
		public String getContents() {
			return contents;
		}

		/**
		 * Sets the contents of this note.
		 *
		 * @param contents the contents
		 */
		public void setContents(String contents) {
			this.contents = contents;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof CellNote)) return false;
			return Objects.equals(contents, ((CellNote) o).contents);
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(contents);
		}

		@Override
		public String toString() {
			return contents;
		}

	}

	/**
	 * Wraps the list of {@link SupportingDetail} entries returned for a single data cell.
	 */
	public static class SupportingDetailWrapper {

		private List<SupportingDetail> items;

		/**
		 * Constructs an empty instance for deserialization.
		 */
		public SupportingDetailWrapper() {
		}

		/**
		 * Constructs an instance holding the given supporting detail entries.
		 *
		 * @param items the supporting detail entries; an empty list, sent with an import, deletes the cell's
		 *              supporting detail
		 */
		public SupportingDetailWrapper(List<SupportingDetail> items) {
			this.items = items;
		}

		/**
		 * Gets the supporting detail entries.
		 *
		 * @return the supporting detail entries
		 */
		public List<SupportingDetail> getItems() {
			return items;
		}

		/**
		 * Sets the supporting detail entries.
		 *
		 * @param items the supporting detail entries
		 */
		public void setItems(List<SupportingDetail> items) {
			this.items = items;
		}

	}

	/**
	 * Represents a single supporting detail line for a data cell: a labelled value that, together with the
	 * cell's other lines, makes up the cell's value.
	 *
	 * <p>A cell's lines are evaluated in order, not by operator precedence: each line's operator combines
	 * its value with the running total of the lines before it at the same {@link #getGeneration() generation}.
	 * A line followed by lines one generation deeper is a parent whose value is the total of those lines.
	 */
	public static class SupportingDetail {

		private String label;

		// The server accepts only ~, +, -, * and /, and answers any other with a 400 that blames JSON syntax.
		private String operator;

		// Absent rather than null on the wire: a parent line stored without a value comes back with no
		// value field at all, and that is the shape the server is known to accept.
		@JsonInclude(JsonInclude.Include.NON_NULL)
		private String value;

		private int position;

		private int generation;

		/**
		 * Constructs an empty instance for deserialization.
		 */
		public SupportingDetail() {
		}

		/**
		 * Constructs a top-level line (generation zero).
		 *
		 * @param label the label for the line
		 * @param operator how the line combines with the lines before it: {@code +}, {@code -}, {@code *},
		 *                 {@code /}, or {@code ~} to ignore it
		 * @param value the line's numeric value, or null for a blank line
		 */
		public SupportingDetail(String label, String operator, String value) {
			this(label, operator, value, 0);
		}

		/**
		 * Constructs a line at the given generation, where zero is top level and each deeper generation nests
		 * under the nearest preceding line one generation up.
		 *
		 * @param label the label for the line
		 * @param operator how the line combines with the lines before it: {@code +}, {@code -}, {@code *},
		 *                 {@code /}, or {@code ~} to ignore it
		 * @param value the line's numeric value, or null for a blank line or a parent line
		 * @param generation the line's depth, zero for top level
		 */
		public SupportingDetail(String label, String operator, String value, int generation) {
			this.label = label;
			this.operator = operator;
			this.value = value;
			this.generation = generation;
		}

		/**
		 * Gets the label for this supporting detail line.
		 *
		 * @return the label
		 */
		public String getLabel() {
			return label;
		}

		/**
		 * Sets the label for this supporting detail line.
		 *
		 * @param label the label
		 */
		public void setLabel(String label) {
			this.label = label;
		}

		/**
		 * Gets the operator for this supporting detail line, e.g. {@code ~}, {@code +}, {@code -}, {@code *}, or
		 * {@code /}.
		 *
		 * @return the operator
		 */
		public String getOperator() {
			return operator;
		}

		/**
		 * Sets the operator for this supporting detail line.
		 *
		 * @param operator the operator
		 */
		public void setOperator(String operator) {
			this.operator = operator;
		}

		/**
		 * Gets the value for this supporting detail line.
		 *
		 * @return the value
		 */
		public String getValue() {
			return value;
		}

		/**
		 * Sets the value for this supporting detail line.
		 *
		 * @param value the value
		 */
		public void setValue(String value) {
			this.value = value;
		}

		/**
		 * Gets the position of this supporting detail line within its parent.
		 *
		 * @return the position
		 */
		public int getPosition() {
			return position;
		}

		/**
		 * Sets the position of this supporting detail line.
		 *
		 * @param position the position
		 */
		public void setPosition(int position) {
			this.position = position;
		}

		/**
		 * Gets the generation of this supporting detail line.
		 *
		 * @return the generation
		 */
		public int getGeneration() {
			return generation;
		}

		/**
		 * Sets the generation of this supporting detail line.
		 *
		 * @param generation the generation
		 */
		public void setGeneration(int generation) {
			this.generation = generation;
		}

	}

}
