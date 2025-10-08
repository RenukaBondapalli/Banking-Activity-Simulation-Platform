package com.example.bank.api;

import com.example.bank.dao.TransactionDAO;
import com.example.bank.model.Transaction;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/export/account/*")   
public class TransactionExportServlet extends HttpServlet {

    private final TransactionDAO transactionDAO = new TransactionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo(); 
        if (pathInfo == null || pathInfo.split("/").length < 1) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Account number is missing in URL.");
            return;
        }

        String accountNumber = pathInfo.replace("/", "").trim();
        if (accountNumber.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid account number.");
            return;
        }

        List<Transaction> transactions;
        try {
            transactions = transactionDAO.getTransactionsByAccountNumber(accountNumber);
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Database error: " + e.getMessage());
            return;
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Transactions");

            String[] columns = {
                    "Transaction ID", "UTR Number", "Transaction Date", "Transaction Amount",
                    "Debited Date", "Account ID", "Balance Amount", "Description",
                    "Modified By", "Receiver", "Transaction Type", "Mode of Transaction"
            };

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                headerRow.createCell(i).setCellValue(columns[i]);
            }

            int rowNum = 1;
            for (Transaction txn : transactions) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(txn.getTransactionId());
                row.createCell(1).setCellValue(txn.getUtrNumber());
                row.createCell(2).setCellValue(txn.getTransactionDate().toString());
                row.createCell(3).setCellValue(txn.getTransactionAmount().doubleValue());
                row.createCell(4).setCellValue(txn.getDebitedDate() != null ? txn.getDebitedDate().toString() : "");
                row.createCell(5).setCellValue(txn.getAccountId());
                row.createCell(6).setCellValue(txn.getBalanceAmount().doubleValue());
                row.createCell(7).setCellValue(txn.getDescription());
                row.createCell(8).setCellValue(txn.getModifiedBy());
                row.createCell(9).setCellValue(txn.getReceiver());
                row.createCell(10).setCellValue(txn.getTransactionType());
                row.createCell(11).setCellValue(txn.getModeOfTransaction());
            }

            for (int i = 0; i < columns.length; i++) sheet.autoSizeColumn(i);

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition",
                    "attachment; filename=\"transactions_" + accountNumber + ".xlsx\"");

            try (OutputStream out = response.getOutputStream()) {
                workbook.write(out);
            }

        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error generating Excel file: " + e.getMessage());
        }
    }
}
