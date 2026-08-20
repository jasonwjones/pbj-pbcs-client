package com.jasonwjones.pbcs.api.v3;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class AliasedMemberTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void deserializesTrimmedDimensionDetailsResponse() throws Exception {
        // shape confirmed against a live tenant for fields=name,alias,children: members without an alias
        // in the requested table omit the "alias" key entirely rather than repeating the member name.
        String json = """
                {
                  "name": "Scenario",
                  "children": [
                    {"name": "Actual"},
                    {"name": "Plan"},
                    {"name": "Current", "alias": "SomeCurrent"}
                  ]
                }
                """;

        AliasedMember root = objectMapper.readValue(json, AliasedMember.class);

        assertThat(root.getName(), is("Scenario"));
        assertThat(root.getAlias(), is(nullValue()));

        List<AliasedMember> children = root.getChildren();
        assertThat(children, hasSize(3));
        assertThat(children.get(0).getName(), is("Actual"));
        assertThat(children.get(0).getAlias(), is(nullValue()));
        assertThat(children.get(2).getName(), is("Current"));
        assertThat(children.get(2).getAlias(), is("SomeCurrent"));
    }

}
