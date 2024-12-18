import React, {useEffect, useRef, useState} from "react";
import {Link, useLocation, useNavigate} from "react-router-dom";
import useAuth from "../hooks/useAuth.js";
import axios from "../api/axios.js";
import usePersist from "../hooks/usePersist";
import {Button} from "@mui/material";
import {GitHub, Google} from "@mui/icons-material";

const LOGIN_URL = "/api/v3/auth/login";
import "./Login.css"

const Login = () => {
    const { auth } = useAuth();
    const navigate = useNavigate();
    const location = useLocation();
    const from = location.state?.from?.pathname || "/";

    const errRef = useRef();
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [errMsg, setErrMsg] = useState("");
    const [loading, setLoading] = useState(false);
    const [rememberMe, setRememberMe] = useState(false);
    const [persist] = usePersist();

    useEffect(() => {
        setErrMsg("");
    }, [username, password, persist]);

    // Check persist state
    useEffect(() => {
        if (persist) {
            localStorage.setItem("persist", "true");
        } else {
            localStorage.removeItem("persist");
        }
    }, [persist]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setErrMsg("");
        try {
            const timestamp = Date.now();
            const response = await axios.post(
                LOGIN_URL,
                JSON.stringify({ username, password,rememberMe,timestamp }),
                { headers: { "Content-Type": "application/json",
                        "Accept": "application/json"
                    } ,
                    withCredentials: true
                }
            );

            if (response.status === 200) {
                const { username, role, accessToken } = response.data;

                // Update auth context
                auth({ username, role, accessToken });

                if (rememberMe) localStorage.setItem("persist", "true");

                // Reset form fields
                setUsername("");
                setPassword("");
                console.log("Logged in successfully!");

                // Navigate to the intended page
                navigate(from, { replace: true });
            }
        } catch (err) {
            if (err?.response?.data) {
                setErrMsg(JSON.stringify(err.response.data));
            } else {
                setErrMsg("Server unreachable! Please try again later.");
            }
            errRef.current?.focus();
        } finally {
            setLoading(false);
        }
    };

    const googleLogin = () => {
        const CLIENT_ID =
            "539426084783-ju5ppl2ofi85nk1bti3ic6hos6vrr62s.apps.googleusercontent.com";
        const REDIRECT_URI = "http://localhost:3000/api/v3/auth/google/callback";
        const SCOPE = "profile email";

        window.location.href = `https://accounts.google.com/o/oauth2/v2/auth?client_id=${CLIENT_ID}&redirect_uri=${REDIRECT_URI}&response_type=code&scope=${SCOPE}&access_type=offline`;
    };

    const githubLogin = () => {
        const CLIENT_ID = "Iv1.23779dd826d2df1f"; // Replace with your GitHub client ID
        const REDIRECT_URI = "http://localhost:3000/api/v3/auth/github/callback";

        window.location.href = `https://github.com/login/oauth/authorize?client_id=${CLIENT_ID}&redirect_uri=${REDIRECT_URI}&scope=read:user`;
    };

    return (
        <section>
            <div className="login-container">
                <div className="login-box">
                    <h2>Welcome Back</h2>
                    <p className="subtitle">Log in to access your account</p>
                    <form className="login-form" onSubmit={handleSubmit}>
                        <div className="form-group">
                            <label htmlFor="username">Username or Email</label>
                            <input
                                id="username"
                                type="text"
                                placeholder="Enter username or email"
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
                                type="checkbox"
                                checked={rememberMe}
                                onChange={(e) => setRememberMe(e.target.checked)}
                            />
                            <label htmlFor="rememberMe">Remember Me</label>
                        </div>
                        {errMsg && (
                            <p ref={errRef} className="error-message">
                                {errMsg}
                            </p>
                        )}
                        <Button type="submit" disabled={loading} variant="contained">
                            {loading ? "Logging in..." : "Login"}
                        </Button>

                        <div className="social-media-buttons">
                            <Button
                                startIcon={<Google />}
                                onClick={googleLogin}
                                variant="outlined"
                            >
                                Login with Google
                            </Button>
                            <Button
                                startIcon={<GitHub />}
                                onClick={githubLogin}
                                variant="outlined"
                            >
                                Login with GitHub
                            </Button>
                        </div>
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
        </section>
    );
};

export default Login;
