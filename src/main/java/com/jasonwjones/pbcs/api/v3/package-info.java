/**
 * Maps the request/response payloads for the PBCS/EPM Cloud Planning REST API (v3), including hypermedia
 * plumbing shared across many endpoints such as {@link com.jasonwjones.pbcs.api.v3.HypermediaLink} and
 * {@link com.jasonwjones.pbcs.api.v3.AbstractHypermediaResponse}. Internal implementation package; classes
 * here should stay simple POJOs with standard getters/setters and no behavior, adjusting field names with
 * Jackson annotations as needed rather than adding logic.
 *
 * @author jasonwjones
 */
package com.jasonwjones.pbcs.api.v3;