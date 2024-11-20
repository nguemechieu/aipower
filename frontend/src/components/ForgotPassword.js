import React, { useState, useEffect, useRef } from "react";
import { Link, useNavigate, useLocation } from "react-router-dom";
import axios from "../api/axios.js";
import "./ForgotPassword.css"; // Include your CSS file for styling

const ForgotPassword = () => {
  const [email, setEmail] = useState("");
  const [success, setSuccess] = useState(false);
  const [errMsg, setErrMsg] = useState("");
  const [loading, setLoading] = useState(false);
  const emailRef = useRef(null);
  const navigate = useNavigate();
  const location = useLocation();
  const from = location.state?.from?.pathname || "/";

  useEffect(() => {
    emailRef.current?.focus();
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setSuccess(false);
    setErrMsg("");


       await axios.post("/api/v3/auth/forgot-password", {
        email,
      }).then(res=>{
        setLoading(false);
        setSuccess(true);
        localStorage.removeItem('accessToken');
        window.location.href = '/login';
        const  resetExpires=res.data.resetExpires;
        const  resetToken=res.data.resetToken;
        localStorage.setItem('resetToken',resetToken);
        localStorage.setItem('resetExpires',resetExpires);
        navigate(from, { replace: true }); // Redirect to the previous page after successful password reset



      }).catch(err => {

       if(err.status===(404)){
          setErrMsg("Email address not found");

       }
       else if(err.status===(400)){
          setErrMsg("Missing email address");
       }
       else if(err.status===(403)){
          setErrMsg("Access Denied: Not authorized");
       }
       else{
          setErrMsg("An error occurred while sending the request");
       }

       })

  };

  const isValidEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);

  return (
      <section className="forgot-password">
        <h2>Forgot Password</h2>
        <p className="instruction">
          Enter your email address to receive password reset instructions.
        </p>

        {errMsg && <p className="error-message">{errMsg}</p>}
        {success && <p className="success-message">Check your email for further instructions.</p>}

        <form onSubmit={handleSubmit} className="forgot-password-form">
          <div className="form-group">
            <label htmlFor="email">Email Address</label>
            <input
                type="email"
                id="email"
                ref={emailRef}
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
                aria-invalid={!isValidEmail}
                placeholder="Enter your email address"
                autoComplete="off"
                pattern="^[^\s@]+@[^\s@]+\.[^\s@]+$"
                title="Please enter a valid email address."
            />
          </div>

          <button type="submit" className="submit-button" disabled={loading || !isValidEmail}>
            {loading ? "Sending..." : "Send Password Reset"}
            {loading && <span className="spinner"></span>}
          </button>
        </form>

        <Link to={from} className="back-link">
          Go Back
        </Link>
      </section>
  );
};

export default ForgotPassword;
