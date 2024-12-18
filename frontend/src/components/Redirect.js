import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import  {axiosPrivate} from "../api/axios";
import useAuth from "../hooks/useAuth";

const Redirect = () => {
    const { auth, setAuth } = useAuth(); // Custom hook to handle authentication
    const navigate = useNavigate();
    const [error, setError] = useState(null);

    useEffect(() => {
        const authenticateGoogle = async () => {
            try {
                const res = await axiosPrivate.post("/api/v3/google/callback");
                if (res.status === 200) {
                    setAuth({ ...res.data }); // Update auth context with response data
                    navigate("/"); // Redirect to home or dashboard
                } else {
                    console.error("Failed to authenticate");
                    setError(
                        "Failed to authenticate. Please try again later."
                    )
                    navigate("/");
                }
            } catch (err) {
                setError(`Failed to authenticate: ${err.message}`);
                console.error("Error during authentication"+JSON.stringify( err));
                navigate("/");

                // Set timeout to clear the error after 3 seconds
                setTimeout(() => {
                    setError(null);
                }, 3000);
            }
        };

        authenticateGoogle().catch((err) => {
            console.error("Failed to authenticate "+JSON.stringify( err));
            setError(`Failed to authenticate: ${err.message}`);

            // Set timeout to clear the error after 3 seconds
            setTimeout(() => {
                setError(null);
            }, 3000);
        });
    }, [navigate, auth, setAuth]);

    return (
        <div>
            <h1>Redirecting...</h1>
            {error && <p className="error">{error}</p>}
            <p>You will be redirected shortly...</p>
        </div>
    );
};

export default Redirect;
