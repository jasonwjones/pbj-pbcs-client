package com.jasonwjones.pbcs.client.impl.export;

import com.jasonwjones.pbcs.client.PbcsMember;
import com.jasonwjones.pbcs.client.PbcsPlanType;
import com.jasonwjones.pbcs.client.PbcsPov;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.stream.Collectors;

import static com.jasonwjones.pbcs.client.impl.export.ExportStringUtils.leftPad;

public class PrintStreamExportCallback implements PbcsPlanType.ExportCallback {

    private final PrintStream out;

    private final String delimiter = ",";

    private final ExportPrintOptions options = new ExportPrintOptions();

    // "#.##" for with decimals
    private final String currencyFormat = "#";

    public PrintStreamExportCallback(OutputStream out) {
        this.out = new PrintStream(out);
    }

    @Override
    public void pov(PbcsPov pov) {
        out.println("POV: " + pov);
    }

    @Override
    public void printHeaders(List<String> memberHeaders, List<String> headers) {
        for (String header : memberHeaders) {
            out.print(leftPad(header, options.getHeaderWidth()));
            out.print(",");
        }
        String rest = headers.stream().map(h -> leftPad(h, options.getDataWidth())).collect(Collectors.joining(delimiter));
        out.println(rest);
    }

    @Override
    public void printRow(List<PbcsMember> headers, List<String> data) {
        for (PbcsMember member : headers) {
            String name = member.getAlias() != null ? member.getAlias() : member.getName();
            out.print(leftPad(name, options.getHeaderWidth()));
            out.print(delimiter);
        }
        out.println(data.stream()
                .map(d -> leftPad(ExportStringUtils.formatCurrency(d, currencyFormat), options.getDataWidth()))
                .collect(Collectors.joining(delimiter)));

    }

}