package com.odp.walled.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.element.*;
import com.odp.walled.model.Transaction;
import java.util.List;

public class PdfGenerator {

    public static ByteArrayInputStream generateTransactionHistory(List<Transaction> transactions) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("Transaction History")
                .setFontSize(18)
                .setMarginBottom(10));

        Table table = new Table(UnitValue.createPercentArray(new float[] { 2, 2, 2, 3, 3 }))
                .useAllAvailableWidth();

        // Header row
        table.addHeaderCell("Date");
        table.addHeaderCell("Type");
        table.addHeaderCell("Amount");
        table.addHeaderCell("From / To");
        table.addHeaderCell("Description");

        for (Transaction tx : transactions) {
            table.addCell(tx.getTransactionDate().toString());
            table.addCell(tx.getTransactionType().toString());
            table.addCell(tx.getAmount().toPlainString());
            table.addCell(tx.getRecipientWallet() != null
                    ? tx.getRecipientWallet().getAccountNumber()
                    : "-");
            table.addCell(tx.getDescription() != null ? tx.getDescription() : "-");
        }

        document.add(table);
        document.close();

        return new ByteArrayInputStream(out.toByteArray());
    }

}
