package com.jasonwjones.pbcs.test;

import com.jasonwjones.pbcs.PbcsClient;
import com.jasonwjones.pbcs.PbcsClientFactory;
import com.jasonwjones.pbcs.client.*;
import com.jasonwjones.pbcs.client.impl.PbcsConnectionToken;
import com.jasonwjones.pbcs.client.impl.PlanTypeConfigurationImpl;
import com.jasonwjones.pbcs.client.sso.IDCSDeviceCodeFlow;
import com.jasonwjones.pbcs.client.sso.RefreshableToken;

import java.util.Arrays;
import java.util.Scanner;

public class IdcsTest {

    public static final String SERVER = "appliedolapepm-test-appliedolapepm.epm.us-phoenix-1.ocs.oraclecloud.com";

    public static final String TENANT = "42ababaade214afaa94fdcb7ced3d7e3";

    public static final String CLIENT_ID = "cc30e98edaca4cefbdb94337fc76b0e5";

    public static final String SCOPE = "urn:opc:serviceInstanceID=687065603urn:opc:resource:consumer::all offline_access";

    // IF DEBUG IS ENABLED, THE R/R INTERCEPTOR IS SWALLOING THE RESPONSE!!!
    public static void main(String[] args) {
        IDCSDeviceCodeFlow flow = new IDCSDeviceCodeFlow(CLIENT_ID, TENANT);

        RefreshableToken token = null;
        try {
            token = flow.getToken(SCOPE);
        } catch (IDCSDeviceCodeFlow.NoExistingRefreshTokenException e) {
            System.out.println("Use code " + e.getUserCode() + " at " + e.getVerificationUri());
            System.out.println("Press enter after finishing device code flow");
            Scanner scanner = new Scanner(System.in);
            scanner.nextLine();
            token = e.confirm();
        }

        System.out.println("Access token: " + token.getAccessToken());

        PbcsConnection connection = new PbcsConnectionToken(SERVER, token);
        PbcsClient client = new PbcsClientFactory().createClient(connection);

//        System.out.println("Apps:");
//
//        for (PbcsApplication app : client.getApplications()) {
//            System.out.println(" - " + app.getName());
//            System.out.println("Plans: " + app.getPlanTypes().size());
//            for (PbcsPlanType cube : app.getPlanTypes()) {
//                System.out.println(cube.getName());
//                for (PbcsDimension dimension : cube.getDimensions()) {
//                    System.out.println("Dim: " + dimension.getName());
//                }
//            }
//        }



        PlanTypeConfigurationImpl config = new PlanTypeConfigurationImpl();
        config.setName("Plan1");
        config.setExplicitDimensions(Arrays.asList("Account", "Currency", "Entity", "Period", "Product", "Scenario", "Version", "Year"));
        config.setSkipCheck(true);

        PbcsMemberProperties actual = client
                .getApplication("Vision")
                .getPlanType("Plan1")
                .getMember("Scenario", "Actual");

        System.out.println("Member: " + actual.getName());

        PbcsPlanType plan = client.getApplication("Vision").getPlanType(config);
//        System.out.println("Top cell: " + plan.getCell());

        String cell = plan.getCell(Arrays.asList("4110", "USD", "000", "Jan", "P_000", "Actual", "Final", "FY22"));
        System.out.println("Cell: " + cell);
        //String topOfTheHouse = plan.getCell();

        //System.out.println("Cell: " + topOfTheHouse);



        //config.set

        //client.getApplication("Vision").getP

//        System.out.println("Apps2:");
//
//        for (PbcsApplication app : client.getApplications()) {
//            System.out.println(" - " + app.getName());
//        }
//
//        System.out.println("Apps3:");
//
//        for (PbcsApplication app : client.getApplications()) {
//            System.out.println(" - " + app.getName());
//        }

    }

}