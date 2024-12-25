import axios, { AxiosInstance, AxiosError } from "axios";
import axiosRetry from "axios-retry";

// Set up API base URL and timeout from environment variables
const BASE_URL: string = "http://localhost:8080";


// Public Axios instance
const axiosPublic: AxiosInstance = axios.create({
  baseURL: BASE_URL,
  headers: { "Content-Type": "application/json", Accept: "application/json" },
});

// Private Axios instance
const axiosPrivate: AxiosInstance = axios.create({
  baseURL: BASE_URL,
  headers: { "Content-Type": "application/json", Accept: "application/json" },
  withCredentials: true,
});

// Add retry logic with exponential backoff
axiosRetry(axiosPrivate, {
  retries: 3,
  retryDelay: axiosRetry.exponentialDelay,
  retryCondition: (error: AxiosError) =>
      !error.response || [500, 503].includes(error.response.status),
});

export { axiosPublic, axiosPrivate };
