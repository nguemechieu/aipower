import React, {useState, useEffect, useRef} from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import useAuth from "../hooks/useAuth.js";
import axios from "../api/axios.js";

import usePersist from "../hooks/usePersist"; // Import the CSS file for styling

const LOGIN_URL = "/login";
import "../components/Login.css"
const Login = () => {
    const  auth  = useAuth();
    const navigate = useNavigate();
    const location = useLocation();
    const from = location.state?.from?.pathname || "/";
    const  errRef=useRef()
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [errMsg, setErrMsg] = useState("");
    const [loading, setLoading] = useState(false);
    const [rememberMe, setRememberMe] = useState(false);
    const [persist, setPersist] = usePersist();

    // Clear error message on input change
    useEffect(() => {
        setErrMsg("");
    }, [username, password,persist]);
useEffect(
    () => {
        if (persist) {
            localStorage.getItem("persist");


        }
    },
    [persist,setPersist]
)
    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        try {
            const timestamp = Date.now();
            const response = await axios.post(LOGIN_URL, JSON.stringify({ username, password ,timestamp })


            );


            if (response.status === 200) {
                const {  id,id2 ,accessToken, refreshToken,  } = response.data;

                // Update authentication context


                auth({ id, id2,  accessToken, refreshToken });


                // Store token based on "Remember Me" option
                if (rememberMe) localStorage.setItem("persist", true);

                // Reset form fields
                setUsername("");
                setPassword("");

                console.log(
                   auth+ "Logged in successfully! Redirecting to: ", from
                )

                // Navigate to the intended page;
                navigate(from,
                    { replace: true },
                    "/"
                    );
            }else{

                setErrMsg(response?.data|| "Login failed!");
                errRef.current?.focus();
                setTimeout(
                    () => {

                        setErrMsg("");},5000);
            }
        } catch (err) {

            if (err?.response ) {
                setErrMsg( JSON.stringify(err?.response?.data));
            } else {
                setErrMsg("Server unreachable! Please try again later.");
            }
            errRef.current?.focus();
            console.log(err.response); // Debug the error response structure
        } finally {
            setLoading(false);
        }

    };


    return (
        <div className="login-container">


            <div className="login-box" >
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
