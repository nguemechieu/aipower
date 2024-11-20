import React, { useState } from "react";
import InvestmentList from "./InvestmentList";
import PerformanceChart from "./PerformanceChart";
import AddInvestmentForm from "./AddInvestmentForm";

const InvestmentPage = () => {
    const [investments, setInvestments] = useState([
        { name: "AAPL", type: "Stock", amountInvested: 1000, currentValue: 1500, roi: 50 },
        { name: "Real Estate", type: "Property", amountInvested: 20000, currentValue: 25000, roi: 25 },
    ]);

    const [performanceData, setPerformanceData] = useState([
        { month: "Jan", value: 20000 },
        { month: "Feb", value: 21000 },
        { month: "Mar", value: 22000 },
        { month: "Apr", value: 23000 },
    ]);

    const addInvestment = (newInvestment) => {
        setInvestments([...investments, newInvestment]);
    };

    return (
        <div style={{ padding: "20px" }}>
            <h2>Investment Dashboard</h2>
            <div style={{ display: "flex", gap: "20px" }}>
                <InvestmentList investments={investments} />
                <PerformanceChart data={performanceData} />
            </div>
            <AddInvestmentForm onAddInvestment={addInvestment} />
        </div>
    );
};

export default InvestmentPage;
