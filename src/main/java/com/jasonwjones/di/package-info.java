/**
 * Public client API for the unofficial Data Management (DM/AIF) REST service, a separate service from the
 * main PBCS/Planning REST API used for jobs and application/dimension metadata that the Planning API
 * doesn't expose. Most callers should prefer {@link com.jasonwjones.pbcs.client.PbcsApplication} and
 * related interfaces; use this package only when you specifically need DM/AIF functionality, such as
 * {@link com.jasonwjones.di.DataManagementClientFactory}, the entry point for creating a
 * {@link com.jasonwjones.di.DataManagementClient}.
 */
package com.jasonwjones.di;
