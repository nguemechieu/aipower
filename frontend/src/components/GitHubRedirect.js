import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import useAuth from "../hooks/useAuth";
import { axiosPrivate } from "../api/axios";

const GitHubRedirect = () => {
  const { setAuth } = useAuth(); // Custom hook to manage authentication
  const [error, setError] = useState("");
  const navigate = useNavigate(); // Hook for navigation

  useEffect(() => {
    const handleOAuthCallback = async (event) => {
      try {
        const params = new URLSearchParams(event.data);
        const code = params.get("code");

        if (!code) {
          setTimeout(() => {
            navigate("/login"); // Redirect to login if authentication fails
          }, 2000);

          return;
        }

        const response = await axiosPrivate.post(
          "/api/v3/auth/github/callback",
          { code },
        );

        const data = await response.data;
        if (data?.username) {
          console.log("User logged in:", data.username);
        } else {
          console.error("User not logged in.");
          setTimeout(() => {
            setError("Failed to authenticate with GitHub. Please try again.");
            navigate("/login"); // Redirect to login if authentication fails
          }, 2000);
        }

        if (response.status === 200 && data) {
          // Assuming the response contains the necessary authentication data
          setAuth({ ...data });
          navigate("/", { replace: true }); // Redirect to dashboard or home page
        } else {
          setTimeout(() => {
            setError("Failed to authenticate with GitHub. Please try again.");
            navigate("/login"); // Redirect to login if authentication fails
          }, 2000);

          console.error(
            "Failed to authenticate with GitHub. Please try again.",
          );
        }
      } catch (error) {
        console.log("Error during GitHub authentication:", error);
        setTimeout(() => {
          setError("An error occurred while authenticating with GitHub.");
          navigate("/login"); // Redirect to login if authentication fails
        }, 2000);
      }
    };

    // Add the message event listener for the OAuth callback
    window.addEventListener("message", handleOAuthCallback);

    // Cleanup the event listener when component unmounts
    return () => {
      window.removeEventListener("message", handleOAuthCallback);
    };
  }, [setAuth, navigate]);

  return (
    <div>
      <h1>Redirecting to GitHub...</h1>
      <p>Please wait while we redirect you to GitHub for authentication.</p>
      {error && <p style={{ color: "red" }}>{error}</p>}
    </div>
  );
};

export default GitHubRedirect;
