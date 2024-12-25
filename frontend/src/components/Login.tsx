import React, { useEffect, useRef, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import useAuth from "../hooks/useAuth";
import { axiosPublic } from "../api/axios";

import {Button, CircularProgress} from "@mui/material";
import { GitHub, Google } from "@mui/icons-material";
import "./Login.css";

function Login() {
    const { setAuth } = useAuth();
    const navigate = useNavigate();

    const errRef = useRef();

    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [errMsg, setErrMsg] = useState("");
    const [loading, setLoading] = useState(false);
    const [rememberMe, setRememberMe] = useState(false);

    useEffect(() => {
        setErrMsg("");
    }, [username, password]);

    // Check persist state
    useEffect(() => {
        if (rememberMe) localStorage.setItem("persist", "true");
    }, [rememberMe]);

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        setLoading(true);

        await axiosPublic
            .post("/api/v3/login", JSON.stringify({ username, password, rememberMe: rememberMe || false }))
            .then((response) => {
                const { username, role, accessToken } = response.data;
                setAuth({ username, role, accessToken });
                navigate("/");
            })
            .catch((error) => {
                setLoading(false);
                setErrMsg(error.response?.statusText);
            });
    };

    const googleLogin = (event: React.MouseEvent<HTMLButtonElement>) => {
        // const handleGoogleLoginSuccess = (response: any) => {
        //     const { username, role, accessToken } = response.data;
        //     setAuth({ username, role, accessToken });
        //     navigate("/");
        // };
        //
        // const handleGoogleLoginFailure = (error: any) => {
        //     console.error("Google login failed:", error);
        // };

        const CLIENT_ID = process.env.REACT_APP_GOOGLE_CLIENT_ID;
        if (!CLIENT_ID) {
            console.error("Google client ID is missing in environment variables.");
            return;
        }

        const REDIRECT_URI = process.env.REACT_APP_GOOGLE_CLIENT_REDIRECT_URI;
        const SCOPE = "profile email";
        const responseType = "code";
        const flowName = window.location.pathname.includes("/admin") ? "admin" : "user";

        window.location.href = `https://accounts.google.com/o/oauth2/v2/auth?client_id=${CLIENT_ID}&redirect_uri=${REDIRECT_URI}&response_type=${responseType}&scope=${SCOPE}&prompt=select_account&access_type=offline&login_hint=${username}&flow=${flowName}`;
        event.preventDefault();
    };

    function githubLogin(event: React.FormEvent<HTMLFormElement>) {
        event.preventDefault();

        const CLIENT_ID = process.env.REACT_APP_GITHUB_CLIENT_ID;
        const REDIRECT_URI = process.env.REACT_APP_GITHUB_CLIENT_REDIRECT_URI || "http://localhost:3000/api/v3/auth/github/callback";

        window.location.href = `https://github.com/login/oauth/authorize?client_id=${CLIENT_ID}&redirect_uri=${REDIRECT_URI}&scope=read:user`;
    }

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
                                aria-label="Username or Email"
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
                                aria-label="Password"
                            />
                        </div>
                        <div className="form-group remember-me">
                            <input
                                id="rememberMe"
                                name="rememberMe"
                                type="checkbox"
                                checked={rememberMe}
                                onChange={(e) => setRememberMe(e.target.checked)}
                                aria-label="Remember Me"
                            />
                            <label htmlFor="rememberMe">Remember Me</label>
                        </div>
                        {errMsg && (
                            <p ref={errRef} className="error-message" role="alert">
                                {errMsg}
                            </p>
                        )}
                        <Button
                            type="submit"
                            disabled={loading}
                            variant="contained"
                            startIcon={loading ? <CircularProgress size={20} /> : null}
                        >{loading ? "Logging in..." : "Login"}</Button>
                        <p>
                            New to our platform? <Link to="/register">Create an account</Link>
                        </p>
                      <div className={"container"}>
                        <div className="social-media-buttons">
                            <Button

                                startIcon={<Google />}
                                onClick={googleLogin} // Fixed event handler
                                variant="outlined"
                                aria-label="Login with Google"
                            >
                                Login with Google
                            </Button>
                        </div>
                        <div className={'social-media-button'}>

                            <Button
                                type="button"
                                className={"github-login"}
                                startIcon={<GitHub />}
                                onClick={githubLogin}
                                variant="outlined"
                                aria-label="Login with GitHub"
                            >
                                Login with GitHub
                            </Button>
                            </div>
                      </div>

                    </form>
                    <div className="links">
                        <Link to="/forgotpassword">Forgot Password?</Link>
                        <p>
                            Don’t have an account? <Link to="/register">Register</Link>
                        </p>
                    </div>
                </div>
            </div>
        </section>
    );
}

export default Login;
