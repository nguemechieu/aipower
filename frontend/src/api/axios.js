import axios from 'axios';
const BASE_URL = 'http://localhost:8080';

export default axios.create({
    baseURL: BASE_URL
});
function getCsrfToken() {
    const cookies = document.cookie.split(';');
    for (let i = 0; i < cookies.length; i++) {
        const cookie = cookies[i].trim();
        if (cookie.startsWith('XSRF-TOKEN=')) {
            return cookie.substring('XSRF-TOKEN='.length, cookie.length);
        }
    }
    return null;
}

export const axiosPrivate = axios.create({
    baseURL: BASE_URL,
    headers: { 'Content-Type': 'application/json' },
     xsrfCookieName: 'XSRF-TOKEN',
    xsrfHeaderName: 'X-XSRF-TOKEN',
     // Include CSRF token in requests
    transformRequest: [
        (data, headers) => {
            if (headers['Content-Type'] === 'application/x-www-form-urlencoded') {
                return data;
            }
            return JSON.stringify(data);
        },
        (headers) => {
            headers['X-XSRF-TOKEN'] = getCsrfToken();
            return headers;
        }
    ],
    withCredentials: true
});
axiosPrivate.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response && error.response.status === 403) {
            console.error('CSRF token missing or invalid.');
        }
        return Promise.reject(error);
    }
);
