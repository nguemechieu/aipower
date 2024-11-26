// components/LoadingSpinner.js
import React from "react";
import "./LoadingSpinner.css";

const LoadingSpinner = () => {
    return (
        <div className="loading-spinner" >
            Loading ...
            <div className="spinner-circle"></div>
            <div className="spinner-line">
                <div className="spinner-line-inner"></div>
            </div>
        </div>
    );
};

export default LoadingSpinner;
