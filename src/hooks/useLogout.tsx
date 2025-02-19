import React from "react";
import useAuth from "./useAuth";
import { useEffect, useState } from "react";

const useLogout = () => {
  const [error, setError] = useState("");
  const { accessToken } = useAuth();
  // code for logout functionality
  // call to backend to invalidate JWT token

  useEffect(() => {
    if (accessToken) {
      setError("Logged out successfully");
      sessionStorage.clear();
      localStorage.removeItem("accessToken");
      window.location.href = "/login";
    } else {
      setError("You are not logged in");
    }
  }, []);
  return error && <p>{error}</p>;
};
export default useLogout;
