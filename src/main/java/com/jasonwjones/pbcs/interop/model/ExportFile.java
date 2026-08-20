package com.jasonwjones.pbcs.interop.model;

/*
 *   "lastModififedTime":"1442596618000",
         "name":"outbox/Repge_851.dat",
         "type":"EXTERNAL",
         "size":"30110"
 */
/**
 * Models a single file entry as returned by the interop service's outbox/file listing endpoints.
 */
public class ExportFile {

	// could be null
	private String lastModifiedTime;

	private String name;

	private String type;

	private String size;

	/**
	 * Constructs an empty instance for deserialization.
	 */
	public ExportFile() {
	}

}
