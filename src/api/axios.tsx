import axios, { AxiosInstance, AxiosError } from "axios";
import axiosRetry from "axios-retry";

const BASE_URL: string =  "http://localhost:8080";

// Public Axios instance (No authentication)
const axiosPublic: AxiosInstance = axios.create({
  baseURL: BASE_URL,
    // No authentication required for public API

});

// Private Axios instance (Authenticated requests)
const axiosPrivate: AxiosInstance = axios.create({
  baseURL: BASE_URL,
  withCredentials: true,
});

// Add retry logic with exponential backoff for retryable errors
axiosRetry(axiosPrivate, {
  retries: 3,
  retryDelay: axiosRetry.exponentialDelay,
  retryCondition: (error: AxiosError) => {
    if (!error.response) {
      console.warn("Retrying request due to network failure...");
      return true; // Retry on network errors
    }
    return [500, 503].includes(error.response.status); // Retry on 500/503 errors
  },
});

// Request interceptor to add Authorization token dynamically
axiosPrivate.interceptors.request.use(
    (config) => {
      const token = localStorage.getItem("accessToken"); // Retrieve token from localStorage or secure storage
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
      return config;
    },
    (error) => Promise.reject(error)
);

// Response interceptor to handle 401 errors (Token expiration handling)
axiosPrivate.interceptors.response.use(
    (response) => response,
    async (error) => {
      if (error.response?.status === 401) {
        console.warn("Unauthorized! Token might be expired. Handle token refresh here.");
        // Implement token refresh logic if applicable (e.g., call refresh token endpoint)
       // await refresh();
        // Retry the original request after token refresh
        const retryConfig = {...error.config, retry: 1 };
        return axiosPrivate(retryConfig);
      }
      return Promise.reject(error);
    }
);

export { axiosPublic, axiosPrivate };
