package com.odp.walled.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
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

        Paragraph accountInfo = new Paragraph("Account Info")
                .setFontSize(20)
                .simulateBold();

        document.add(accountInfo);
        Paragraph userInfo = new Paragraph(
                "Name: " + wallet.getUser().getFullname() + "\n" +
                        "Email: " + wallet.getUser().getEmail() + "\n" +
                        "Account Number: " + wallet.getAccountNumber())
                .setFontSize(12);

        document.add(userInfo);

        Paragraph title = new Paragraph("TRANSACTION HISTORY")
                .setFontSize(16)
                .simulateBold()
                .setMarginBottom(10);

        document.add(title);

        DeviceRgb headerColor = new DeviceRgb(0, 128, 128); // Dark teal
        DeviceRgb altRowColor = new DeviceRgb(232, 252, 252); // Light teal background
        DeviceRgb white = new DeviceRgb(255, 255, 255);
        DeviceRgb summaryRowColor = new DeviceRgb(255, 224, 179); // soft orange/peach
        DeviceRgb altSummaryRowColor = new DeviceRgb(255, 239, 214); // light peach

        Table table = new Table(UnitValue.createPercentArray(new float[] { 2, 2, 3, 3, 3 }))
                .useAllAvailableWidth();

        String[] headers = { "Date", "Type", "Amount", "From / To", "Description" };
        for (String header : headers) {
            table.addHeaderCell(styledCell(header, headerColor).setFontColor(white));
        }

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        boolean useAlt = true;

        for (Transaction tx : transactions) {
            String date = tx.getTransactionDate().format(dateFormat);
            String type = tx.getTransactionType().toString();
            String desc = tx.getDescription() == null ? "-" : tx.getDescription();
            String fromTo = "-";
            String formattedAmount = "-";

            boolean isTransfer = tx.getTransactionType() == Transaction.TransactionType.TRANSFER;
            boolean isTopUp = tx.getTransactionType() == Transaction.TransactionType.TOP_UP;

            DeviceRgb rowColor = useAlt ? altRowColor : white;
            ;

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
                type = "TOP-UP";
                formattedAmount = "+ " + currencyFormat.format(tx.getAmount().setScale(2, RoundingMode.HALF_UP));
            } else {
                formattedAmount = currencyFormat.format(tx.getAmount().setScale(2, RoundingMode.HALF_UP));
            }

            table.addCell(styledCell(date, rowColor));
            table.addCell(styledCell(type, rowColor));
            table.addCell(styledCell(formattedAmount, rowColor));
            table.addCell(styledCell(fromTo, rowColor));
            table.addCell(styledCell(desc, rowColor));

            useAlt = !useAlt;
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
        Table summaryTable = new Table(UnitValue.createPercentArray(new float[] { 4, 2 }))
                .useAllAvailableWidth();

        summaryTable.addCell(
                styledCell("Total Income: ", altSummaryRowColor).setTextAlignment(TextAlignment.RIGHT).simulateBold());
        summaryTable.addCell(
                styledCell(formattedIncome, altSummaryRowColor).setTextAlignment(TextAlignment.RIGHT).simulateBold());

        summaryTable.addCell(
                styledCell("Total Outcome:", summaryRowColor).setTextAlignment(TextAlignment.RIGHT).simulateBold());
        summaryTable.addCell(
                styledCell(formattedOutcome, summaryRowColor).setTextAlignment(TextAlignment.RIGHT).simulateBold());

        summaryTable.addCell(
                styledCell("Net Balance: ", altSummaryRowColor).setTextAlignment(TextAlignment.RIGHT).simulateBold());
        summaryTable.addCell(
                styledCell(formattedNet, altSummaryRowColor).setTextAlignment(TextAlignment.RIGHT).simulateBold());

        document.add(summaryTable);
        document.close();

        return new ByteArrayInputStream(out.toByteArray());
    }

    private static Cell styledCell(String content, DeviceRgb bgColor) {
        return new Cell()
                .add(new Paragraph(content))
                .setBackgroundColor(bgColor)
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.CENTER);
    }

}