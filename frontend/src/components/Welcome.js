import React from 'react';
import { Link } from 'react-router-dom'; // Import Link for navigation

export default class Welcome extends React.Component {
    render() {
        return (
            <main style={{ textAlign: "center", padding: "2rem" }}>
                <h1>Welcome to AI Power</h1>
                <p>Your ultimate platform for advanced AI-powered solutions.</p>
                <p>
                    <Link to="/about" style={{
                        display: "inline-block",
                        margin: "0 10px",
                        textDecoration: "none"
                    }}>
                        Learn More
                    </Link>
                    &bull;
                    <Link to="/register" style={{
                        display: "inline-block",
                        margin: "0 10px",
                        textDecoration: "none"
                    }}>
                        Sign Up
                    </Link>
                    &bull;
                    <Link to="/forgot-password" style={{
                        display: "inline-block",
                        margin: "0 10px",
                        textDecoration: "none"
                    }}>
                        Forgot Password?


                <div style={{ marginTop: "2rem" }}>
                    <Link to="/login" style={{
                        display: "inline-block",
                        padding: "10px 20px",
                        fontSize: "16px",
                        color: "#fff",
                        backgroundColor: "#007BFF",
                        border: "none",
                        borderRadius: "5px",
                        textDecoration: "none",
                        cursor: "pointer"
                    }}>
                        Go to Login
                    </Link>
                    <p>Don't have an account? <Link to="/register">Sign Up</Link></p>
                </div>
                    </Link></p>

            </main>
        );
    }
}
