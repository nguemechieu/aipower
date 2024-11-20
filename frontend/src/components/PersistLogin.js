import React, { useContext, useEffect, useState } from "react";
import { Outlet, Navigate } from "react-router-dom";
import AuthContext from "../context/AuthProvider.js";
import axios from "../api/axios.js";

const PersistLogin = () => {
  const { auth, setAuth } = useContext(AuthContext);  // Add setAuth to update context
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const verifyToken = async () => {
      if (auth?.token) {  // Check if token exists before making the request
        try {
          const response = await axios.post("/api/v3/auth/refresh", {
            headers: {
              Authorization: `Bearer ${auth.token}`
            },
            withCredentials: true,
          });

          if (response.status === 200) {
            console.log("Token verified successfully");
            setAuth((prevAuth) => ({
              ...prevAuth,
              token: response.data.token, // Update the token in context
            }));
          }
        } catch (error) {
          console.log("Token verification failed ", error);
          setAuth(null); // Clear auth context if verification fails
        }
      }
      setLoading(false);
    };

    verifyToken().then(r =>
    console.log("Request completed successfully"  ))
  }, [auth, setAuth]);

  if (loading) {
    return <p>Loading...</p>; // Loading screen while token is verified
  }

  return auth ? <Outlet /> : <Navigate to="/login" replace />;
};

export default PersistLogin;
