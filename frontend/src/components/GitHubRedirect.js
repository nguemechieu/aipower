import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import useAuth from "../hooks/useAuth";

const GitHubRedirect = () => {
    const { auth, setAuth } = useAuth(); // Custom hook to manage authentication
    const [error, setError] = useState("");
    const navigate = useNavigate(); // Hook for navigation



    useEffect(() => {
        // Listen for OAuth callback message
        const handleOAuthCallback = async (event) => {
            // Validate event origin
            if (event.origin !== "http://localhost:3000") return;

            const params = new URLSearchParams(event.data);
            const code = params.get("code");

            try {
                const response = await fetch("http://localhost:3000/api/v3/auth/github/callback", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                        Authorization: `Bearer ${event?.data?.access_token}`,
                    },
                    body: JSON.stringify({ code }),
                });

                const data = await response.json();
                    if (data.status === 200) {
                        // Set user data and redirect to dashboard
                        setAuth({ ...data });
                        navigate("/dashboard");
                    } else {
                        setError("Failed to authenticate with GitHub. Please try again.");
                        navigate("/login");
                    }

            } catch (error) {
                console.error("Error:"+JSON.stringify( error));
                setError("An error occurred while authenticating with GitHub.");
                navigate("/login");
            }
        };

        // Add the message event listener for the OAuth callback
        window.addEventListener("message", handleOAuthCallback);

        // Cleanup the event listener when component unmounts
        return () => {
            window.removeEventListener("message", handleOAuthCallback);
        };
    }, [auth, setAuth, navigate]);

    return (
        <div>
            <h1>Redirecting to GitHub...</h1>
            <p>Please wait while we redirect you to GitHub for authentication.</p>
            {error && <p style={{ color: "red" }}>{error}</p>}
        </div>
    );
};

export default GitHubRedirect;
