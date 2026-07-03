import axios from 'axios';
import toast from 'react-hot-toast';

const api = axios.create({
    baseURL: 'http://localhost:8080', // Default Spring Boot port
    timeout: 10000,
});

// Interceptor for global error handling
api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response) {
            // The request was made and the server responded with a status code
            // that falls out of the range of 2xx
            const message = error.response.data.error || 'An unexpected error occurred';
            
            // Display toast based on status code
            if (error.response.status === 400) {
                toast.error(`Validation Error: ${message}`);
            } else if (error.response.status === 409) {
                toast.error(`State Error: ${message}`);
            } else if (error.response.status === 404) {
                toast.error(`Not Found: ${message}`);
            } else {
                toast.error(`Server Error: ${message}`);
            }
        } else if (error.request) {
            // The request was made but no response was received
            toast.error('Network Error: Cannot connect to the server');
        } else {
            // Something happened in setting up the request that triggered an Error
            toast.error('Error: ' + error.message);
        }
        return Promise.reject(error);
    }
);

export default api;
