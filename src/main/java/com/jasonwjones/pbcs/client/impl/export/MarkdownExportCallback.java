package com.jasonwjones.pbcs.client.impl.export;

import com.jasonwjones.pbcs.client.PbcsMember;
import com.jasonwjones.pbcs.client.PbcsPlanType;
import com.jasonwjones.pbcs.client.PbcsPov;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.StringJoiner;

import static com.jasonwjones.pbcs.client.impl.export.ExportStringUtils.leftPad;

public class MarkdownExportCallback implements PbcsPlanType.ExportCallback {

    private final PrintStream out;

    private final ExportPrintOptions options = new ExportPrintOptions();

    public MarkdownExportCallback(OutputStream out) {
        this.out = new PrintStream(out);
    }

    @Override
    public void pov(PbcsPov pov) {
        // nothing
    }

    @Override
    public void printHeaders(List<String> memberHeaders, List<String> headers) {
        StringJoiner joiner = stringJoiner();
        for (String memberHeader : memberHeaders) {
            joiner.add(leftPad(memberHeader, options.getHeaderWidth()));
        }
        for (String header : headers) {
            joiner.add(leftPad(header, options.getDataWidth()));
        }
        out.println(joiner);

        StringJoiner joiner2 = stringJoiner();
        for (int i = 0; i < memberHeaders.size(); i++) {
            joiner2.add(ExportStringUtils.repeat("-", options.getHeaderWidth()));
        }
        for (int i = 0; i < headers.size(); i++) {
            joiner2.add(ExportStringUtils.repeat("-", options.getDataWidth()));
        }
        out.println(joiner2);
    }

    @Override
    public void printRow(List<PbcsMember> headers, List<String> data) {
        StringJoiner joiner = stringJoiner();
        for (PbcsMember member : headers) {
            String name = member.getAlias() != null ? member.getAlias() : member.getName();
            joiner.add(leftPad(name, options.getHeaderWidth()));
        }
        for (String value : data) {
            joiner.add(leftPad(ExportStringUtils.formatCurrency(value, "#"), options.getDataWidth()));
        }
        out.println(joiner);
    }

    private StringJoiner stringJoiner() {
        return new StringJoiner(" | ", "| ", " |");
    }

}