import axios from "axios";
import axiosRetry from "axios-retry";

// Set up API base URL and timeout from environment variables
const BASE_URL = process.env.REACT_APP_API_URL || "http://localhost:8080";
const TIMEOUT = process.env.REACT_APP_API_TIMEOUT || 5000;

// Public Axios instance
const axiosPublic = axios.create({
  baseURL: BASE_URL,
  headers: { "Content-Type": "application/json", Accept: "application/json" },
  timeout: TIMEOUT,
});

// Private Axios instance
const axiosPrivate = axios.create({
  baseURL: BASE_URL,
  headers: { "Content-Type": "application/json", Accept: "application/json" },
  timeout: TIMEOUT,
  withCredentials: true,
});

// Add retry logic with exponential backoff
axiosRetry(axiosPrivate, {
  retries: 3,
  retryDelay: axiosRetry.exponentialDelay,
  retryCondition: (error) =>
    !error.response || [500, 503].includes(error.response.status),
});

export { axiosPublic, axiosPrivate };
