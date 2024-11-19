import  React from 'react';

import { Link, useNavigate, useLocation } from "react-router-dom";
import { useRef, useState, useEffect } from "react";
import {axiosPrivate} from "../api/axios.js";

const ForgotPassword = () => {
  const [email, setEmail] = useState("");
  const [success, setSuccess] = useState(false);
  const [errMsg, setErrMsg] = useState("");
  const [loading, setLoading] = useState(false);
  const [sent, setSent] = useState(false);
  const [rememberMe] = useState(false);
  const emailRef = useRef(null);
  const navigate = useNavigate();
  const location = useLocation();
  const from = location.state?.from?.pathname || "/";
  useEffect(() => {
    emailRef.current.focus();
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setSent(false);
    setSuccess(false);
    setErrMsg("");

    try {
      const response = await axiosPrivate.post("/v3/forgot-password", {
        "email":email
      });

      if(response.status === 200) {
        setSuccess(true);
        setLoading(false);
        setSent(true);
        localStorage.setItem("resetEmail", email);
        localStorage.setItem("resetToken", response.data.token);
        localStorage.setItem("resetExpires", response.data.expires);
            rememberMe?
            localStorage.setItem("rememberMe", "true") :
            localStorage.removeItem("rememberMe");
        setEmail("");
        navigate(`/login?reset=true&from=${from}`);
      }else {
        setLoading(false);
        if (response.status === 401){
          setErrMsg("Invalid email address");
        }
        else if (response.status === 400){
          setErrMsg("Missing email address");
        }
        else if (response.status === 403){
          setErrMsg("Access Denied: Not authorized");
        }
        else{
          setErrMsg("An error occurred while sending the request");
        }

      }
    } catch (error) {
      setLoading(false);
      setErrMsg(error.toString());
    }
  };

  const pattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

  return (
    <section >
      <h1>Forgot Password</h1>
      <p>Enter your email address to reset your password.</p>

      <form >
        <input
          type="email"
          id="email"
          ref={emailRef}
          required
          placeholder="Enter your email address"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          aria-invalid={!!errMsg}
          aria-describedby={errMsg ? "errmsg" : "offscreen"}
          onFocus={() => setErrMsg("")}
          autoComplete="off"
          autoFocus
          spellCheck="false"
          pattern={pattern.source}
          title="Please enter a valid email address."
        />
        {errMsg && (
          <p id="errmsg" className="error">
            {errMsg}
          </p>
        )}
        {success && sent && <p>Check your email for further instructions.</p>}

        <button type="submit" disabled={loading}
                onSubmit={(e)=>handleSubmit(e)}
        >
          {loading ? "Loading..." : "Send Password Reset"}
          {loading && <span className="spinner">
            <svg className="spinner-icon" width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="8" cy="8" r="3" fill="white" />
              <circle cx="8" cy="8" r="3" fill="var(--spinner-color)" />
            </svg>+
          </span>}
        </button>

        <div className={'footer'}>
          <Link to={from}>Back</Link>
        </div>
      </form>
    </section>
  );
};

export default ForgotPassword;
