import React from "react";
import { Link } from "react-router-dom";

const Missing = () => {
  return (
    <div
      style={{
        display: "flex",
        flexDirection: "column",
        justifyContent: "center",
        alignItems: "center",
        height: "100vh",
        textAlign: "center",
        padding: "20px",
      }}
    >
      {/* Not Found Icon */}
      <img
        src="https://via.placeholder.com/300x200.png?text=Page+Not+Found" // Replace this URL with your desired image
        alt="Page Not Found"
        style={{ width: "300px", height: "auto", marginBottom: "1.5rem" }}
      />

      {/* Text Content */}
      <h1 style={{ fontSize: "3rem", marginBottom: "1rem", color: "#ff6f61" }}>
        Oops!
      </h1>
      <p style={{ fontSize: "1.5rem", marginBottom: "1.5rem" }}>
        We can&#39;t find the page you&#39;re looking for.
      </p>

      {/* Homepage Link */}
      <Link
        to="/"
        style={{
          display: "inline-block",
          padding: "0.8rem 1.5rem",
          fontSize: "1rem",
          fontWeight: "bold",
          color: "#fff",
          backgroundColor: "#007bff",
          borderRadius: "5px",
          textDecoration: "none",
          transition: "background-color 0.3s ease",
        }}
        onMouseEnter={(e) => (e.target.style.backgroundColor = "#80a7d1")}
        onMouseLeave={(e) => (e.target.style.backgroundColor = "#28b612")}
      >
        Go to Homepage
      </Link>
    </div>
  );
};

export default Missing;
