package com.billing.logic;

/**
 * This class contains the core "business logic" for calculating an electricity bill.
 * It has NO Swing (GUI) code. It just does math.
 * This is a utility class, so all methods and constants are static.
 */
public class BillCalculator {

    // --- Define all our costs. This makes them easy to change later ---
    
    // Fixed charge applied to every bill
    private static final double FIXED_SERVICE_CHARGE = 150.00; // Rs. 150

    // Unit slab rates (per unit)
    private static final double RATE_SLAB_1 = 5.00; // Rs. 5 (for first 100 units)
    private static final double RATE_SLAB_2 = 7.00; // Rs. 7 (for next 200 units)
    private static final double RATE_SLAB_3 = 9.00; // Rs. 9 (for next 200 units)
    private static final double RATE_SLAB_4 = 11.00; // Rs. 11 (for all units above 500)

    // Unit slab limits
    private static final int LIMIT_SLAB_1 = 100; // Slab 1 is 0-100
    private static final int LIMIT_SLAB_2 = 300; // Slab 2 is 101-300
    private static final int LIMIT_SLAB_3 = 500; // Slab 3 is 301-500

    /**
     * Calculates the total electricity bill based on the units consumed.
     * @param units The total number of units consumed.
     * @return A BillDetails object containing the breakdown and total cost.
     */
    public static BillDetails calculate(int units) {
        
        // --- Calculation Variables ---
        double slab1Cost = 0;
        double slab2Cost = 0;
        double slab3Cost = 0;
        double slab4Cost = 0;
        double totalUnitCost = 0;
        double totalAmountDue = 0;

        // --- The Slab Logic ---
        // This is a "waterfall" calculation. We process the units in chunks.
        
        int remainingUnits = units;

        // --- Slab 1 (0 - 100 units) ---
        if (remainingUnits > 0) {
            // Get the units for this slab (either all remaining, or 100, whichever is smaller)
            int unitsInThisSlab = Math.min(remainingUnits, LIMIT_SLAB_1);
            slab1Cost = unitsInThisSlab * RATE_SLAB_1;
            remainingUnits -= unitsInThisSlab; // Subtract the units we just processed
        }

        // --- Slab 2 (101 - 300 units) ---
        if (remainingUnits > 0) {
            // The limit for this slab is 200 units (300 - 100)
            int unitsInThisSlab = Math.min(remainingUnits, LIMIT_SLAB_2 - LIMIT_SLAB_1);
            slab2Cost = unitsInThisSlab * RATE_SLAB_2;
            remainingUnits -= unitsInThisSlab;
        }

        // --- Slab 3 (301 - 500 units) ---
        if (remainingUnits > 0) {
            // The limit for this slab is 200 units (500 - 300)
            int unitsInThisSlab = Math.min(remainingUnits, LIMIT_SLAB_3 - LIMIT_SLAB_2);
            slab3Cost = unitsInThisSlab * RATE_SLAB_3;
            remainingUnits -= unitsInThisSlab;
        }

        // --- Slab 4 (Above 500 units) ---
        if (remainingUnits > 0) {
            // Anything left over falls into this slab
            int unitsInThisSlab = remainingUnits;
            slab4Cost = unitsInThisSlab * RATE_SLAB_4;
        }

        // --- Final Totals ---
        totalUnitCost = slab1Cost + slab2Cost + slab3Cost + slab4Cost;
        totalAmountDue = totalUnitCost + FIXED_SERVICE_CHARGE;

        // We create a new "BillDetails" object to send all this data back.
        // This is much cleaner than just returning a single number.
        return new BillDetails(
            FIXED_SERVICE_CHARGE, 
            slab1Cost, 
            slab2Cost, 
            slab3Cost, 
            slab4Cost, 
            totalUnitCost, 
            totalAmountDue
        );
    }


    /**
     * A simple "container" class to hold all the calculated bill values.
     * This makes it easy to pass all the data from the calculator to the GUI.
     * This is an "inner class" because it's only used by BillCalculator.
     */
    public static class BillDetails {
        public final double serviceCharge;
        public final double slab1Cost;
        public final double slab2Cost;
        public final double slab3Cost;
        public final double slab4Cost;
        public final double totalUnitCost;
        public final double totalAmountDue;

        public BillDetails(double serviceCharge, double slab1, double slab2, double slab3, double slab4, double totalUnit, double total) {
            this.serviceCharge = serviceCharge;
            this.slab1Cost = slab1;
            this.slab2Cost = slab2;
            this.slab3Cost = slab3;
            this.slab4Cost = slab4;
            this.totalUnitCost = totalUnit;
            this.totalAmountDue = total;
        }
    }
    
    /**
     * A main method just for testing our logic.
     * We can run this file directly to see if our math is correct.
     */
    public static void main(String[] args) {
        System.out.println("--- Testing Bill Calculator Logic ---");
        
        // Test Case 1: 80 units (Should be in Slab 1)
        // 150 (fixed) + (80 * 5) = 150 + 400 = 550
        BillDetails test1 = calculate(80);
        System.out.println("Test 1 (80 units): Total = Rs. " + test1.totalAmountDue + " (Expected: 550)");
        
        // Test Case 2: 250 units (Slab 1 + Slab 2)
        // 150 (fixed)
        // + (100 * 5) = 500 (Slab 1)
        // + (150 * 7) = 1050 (Slab 2)
        // Total = 150 + 500 + 1050 = 1700
        BillDetails test2 = calculate(250);
        System.out.println("Test 2 (250 units): Total = Rs. " + test2.totalAmountDue + " (Expected: 1700)");

        // Test Case 3: 600 units (All 4 Slabs)
        // 150 (fixed)
        // + (100 * 5) = 500 (Slab 1)
        // + (200 * 7) = 1400 (Slab 2)
        // + (200 * 9) = 1800 (Slab 3)
        // + (100 * 11) = 1100 (Slab 4)
        // Total = 150 + 500 + 1400 + 1800 + 1100 = 4950
        BillDetails test3 = calculate(600);
        System.out.println("Test 3 (600 units): Total = Rs. " + test3.totalAmountDue + " (Expected: 4950)");
    }
}


