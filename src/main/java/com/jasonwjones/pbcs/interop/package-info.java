/**
 * Public client API for PBCS/EPM Cloud's Lifecycle Management (LCM) functionality - application
 * snapshots, backup/restore, and file upload/download - centered on
 * {@link com.jasonwjones.pbcs.interop.InteropClient}. Named "interop" after a historical URL in the
 * underlying servlet; this is a separate REST service ({@code /interop/rest/...}) from the main Planning
 * API in {@link com.jasonwjones.pbcs.client}, not just another resource within it.
 *
 * @author Jason Jones
 */
package com.jasonwjones.pbcs.interop;