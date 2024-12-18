import React, { useContext, useEffect, useState } from "react";
import { Outlet, Navigate } from "react-router-dom";
import AuthContext from "../context/AuthProvider.js";
import { axiosPrivate } from "../api/axios.js";
import LoadingSpinner from "./LoadingSpinner";

const PersistLogin = () => {
  const { auth, setAuth } = useContext(AuthContext); // Use context for authentication
  const [loading, setLoading] = useState(true); // Track loading state

  useEffect(() => {
    const verifyToken = async () => {
      try {
        if (auth?.accessToken) { // Check for an existing access token
          const response = await axiosPrivate.post("/api/v3/auth/refresh");

          if (response.status === 200) {
            console.log("Token verified successfully");
            setAuth((prevAuth) => ({
              ...prevAuth,
              accessToken: response.data.accessToken,

              username: response.data.username,
              role: response.data.role,
            }));
          }
        }
      } catch (error) {
        console.error("Token verification failed:", error);
        setAuth(null); // Clear auth context on failure
      } finally {
        setLoading(false); // Stop loading whether success or failure
      }
    };

    verifyToken().catch(error =>
        console.error("Token verification failed:", error)
      // Catch any errors during token verification and clear auth context on failure
    )
  }, [auth?.accessToken, setAuth]); // Add only relevant dependencies

  if (loading) {
    return <LoadingSpinner />; // Show loading spinner while verifying
  }

  return auth?.username ? <Outlet /> : <Navigate to="/login" replace />; // Redirect if not authenticated
};

export default PersistLogin;
