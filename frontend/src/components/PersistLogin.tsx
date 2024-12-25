import React, { useContext, useEffect, useState } from "react";
import { Outlet, Navigate } from "react-router-dom";
import AuthContext, { AuthContextType } from "../context/AuthProvider"; // Import the correct type for context
import { axiosPrivate } from "../api/axios";
import LoadingSpinner from "./LoadingSpinner";

const PersistLogin: React.FC = () => {
  // Use context for authentication
  const { auth, setAuth } = useContext(AuthContext) as AuthContextType;

  // Track loading state
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    const verifyToken = async () => {
      try {
        if (auth?.accessToken) {
          // Check for an existing access token
          const response = await axiosPrivate.post("/api/v3/refresh");

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

    verifyToken().catch((error) =>
        console.error("Token verification failed:", error)
    );
  }, [auth?.accessToken, setAuth]); // Add only relevant dependencies

  if (loading) {
    return <LoadingSpinner />; // Show loading spinner while verifying
  }

  return auth?.username ? <Outlet /> : <Navigate to="/login" replace />; // Redirect if not authenticated
};

export default PersistLogin;
