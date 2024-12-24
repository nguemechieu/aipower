import { Link, useLocation, useNavigate } from "react-router-dom";
import { useEffect, useRef, useState } from "react";
import { axiosPrivate } from "../api/axios";
import { Button } from "@mui/material";

const ResetPassword = () => {
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [errMsg, setErrMsg] = useState("");
  const [success, setSuccess] = useState(false);
  const [loading, setLoading] = useState(false);
  const [token, setToken] = useState("");

  const navigate = useNavigate();
  const location = useLocation();
  const from = location.state?.from?.pathname || "/";

  useEffect(() => {
    tokenRef.current?.focus();
  }, []);

  const tokenRef = useRef(null);

  const resetPassword = async () => {
    setLoading(true);
    setSuccess(false);
    setErrMsg("");

    try {
      const response = await axiosPrivate.post("/api/v3/reset-password", {
        reset_token: token,
        password,
      });

      if (response.status === 200) {
        setSuccess(true);
        navigate(from);
      }
    } catch (error) {
      if (error.response) {
        setErrMsg(error.response.data.message);
      } else if (error.request) {
        setErrMsg("Network Error");
      } else {
        setErrMsg("An error occurred");
      }
    }

    setLoading(false);
  };

  return (
    <div>
      <h1>Reset Password</h1>
      {errMsg && <div className="error">{errMsg}</div>}
      {success && <div className="success">Password reset successful!</div>}
      <form onSubmit={resetPassword}>
        <input
          type="password"
          placeholder="New Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />
        <input
          type="password"
          placeholder="Confirm New Password"
          value={confirmPassword}
          onChange={(e) => setConfirmPassword(e.target.value)}
        />
        <input
          type="text"
          placeholder="Token (from email)"
          value={token}
          ref={tokenRef}
          onChange={(e) => setToken(e.target.value)}
        />
        <Button type="submit" disabled={loading}>
          {loading ? "Resetting Password..." : "Reset Password"}
        </Button>

        <div className="link">
          <Link to={from} className="back-link">
            Go Back
          </Link>
        </div>
      </form>
    </div>
  );
};

export default ResetPassword;
