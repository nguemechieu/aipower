import React, { useEffect, useState } from "react";
import SwaggerUI from "swagger-ui-react";
import "swagger-ui-react/swagger-ui.css";

import LoadingSpinner from "./LoadingSpinner";
import { axiosPublic } from "../api/axios";

const SwaggerUIComponent = () => {
  const [swaggerData, setSwaggerData] = useState(null);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchSwaggerData = async () => {
      try {
        const url = "/v3/api-docs"; // API docs URL

        // Directly fetch the Swagger documentation
        const response = await axiosPublic.get(url);

        // Set the fetched Swagger data
        setSwaggerData(response.data);
        console.log("Swagger data fetched successfully");
      } catch (error) {
        setError(error.message); // Handle error in case of failure
      }
    };

    fetchSwaggerData().catch((error) =>
      console.error("Failed to fetch Swagger data: ", error),
    ); // Fetch the Swagger data on component mount
  }, []);

  if (error) {
    return <div>{`Error: ${error}`}</div>; // Display an error message if fetching fails
  }

  if (!swaggerData) {
    return <LoadingSpinner />; // Show a loading message while data is being fetched
  }

  return <SwaggerUI spec={swaggerData} />; // Pass the Swagger spec to SwaggerUI component
};

export default SwaggerUIComponent;
