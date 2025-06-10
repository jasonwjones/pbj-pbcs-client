package com.jasonwjones.pbcs.client.impl;

import com.jasonwjones.pbcs.client.PbcsExplicitDimensionsPlanType;
import com.jasonwjones.pbcs.client.PbcsMember;
import com.jasonwjones.pbcs.client.PbcsPov;
import com.jasonwjones.pbcs.client.exceptions.PbcsInvalidDimensionException;
import com.jasonwjones.pbcs.client.exceptions.PbcsInvalidMemberException;
import org.springframework.lang.NonNull;

import java.util.*;
import java.util.stream.Collectors;

public class PbcsPovImpl implements PbcsPov {

    private final PbcsExplicitDimensionsPlanType planType;

    private final Map<String, PbcsMember> members;

    PbcsPovImpl(PbcsExplicitDimensionsPlanType planType, List<PbcsMember> members) {
        this.planType = planType;
        this.members = new HashMap<>();
        for (PbcsMember member : members) {
            this.members.put(member.getDimensionName(), member);
        }
    }

    private PbcsPovImpl(PbcsExplicitDimensionsPlanType planType, Map<String, PbcsMember> members) {
        this.planType = planType;
        this.members = members;
    }

    @Override
    public Collection<PbcsMember> getMembers() {
        return Collections.unmodifiableCollection(members.values());
    }

    @Override
    public PbcsPov member(PbcsMember member) {
        Map<String, PbcsMember> newMembers = createMemberMap();
        newMembers.put(member.getDimensionName(), member);
        return new PbcsPovImpl(planType, newMembers);
    }

    @Override
    public PbcsPov member(String memberName) {
        PbcsMember member = planType.getMember(memberName);
        if (member != null) {
            return member(member);
        } else {
            throw new PbcsInvalidMemberException(memberName);
        }
    }

    @Override
    public PbcsPov without(String dimension) {
        Map<String, PbcsMember> newMembers = createMemberMap();
        if (newMembers.remove(dimension) == null) throw new PbcsInvalidDimensionException(dimension);
        return new PbcsPovImpl(planType, newMembers);
    }

    @Override
    public List<String> memberNames() {
        return members.values().stream()
                .map(PbcsMember::getName)
                .collect(Collectors.toList());
    }

    private Map<String, PbcsMember> createMemberMap() {
        return new HashMap<>(members);
    }

    @Override
    @NonNull
    public Iterator<PbcsMember> iterator() {
        return members.values().iterator();
    }

    @Override
    public String toString() {
        return members.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue().getName())
                .collect(Collectors.joining(", ", "[", "]"));
    }

}