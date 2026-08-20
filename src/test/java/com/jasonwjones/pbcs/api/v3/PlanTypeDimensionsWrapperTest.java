package com.jasonwjones.pbcs.api.v3;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

public class PlanTypeDimensionsWrapperTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void deserializesPlanTypeDimensionListResponse() throws Exception {
        String json = """
                {
                  "items": [
                    {
                      "dimName": "Account",
                      "name": "Account",
                      "dimType": "Account",
                      "density": "Dense",
                      "enforceSecurity": false,
                      "objectTypeId": 2,
                      "evaluationOrder": 1,
                      "generation": 1,
                      "level": 9,
                      "objectType": "Dimension",
                      "usedIn": ["Plan1", "Vision"],
                      "valid": true,
                      "invalidDueToValidIntersection": false,
                      "id": "6c39b137-3c33-49f3-8f55-6df62a9fcc4c"
                    },
                    {
                      "dimName": "Market",
                      "name": "Market",
                      "dimType": "Attribute Dimension",
                      "density": "Sparse",
                      "enforceSecurity": false,
                      "objectTypeId": 2,
                      "evaluationOrder": 0,
                      "generation": 1,
                      "level": 0,
                      "objectType": "Dimension",
                      "usedIn": ["Plan1", "Vision"],
                      "valid": true,
                      "invalidDueToValidIntersection": false,
                      "id": "5cb280af-ce17-4203-a9b2-7de445b25bf5"
                    }
                  ]
                }
                """;

        PlanTypeDimensionsWrapper wrapper = objectMapper.readValue(json, PlanTypeDimensionsWrapper.class);
        List<PlanTypeDimension> items = wrapper.getItems();

        assertThat(items, hasSize(2));

        PlanTypeDimension account = items.get(0);
        assertThat(account.getDimensionName(), is("Account"));
        assertThat(account.getDimType(), is("Account"));
        assertThat(account.isValid(), is(true));
        assertThat(account.getUsedIn(), is(List.of("Plan1", "Vision")));

        PlanTypeDimension market = items.get(1);
        assertThat(market.getDimensionName(), is("Market"));
        assertThat(market.getDimType(), is("Attribute Dimension"));
    }

}
