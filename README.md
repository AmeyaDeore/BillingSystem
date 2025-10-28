# Electricity Billing System

A desktop Java application for calculating electricity bills with user-authenticated access, monthly bill tracking, and historical record management.

## Features

- **User Authentication**: Secure login/signup system with file-based storage
- **Bill Calculation**: Automated bill calculation using tiered slab rates
- **Monthly Locking**: Prevents duplicate bill calculations for the same month per user
- **Unique Meter Numbers**: Ensures no two bills share the same meter number
- **Bill History**: "My Months" tab shows all previously calculated bills with:
  - Month and Year
  - Units Consumed
  - Total Bill Amount
  - Search and filter functionality
  - Export selected bill summaries
- **Receipt Generation**: Save detailed bill receipts to text files
- **Clean UI**: Modern, well-organized interface with proper spacing and alignment

## Technologies Used

- **Java SE**: Core programming language
- **Java Swing/AWT**: GUI framework for desktop application
- **JTable**: For tabular bill history display
- **JFileChooser**: File saving functionality
- **Java I/O**: File-based data persistence
- **JDBC** (Optional): MySQL database connectivity for authentication

## Project Structure

```
src/
├── com/billing/
│   ├── main/
│   │   └── Main.java              # Application entry point
│   ├── gui/
│   │   ├── LoginFrame.java        # User login interface
│   │   ├── SignupFrame.java       # User registration
│   │   ├── MainDashboard.java     # Main dashboard
│   │   └── BillingFrame.java      # Bill calculator and history
│   ├── logic/
│   │   └── BillCalculator.java    # Bill calculation logic
│   └── database/
│       ├── FileUserStorage.java   # File-based user storage
│       ├── UserBillStorage.java   # Bill history storage
│       └── DatabaseConnection.java # MySQL connectivity (optional)
```

## Bill Calculation Logic

The system uses a tiered slab pricing structure:

- **Fixed Service Charge**: Rs. 150.00 (applied to every bill)
- **Slab 1** (0-100 units): Rs. 5.00 per unit
- **Slab 2** (101-300 units): Rs. 7.00 per unit
- **Slab 3** (301-500 units): Rs. 9.00 per unit
- **Slab 4** (Above 500 units): Rs. 11.00 per unit

## Getting Started

### Prerequisites

- Java JDK 8 or higher
- MySQL (optional, for database authentication)

### Installation

1. Clone the repository:
```bash
git clone https://github.com/yourusername/electricity-billing-system.git
cd electricity-billing-system
```

2. Launch the application:
```bash
# Windows - Just double-click!
START.bat

# OR Command line
run.bat  # If already compiled

# Linux/Mac (if scripts are created)
./START.sh
```

### Default Credentials

- **Username**: `admin`
- **Password**: `12345`

## Usage

1. **Login**: Enter your username and password, or sign up for a new account
2. **Calculate Bill**: Click "Calculate New Bill" and enter:
   - Billing period (month/year)
   - Customer details (name, meter number, address)
   - Units consumed
3. **View History**: Click "My Months" tab to see all your calculated bills
4. **Export**: Select a bill from history and click "Export Selected" to save a summary
5. **Save Receipt**: Use "Save Bill to File" to save the detailed bill receipt

## Data Storage

The application uses two file-based storage systems:

### users.dat
Stores user credentials in format: `username:password`

### user_bills.dat
Stores bill history in format: `username:YYYY-MM|units|amount|meter,YYYY-MM|units|amount|meter,...`

## Key Features in Detail

### Monthly Locking
Once a user calculates a bill for a specific month, that period is locked to prevent duplicate calculations. The system displays a red warning message if attempting to calculate an already processed month.

### Unique Meter Numbers
Each meter number can only be used once in the entire system. If a user attempts to enter a meter number that has already been used (by any user for any previous bill), the system will display an error message and prevent the calculation until a unique meter number is provided. This ensures data integrity and prevents duplicate billing for the same customer.

### Bill History Management
- **Search**: Filter bills by typing keywords in the search box
- **Year Filter**: Dropdown to filter bills by specific year
- **Export**: Generate summary files for selected bills
- **Double-Click Export**: Quick export by double-clicking a table row

## Future Enhancements

- PDF receipt generation
- Cloud database integration for multi-device access
- Analytics dashboard with charts and graphs
- Report generation with advanced filtering
- Role-based access control
- Audit trail for billing operations

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is open source and available under the MIT License.

## Author

Developed as an academic project demonstrating Java Swing GUI development, file I/O, and modular architecture.

## Acknowledgments

- Java Swing documentation
- MySQL Connector/J for database connectivity (optional)

