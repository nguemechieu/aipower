import React, {useEffect, useRef, useState} from "react";
import {Link, useNavigate} from "react-router-dom";
import useAuth from "../hooks/useAuth";
import {axiosPublic} from "../api/axios";

import {Button, CircularProgress} from "@mui/material";
import {GitHub, Google} from "@mui/icons-material";
import "./Login.css";

const Login = () => {
  const { setAuth } = useAuth(); // Custom hook to manage authentication
  const navigate = useNavigate();

  const errRef = useRef<HTMLParagraphElement>(null); // Error message reference for accessibility

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [errMsg, setErrMsg] = useState("");
  const [loading, setLoading] = useState(false);
  const [rememberMe, setRememberMe] = useState(false);

  useEffect(() => {
    setErrMsg(""); // Clear error messages on input change
  }, [username, password]);

  useEffect(() => {
    if (rememberMe) localStorage.setItem("persist", "true");
    else localStorage.removeItem("persist");
  }, [rememberMe]);

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setLoading(true);

    try {
      const response = await axiosPublic.post("/api/v3/login", {
        username,
        password,
        rememberMe,
      },
          {
            headers: {
              "Content-Type": "application/json",
              "Accept": "application/json",
              "Access-Control-Allow-Origin": "*",
              "Access-Control-Allow-Methods": "GET, POST",
              "Access-Control-Allow-Headers": "Content-Type, Authorization",
            },
          }







          );

      const { username: user, role, accessToken } = response.data;
      setAuth({ username: user, role, accessToken });
      navigate("/", { replace: true });
    } catch (error) {
      setLoading(false);
      setErrMsg(
          error.response?.data?.message ||
          error.response?.statusText ||
          "Login failed. Please try again."
      );
    }
  };

  const googleLogin = () => {
    const CLIENT_ID =
        process.env.REACT_APP_GOOGLE_CLIENT_ID ||
        "539426084783-ju5ppl2ofi85nk1bti3ic6hos6vrr62s.apps.googleusercontent.com";

    const REDIRECT_URI = `${window.location.origin}/api/v3/auth/google/callback`;
    const SCOPE = "profile email";

    if (!CLIENT_ID) {
      console.error("Google client ID is missing in environment variables.");
      setErrMsg("Google login is not configured. Please contact support.");
      return;
    }

    window.location.href = `https://accounts.google.com/o/oauth2/v2/auth?client_id=${CLIENT_ID}&redirect_uri=${REDIRECT_URI}&response_type=code&scope=${SCOPE}&prompt=select_account&access_type=offline`;
  };

  const githubLogin = () => {
    const CLIENT_ID =
        process.env.REACT_APP_GITHUB_CLIENT_ID || "906b7888f82f9f1301b7";

    const REDIRECT_URI = `${window.location.origin}/api/v3/auth/github/callback`;

    if (!CLIENT_ID) {
      console.error("GitHub configuration is incomplete.");
      setErrMsg("GitHub login is not configured. Please contact support.");
      return;
    }

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
                  <p
                      ref={errRef}
                      className="error-message"
                      role="alert"
                      aria-live="assertive"
                  >
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
            </form>
            <p>
              New to our platform? <Link to="/register">Create an account</Link>
            </p>
            <div className="social-media-buttons">
              <Button
                  startIcon={<Google />}
                  onClick={googleLogin}
                  variant="outlined"
                  aria-label="Login with Google"
              >
                Login with Google
              </Button>
              <Button
                  startIcon={<GitHub />}
                  onClick={githubLogin}
                  variant="outlined"
                  aria-label="Login with GitHub"
              >
                Login with GitHub
              </Button>
            </div>
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
};

export default Login;
