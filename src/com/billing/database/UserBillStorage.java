package com.billing.database;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Simple file-based storage to track which billing months a user has already calculated/saved.
 * File format (user_bills.dat): one line per user: username:YYYY-MM,YYYY-MM,...
 */
public class UserBillStorage {
    private static final String STORAGE_FILE = "user_bills.dat";
    // username -> periodKey -> BillRecord
    private static final Map<String, Map<String, BillRecord>> userToRecords = new HashMap<>();

    static {
        load();
    }

    private static synchronized void load() {
        File file = new File(STORAGE_FILE);
        if (!file.exists()) {
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(":", 2);
                if (parts.length != 2) continue;
                String username = parts[0].trim();
                String monthsCsv = parts[1].trim();
                Map<String, BillRecord> records = new HashMap<>();
                if (!monthsCsv.isEmpty()) {
                    for (String token : monthsCsv.split(",")) {
                        String t = token.trim();
                        if (t.isEmpty()) continue;
                        // Support formats: YYYY-MM, YYYY-MM|units|amount, or YYYY-MM|units|amount|meter
                        String[] fields = t.split("\\|");
                        String periodKey = fields[0];
                        int units = -1;
                        double amount = -1;
                        String meterNumber = null;
                        if (fields.length >= 3) {
                            try { units = Integer.parseInt(fields[1]); } catch (Exception ignored) {}
                            try { amount = Double.parseDouble(fields[2]); } catch (Exception ignored) {}
                        }
                        if (fields.length >= 4) {
                            meterNumber = fields[3].trim();
                        }
                        records.put(periodKey, new BillRecord(periodKey, units, amount, meterNumber));
                    }
                }
                userToRecords.put(username, records);
            }
        } catch (IOException e) {
            System.err.println("Failed to load user bill storage: " + e.getMessage());
        }
    }

    private static synchronized void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(STORAGE_FILE))) {
            for (Map.Entry<String, Map<String, BillRecord>> entry : userToRecords.entrySet()) {
                String username = entry.getKey();
                Map<String, BillRecord> records = entry.getValue();
                StringBuilder sb = new StringBuilder();
                sb.append(username).append(":");
                boolean first = true;
            for (BillRecord r : records.values()) {
                if (!first) sb.append(',');
                // Write with details if available
                if (r.units >= 0 && r.amount >= 0) {
                    sb.append(r.periodKey).append('|').append(r.units).append('|').append(r.amount);
                    if (r.meterNumber != null && !r.meterNumber.isEmpty()) {
                        sb.append('|').append(r.meterNumber);
                    }
                } else {
                    sb.append(r.periodKey);
                }
                first = false;
            }
                bw.write(sb.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Failed to save user bill storage: " + e.getMessage());
        }
    }

    /**
     * Returns a canonical key like YYYY-MM (01-12) for given inputs.
     */
    public static String toPeriodKey(int year, int monthIndexZeroBased) {
        int oneBasedMonth = monthIndexZeroBased + 1;
        String month = (oneBasedMonth < 10 ? "0" : "") + oneBasedMonth;
        return year + "-" + month;
    }

    public static synchronized boolean hasCalculated(String username, int year, int monthIndexZeroBased) {
        String key = toPeriodKey(year, monthIndexZeroBased);
        Map<String, BillRecord> records = userToRecords.get(username);
        return records != null && records.containsKey(key);
    }

    /**
     * Legacy add method without details. Prefer {@link #addCalculated(String, int, int, int, double, String)}.
     */
    public static synchronized void addCalculated(String username, int year, int monthIndexZeroBased) {
        addCalculated(username, year, monthIndexZeroBased, -1, -1, null);
    }

    public static synchronized void addCalculated(String username, int year, int monthIndexZeroBased, int units, double amount) {
        addCalculated(username, year, monthIndexZeroBased, units, amount, null);
    }

    public static synchronized void addCalculated(String username, int year, int monthIndexZeroBased, int units, double amount, String meterNumber) {
        String key = toPeriodKey(year, monthIndexZeroBased);
        Map<String, BillRecord> records = userToRecords.computeIfAbsent(username, k -> new HashMap<>());
        BillRecord existing = records.get(key);
        if (existing == null || existing.units < 0 || existing.amount < 0) {
            records.put(key, new BillRecord(key, units, amount, meterNumber));
            save();
        }
    }

    public static synchronized List<BillRecord> getCalculatedRecords(String username) {
        Map<String, BillRecord> records = userToRecords.get(username);
        if (records == null || records.isEmpty()) return Collections.emptyList();
        List<BillRecord> list = new ArrayList<>(records.values());
        Collections.sort(list, (a, b) -> a.periodKey.compareTo(b.periodKey));
        return list;
    }

    /**
     * Check if a meter number already exists for a different user (not the current user)
     * Returns true if meter number exists for another user, false if it's available or belongs to current user
     */
    public static synchronized boolean meterNumberExistsForOtherUser(String meterNumber, String currentUser) {
        for (Map.Entry<String, Map<String, BillRecord>> entry : userToRecords.entrySet()) {
            String username = entry.getKey();
            // Skip the current user - they can reuse their own meter numbers
            if (username.equals(currentUser)) {
                continue;
            }
            
            Map<String, BillRecord> records = entry.getValue();
            for (BillRecord record : records.values()) {
                if (record.meterNumber != null && record.meterNumber.equalsIgnoreCase(meterNumber)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Get the username who owns a specific meter number
     */
    public static synchronized String getMeterNumberOwner(String meterNumber) {
        for (Map.Entry<String, Map<String, BillRecord>> entry : userToRecords.entrySet()) {
            String username = entry.getKey();
            Map<String, BillRecord> records = entry.getValue();
            for (BillRecord record : records.values()) {
                if (record.meterNumber != null && record.meterNumber.equalsIgnoreCase(meterNumber)) {
                    return username;
                }
            }
        }
        return null;
    }

    /**
     * Get bill record by meter number and month
     */
    public static synchronized BillRecord getRecordByMeterAndMonth(String meterNumber, String periodKey) {
        for (Map<String, BillRecord> records : userToRecords.values()) {
            for (BillRecord record : records.values()) {
                if (record.meterNumber != null && record.meterNumber.equalsIgnoreCase(meterNumber) 
                    && record.periodKey.equals(periodKey)) {
                    return record;
                }
            }
        }
        return null;
    }

    public static class BillRecord {
        public final String periodKey; // YYYY-MM
        public final int units;        // -1 if unknown
        public final double amount;    // -1 if unknown
        public final String meterNumber; // Meter number for this bill

        public BillRecord(String periodKey, int units, double amount) {
            this.periodKey = periodKey;
            this.units = units;
            this.amount = amount;
            this.meterNumber = null;
        }

        public BillRecord(String periodKey, int units, double amount, String meterNumber) {
            this.periodKey = periodKey;
            this.units = units;
            this.amount = amount;
            this.meterNumber = meterNumber;
        }
    }
}


