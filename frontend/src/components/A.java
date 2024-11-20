import React from "react";

const SearchBar = ({ onSearch }) => {
  return (
    <div style={{ marginBottom: "20px" }}>
      <input
        type="text"
        placeholder="Search friends..."
        onChange={(e) => onSearch(e.target.value)}
        style={{
          padding: "10px",
          width: "100%",
          border: "1px solid #ddd",
          borderRadius: "5px",
        }}
      />
    </div>
  );
};

export default SearchBar;
