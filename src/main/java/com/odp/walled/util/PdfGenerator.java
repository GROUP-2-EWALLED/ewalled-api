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
import com.itextpdf.kernel.geom.PageSize;
import com.odp.walled.model.Transaction;
import com.odp.walled.model.Wallet;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import java.io.InputStream;

public class PdfGenerator {

    public static ByteArrayInputStream generateTransactionHistory(List<Transaction> transactions,
            Wallet wallet) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);
        Long currentWalletId = wallet.getId();

        try {
            InputStream imageStream = PdfGenerator.class.getClassLoader().getResourceAsStream("static/logo.png");
            if (imageStream != null) {
                byte[] imageBytes = imageStream.readAllBytes();
                ImageData imageData = ImageDataFactory.create(imageBytes);
                Image logo = new Image(imageData).scaleToFit(100, 100); // Resize logo
                logo.setMarginBottom(10);
                document.add(logo);
            }
        } catch (Exception e) {
            System.err.println("Could not load logo image: " + e.getMessage());
        }

        Paragraph userInfo = new Paragraph(
                "Account Info\n" +
                        "Name: " + wallet.getUser().getFullname() + "\n" +
                        "Email: " + wallet.getUser().getEmail() + "\n" +
                        "Account Number: " + wallet.getAccountNumber())
                .setFontSize(12)
                .setMarginBottom(15);

        document.add(userInfo);

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

        // Calculate income, outcome, and net balance
        java.math.BigDecimal totalIncome = java.math.BigDecimal.ZERO;
        java.math.BigDecimal totalOutcome = java.math.BigDecimal.ZERO;

        for (Transaction tx : transactions) {
            if (tx.getTransactionType() == Transaction.TransactionType.TOP_UP ||
                    (tx.getRecipientWallet() != null && tx.getRecipientWallet().getId().equals(currentWalletId))) {
                totalIncome = totalIncome.add(tx.getAmount());
            } else if (tx.getWallet().getId().equals(currentWalletId)) {
                totalOutcome = totalOutcome.add(tx.getAmount());
            }
        }

        java.math.BigDecimal netBalance = totalIncome.subtract(totalOutcome);

        String formattedIncome = "+ " + currencyFormat.format(totalIncome.setScale(2, RoundingMode.HALF_UP));
        String formattedOutcome = "- " + currencyFormat.format(totalOutcome.setScale(2, RoundingMode.HALF_UP));
        String formattedNet = (netBalance.compareTo(java.math.BigDecimal.ZERO) >= 0 ? "+ " : "- ")
                + currencyFormat.format(netBalance.abs().setScale(2, RoundingMode.HALF_UP));

        // Add spacing
        document.add(new Paragraph("\n"));

        // Add summary in vertical table
        Table summaryTable = new Table(2)
                .useAllAvailableWidth()
                .setMarginTop(10);

        summaryTable.addCell("Total Income");
        summaryTable.addCell(formattedIncome);

        summaryTable.addCell("Total Outcome");
        summaryTable.addCell(formattedOutcome);

        summaryTable.addCell("Net Balance");
        summaryTable.addCell(formattedNet);

        document.add(summaryTable);
        document.close();

        return new ByteArrayInputStream(out.toByteArray());
    }

}
