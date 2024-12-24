import React, { useState } from "react";

const AddInvestmentForm = ({ onAddInvestment }) => {
  const [investment, setInvestment] = useState({
    name: "",
    type: "",
    amountInvested: "",
    currentValue: "",
  });

  const handleChange = (e) => {
    setInvestment({ ...investment, [e.target.name]: e.target.value });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    onAddInvestment({
      ...investment,
      roi:
        ((investment.currentValue - investment.amountInvested) /
          investment.amountInvested) *
        100,
    });
    setInvestment({ name: "", type: "", amountInvested: "", currentValue: "" });
  };

  return (
    <form onSubmit={handleSubmit} style={{ marginTop: "20px" }}>
      <h3>Add New Investment</h3>
      <input
        type="text"
        name="name"
        placeholder="Name"
        value={investment.name}
        onChange={handleChange}
        required
      />
      <input
        type="text"
        name="type"
        placeholder="Type (e.g., Stock, Real Estate)"
        value={investment.type}
        onChange={handleChange}
        required
      />
      <input
        type="number"
        name="amountInvested"
        placeholder="Amount Invested"
        value={investment.amountInvested}
        onChange={handleChange}
        required
      />
      <input
        type="number"
        name="currentValue"
        placeholder="Current Value"
        value={investment.currentValue}
        onChange={handleChange}
        required
      />
      <button type="submit">Add Investment</button>
    </form>
  );
};

export default AddInvestmentForm;
