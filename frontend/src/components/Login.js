import React from 'react';

import { useEffect, useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import useAuth from "../hooks/useAuth.js";
import axios from "../api/axios.js";

const LOGIN_URL = "/api/v3/auth/login";

const Login = () => {
    const { setAuth } = useAuth();
    const navigate = useNavigate();
    const location = useLocation();
    const from = location.state?.from?.pathname || "/";

    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [errMsg, setErrMsg] = useState("");
    const [loading, setLoading] = useState(false);
    const [rememberMe, setRememberMe] = useState(false);

    useEffect(() => {
        const token = localStorage.getItem("token") || sessionStorage.getItem("token");
        if (token) {
            setAuth({ username, token });
            navigate(from, { replace: true });
        }
    }, []); // Run on mount only

    useEffect(() => {
        setErrMsg(""); // Clear error message on input change
    }, [username, password]);

    const handleLoginError = (err) => {
        const status = err?.response?.status;
        const messages = {
            400: "Missing Username or Password",
            401: "Unauthorized - Check your credentials",
            403: "Access Denied: Not authorized",
            404: "User not found",
            422: "Invalid username or password",
            500: "Internal Server Error!\nPlease try again later",
            503: "Service Unavailable - Please try again later"
        };
        setErrMsg(
            messages[status]
        );
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);

        try {
            const response = await axios.post(LOGIN_URL, { username, password, rememberMe });

            if (response.status === 200) {
                const { token, role } = response.data;
                setAuth({ username, role, rememberMe,token });

                rememberMe
                    ? localStorage.setItem("token", token)
                    : sessionStorage.setItem("token", token);

                setUsername("");
                setPassword("");
                navigate(from, { replace: true });
            }
        } catch (err) {
            handleLoginError(err);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="login-container">
            <h2>Login</h2>
            <form className="login-form" onSubmit={handleSubmit}>
                <div className="form-group">

                    <input
                        id="username"
                        type="text"
                        placeholder="Enter username"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        required
                    />
                </div>
                <div className="form-group">

                    <input
                        id="password"
                         placeholder="Enter password"
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                </div>
                <div className="form-group remember-me">
                    <input
                        id="remember-me"
                        type="checkbox"
                        checked={rememberMe}
                        onChange={(e) => setRememberMe(e.target.checked)}
                    />
                    <label htmlFor="remember-me">Remember Me</label>
                </div>
                {errMsg && <p className="error-message">{errMsg}</p>}
                <button type="submit" disabled={loading}>
                    {loading ? "Logging in..." : "Login"}
                </button>
            </form>
            <div className="links">
                <Link to='/forgot-password'>Forgot Password?</Link>
                <p>
                    Don’t have an account? <Link to="/register">Register</Link>
                </p>
                <p>
                    By continuing, you agree to our{" "}
                    <Link to="/terms-of-service">Terms of Service</Link>.
                </p>
            </div>
        </div>
    );
};

export default Login;
