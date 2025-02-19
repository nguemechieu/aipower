import React from "react";

const InvestmentList = ({ investments }) =>
    (
        <div>
          <h3>Investment Portfolio</h3>
          <table style={{width: "100%", marginTop: "10px"}
          }>
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
            {investments.map((investment: {
              name: string | number | boolean | React.ReactElement<never, string | React.JSXElementConstructor<any>> | Iterable<React.ReactNode> | React.ReactPortal | null | undefined;
              type: string | number | boolean | React.ReactElement<never, string | React.JSXElementConstructor<any>> | Iterable<React.ReactNode> | React.ReactPortal | null | undefined;
              amountInvested: string | number | boolean | React.ReactElement<never, string | React.JSXElementConstructor<any>> | Iterable<React.ReactNode> | React.ReactPortal | null | undefined;
              currentValue: string | number | boolean | React.ReactElement<never, string | React.JSXElementConstructor<any>> | Iterable<React.ReactNode> | React.ReactPortal | null | undefined;
              roi: string | number | boolean | React.ReactElement<never, string | React.JSXElementConstructor<any>> | Iterable<React.ReactNode> | React.ReactPortal | null | undefined;
            }, index: React.Key | null | undefined) => (
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

export default InvestmentList;
