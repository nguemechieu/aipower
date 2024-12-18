import React from "react";
import SwaggerUI from "swagger-ui-react";
import "swagger-ui-react/swagger-ui.css";

const SwaggerUIComponent = () => {
    const customHeaders = {
        Authorization: `Bearer ${localStorage.getItem("accessToken")}`, // Replace with your token retrieval logic
    };

    return (
        <SwaggerUI
            url="http://localhost:3000/api-docs"
            requestInterceptor={(req) => {
                req.headers = { ...req.headers, ...customHeaders };
                return req;
            }}

        />
    );
};

export default SwaggerUIComponent;
