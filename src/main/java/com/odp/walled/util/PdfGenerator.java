package com.odp.walled.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.odp.walled.model.Transaction;

public class PdfGenerator {

    public static ByteArrayInputStream generateTransactionHistory(List<Transaction> transactions,
            Long currentWalletId) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        Paragraph title = new Paragraph("Transaction History")
                .setFontSize(20)
                .simulateBold()
                .setMarginBottom(10);

        document.add(title);

        Table table = new Table(UnitValue.createPercentArray(new float[] { 2, 2, 2, 3, 4 }))
                .useAllAvailableWidth();

        // Header row
        table.addHeaderCell("Date")
                .setTextAlignment(TextAlignment.CENTER);
        table.addHeaderCell("Type")
                .setTextAlignment(TextAlignment.CENTER);
        table.addHeaderCell("Amount")
                .setTextAlignment(TextAlignment.CENTER);
        table.addHeaderCell("From / To")
                .setTextAlignment(TextAlignment.CENTER);
        table.addHeaderCell("Description")
                .setTextAlignment(TextAlignment.CENTER);

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

        for (Transaction tx : transactions) {
            String date = tx.getTransactionDate().format(dateFormat);
            String type = tx.getTransactionType().toString();
            String desc = tx.getDescription() == null ? "-" : tx.getDescription();
            String fromTo = "-";
            String formattedAmount = "-";

            boolean isTransfer = tx.getTransactionType() == Transaction.TransactionType.TRANSFER;
            boolean isTopUp = tx.getTransactionType() == Transaction.TransactionType.TOP_UP;

            if (isTransfer) {
                Long senderId = tx.getWallet().getId();
                Long recipientId = tx.getRecipientWallet() != null ? tx.getRecipientWallet().getId() : null;

                // If the wallet that made the transaction is the sender
                if (senderId.equals(currentWalletId)) {
                    fromTo = "To: " + tx.getRecipientWallet().getUser().getUsername();
                    formattedAmount = "- " + currencyFormat.format(tx.getAmount().setScale(2, RoundingMode.HALF_UP));
                } else if (recipientId != null && recipientId.equals(currentWalletId)) {
                    // This is an incoming transfer
                    fromTo = "From: " + tx.getWallet().getUser().getUsername();
                    formattedAmount = "+ " + currencyFormat.format(tx.getAmount().setScale(2, RoundingMode.HALF_UP));
                }
            } else if (isTopUp) {
                type = "Top-up";
                formattedAmount = "+ " + currencyFormat.format(tx.getAmount().setScale(2, RoundingMode.HALF_UP));
            } else {
                formattedAmount = currencyFormat.format(tx.getAmount().setScale(2, RoundingMode.HALF_UP));
            }

            table.addCell(date).setTextAlignment(TextAlignment.CENTER);
            table.addCell(type).setTextAlignment(TextAlignment.CENTER);
            table.addCell(formattedAmount).setTextAlignment(TextAlignment.CENTER);
            table.addCell(fromTo).setTextAlignment(TextAlignment.CENTER);
            table.addCell(desc).setTextAlignment(TextAlignment.CENTER);
        }

        document.add(table);
        document.close();

        return new ByteArrayInputStream(out.toByteArray());
    }

}
