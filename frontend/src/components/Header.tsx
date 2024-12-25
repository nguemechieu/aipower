import React, { useEffect, useState } from "react";
import { NavLink } from "react-router-dom";
import useAuth from "../hooks/useAuth";
import useLogout from "../hooks/useLogout";
import "./Header.css";

const Header = () => {
  const { username, accessToken } = useAuth();
  const { logout } = useLogout(); // Custom hook to manage authentication
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  useEffect(() => {
    setIsAuthenticated(Boolean(accessToken));
  }, [accessToken]);

  return (
      <header>
        <h1>AI Power</h1>
        {isAuthenticated ? (
            <>
              <nav>
                <ul>
                  <li>
                    <NavLink to="/dashboard">Dashboard</NavLink>
                  </li>
                  <li>
                    <NavLink to="/about">About</NavLink>
                  </li>
                  <li>
                    <NavLink to="/users">User Management</NavLink>
                  </li>
                  <li>
                    <button onClick={logout}>Logout</button>
                  </li>
                </ul>
              </nav>
              <div className="user-info">
                {username && <p>Welcome, {username}</p>}
              </div>
            </>
        ) : (
            <div className="row">
              <div className="col-md-8">
                <nav>
                  <ul>
                    <li>
                      <NavLink to="/login">Login</NavLink>
                    </li>
                    <li>
                      <NavLink to="/register">Register</NavLink>
                    </li>
                    <li>
                      <NavLink to="/forgotpassword">Forgot Password</NavLink>
                    </li>
                    <li>
                      <NavLink to="/terms-of-service">Terms of Service</NavLink>
                    </li>
                    <li>
                      <NavLink to="/help">Help</NavLink>
                    </li>
                    <li>
                      <NavLink to="/about">About</NavLink>
                    </li>
                  </ul>
                </nav>
              </div>
            </div>
        )}
      </header>
  );
};

export default Header;
