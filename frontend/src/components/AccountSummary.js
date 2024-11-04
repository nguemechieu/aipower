import React from 'react';

const AccountSummary = () => {
    return (
        <div className="account-summary">
            <h2>Gain Capital Group, LLC</h2>
            <div>
                <p><strong>A/C No:</strong> 24264448</p>
                <p><strong>Name:</strong> NOEL NGUEMECHIEU</p>
                <p><strong>Currency:</strong> USD</p>
                <p><strong>Date:</strong> 2024.10.31 23:59</p>
            </div>

            <Section title="Orders">
                <Table headers={["Open Time", "Ticket", "Type", "Size", "Item", "Price", "S / L", "T / P", "Time", "State", "Comment"]}>
                    <tr><td colSpan="11" className="no-transactions">No transactions</td></tr>
                </Table>
            </Section>

            <Section title="Deals">
                <Table headers={["Open Time", "Ticket", "Type", "Size", "Item", "Price", "Order", "Comment", "Entry", "Cost", "Commission", "Fee", "Swap", "Profit"]}>
                    <tr><td colSpan="14" className="no-transactions">No transactions</td></tr>
                </Table>
                <SummaryRow label="Closed P/L:" value="0.00" />
                <SummaryRow label="Deposit/Withdrawal:" value="0.00" />
                <SummaryRow label="Credit Facility:" value="0.00" />
                <SummaryRow label="Round Commission:" value="0.00" />
                <SummaryRow label="Instant Commission:" value="0.00" />
                <SummaryRow label="Fee:" value="0.00" />
                <SummaryRow label="Additional Operations:" value="0.00" />
                <SummaryRow label="Total:" value="0.00" />
            </Section>

            <Section title="Positions">
                <Table headers={["Open Time", "Ticket", "Type", "Size", "Item", "Price", "S / L", "T / P", "Market Price", "Swap", "Profit"]}>
                    <tr><td colSpan="11" className="no-transactions">No transactions</td></tr>
                </Table>
                <SummaryRow label="Floating P/L:" value="0.00" />
            </Section>

            <Section title="Working Orders">
                <Table headers={["Open Time", "Ticket", "Type", "Size", "Item", "Price", "S / L", "T / P", "Market Price", "Comment"]}>
                    <tr><td colSpan="10" className="no-transactions">No transactions</td></tr>
                </Table>
            </Section>

            <Section title="A/C Summary">
                <AccountSummaryTable />
            </Section>

            <Notice />
            <ContactInfo />
        </div>
    );
};

const Section = ({ title, children }) => (
    <div className="section">
        <h3>{title}</h3>
        {children}
    </div>
);

const Table = ({ headers, children }) => (
    <table>
        <thead>
            <tr>
                {headers.map(header => <th key={header}>{header}</th>)}
            </tr>
        </thead>
        <tbody>
            {children}
        </tbody>
    </table>
);

const SummaryRow = ({ label, value }) => (
    <div className="summary-row">
        <span>{label}</span>
        <span>{value}</span>
    </div>
);

const AccountSummaryTable = () => (
    <div className="account-summary-table">
        <div><strong>Closed Trade P/L:</strong> 0.00</div>
        <div><strong>Previous Ledger Balance:</strong> 0.00</div>
        <div><strong>Deposit/Withdrawal:</strong> 0.00</div>
        <div><strong>Previous Equity:</strong> 0.00</div>
        <div><strong>Total Credit Facility:</strong> 0.00</div>
        <div><strong>Balance:</strong> 0.00</div>
        <div><strong>Round Commission:</strong> 0.00</div>
        <div><strong>Equity:</strong> 0.00</div>
        <div><strong>Instant Commission:</strong> 0.00</div>
        <div><strong>Floating P/L:</strong> 0.00</div>
        <div><strong>Additional Operations:</strong> 0.00</div>
        <div><strong>Margin Requirements:</strong> 0.00</div>
        <div><strong>Fee:</strong> 0.00</div>
        <div><strong>Available Margin:</strong> 0.00</div>
        <div><strong>Total:</strong> 0.00</div>
    </div>
);

const Notice = () => (
    <p className="notice">
        Please report any discrepancies within 24 hours. If no report is received, this statement is considered confirmed.
    </p>
);

const ContactInfo = () => (
    <div className="contact-info">
        <p>FOREX.com is CFTC Registered FCM/RFED & a member of the National Futures Association (NFA ID# 0339826).</p>
        <p>Transaction Data Reports as per NFA Compliance Rule 2-36 are available upon request.</p>
        <p><strong>Support Contact:</strong></p>
        <p>Phone: 1-877-367-3946 (Toll-Free) | +1-908-731-0730 (International)</p>
        <p>Email: <a href="mailto:metatrader@tradeadviser.org">metatrader@forex.com</a></p>
        <p>Address: 30 Independence Blvd, Suite 300 (3rd floor), Warren, NJ 07059, USA</p>
    </div>
);

export default AccountSummary;
