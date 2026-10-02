package com.jasonwjones.pbcs.api.v3.dataslices;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

/**
 * Supporting detail on the wire, in the shapes a live pod sent and accepted.
 */
public class DataSliceSupportingDetailTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /** A parent line imported without a value is exported without a value field, not with a null one. */
    @Test
    public void aLineWithoutAValueIsReadAsNull() throws Exception {
        String json = """
                {"pov":["Actual","FY23","Final","USD","000","P_000"],"columns":[["Apr"]],"rows":[
                  {"headers":["4110"],"data":["3886"],"supportingDetail":[{"items":[
                    {"label":"Postage","operator":"+","position":0,"generation":0},
                    {"label":"Employees","operator":"+","value":"43","position":1,"generation":1}
                  ]}]}
                ]}
                """;

        DataSlice slice = objectMapper.readValue(json, DataSlice.class);

        DataSlice.SupportingDetail parent = slice.getRows().get(0).getSupportingDetail().get(0).getItems().get(0);
        DataSlice.SupportingDetail child = slice.getRows().get(0).getSupportingDetail().get(0).getItems().get(1);
        assertThat(parent.getValue(), is(nullValue()));
        assertThat(child.getValue(), is("43"));
        assertThat(child.getPosition(), is(1));
        assertThat(child.getGeneration(), is(1));
    }

    /** And it is sent the same way, which is the shape the server is known to accept. */
    @Test
    public void aLineWithoutAValueIsSentWithoutAValueField() throws Exception {
        String json = objectMapper.writeValueAsString(new DataSlice.SupportingDetail("Postage", "+", null));

        assertThat(json, is("{\"label\":\"Postage\",\"operator\":\"+\",\"position\":0,\"generation\":0}"));
    }

}
