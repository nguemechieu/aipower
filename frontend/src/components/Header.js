import React from 'react';
import { Link } from 'react-router-dom';
import './Header.css'; // Optional: Add a CSS file for styling

const Header = () => {
    return (
        <header className="header">
            <div className="logo">
                <Link to="/">AiPower</Link>
            </div>
            <nav className="navigation">
                <ul>
                    <li>
                        <Link to="/dashboard">Dashboard</Link>
                    </li>
                    <li>
                        <Link to="/market">Market</Link>
                    </li>
                    <li>
                        <Link to="/profile">Profile</Link>
                    </li>
                    <li>
                        <Link to="/news">News</Link>
                    </li>
                    <li>
                        <Link to="/investment">Investment</Link>
                    </li>

                    <li>
                        <Link to="/contact">Contact</Link>
                    </li>


                    <li>
                        <Link to="/" className="login-button">
                            Login
                        </Link>
                    </li>
                </ul>
            </nav>
        </header>
    );
};

export default Header;
