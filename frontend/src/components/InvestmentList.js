import React from "react";

const InvestmentList = ({ investments }) => {
    return (
        <div>
            <h3>Investment Portfolio</h3>
            <table border="1" style={{ width: "100%", marginTop: "10px" }}>
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Type</th>
                    <th>Amount Invested</th>
                    <th>Current Value</th>
                    <th>ROI (%)</th>
                </tr>
                </thead>
                <tbody>
                {investments.map((investment, index) => (
                    <tr key={index}>
                        <td>{investment.name}</td>
                        <td>{investment.type}</td>
                        <td>${investment.amountInvested}</td>
                        <td>${investment.currentValue}</td>
                        <td>{investment.roi}%</td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
};

export default InvestmentList;
