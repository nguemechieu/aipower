import React, { useEffect, useState } from "react";
import { Link, useLocation } from "react-router-dom";
import { axiosPublic } from "../api/axios";

const ForgotPassword: React.FC = () => {
  const [email, setEmail] = useState<string>("");
  const [success, setSuccess] = useState<boolean>(false);
  const [errMsg, setErrMsg] = useState<string>("");
  const [loading, setLoading] = useState<boolean>(false);

  const location = useLocation();
  const from = location.state?.from?.pathname || "/";

  const isValidEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);

  useEffect(() => {
    if (isValidEmail) {
      setErrMsg("");
    } else if (email) {
      setErrMsg("Invalid email address");
    }
  }, [email, isValidEmail]);

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setLoading(true);
    setSuccess(false);
    setErrMsg("");

    await axiosPublic
      .post("/api/v3/forgotpassword", JSON.stringify({ email }), {
        headers: { "Content-Type": "application/json" },
      })
      .then((response) => {
        setErrMsg(response.data);
        setSuccess(true);
        setLoading(false);
      })
      .catch((error) => {
        setErrMsg(error.response?.data?.message || "An error occurred");
        setLoading(false);
      });
  };

  return (
    <section className="forgot-password">
      <h2>Forgot Password</h2>
      <p className="instruction">
        Enter your email address to receive password reset instructions.
      </p>

      {errMsg && <p className="error-message">{errMsg}</p>}
      {success && (
        <p className="success-message">
          Check your email for further instructions.
        </p>
      )}

      {!success && (
        <form
          onSubmit={(e) => handleSubmit(e)}
          className="forgot-password-form"
        >
          <div className="form-group">
            <label htmlFor="email">Email Address</label>
            <input
              type="email"
              id="email"
              name="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              aria-invalid={!isValidEmail}
              placeholder="Enter your email address"
              autoComplete="email"
              pattern="^[^\s@]+@[^\s@]+\.[^\s@]+$"
              title="Please enter a valid email address."
            />
          </div>

          <button
            type="submit"
            className="submit-button"
            disabled={loading || !isValidEmail}
          >
            {loading ? "Sending..." : "Send Password Reset"}
            {loading && <span className="spinner"></span>}
          </button>
        </form>
      )}

      <Link to={from} className="back-link">
        Go Back
      </Link>
    </section>
  );
};

export default ForgotPassword;
