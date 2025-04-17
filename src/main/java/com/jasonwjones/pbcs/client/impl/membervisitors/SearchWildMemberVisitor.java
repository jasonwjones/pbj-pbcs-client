package com.jasonwjones.pbcs.client.impl.membervisitors;

public class SearchWildMemberVisitor extends SearchRegexMemberVisitor {

    public SearchWildMemberVisitor(String regex) {
        super(regex.replace("*", ".*"));
    }

}