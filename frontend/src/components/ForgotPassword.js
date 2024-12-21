import React, { useState, useEffect, useRef } from "react";
import { Link, useLocation } from "react-router-dom";
import {axiosPublic} from "../api/axios.js";

const ForgotPassword = () => {
  const [email, setEmail] = useState("");
  const [success, setSuccess] = useState(false);
  const [errMsg, setErrMsg] = useState("");
  const [loading, setLoading] = useState(false);
  const emailRef = useRef(null);

  const location = useLocation();
  const from = location.state?.from?.pathname || "/";

  useEffect(() => {
    emailRef.current?.focus();

  }, [email]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setSuccess(false);
    setErrMsg("");

try {


    const res = await axiosPublic.post("/auth/forgot-password", {email})
    if (res.status === 200) {
        setSuccess(true);

    } else {
        setErrMsg(res.data)
    }

}
    catch (error) {
      if(error.status===404){
          setErrMsg("Email not found");
      }
      else{
          setErrMsg(error.message|| error);
      }
    } finally {
        setLoading(false);
    }





  };

  const isValidEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);

  return (success?(<>
          <p className="success-message">Check your email for further instructions.</p>
          <Link to={from} className="back-link">
            Go Back
          </Link>
      </>):(
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
                name="email"
                ref={emailRef}

                value={email}
                onChange={(e) => {e.preventDefault();setEmail(e.target.value)}}
                required
                aria-invalid={!isValidEmail}
                placeholder="Enter your email address"
                autoComplete="true"
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
      </section>)
  );
};

export default ForgotPassword;
