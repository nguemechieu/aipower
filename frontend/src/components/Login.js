import React, { useEffect, useRef, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import useAuth from "../hooks/useAuth.js";
import { axiosPublic } from "../api/axios.js";

import { Button, CircularProgress } from "@mui/material";
import { GitHub, Google } from "@mui/icons-material";
import "./Login.css";

const Login = () => {
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

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setErrMsg("");

    await axiosPublic
      .post(
        "/api/v3/login",
        JSON.stringify(
          { username, password, rememberMe: rememberMe || false },
          {
            headers: {
              "Content-Type": "application/json",
            },
            timeout: 30000,
            withCredentials: true,
          },
        ),
      )
      .then((response) => {
        console.log(response.data);
        const { username, role, accessToken } = response.data;
        setAuth({ username, role, accessToken });
        localStorage.setItem("accessToken", accessToken);

        setUsername("");
        setPassword("");
        console.log("Logged in successfully!");
        navigate("/", { replace: true });
      })
      .catch((err) => {
        console.error(
          "Failed to log in. Please check your credentials and try again." +
            " " +
            JSON.stringify(err),
        );
        if (err.response) {
          switch (err.response.status) {
            case 401:
              setErrMsg("Invalid username or password.");
              break;
            case 403:
              setErrMsg("Access Denied: Not authorized.");
              break;
            case 500:
              setErrMsg("Internal server error. Please try again later.");
              break;
            default:
              setErrMsg(
                `Unexpected error: ${err.response.statusText || "Unknown"}`,
              );
          }
        } else {
          setErrMsg("Unable to connect to the server. Please try again later.");
        }
        setLoading(false);
      });
  };
  const googleLogin = () => {
    const CLIENT_ID = process.env.REACT_APP_GOOGLE_CLIENT_ID;

    // Check if CLIENT_ID is available
    if (!CLIENT_ID) {
      console.error("Google client ID is missing in environment variables.");
      return; // Or handle accordingly
    }

    const REDIRECT_URI = "http://localhost:3000/api/v3/auth/google/callback";

    // Check if CLIENT_ID is available
    const SCOPE = "profile email";

    const responseType = "code";
    const flowName = window.location.pathname.includes("/admin")
      ? "admin"
      : "user";

    window.location.href = `https://accounts.google.com/o/oauth2/v2/auth?client_id=${CLIENT_ID}&redirect_uri=${REDIRECT_URI}&response_type=${responseType}&scope=${SCOPE}&prompt=select_account&access_type=offline&login_hint=${username}&flow=${flowName}`;
  };

  const githubLogin = () => {
    const CLIENT_ID = process.env.REACT_APP_GITHUB_CLIENT_ID;
    const REDIRECT_URI =
      process.env.REACT_APP_GITHUB_REDIRECT_URI ||
      "http://localhost:3000/api/v3/auth/github/callback";
    // Check if CLIENT_ID is available
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
            <Button
              type="submit"
              disabled={loading}
              variant="contained"
              startIcon={loading ? <CircularProgress size={20} /> : null}
            >
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
      </div>
    </section>
  );
};

export default Login;
