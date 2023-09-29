package com.jasonwjones.pbcs;

import com.jasonwjones.pbcs.client.PbcsPlanningClient;
import com.jasonwjones.pbcs.interop.InteropClient;

/**
 * The PbcsClient interface is the main interface that programs using this API
 * should use. It provides a unified facade over what are essentially two REST
 * APIs that make up PBCS: A Planning API and an LCM ("Interop") API.
 *
 * @author Jason Jones
 *
 */
public interface PbcsClient extends PbcsPlanningClient {

}