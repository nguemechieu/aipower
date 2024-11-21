import React, { useState, useEffect } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import useAuth from "../hooks/useAuth.js";
import axios from "../api/axios.js";
import "./Login.css"; // Import the CSS file for styling

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
        setErrMsg(""); // Clear error message on input change
    }, [username, password,rememberMe]);
useEffect(() => {
    setUsername(username);
    setPassword(password);
},[username, password]);
    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);

        try {
            const response = await axios.post(
                LOGIN_URL,
              {username,password,
                  rememberMe

                }
            );

            if (response.status === 200) {
                const { accessToken,id, refreshToken, role } = response.data;
                setAuth({ id,role, rememberMe, accessToken, refreshToken });

                if (rememberMe) {
                    localStorage.setItem("accessToken", accessToken);
                } else {
                    sessionStorage.setItem("accessToken", accessToken);
                }

                setUsername("");
                setPassword("");
                navigate(from, { replace: true });
            } else {
                setErrMsg(response.data.message || "Login failed.");
            }
        } catch (err) {
           if(err.status ===404){
               setErrMsg("User not found. Please check your credentials.");
           }
           else if(err.status ===400){
               setErrMsg("Missing username or password");
           }
           else if(err.status ===401){
               setErrMsg("Invalid username or password");
           }
           else if(err.status ===500){
               setErrMsg("Server Error, please try again later");
           }
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="login-container">
            <div className="login-box">
                <h2>Welcome Back</h2>
                <p className="subtitle">Log in to access your account</p>
                <form className="login-form" onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label htmlFor="username">Username</label>
                        <input
                            id="username"
                            type="text"
                            placeholder="Enter username Or Email Address"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            required
                        />
                    </div>
                    <div className="form-group">
                        <label htmlFor="password">Password</label>
                        <input
                            id="password"
                            type="password"
                            placeholder="Enter password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                        />
                    </div>
                    <div className="form-group remember-me">
                        <input
                            id="rememberMe"
                         name="rememberMe"
                         value={rememberMe}

                            type="checkbox"
                            checked={rememberMe}
                            onChange={(e) => setRememberMe(e.target.checked)}
                        />
                        <label htmlFor="rememberMe">Remember Me</label>
                    </div>
                    {errMsg && <p className="error-message">{errMsg}</p>}
                    <button type="submit" disabled={loading}>
                        {loading ? "Logging in..." : "Login"}
                    </button>
                </form>
                <div className="links">
                    <Link to="/forgot-password">Forgot Password?</Link>
                    <p>
                        Don’t have an account? <Link to="/register">Register</Link>
                    </p>

                </div>
            </div>
            <p>
                By continuing, you agree to our{" "}
                <Link to="/terms-of-service">Terms of Service</Link>.
            </p>
        </div>
    );
};

export default Login;
