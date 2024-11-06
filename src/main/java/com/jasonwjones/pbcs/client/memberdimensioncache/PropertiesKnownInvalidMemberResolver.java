package com.jasonwjones.pbcs.client.memberdimensioncache;

import com.jasonwjones.pbcs.client.PbcsMember;
import com.jasonwjones.pbcs.client.PbcsPlanType;
import com.jasonwjones.pbcs.client.exceptions.PbcsKnownInvalidMemberException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

/**
 * A simple invalid member resolver that stores contents in a flat text file (one member per line). Mostly intended to
 * be used in integration tests.
 */
public class PropertiesKnownInvalidMemberResolver implements PbcsPlanType.MemberResolver {

    private static final Logger logger = LoggerFactory.getLogger(PropertiesKnownInvalidMemberResolver.class);

    private final File file;

    private final Set<String> knownInvalidMembers;

    public PropertiesKnownInvalidMemberResolver(File file) {
        this.file = file;
        this.knownInvalidMembers = new HashSet<>();
        loadKnownInvalidMembers();
    }

    private void loadKnownInvalidMembers() {
        try {
            knownInvalidMembers.clear();
            if (file.exists()) {
                knownInvalidMembers.addAll(Files.readAllLines(file.toPath()));
                logger.info("Read {} known invalid members from {}", knownInvalidMembers.size(), file.getAbsolutePath());
            } else {
                logger.info("Known invalid member file does not exist: {}", file.getAbsolutePath());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeKnownInvalidMembers() {
        try (PrintWriter writer = new PrintWriter(file.getAbsolutePath() )) {
            for (String invalidMember : knownInvalidMembers) {
                writer.println(invalidMember);
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to write known invalid member file: " + e.getMessage(), e);
        }
    }

    @Override
    public PbcsMember getMember(PbcsPlanType planType, String memberOrAliasName) {
        if (knownInvalidMembers.contains(memberOrAliasName)) {
            throw new PbcsKnownInvalidMemberException(memberOrAliasName);
        }
        return null;
    }

    @Override
    public void setMember(PbcsPlanType planType, String resolvedName, PbcsMember member) {
        // in the future we may potentially want to have this clear a known bad name
    }

    @Override
    public void addInvalidMember(PbcsPlanType planType, String invalidMemberOrAliasName) {
        logger.info("{} is now a known invalid member name in {}", invalidMemberOrAliasName, planType.getQualifiedName());
        knownInvalidMembers.add(invalidMemberOrAliasName);
        writeKnownInvalidMembers();
    }

}