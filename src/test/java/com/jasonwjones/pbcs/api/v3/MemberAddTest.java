package com.jasonwjones.pbcs.api.v3;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class MemberAddTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void serializesToTheExpectedRequestShape() throws Exception {
        MemberAdd memberAdd = new MemberAdd("North America", "Enterprise Global");

        JsonNode json = objectMapper.valueToTree(memberAdd);

        assertThat(json.get("memberName").asText(), is("North America"));
        assertThat(json.get("parentName").asText(), is("Enterprise Global"));
        assertThat(json.size(), is(2));
    }

    @Test
    public void addMemberResponseDeserializesIntoMemberProperties() throws Exception {
        String json = """
                {
                  "name": "North America",
                  "children": null,
                  "description": null,
                  "parentName": "Enterprise Global",
                  "dataType": "UNSPECIFIED",
                  "objectType": 33,
                  "dataStorage": "STOREDATA",
                  "dimName": "Entity",
                  "twoPass": false
                }
                """;

        PbcsMemberPropertiesImpl properties = objectMapper.readValue(json, PbcsMemberPropertiesImpl.class);

        assertThat(properties.getName(), is("North America"));
        assertThat(properties.getParentName(), is("Enterprise Global"));
        assertThat(properties.getDataType(), is("UNSPECIFIED"));
        assertThat(properties.getObjectType(), is(33));
        assertThat(properties.getDataStorage(), is("STOREDATA"));
        assertThat(properties.getDimensionName(), is("Entity"));
        assertThat(properties.isTwoPass(), is(false));
        assertThat(properties.getChildren().isEmpty(), is(true));
    }

}
