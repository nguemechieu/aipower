import axios from "axios";

const BASE_URL = "http://localhost:8080";

// Default instance for public requests
export default axios.create({
    baseURL: BASE_URL,
    headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
    }
});

// Instance for private requests
export const axiosPrivate = axios.create({
    baseURL: BASE_URL,
    headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
    },
    withCredentials: true,
    timeout: 10000
});

// Authorization Interceptor
axiosPrivate.interceptors.request.use(
    config => {
        const token = localStorage.getItem('accessToken');
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        return config;
    },
    error => Promise.reject(error)
);

// Error Handling Interceptor
axiosPrivate.interceptors.response.use(
    response => response,
    error => {
        if (error.response && error.response.status === 401) {
            // Handle unauthorized access, e.g., refresh token or redirect to login
            localStorage.removeItem('accessToken');
            window.location.href = '/login';
        }
        return Promise.reject(error);
    }
);
axios.interceptors.request.use((config) => {
    console.log("Request:", config);
    return config;
});

axios.interceptors.response.use((response) => {
    console.log("Response:", response);
    return response;
});