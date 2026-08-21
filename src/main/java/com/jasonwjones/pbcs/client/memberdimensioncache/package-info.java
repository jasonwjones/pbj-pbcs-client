/**
 * Pluggable caching strategies for member/dimension lookups and aliases, implementing
 * {@link com.jasonwjones.pbcs.client.PbcsPlanType.MemberDimensionCache} and
 * {@link com.jasonwjones.pbcs.client.PbcsPlanType.MemberResolver}. This is a public extension point: callers
 * configure one of these (or their own implementation) via
 * {@link com.jasonwjones.pbcs.client.impl.PlanTypeConfigurationImpl.Builder#memberResolver} to control how
 * aggressively a plan type caches member/dimension/alias lookups.
 */
package com.jasonwjones.pbcs.client.memberdimensioncache;
